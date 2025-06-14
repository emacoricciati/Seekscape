const { onDocumentWritten } = require("firebase-functions/v2/firestore");
const { onSchedule } = require("firebase-functions/v2/scheduler");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");

initializeApp();
const db = getFirestore();


//Update number of travels for each travel companion
async function updateNumTravelsForCompanions(travelCompanions) {
  const batch = db.batch();
  travelCompanions.forEach(comp => {
    const userRef = db.collection("Users").doc(comp.userId);
    batch.update(userRef, {
      numTravels: (comp.numTravels || 0) + 1
    });
  });
  await batch.commit();
}




// --------------------------- MANAGE TRAVEL STATUS -----------------------------

function parseDate(dateStr) {
  const [year, month, day] = dateStr.split("-").map(Number);
  return new Date(year, month - 1, day); // month is 0-based in JS
}


async function updateTravelStatus(docSnap) {

  if (!docSnap.exists) return;
  const docRef = docSnap.ref;


  await db.runTransaction(async (transaction) => {
    const doc = await transaction.get(docRef);
    if (!doc.exists) return;

    const data = doc.data();
    if (!data || data.status === "deleted") return;

    const today = new Date();
    const startDate = parseDate(data.startDate);
    const endDate = parseDate(data.endDate);

    let status = "available";
    if (today < startDate) {
      let numberOfParticipants = 0;
      (data.travelCompanions || []).forEach(comp => {
        numberOfParticipants += 1 + (comp.extras || 0);
      });
      if (numberOfParticipants >= data.maxPeople) {
        status = "full";
      }
    } else if (today >= startDate && today <= endDate) {
      status = "full";
    } else if (today > endDate) {
      status = "past";
    }

    if (data.status !== status) {
      transaction.update(docRef, { status });
      console.log(`Status of travel ${data.travelId} updated to ${status}`);

      if (status == "past") {
        await updateNumTravelsForCompanions(data.travelCompanions);
      }
    }
  });
}


exports.travelStatus_checkOnUpdate = onDocumentWritten("Travels/{travelId}", async (event) => {
  await updateTravelStatus(event.data.after);
  await sendNotificationMessage(event);
  return null;
});


async function manageExpiredRequests() {
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, '0');
  const dd = String(today.getDate()).padStart(2, '0');
  const formattedDate = `${yyyy}-${mm}-${dd}`;

  const snapshot = await db.collection("Travels")
    .where("startDate", "==", formattedDate)
    .get();
  const travelIds = snapshot.docs.map(doc => doc.id);

  if (travelIds.length === 0) {
    console.log("No travels found starting today.");
    return travelIds;
  }

  const requestsRef = db.collection("Requests");
  const batchSize = 10;

  for (let i = 0; i < travelIds.length; i += batchSize) {
    const chunk = travelIds.slice(i, i + batchSize);

    const snapshot = await requestsRef
      .where("tripId", "in", chunk)
      .where("accepted", "==", false)
      .where("refused", "==", false)
      .get();

    const batch = db.batch();

    snapshot.docs.forEach(doc => {
      batch.update(doc.ref, {
        refused: true,
        lastUpdate: formattedDate,
        responseMessage: "The creator of the travel has not accepted your request, so it has been automatically refused."
      });
    });
    await batch.commit();
  }

}

exports.travelStatus_checkStartDay = onSchedule("1 0 * * *", async (event) => {
  const snapshot = await db.collection("Travels").get();
  const promises = snapshot.docs.map(docSnap => updateTravelStatus(docSnap));
  await Promise.all(promises);

  await manageExpiredRequests();
});




// --------------------------- MANAGE NOTIFICATIONS -----------------------------


//manage the sending of notifications to users

async function sendFirebaseCloudMessagge(notification, userTokens, userId) {
  try {
    const messages = userTokens.map(token => ({
      token: token,
      notification: {
        title: notification.title,
        body: notification.description,
      },
      android: {
        notification: {
          sound: "default",
          priority: "high",
        },
        restrictedPackageName: "it.polito.mad.lab5g10.seekscape",
      },
      data: {
        ...notification,
      },
    }));

    const response = await getMessaging().sendEach(messages);

    const successCount = response.responses.filter(res => res.success).length;
    const failureCount = response.responses.length - successCount;

    console.log(`For user ${userId}, ${successCount} notifications sent, ${failureCount} failed.`);

    if (failureCount > 0) {
      const failedTokens = response.responses
        .map((res, idx) => (!res.success ? userTokens[idx] : null))
        .filter(Boolean);
      console.warn("Failed tokens:", failedTokens);
    }

    return response;
  } catch (error) {
    console.error("Error sending FCM:", error);
  }
}

exports.notifications_send = onDocumentWritten("Users/{userId}", async (event) => {
  const before = event.data.before?.data();
  const after = event.data.after?.data();

  if (!after || !before) return null;

  const beforeNotifications = before.notifications || [];
  const afterNotifications = after.notifications || [];

  const numNewNotifications = afterNotifications.length - beforeNotifications.length;
  if (numNewNotifications > 0) {
    const userTokens = after.fcmTokens;

    if (!Array.isArray(userTokens) || userTokens.length === 0) {
      console.warn(`No FCM tokens found for user ${event.params.userId}`);
      return null;
    }

    for (let i = 0; i < numNewNotifications; i++) {
      const newNotification = afterNotifications[i];

      console.log(`For user ${event.params.userId}, sending the notification:`, newNotification);
      await sendFirebaseCloudMessagge(newNotification, userTokens, event.params.userId);
    }
  }

  return null;
});

async function addNotificationToUser(notification, userId) {
  console.log(`userId: ${userId}, notification composed:`, notification);

  const targetUserRef = db.collection("Users").doc(userId);
  await db.runTransaction(async (transaction) => {
    const targetUserSnap = await transaction.get(targetUserRef);
    if (!targetUserSnap.exists) return;

    const targetUser = targetUserSnap.data();
    const existingNotifications = targetUser.notifications || [];

    if (notification.id.startsWith("msg_")) {
      const notificationExists = existingNotifications.some(
        (notif) => notif.id === notification.id
      );
      if (notificationExists) {
        console.log(`Notification with id ${notification.id} already exists for user ${userId}.`);
        return;
      }
    }


    transaction.update(targetUserRef, {
      notifications: [notification, ...existingNotifications]
    });

    console.log(`Notification of type '${notification.type}' added to user ${userId}`);
  });
}

/*
data class NotificationItem(
    val id: Int,
    val type: String,
    val title: String,
    val description: String,
    val tab: String,
    val navRoute: String,
) : Serializable
*/


// manage notification for review created on user 

exports.notifications_create_my_profile_review = onDocumentWritten("Users/{userId}", async (event) => {
  const before = event.data.before?.data();
  const after = event.data.after?.data();

  if (!after || !before) return null;

  const beforeReviews = before.reviews || [];
  const afterReviews = after.reviews || [];

  const numReviews = afterReviews.length - beforeReviews.length;
  for (i = 0; i < numReviews; i++) {
    const review = afterReviews[i];
    const userId = after.userId;

    const authorDocSnap = await db.collection("Users").doc(review.authorId).get();
    if (!authorDocSnap || !authorDocSnap.exists) return;
    const authorDoc = authorDocSnap.data();

    const notification_type = "my_profile_review";
    const notification = {
      id: `${notification_type}_${authorDoc.userId}`,
      type: notification_type,
      title: "You have a new review",
      description: `Your profile has been reviewed by ${authorDoc.nickname || "someone"}.`,
      tab: "profile",
      navRoute: `profile/${userId}`
    };

    if (!userId || !notification) return;
    await addNotificationToUser(notification, event.params.userId);
  }

  return null;
});



// manage notification for review created on travel 

exports.notifications_create_my_travel_review = onDocumentWritten("Travels/{travelId}", async (event) => {
  const before = event.data.before?.data();
  const after = event.data.after?.data();

  if (!after || !before) return null;

  const beforeReviews = before.travelReviews || [];
  const afterReviews = after.travelReviews || [];

  const numReviews = afterReviews.length - beforeReviews.length;
  for (i = 0; i < numReviews; i++) {
    const review = afterReviews[i];

    await new Promise(resolve => setTimeout(resolve, 1000));

    // --- Update average rating with 1 decimal ---
    const ratings = afterReviews.map(r => r.rating).filter(r => typeof r === 'number' && !isNaN(r));
    if (ratings.length > 0) {
      const total = ratings.reduce((sum, rating) => sum + rating, 0);
      const average = Math.round((total / ratings.length) * 10) / 10;

      console.log(`Updating travelRating to: ${average}`);

      try {
        const travelRef = db.collection("Travels").doc(event.params.travelId);
        await travelRef.update({ travelRating: average });
        console.log("travelRating successfully updated.");
      } catch (error) {
        console.error("Failed to update travelRating:", error);
      }
    }

    const creatorId = after.creatorId;
    const authorDocSnap = await db.collection("Users").doc(review.authorId).get();
    if (!authorDocSnap || !authorDocSnap.exists) return;
    const authorDoc = authorDocSnap.data();

    const notification_type = "my_travel_review";
    const notification = {
      id: `${notification_type}_${after.travelId}`,
      type: notification_type,
      title: "Your travel has a new review",
      description: `Your travel '${after.title}' has been reviewed by ${authorDoc.nickname || "someone"}.`,
      tab: "travels",
      navRoute: `travel/${after.travelId}`
    };

    if (creatorId && notification) {//apparently it does not enter here
      try {
        await addNotificationToUser(notification, creatorId);
        console.log("notifications_create_my_travel_review successfully updated.");
      } catch (error) {
        console.error("Failed to update notifications_create_my_travel_review:", error);
      }
    } else {
      console.warn(`No creatorId found for travel ${event.params.travelId} or notification is null.`);
      console.warn(`creatorId: ${creatorId}, notification:`, notification);
    }


  }
  return null;
});





// manage notification for request creation, acceptance, and refusal

exports.notifications_create_requestsNot = onDocumentWritten("Requests/{requestId}", async (event) => {
  const doc = event.data.after?.data();

  if (!doc) return null;

  const authorDocSnap = await db.collection("Users").doc(doc.authorId).get();
  if (!authorDocSnap || !authorDocSnap.exists) return;
  const authorDoc = authorDocSnap.data();

  const travelDocSnap = await db.collection("Travels").doc(doc.tripId).get();
  if (!travelDocSnap || !travelDocSnap.exists) return;
  const travelDoc = travelDocSnap.data();

  const creatorDocSnap = await db.collection("Users").doc(travelDoc.creatorId).get();
  if (!creatorDocSnap || !creatorDocSnap.exists) return;
  const creatorDoc = creatorDocSnap.data();

  const tripTitle = travelDoc?.title || "a travel experience";

  let targetUserId = null;
  let notification = null;

  if (!doc.accepted && !doc.refused) {
    // Case: request created

    const notification_type = "manage_apply";
    targetUserId = creatorDoc.userId;
    notification = {
      id: `${notification_type}_${doc.authorId}_${doc.tripId}`,
      type: notification_type,
      title: `Application for ${doc.spots || 1} spot(s)`,
      description: `${authorDoc.nickname || "Someone"} is interested in '${tripTitle}'.`,
      tab: "travels",
      navRoute: `travels/action/SHOW_APPLY_${doc.tripId}_${doc.authorId}`
    };

  } else if (doc.accepted && !doc.refused) {
    // Case: request accepted

    const notification_type = "request_accepted";
    targetUserId = authorDoc.userId;
    notification = {
      id: `${notification_type}_${doc.authorId}_${doc.tripId}`,
      type: notification_type,
      title: "Request Accepted",
      description: `Your request for '${tripTitle}' has been accepted.`,
      tab: "travels",
      navRoute: `travel/${doc.tripId}/action/SHOW_RESPONSE`
    };
  } else if (!doc.accepted && doc.refused) {
    // Case: request refused

    const notification_type = "request_denied";
    targetUserId = authorDoc.userId;
    notification = {
      id: `${notification_type}_${doc.authorId}_${doc.tripId}`,
      type: notification_type,
      title: "Request Denied",
      description: `Your request for '${tripTitle}' has been denied.`,
      tab: "travels",
      navRoute: `travel/${doc.tripId}/action/SHOW_RESPONSE`
    };
  }

  if (!targetUserId || !notification) return;
  await addNotificationToUser(notification, targetUserId);



  //DELETE NOTIFICATION
  let docSnapBefore = event.data.before;
  if (!docSnapBefore || !docSnapBefore.exists) return;

  const docBefore = docSnapBefore.data();
  if (!docBefore) return;

  if (!docBefore.accepted && !docBefore.refused && (doc.accepted || doc.refused)) {
    await deleteUserNotificationById(creatorDoc.userId, `manage_apply_${doc.authorId}_${doc.tripId}`);
  }

  return null;
});



async function sendNotificationMessage(event) {
  const before = event.data.before?.data();
  const after = event.data.after?.data();

  if (!after || !before) return null;

  const beforeMessages = before.travelChat || [];
  const afterMessages = after.travelChat || [];

  const numMessages = afterMessages.length - beforeMessages.length;
  if (numMessages > 0) {
    const msg = afterMessages[afterMessages.length - 1];

    const notification_type = "msg";
    const notification = {
      id: `${notification_type}_${after.travelId}`,
      type: notification_type,
      title: `New messages for ${after.title || "a travel"}`,
      description: `Stay updated with the latest news from your travel companions.`,
      tab: "travels",
      navRoute: `travel/${after.travelId}/chat`
    };

    if (!notification) return;

    const companionIds = after.travelCompanions.map(comp => comp.userId);
    companionIds.forEach(async companionId => {
      if (companionId !== msg.authorId) {
        await addNotificationToUser(notification, companionId);
      }
    })
  }

  return null;


}


// manage notification for last minute trip

exports.notifications_create_last_minute_join = onSchedule("0 17 * * *", async (event) => {

  const snapshotUsers = await db.collection("Users").get();
  const allUsersIds = snapshotUsers.docs.map(doc => doc.id);
  await deleteAllLastMinuteJoin(allUsersIds)

  const snapshotTravels = await db.collection("Travels").get();
  const promises = snapshotTravels.docs.map(docSnap => {
    if (!docSnap.exists) return;

    const doc = docSnap.data();
    if (!doc || doc.status === "deleted" || doc.status === "full" || doc.status === "past") return;

    const today = new Date();
    const threeDaysFromNow = new Date();
    threeDaysFromNow.setDate(today.getDate() + 3);
    const startDate = parseDate(doc.startDate);

    if (startDate > today && startDate < threeDaysFromNow) {

      const notification_type = "last_minute_join";
      const notification = {
        id: `${notification_type}_${doc.travelId}`,
        type: notification_type,
        title: `Last Minute Trip: ${doc.title}`,
        description: `Join '${doc.title}' starting on ${doc.startDate}.`,
        tab: "explore",
        navRoute: `travel/${doc.travelId}`
      };

      for (const userId of allUsersIds) {
        if (userId === doc.creatorId) continue;
        if (doc.travelCompanions.some(comp => comp.userId === userId)) continue;

        return addNotificationToUser(notification, userId);
      }

    }
  });
  await Promise.all(promises);
});

async function deleteAllLastMinuteJoin(allUsersIds) {
  const notification_type = "last_minute_join";
  const batch = db.batch();
  for (const userId of allUsersIds) {
    const userRef = db.collection("Users").doc(userId);
    const userSnap = await userRef.get();
    if (!userSnap.exists) continue;

    const userData = userSnap.data();
    const notifications = userData.notifications || [];
    const updatedNotifications = notifications.filter(notif => !notif.id.startsWith(notification_type));

    batch.update(userRef, { notifications: updatedNotifications });
  }

  await batch.commit();
  console.log(`Deleted all "${notification_type}" notifications for ${allUsersIds.length} users.`);
}


async function deleteUserNotificationById(userId, notificationId) {
  const userRef = db.collection("Users").doc(userId);
  const userSnap = await userRef.get();
  if (!userSnap.exists) return;
  try {
    await db.runTransaction(async (transaction) => {
      const userSnap = await transaction.get(userRef);
      if (!userSnap.exists) {
        console.log(`User ${userId} does not exist.`);
        return;
      }

      const userData = userSnap.data();
      const notifications = userData.notifications || [];

      const updatedNotifications = notifications.filter(
        (notif) => notif.id !== notificationId
      );

      if (updatedNotifications.length !== notifications.length) {
        transaction.update(userRef, { notifications: updatedNotifications });
      } else {
        console.log(`Notification ${notificationId} not found for user ${userId}.`);
      }
    });

    console.log(`Notification with id ${notificationId} deleted for user ${userId}`);
  } catch (error) {
    console.error(`Transaction failed for user ${userId} notification ${notificationId}:`, error);
  }
}


