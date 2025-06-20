package it.polito.mad.lab5g10.seekscape.models

import java.io.Serializable


//TYPE OF NOTIFICATIONS
const val NOT_MY_PROFILE_REV="my_profile_review"
const val NOT_MY_TRAVEL_REV="my_travel_review"
const val NOT_LAST_MINUTE_JOIN="last_minute_join"
const val NOT_MSG = "msg"
const val NOT_REQ_ACC = "request_accepted"
const val NOT_REQ_DEN = "request_denied"
const val NOT_APPLY = "manage_apply"
const val NOT_ACCOUNT = "account"
const val NOT_REMINDER = "reminder"

data class NotificationItem(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val tab: String = "",
    val navRoute: String = ""
) : Serializable


data class UserNotificationSettings(
    val apply: Boolean = true,
    val applyAnswer: Boolean = true,
    val msg: Boolean = true,
    val lastMinute: Boolean = true,
    val review: Boolean = true,
) : Serializable