# 🔥 Firestore database overview

## Collections

### Users collection

```json
{
  "userId": "2",
  
  "authUID": "", // for firebase authentication
  "fmcTokens":["","token"], // to send push notifications

  "nickname": "olivia",
  "name": "Olivia",
  "surname": "Bennett",
  "birthDay": "YYYY-MM-dd",
  "nationality": "Canadian",
  "city": "Toronto, Canada",
  "language": "English",
  "phoneNumber": "N/A",
  "email": "olivia.bennett@example.com",
  "profilePic": {
    "type": "url",
    "resId": "id_of_resource"
  },
  "bio": "I’m a regular traveler who enjoys nature and peaceful destinations. I like to travel independently and live like a local with a focus on eco-travel and self-discovery.",
  "personality": [
    "Introvert",
    "Nature-lover",
    "Budget-concious"
  ],
  "travelPreferences": [
    "Eco-friendly accommodations",
    "Access to nature",
    "Affordable and sustainable travel options"
  ],
  "desiredDestinations": [
    "Canada",
    "Nature destinations"
  ],
  "reviews": [
    {
      "date": "YYYY-MM-dd",
      "reviewText": "",
      "rating": 4.5,
      "author": "3", //userId of author
    }
  ],
  "notifications":[
    {//class NotificationItem
      "id": "String",
      "type": "String",
      "title": "String",
      "description": "String",
      "tab": "String",
      "navRoute": "String",
    }
  ],
  "notificationSettings": {
    "apply": true,
    "applyAnswer": true,
    "msg": true,
    "lastMinute": true,
    "review": true,
  }
  
}
```

### Travels collection

```json
{
  "travelId": "1",
  "creator": "2", //userId of creator
  "title": "",
  "description": "",
  "country": "Portugal",
  "priceMin": 200,
  "priceMax": 250,
  "distance": "58 km",
  "startDate": "YYYY-MM-dd",
  "endDate": "YYYY-MM-dd",
  "maxPeople": 5,
  "travelTypes": ["Cultural"],
  "travelImages": [
    { "type": "Url", "resId": "id_resource" },
  ],
  "travelItinerary": [
    {
      "itineraryId": 1,
      "name": "Day 1: Welcome to Lisbon!",
      "startDate": "YYYY-MM-dd",
      "endDate": null, //optional value
      "places": ["Lisbon, Portugal"],
      "description": "",
      "itineraryImages": [],
      "activities": [
        {
          "name": "Landscapes",
          "optional": true,
          "icon": "view"
        },
      ]
    },
  ],
  "travelCompanions": [
    {
      "user": "10", //userId of companion
      "extras": 0
    },
    {
      "user": "11", //userId of companion
      "extras": 1
    }
  ],
  "travelReviews": [
    {
    "travelReviewText": "",
     "rating": 4.0,
     "author": "3", //userId of companion
     "reviewImages": [
        { "type": "Url", "resId": "id_resource" },
      ],
     "date": "YYYY-MM-dd"
     }
  ],
  "travelChat":[
    {
      "authorId": "system",
      "date": "YYYY-MM-dd HH-mm",
      "text": "New companion"
    }
  ]
}

```



### Requests collection

```json
{ //class Request
  "id": "5",
  "author": "5", // userId 
  "reqMessage": "please",
  "lastUpdate": "YYYY-MM-dd",
  "isAccepted": false,
  "isRefused": false,
  "spots": 1,
  "responseMessage": ""
}
```
## DTO

### My profile - class User
```json
{
  "userId": "2",
  "nickname": "olivia",
  "name": "Olivia",
  "surname": "Bennett",
  "age": 19, //calculated based on birthday
  "nationality": "Canadian",
  "city": "Toronto, Canada",
  "language": "English",
  "phoneNumber": "N/A",
  "email": "olivia.bennett@example.com",
  "profilePic": {//class ProfilePic
    "type": "url",
    "resId": "https://firebasestorage.googleapis.com/"// url composed
  },
  "bio": "I’m a regular traveler who enjoys nature and peaceful destinations. I like to travel independently and live like a local with a focus on eco-travel and self-discovery.",
  "personality": [
    "Introvert",
    "Nature-lover",
    "Budget-concious"
  ],
  "travelPreferences": [
    "Eco-friendly accommodations",
    "Access to nature",
    "Affordable and sustainable travel options"
  ],
  "desiredDestinations": [
    "Canada",
    "Nature destinations"
  ],
  "reviews": [
    { // class Review
      "date": "YYYY-MM-dd",
      "reviewText": "",
      "rating": 4.5,
      "author": {/*profile lite - class User*/}
    }
  ],
  "notifications":[
    {//class NotificationItem
      "id": "String",
      "type": "String",
      "title": "String",
      "description": "String",
      "tab": "String",
      "navRoute": "String",
    }
  ]
  
}
```

### Others profile - class User
```json
{
  "userId": "2",
  "nickname": "olivia",
  "name": "Olivia",
  "surname": "Bennett",
  "age": 19, //calculated based on birthday
  "nationality": "Canadian",
  "city": "Toronto, Canada",
  "language": "English",
  /* // OMITTED
  "phoneNumber": "N/A",
  "email": "olivia.bennett@example.com",
  */
  "profilePic": {//class ProfilePic
    "type": "url",
    "resId": "https://firebasestorage.googleapis.com/"// url composed
  },
  "bio": "I’m a regular traveler who enjoys nature and peaceful destinations. I like to travel independently and live like a local with a focus on eco-travel and self-discovery.",
  "personality": [
    "Introvert",
    "Nature-lover",
    "Budget-concious"
  ],
  "travelPreferences": [
    "Eco-friendly accommodations",
    "Access to nature",
    "Affordable and sustainable travel options"
  ],
  "desiredDestinations": [
    "Canada",
    "Nature destinations"
  ],
  "reviews": [
    {// class Review
      "date": "YYYY-MM-dd",
      "reviewText": "",
      "rating": 4.5,
      "author": {/*profile lite - class User*/}
    }
  ]
  
}
```

### profile lite - class User
```json
{
  "userId": "2",
  "nickname": "olivia",
  "name": "Olivia",
  "surname": "Bennett",
  "profilePic": { //class ProfilePic
    "type": "url",
    "resId": "https://firebasestorage.googleapis.com/"// url composed
  },
}
```




### travel - class Travel
```json
{
  "travelId": "1",
  "creator": {/*profile lite - class User*/},
  "title": "",
  "description": "",
  "country": "Portugal",
  "priceMin": 200,
  "priceMax": 250,

  "status":"", //evaluated from travel data
  "statusForUser":"",//evaluated from travel data & request (requests or ownership)

  "distance": "58 km",
  "startDate": "YYYY-MM-dd",
  "endDate": "YYYY-MM-dd",
  "maxPeople": 5,
  "travelTypes": ["Cultural"],
  "travelImages": [
    { "type": "Url", "resId": "id_resource" },
  ],
  "travelItinerary": [
    { // class Itinerary
      "itineraryId": 1,
      "name": "Day 1: Welcome to Lisbon!",
      "startDate": "YYYY-MM-dd",
      "endDate": null, //optional value
      "places": ["Lisbon, Portugal"],
      "description": "",
      "itineraryImages": [],
      "activities": [
        { // class Activities
          "name": "Landscapes",
          "optional": true,
          "icon": "view"
        },
      ]
    },
  ],
  "travelCompanions": [
    {
      "user": {/*profile lite - class User*/},
      "extras": 0
    },
    {
      "user": {/*profile lite - class User*/},
      "extras": 1
    }
  ],
  "travelReviews": [
    {
    "travelReviewText": "",
     "rating": 4.0,
     "author": {/*profile lite - class User*/},
     "reviewImages": [
        { "type": "Url", "resId": "id_resource" },
      ],
     "date": "YYYY-MM-dd"
     }
  ],
}
```



