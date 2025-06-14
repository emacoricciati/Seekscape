package it.polito.mad.lab5g10.seekscape.firebase

import java.time.LocalDate

import it.polito.mad.lab5g10.seekscape.R
import it.polito.mad.lab5g10.seekscape.models.AVAILABLE
import it.polito.mad.lab5g10.seekscape.models.Activity
import it.polito.mad.lab5g10.seekscape.models.ChatMessage
import it.polito.mad.lab5g10.seekscape.models.FULL
import it.polito.mad.lab5g10.seekscape.models.Itinerary
import it.polito.mad.lab5g10.seekscape.models.PAST
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.Review
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelReview
import it.polito.mad.lab5g10.seekscape.models.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.String
import kotlin.collections.listOf
import kotlin.random.Random

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


val system_light = User(
    "system", "", "", "", "", 0, "", "", "", "", "",
    ProfilePic.Resource(R.drawable.icon_logo),
    "",listOf<String>(),null,null,null,null,null,0
)
val system_dark = User(
    "system", "", "", "", "", 0, "", "", "", "", "",
    ProfilePic.Resource(R.drawable.icon_logo_dark_mode),
    "",listOf<String>(),null,null,null,null,null,0
)

fun getSystemMessageJoined(user: User): String {
    return "New companion - ${user.nickname}"
}
fun getSystemMessageLeft(user: User): String {
    return "Companion left - ${user.nickname}"
}

val unknown_User = User(
    "",
    "",
    "unknown_user",
    "unknown",
    "user",
    0, "","","","","",
    null,
    "",listOf<String>(),null,null,null,null,null,0
)


fun generateRandomDateOfBirth(age: Int): String {
    val today = LocalDate.now()
    val startDate = today.minusYears((age + 1).toLong()).plusDays(1)
    val endDate = today.minusYears(age.toLong())

    val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt()
    val randomDays = Random.nextInt(daysBetween + 1)

    val randomDate = startDate.plusDays(randomDays.toLong())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return randomDate.format(formatter)
}



//--------------------------USERS

val user_ob = User(
    userId = "2",
    authUID = "CG9b5uxR2Xc9tq2Pqm09JPsiN5G3",
    nickname = "olivia",
    name = "Olivia",
    surname = "Bennett",
    phoneNumber = "N/A",
    email = "olivia.bennett@example.com",
    profilePic = ProfilePic.Resource(R.drawable.avatar_ob),
    bio = "I’m a regular traveler who enjoys nature and peaceful destinations. I like to travel independently and live like a local with a focus on eco-travel and self-discovery.",
    travelPreferences = mutableListOf(
        "Eco-friendly accommodations",
        "Access to nature",
        "Affordable and sustainable travel options"
    ),
    desiredDestinations = mutableListOf("Canada", "Nature destinations"),
    age = 19,
    nationality = "Canadian",
    city = "Toronto, Canada",
    language = "English",
    numTravels = 5,
    personality = listOf("Introvert", "Nature-lover", "Budget-concious")
)

val user_sl = User(
    userId = "3",
    authUID = "fxlUB1dO5FNha2MGSfNdcviXmPt1",
    nickname = "sophia",
    name = "Sophia",
    surname = "Lancaster",
    phoneNumber = "N/A",
    email = "sophia.lancaster@example.com",
    profilePic = ProfilePic.Resource(R.drawable.avatar_sl),
    bio = "I’m new to solo and guided traveler, quieter trips. I enjoy cultural destinations but need comfort and safety. I’m looking for safe and clean places to help explore the world.",
    travelPreferences = mutableListOf(
        "Safe and clean accommodations",
        "Clear itineraries with free time to explore",
        "Affordable travel options"
    ),
    desiredDestinations = mutableListOf("UK", "Europe"),
    age = 32,
    nationality = "British",
    city = "London, UK",
    language = "English",
    numTravels = 9,
    personality = listOf("Introvert", "Urban", "Budget-concious"),
    reviews = listOf(
        Review(
            LocalDate.parse("07/04/2025", formatter)!!,
            "Sophia is incredibly insightful and has a great eye for beautiful places. We had such enriching conversations. She's peaceful, curious, and always respectful.",
            4.9,
            user_ob
        ),
    )
)

val user_dw = User(
    userId = "4",
    authUID = "2LvKZ8e73qfGEfYr0VLl7iL5AWz1",
    nickname = "daniel",
    name = "Daniel",
    surname = "Whitmore",
    phoneNumber = "N/A",
    email = "daniel.whitmore@example.com",
    profilePic = ProfilePic.Resource(R.drawable.avatar_dw),
    bio = "I love traveling with groups and organizing trips for friends. I enjoy social experiences, good food, and spontaneity. I'm always searching for memorable trips within my crew.",
    travelPreferences = mutableListOf(
        "Group-friendly accommodations",
        "Reliable transportation for groups",
        "Access to local nightlife and dining"
    ),
    desiredDestinations = mutableListOf("Germany", "Group travel spots"),
    age = 35,
    nationality = "German",
    city = "berlin, Germany",
    language = "German",
    numTravels = 5,
    personality = listOf("Extrovert", "Urban", "Spender"),
    reviews = listOf(
        Review(
            LocalDate.parse("30/04/2025", formatter)!!,
            "Daniel is super down-to-earth and easy to travel with. He has a knack for finding great deals and interesting spots off the map.",
            4.4,
            user_sl
        ),
        Review(
            LocalDate.parse("19/01/2025", formatter)!!,
            "Traveling with Daniel was relaxed and smooth. He’s resourceful and thoughtful. We had some great conversations during long train rides!",
            4.3,
            user_ob
        )
    )
)

val user_me = User(
    userId = "5",
    authUID = "qHqjgr6hNQPWWNzIQs3MpeRt65Y2",
    nickname = "marcus",
    name = "Marcus",
    surname = "Ellison",
    phoneNumber = "N/A",
    email = "marcus.ellison@example.com",
    profilePic = ProfilePic.Resource(R.drawable.avatar_me),
    bio = "I’m an adrenaline junkie who loves extreme sports and off-the-beaten-path adventures. I'm always up for a challenge and enjoy meeting motivated travelers.",
    travelPreferences = mutableListOf(
        "Adventure activities (e.g., climbing, scuba diving)",
        "Flexible itineraries with room for spontaneity",
        "Peer-rated accommodations"
    ),
    desiredDestinations = mutableListOf("Australia", "Extreme destinations"),
    age = 29,
    nationality = "Australian",
    city = "Sydney, Australia",
    language = "English",
    numTravels = 4,
    personality = listOf("Extrovert", "Adventurous", "Tech-savy"),
    reviews = listOf(
        Review(
            LocalDate.parse("18/02/2025", formatter)!!,
            "Marcus brought so much energy to the trip. He’s outgoing, full of ideas, and always looking for the next exciting thing. Never a dull moment with him around!",
            4.7,
            user_dw
        ),
        Review(
            LocalDate.parse("10/06/2024", formatter)!!,
            "Super fun and fearless traveler. Marcus will turn a normal day into an adventure. Great vibe and easy to get along with.",
            4.5,
            user_sl
        )
    )
)

val user_eh = User(
    userId = "6",
    authUID = "UicvwzSRzpQQ80yXS5X6LdHN2Ly2",
    nickname = "emily",
    name = "Emily",
    surname = "Hawthorne",
    phoneNumber = "N/A",
    email = "emily.hawthorne@example.com",
    profilePic = null,
    bio = "I like my travel with a story: exploring cities and historical sites. I enjoy traveling with small groups and need a balance of structure and free time.",
    travelPreferences = mutableListOf(
        "Cultural and historical attractions",
        "Clear itineraries with free time to explore",
        "Affordable travel options"
    ),
    desiredDestinations = mutableListOf("France", "Historic cities"),
    age = 27,
    nationality = "French",
    city = "Paris, France",
    language = "French",
    numTravels = 9,
    personality = listOf("Introvert", "Urban", "Budget-concious"),
    reviews = listOf(
        Review(
            LocalDate.parse("12/03/2025", formatter)!!,
            "Emily is incredibly organized and considerate. She brought a calm and thoughtful energy to the trip. Perfect hiking partner with a good sense of direction and a great eye for photo spots.",
            4.8,
            user_me
        ),
        Review(
            LocalDate.parse("05/08/2024", formatter)!!,
            "I loved traveling with Emily! She’s responsible and always prepared. It was a really grounding experience. Would absolutely do a nature trip together again.",
            4.6,
            user_dw
        )
    )
)

val user_ec = User(
    userId = "1",
    authUID = "SaBbm7SAOfYkO4CB3Il8xNSQxyp2",
    nickname = "ethan_c",
    name = "Ethan",
    surname = "Caldwell",
    phoneNumber = "+212 212 555 1234",
    email = "ethan.caldwell@example.com",
    profilePic = ProfilePic.Resource(R.drawable.avatar_ec),
    bio = "I'm a seasoned traveler who loves exploring new cultures and meeting people. I enjoy urban but also accessible authentic experiences. I'm particularly looking for unique adventures and budget-friendly travel stories.",
    travelPreferences = mutableListOf(
        "Hotels and accommodations",
        "Cultural experiences",
        "Reliable transportation"
    ),
    desiredDestinations = mutableListOf("New York City", "Cultural cities"),
    age = 24,
    nationality = "American",
    city = "New York City, USA",
    language = "English",
    numTravels = 8,
    personality = listOf("Extrovert", "Urban", "Spender", "Independent"),
    reviews = listOf(
        Review(
            LocalDate.parse("27/12/2025", formatter)!!,
            "I had the pleasure of traveling with Ethan, and I couldn’t have asked for a better travel companion. He’s kind, thoughtful, and always had a smile on his face. His curiosity and enthusiasm for exploring new places were truly contagious, making every moment of the trip more enjoyable. I’d absolutely love to travel together again someday!",
            4.5,
            user_ob
        ),
        Review(
            LocalDate.parse("21/09/2025", formatter)!!,
            "Traveling with Ethan was a really nice experience. He's easygoing, respectful, and fun to be around. Everything felt smooth and relaxed throughout the trip. Would definitely be happy to travel together again in the future!",
            4.0,
            user_dw
        )
    )
)


//--------------------------TRAVELS & REQUESTS


val t1_i4 = Itinerary(
    itineraryId = 4,
    name = "Day 4: Lisbon Nightlife",
    startDate = LocalDate.parse("23/03/2025", formatter)!!,
    places = listOf("Lisbon, Portugal"),
    description = "On the final day of our journey, we’ll explore the vibrant nightlife of Lisbon. From trendy bars in Bairro Alto to upscale nightclubs, Lisbon has something for everyone. This day is all about enjoying the energy of the city after the sun goes down, with a mix of great music, drinks, and dancing.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity(
            name = "Visit Bairro Alto for nightlife",
            optional = false,
            icon = "nightlife"
        ),
        Activity(
            name = "Try Lisbon’s famous cocktails",
            optional = false,
            icon = "drink"
        ),
        Activity(
            name = "Dance at a local club",
            optional = true,
            icon = "dance"
        ),
        Activity(
            name = "Bar-hopping",
            optional = true,
            icon = "bar"
        )
    )
)


val t1_i3 = Itinerary(
    itineraryId = 3,
    name = "Day 3: Sintra and Pena Palace",
    startDate = LocalDate.parse("22/03/2025", formatter)!!,
    places = listOf("Sintra, Portugal"),
    description = "Day 3 is all about the enchanting town of Sintra, located just outside of Lisbon. Famous for its fairy-tale palaces, we will visit the Pena Palace, a colorful and dramatic castle set atop the mountain. The surrounding gardens and views are absolutely breathtaking, making this day an unforgettable part of our journey.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity(
            name = "Visit Pena Palace",
            optional = false,
            icon = "palace"
        ),
        Activity(
            name = "Explore the gardens of Pena",
            optional = true,
            icon = "garden"
        ),
        Activity(
            name = "Hike through the forest",
            optional = true,
            icon = "hike"
        ),
        Activity(
            name = "Try traditional pastries",
            optional = false,
            icon = "pastry"
        )
    )
)


val t1_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Porto Exploration",
    startDate = LocalDate.parse("21/03/2025", formatter)!!,
    places = listOf("Porto, Portugal"),
    description = "On Day 2, we dive into the charming city of Porto. Known for its beautiful architecture, river views, and delicious wine, Porto offers a unique blend of culture and relaxation. We'll explore the iconic Ribeira District and experience the traditional port wine tasting at one of the city's historic wine cellars.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity(
            name = "Explore Ribeira District",
            optional = false,
            icon = "district"
        ),
        Activity(
            name = "Port Wine Tasting",
            optional = false,
            icon = "wine"
        ),
        Activity(
            name = "Visit Livraria Lello",
            optional = true,
            icon = "bookstore"
        ),
        Activity(
            name = "Take a boat tour on the Douro River",
            optional = true,
            icon = "boat"
        )
    )
)


val t1_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Welcome to Lisbon!",
    startDate = LocalDate.parse("20/03/2025", formatter)!!,
    places = listOf("Lisbon, Portugal"),
    description = "Hello everyone, I'm organizing this trip to Portugal, specifically to the cities of Porto and Lisbon. The purpose of this journey is to explore the rich history of this city, which has always intrigued me. However, amidst all this culture, I also want to include a bit of nightlife, so why not also take a nice tour of the best pubs in Porto? I hope as many people as possible will join!",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity(
            name = "Landscapes",
            optional = true,
            icon = "view"
        ),
        Activity(
            name = "Try the typical cuisine",
            optional = false,
            icon = "food"
        ),
        Activity(
            name = "People can rent a own veichle",
            optional = true,
            icon = "car"
        )
    )
)

val travel1 = Travel(
    travelId = "1",
    creator = user_ec,
    title = "The Best of Portugal: Lisbon, Porto & Beyond",
    description = "Hello everyone, I'm organizing this trip to Portugal, specifically to the cities of Porto and Lisbon. The purpose of this journey is to explore the rich history of this city, which has always intrigued me. However, amidst all this culture, I also want to include a bit of nightlife, so why not also take a nice tour of the best pubs in Porto? I hope as many people as possible will join!",
    country = "Portugal",
    travelTypes = mutableListOf("Cultural"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t1_image1),
        TravelImage.Resource(R.drawable.t1_image2),
        TravelImage.Resource(R.drawable.t1_image3),
        TravelImage.Resource(R.drawable.t1_image4),
        TravelImage.Resource(R.drawable.t1_image5),
        TravelImage.Resource(R.drawable.t1_image6),
        TravelImage.Resource(R.drawable.t1_image7),
        TravelImage.Resource(R.drawable.t1_image8)
    ),
    priceMin = 200,
    priceMax = 250,
    status = AVAILABLE,
    distance = "58 km",
    startDate = LocalDate.parse("20/03/2025", formatter)!!,
    endDate = LocalDate.parse("30/03/2025", formatter)!!,
    travelItinerary = mutableListOf(t1_i1, t1_i2, t1_i3, t1_i4),
    travelCompanions = mutableListOf(TravelCompanion(user_ec), TravelCompanion(user_eh, 1)),
    maxPeople = 5,
    travelReviews = listOf()
)

val t1_req1 = Request(
    lastUpdate = LocalDate.parse("16/02/2025", formatter)!!,
    id = "",
    author = user_ob,
    trip = travel1,
    isAccepted = false,
    isRefused = true,
    spots = 1,
    reqMessage = "Hi there! Istanbul in December sounds amazing—I'm really interested in joining this trip.",
    responseMessage = "I'm so sorry but I don't know you so well"
)

val t1_req2 = Request(
    lastUpdate = LocalDate.parse("25/01/2025", formatter)!!,
    id = "",
    author = user_eh,
    trip = travel1,
    isAccepted = true,
    isRefused = false,
    spots = 2,
    reqMessage = "Hi there! Istanbul in December sounds amazing—I'm really interested in joining this trip.",
    responseMessage = "Yea let's go"
)





val t2_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Interlaken",
    startDate = LocalDate.parse("15/07/2025", formatter)!!,
    places = listOf("Interlaken, Switzerland"),
    description = "We kick off our alpine journey in Interlaken, a beautiful town nestled between two lakes and surrounded by mountains.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Check-in and rest", false, "hotel"),
        Activity("Short walk along Lake Thun", true, "lake"),
        Activity("Group dinner in a traditional chalet", false, "food")
    )
)

val t2_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Lauterbrunnen Valley Hiking",
    startDate = LocalDate.parse("16/07/2025", formatter)!!,
    places = listOf("Lauterbrunnen, Switzerland"),
    description = "A full day hike in Lauterbrunnen Valley, one of the most picturesque places in Switzerland.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Hike to Staubbach Falls", false, "hiking"),
        Activity("Picnic lunch", true, "food"),
        Activity("Visit Trümmelbach Falls", false, "waterfall")
    )
)

val travel2 = Travel(
    travelId = "101",
    creator = user_ob,
    title = "Alpine Escape – Switzerland Hiking Adventure",
    description = "A scenic adventure through the Swiss Alps. Ideal for nature lovers and hikers.",
    country = "Interlaken, Switzerland",
    travelTypes = mutableListOf("Adventure", "Nature"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t2_image1),
        TravelImage.Resource(R.drawable.t2_image2),
    ),
    priceMin = 400,
    priceMax = 600,
    status = AVAILABLE,
    distance = "120 km",
    startDate = LocalDate.parse("15/07/2025", formatter)!!,
    endDate = LocalDate.parse("22/07/2025", formatter)!!,
    travelItinerary = mutableListOf(t2_i1, t2_i2),
    travelCompanions = mutableListOf(TravelCompanion(user_ob), TravelCompanion(user_ec), TravelCompanion(user_me)),
    maxPeople = 6,
    travelReviews = listOf(),
    travelChat = listOf(
        ChatMessage(system_light, LocalDateTime.parse("2025-07-06 15:30", firebaseChatFormatter)!!, getSystemMessageJoined(user_ec)),
        ChatMessage(user_ec, LocalDateTime.parse("2025-07-06 17:20", firebaseChatFormatter)!!, "I have great plans for this trip"),
        ChatMessage(system_light, LocalDateTime.parse("2025-07-16 12:03", firebaseChatFormatter)!!, getSystemMessageJoined(user_me)),
    )
)

val t2_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "1",
    author = user_dw,
    trip = travel2,
    isAccepted = false,
    isRefused = false,
    spots = 1,
    reqMessage = "Hi, I would like to join your trip because I'm very interested in going again to see that beautiful landscapes!"
)

val t2_req2 = Request(
    lastUpdate = LocalDate.parse("13/07/2025", formatter)!!,
    id = "2",
    author = user_sl,
    trip = travel2,
    isAccepted = false,
    isRefused = false,
    spots = 2,
    reqMessage = "Hi, I would like to join your trip because I've never been in the Alps. I hope you'll accept me"
)

val t2_req3 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "13",
    author = user_me,
    trip = travel2,
    isAccepted = false,
    isRefused = true,
    spots = 1,
    reqMessage = "Hey! This trip to the Swiss Alps is fire, I would like to have parties there! Can I be part of your trip team?",
    responseMessage = "Thanks for your interest, but I prefer to travel with people whose aim is to visit and enjoy all the landscapes of the nature"
)

val t2_req4 = Request(
    lastUpdate = LocalDate.parse("06/07/2025", formatter)!!,
    id = "14",
    author = user_ec,
    trip = travel2,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hi! I see the advert of your trip in the Alps and I think, as a nature lover, I can be a good companion for this trip",
    responseMessage = "Yes sure!! I like people like you for this kind of travels towards several nature landscapes"
)




val t3_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival and Beach Relaxation",
    startDate = LocalDate.parse("05/08/2025", formatter)!!,
    places = listOf("Kuta, Bali"),
    description = "Start your trip unwinding on the sunny beaches of Bali with drinks and group bonding activities.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Relax at Kuta Beach", false, "beach"),
        Activity("Welcome dinner at a beachside restaurant", false, "food"),
        Activity("Optional massage", true, "spa")
    )
)

val t3_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Surfing and Temple Visit",
    startDate = LocalDate.parse("06/08/2025", formatter)!!,
    places = listOf("Uluwatu, Bali"),
    description = "Get your surfboard ready for some morning waves and explore Balinese culture in the afternoon.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Surfing lesson", false, "surf"),
        Activity("Visit Uluwatu Temple", false, "temple"),
        Activity("Evening Kecak dance show", true, "culture")
    )
)

val travel3 = Travel(
    travelId = "3",
    creator = user_eh,
    title = "Bali Bliss – Surf, Culture & Relax",
    description = "Balance adventure and tranquility in this tropical getaway with surfing, temples and beachside fun.",
    country = "Bali, Indonesia",
    travelTypes = mutableListOf("Relax", "Adventure", "Culture"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t3_image1),
        TravelImage.Resource(R.drawable.t3_image2),
        TravelImage.Resource(R.drawable.t3_image3)
    ),
    priceMin = 300,
    priceMax = 500,
    status = AVAILABLE,
    distance = "80 km",
    startDate = LocalDate.parse("05/08/2025", formatter)!!,
    endDate = LocalDate.parse("12/08/2025", formatter)!!,
    travelItinerary = mutableListOf(t3_i1, t3_i2),
    travelCompanions = mutableListOf(TravelCompanion(user_eh), TravelCompanion(user_ec)),
    maxPeople = 8,
    travelReviews = listOf()
)

val t3_req1 = Request(
    lastUpdate = LocalDate.parse("16/07/2025", formatter)!!,
    id = "1",
    author = user_ec,
    trip = travel3,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hi, I would like to join your trip",
    responseMessage = "yea let's go"
)

val t3_req2 = Request(
    lastUpdate = LocalDate.parse("26/05/2025", formatter)!!,
    id = "1",
    author = user_dw,
    trip = travel3,
    isAccepted = false,
    isRefused = false,
    spots = 3,
    reqMessage = "Hi, I would like chill on the beach with you"
)





val t4_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Casablanca & Transfer to Rabat",
    startDate = LocalDate.parse("10/10/2025", formatter)!!,
    places = listOf("Casablanca, Morocco"),
    description = "Land in Casablanca, explore the iconic mosque and head to Rabat for an evening stroll in the medina.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit Hassan II Mosque", false, "mosque"),
        Activity("Transfer to Rabat", false, "bus"),
        Activity("Explore Rabat Medina", true, "market")
    )
)

val t4_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Chefchaouen – The Blue City",
    startDate = LocalDate.parse("11/10/2025", formatter)!!,
    places = listOf("Chefchaouen, Morocco"),
    description = "Explore one of Morocco’s most photogenic cities, known for its blue-washed streets.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Photo walk in Chefchaouen", false, "photo"),
        Activity("Traditional Berber lunch", false, "food"),
        Activity("Optional hike to the Spanish Mosque", true, "hiking")
    )
)

val travel4 = Travel(
    travelId = "4",
    creator = user_eh,
    title = "Moroccan Mosaic – A Cultural Journey",
    description = "Immerse yourself in Moroccan culture, architecture, and cuisine while exploring stunning cities.",
    country = "Casablanca, Morocco",
    travelTypes = mutableListOf("Culture", "Exploration"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t4_image1),
        TravelImage.Resource(R.drawable.t4_image2)
    ),
    priceMin = 350,
    priceMax = 480,
    status = AVAILABLE,
    distance = "300 km",
    startDate = LocalDate.parse("10/10/2025", formatter)!!,
    endDate = LocalDate.parse("18/10/2025", formatter)!!,
    travelItinerary = mutableListOf(t4_i1, t4_i2),
    travelCompanions = mutableListOf(TravelCompanion(user_eh)),
    maxPeople = 7,
    travelReviews = listOf()
)





val t5_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Tokyo",
    startDate = LocalDate.parse("01/03/2026", formatter)!!,
    places = listOf("Tokyo, Japan"),
    description = "Arrive in Tokyo, check into your hotel, and enjoy a welcome dinner.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Arrival and Hotel Check-in", true, "hotel"),
        Activity("Welcome Dinner", true, "food")
    )
)

val t5_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Tokyo City Tour",
    startDate = LocalDate.parse("02/03/2026", formatter)!!,
    places = listOf("Tokyo, Japan"),
    description = "Explore Tokyo's highlights, including Senso-ji Temple, the Meiji Shrine, and Shibuya crossing.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit Senso-ji Temple", false, "temple"),
        Activity("Explore Meiji Shrine", false, "temple"),
        Activity("Experience Shibuya Crossing", true, "walking")
    )
)

val travel5 = Travel(
    travelId = "5",
    creator = user_ec,
    title = "Tokyo Highlights",
    description = "Discover the vibrant city of Tokyo, its culture, and its iconic landmarks.",
    country = "Tokyo, Japan",
    travelTypes = mutableListOf("City", "Culture"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t5_image1),
        TravelImage.Resource(R.drawable.t5_image2)
    ),
    priceMin = 600,
    priceMax = 850,
    status = AVAILABLE,
    distance = "100 km",
    startDate = LocalDate.parse("01/03/2026", formatter)!!,
    endDate = LocalDate.parse("05/03/2026", formatter)!!,
    travelItinerary = mutableListOf(t5_i1, t5_i2),
    travelCompanions = mutableListOf(TravelCompanion(user_ec), TravelCompanion(user_eh, 1)),
    maxPeople = 5,
    travelReviews = listOf()
)

val t5_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "1",
    author = user_eh,
    trip = travel5,
    isAccepted = true,
    isRefused = false,
    spots = 2,
    reqMessage = "Let's go eat sushiii",
    responseMessage = "I like your spirit"
)

val t5_req2 = Request(
    lastUpdate = LocalDate.parse("20/06/2025", formatter)!!,
    id = "1",
    author = user_dw,
    trip = travel5,
    isAccepted = false,
    isRefused = false,
    spots = 1,
    reqMessage = "I can speak Japanese ",
)





val t6_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Historical Athens",
    startDate = LocalDate.parse("15/05/2025", formatter)!!,
    places = listOf("Athens, Greece"),
    description = "Explore the ancient wonders of Athens.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit the Acropolis", true, "history"),
        Activity("Explore the Ancient Agora", false, "history"),
    )
)

val t6_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Island Getaway",
    startDate = LocalDate.parse("16/05/2025", formatter)!!,
    places = listOf("Mykonos, Greece"),
    description = "Travel to Mykonos and enjoy the beautiful beaches.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Ferry to Mykonos", false, "boat"),
        Activity("Relax on the beach", true, "beach"),
    )
)

val travel6 = Travel(
    travelId = "6",
    creator = user_dw,
    title = "Greek History and Beaches",
    description = "A journey through history in Athens and relaxation on the islands.",
    country = "Athens, Greece",
    travelTypes = mutableListOf("Culture", "Beach"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t6_image1),
        TravelImage.Resource(R.drawable.t6_image2)
    ),
    priceMin = 500,
    priceMax = 1000,
    status = AVAILABLE,
    distance = "200 km",
    startDate = LocalDate.parse("15/05/2025", formatter)!!,
    endDate = LocalDate.parse("22/05/2025", formatter)!!,
    travelItinerary = mutableListOf(t6_i1, t6_i2),
    travelCompanions = mutableListOf(TravelCompanion(user_dw), TravelCompanion(user_sl)),
    maxPeople = 5,
    travelReviews = listOf()
)

val t6_req1 = Request(
    lastUpdate = LocalDate.parse("16/04/2025", formatter)!!,
    id = "1",
    author = user_ob,
    trip = travel6,
    isAccepted = false,
    isRefused = true,
    spots = 1,
    reqMessage = "I love grece, please let's go together, I want to fish and eat what I catch",
    responseMessage = "Sorry I am vegan i do not appreciate that",
)

val t6_req2 = Request(
    lastUpdate = LocalDate.parse("16/04/2025", formatter)!!,
    id = "1",
    author = user_sl,
    trip = travel6,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "I love grece, please let's go together",
    responseMessage = "We will have fun",
)




val t7_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Rome",
    startDate = LocalDate.parse("01/09/2025", formatter)!!,
    places = listOf("Rome, Italy"),
    description = "Arrive in Rome and start your Italian adventure.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Check into hotel", true, "hotel"),
        Activity("Explore the city center", false, "walking"),
    )
)

val t7_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Ancient Rome",
    startDate = LocalDate.parse("02/09/2025", formatter)!!,
    places = listOf("Rome, Italy"),
    description = "Visit the Colosseum and Roman Forum.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit the Colosseum", false, "history"),
        Activity("Explore the Roman Forum", true, "history"),
    )
)

val travel7 = Travel(
    travelId = "7",
    creator = user_me,
    title = "Taste of Italy",
    description = "Experience the best of Italy, from Rome to Venice.",
    country = "Rome, Italy",
    travelTypes = mutableListOf("Culture", "Food"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t7_image1),
        TravelImage.Resource(R.drawable.t7_image2)
    ),
    priceMin = 700,
    priceMax = 1200,
    status = AVAILABLE,
    distance = "400 km",
    startDate = LocalDate.parse("01/09/2025", formatter)!!,
    endDate = LocalDate.parse("08/09/2025", formatter)!!,
    travelItinerary = mutableListOf(t7_i1, t7_i2),
    travelCompanions = mutableListOf(TravelCompanion(user_me), TravelCompanion(user_dw, 3)),
    maxPeople = 8,
    travelReviews = listOf()
)


val t7_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "",
    author = user_dw,
    trip = travel7,
    isAccepted = true,
    isRefused = false,
    spots = 4,
    reqMessage = "Pizza partyyyyy",
    responseMessage = "Yea we will eat a lot",
)

val t7_req2 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "",
    author = user_sl,
    trip = travel7,
    isAccepted = false,
    isRefused = false,
    spots = 1,
    reqMessage = "I love Italy, I want pizza",
)




val t8_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Paris",
    startDate = LocalDate.parse("20/07/2025", formatter)!!,
    places = listOf("Paris, France"),
    description = "Arrive in Paris, check into your hotel, and begin your Parisian adventure.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Arrival and Hotel Check-in", true, "hotel"),
        Activity("Evening stroll along the Seine", false, "walking")
    )
)

val t8_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Iconic Paris",
    startDate = LocalDate.parse("21/07/2025", formatter)!!,
    places = listOf("Paris, France"),
    description = "Explore Paris's most famous landmarks.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit the Eiffel Tower", false, "monument"),
        Activity("Explore the Louvre Museum", false, "museum"),
        Activity("Walk the Champs-Élysées", true, "walking")
    )
)

val travel8 = Travel(
    travelId = "8",
    creator = user_eh,
    title = "Charming Paris",
    description = "Discover the romance and beauty of Paris.",
    country = "Paris, France",
    travelTypes = mutableListOf("City", "Culture", "Romance"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t8_image1),
        TravelImage.Resource(R.drawable.t8_image2)
    ),
    priceMin = 800,
    priceMax = 1500,
    status = AVAILABLE,
    distance = "200 km",
    startDate = LocalDate.parse("20/07/2025", formatter)!!,
    endDate = LocalDate.parse("27/07/2025", formatter)!!,
    travelItinerary = mutableListOf(t8_i1, t8_i2),
    travelCompanions = mutableListOf(
        TravelCompanion(user_eh),
        TravelCompanion(user_me, 2),
        TravelCompanion(user_sl),
        TravelCompanion(user_dw)
    ),
    maxPeople = 18,
    travelReviews = listOf()
)

val t8_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "",
    author = user_me,
    trip = travel8,
    isAccepted = true,
    isRefused = false,
    spots = 3,
    reqMessage = "I want to see Paris with you",
    responseMessage = "Let's go then"
)

val t8_req2 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "",
    author = user_sl,
    trip = travel8,
    isAccepted = true,
    isRefused = false,
    spots = 3,
    reqMessage = "I want to see the Louvre with you",
    responseMessage = "Let's go then"
)

val t8_req3 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "",
    author = user_dw,
    trip = travel8,
    isAccepted = true,
    isRefused = false,
    spots = 3,
    reqMessage = "I want to see Paris with you",
    responseMessage = "Let's go then"
)




val t9_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Kyoto",
    startDate = LocalDate.parse("10/11/2024", formatter)!!,
    places = listOf("Kyoto, Japan"),
    description = "Arrive in Kyoto and settle into your accommodation.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Check into ryokan", true, "accommodation"),
        Activity("Evening walk in Gion District", false, "walking")
    )
)

val t9_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Temples and Tradition",
    startDate = LocalDate.parse("11/11/2024", formatter)!!,
    places = listOf("Kyoto, Japan"),
    description = "Explore the famous temples and cultural sites of Kyoto.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit Kinkaku-ji (Golden Pavilion)", false, "culture"),
        Activity("Traditional tea ceremony experience", true, "culture")
    )
)

val t9_i3 = Itinerary(
    itineraryId = 3,
    name = "Day 3: Travel to Osaka",
    startDate = LocalDate.parse("12/11/2024", formatter)!!,
    places = listOf("Osaka, Japan"),
    description = "Take the train to Osaka and enjoy local street food.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Ride the Shinkansen to Osaka", false, "travel"),
        Activity("Street food tour in Dotonbori", true, "food")
    )
)

val t9_r1 = TravelReview(
    travel_review_id = "1",
    travel_id = "9",
    travelReviewText = "I truly enjoyed this experience, it was a wonderful time spent together. Everything was well organized, and the atmosphere made it even more memorable. I'd definitely recommend it to others.",
    rating = 4.5,
    author = user_ob,
    date = LocalDate.now().minusYears(2).plusMonths(3)
)
val t9_r2 = TravelReview(
    travel_review_id = "2",
    travel_id = "9",
    travelReviewText = "A great trip overall! I loved the sights, and the schedule gave us enough time to relax and explore. There were a few minor hiccups, but nothing that took away from the experience.",
    rating = 4.0,
    author = user_dw,
    date = LocalDate.now().minusYears(1).minusMonths(3)
)

val travel9 = Travel(
    travelId = "9",
    creator = user_me,
    title = "Autumn in Japan",
    description = "Discover the beauty of Japan during fall with Kyoto’s temples and Osaka’s vibrant streets.",
    country = "Kyoto & Osaka, Japan",
    travelTypes = mutableListOf("Culture", "Food"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t9_image1),
        TravelImage.Resource(R.drawable.t9_image2)
    ),
    priceMin = 900,
    priceMax = 1500,
    status = PAST,
    distance = "430 km",
    startDate = LocalDate.parse("10/11/2024", formatter)!!,
    endDate = LocalDate.parse("17/11/2024", formatter)!!,
    travelItinerary = mutableListOf(t9_i1, t9_i2, t9_i3),
    travelCompanions = mutableListOf(
        TravelCompanion(user_me),
        TravelCompanion(user_dw, 3),
        TravelCompanion(user_ob)
    ),
    maxPeople = 8,
    travelRating = 4.25,
    travelReviews = listOf(t9_r1, t9_r2),
    travelChat = listOf(
        ChatMessage(user_ob, LocalDateTime.parse("2025-10-10 12:03", firebaseChatFormatter)!!, getSystemMessageJoined(user_ob)),
        ChatMessage(system_light, LocalDateTime.parse("2025-10-10 15:30", firebaseChatFormatter)!!, getSystemMessageJoined(user_dw)),
        ChatMessage(user_dw, LocalDateTime.parse("2025-10-10 17:20", firebaseChatFormatter)!!, "I have great plans for this trip"),
    )
)

val t9_req1 = Request(
    lastUpdate = LocalDate.parse("10/10/2024", formatter)!!,
    id = "3",
    author = user_ob,
    trip = travel9,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hello! I'd love to join your trip to Japan—I've always dreamed of visiting Kyoto in the fall!",
    responseMessage = "Sure"
)

val t9_req2 = Request(
    lastUpdate = LocalDate.parse("10/10/2024", formatter)!!,
    id = "3",
    author = user_dw,
    trip = travel9,
    isAccepted = true,
    isRefused = false,
    spots = 4,
    reqMessage = "Hello! I'd love to join your trip to Japan, I have some friends hope you don't mind",
    responseMessage = "No problem, let's go"
)






val t10_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Istanbul",
    startDate = LocalDate.parse("05/12/2024", formatter)!!,
    places = listOf("Istanbul, Turkey"),
    description = "Arrive in Istanbul and begin exploring the historic city.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Check into hotel in Sultanahmet", true, "accommodation"),
        Activity("Walk along the Bosphorus at sunset", false, "scenic")
    )
)

val t10_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Historical Sites Tour",
    startDate = LocalDate.parse("06/12/2024", formatter)!!,
    places = listOf("Istanbul, Turkey"),
    description = "Dive into the rich history of Istanbul by visiting its iconic monuments.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit Hagia Sophia", false, "history"),
        Activity("Tour the Blue Mosque and Grand Bazaar", true, "culture")
    )
)

val travel10 = Travel(
    travelId = "10",
    creator = user_sl,
    title = "Winter Charm in Istanbul",
    description = "Enjoy the mystique of Istanbul in winter, with its historic sites and vibrant bazaars.",
    country = "Istanbul, Turkey",
    travelTypes = mutableListOf("Culture", "History"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t10_image1),
        TravelImage.Resource(R.drawable.t10_image2),
        TravelImage.Resource(R.drawable.t10_image3)
    ),
    priceMin = 600,
    priceMax = 1000,
    status = PAST,
    distance = "320 km",
    startDate = LocalDate.parse("05/12/2024", formatter)!!,
    endDate = LocalDate.parse("07/12/2024", formatter)!!,
    travelItinerary = mutableListOf(t10_i1, t10_i2),
    travelCompanions = mutableListOf(
        TravelCompanion(user_sl),
        TravelCompanion(user_ob),
        TravelCompanion(user_ec)
    ),
    maxPeople = 4,
    travelReviews = listOf()
)

val t10_req1 = Request(
    lastUpdate = LocalDate.parse("10/10/2024", formatter)!!,
    id = "4",
    author = user_ob,
    trip = travel10,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hi there! Istanbul in December sounds amazing—I'm really interested in joining this trip.",
    responseMessage = "It is, let's go"
)

val t10_req2 = Request(
    lastUpdate = LocalDate.parse("10/10/2024", formatter)!!,
    id = "4",
    author = user_ec,
    trip = travel10,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hi there! Istanbul in December sounds amazing—I'm really interested in joining this trip.",
    responseMessage = "It is, let's go"
)




val t11_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Discovering Barcelona",
    startDate = LocalDate.parse("12/08/2025", formatter)!!,
    places = listOf("Barcelona, Spain"),
    description = "Arrival in Barcelona and start exploring its iconic landmarks.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit Sagrada Familia", false, "architecture"),
        Activity("Walk along La Rambla", true, "walking")
    )
)

val t11_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Valencia Vibes",
    startDate = LocalDate.parse("13/08/2025", formatter)!!,
    places = listOf("Valencia, Spain"),
    description = "A day trip to Valencia with food, beach, and culture.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Try authentic paella", true, "food"),
        Activity("Relax at Malvarrosa beach", false, "relaxation")
    )
)

val travel11 = Travel(
    travelId = "11",
    creator = user_me,
    title = "Sun & Culture: Spain Getaway",
    description = "A summer escape through the heart of Spain, mixing iconic landmarks with Mediterranean vibes.",
    country = "Spain",
    travelTypes = mutableListOf("Culture", "Food", "Beach"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t11_image1),
        TravelImage.Resource(R.drawable.t11_image2),
        TravelImage.Resource(R.drawable.t11_image3)
    ),
    priceMin = 750,
    priceMax = 1300,
    status = AVAILABLE,
    distance = "350 km",
    startDate = LocalDate.parse("12/08/2025", formatter)!!,
    endDate = LocalDate.parse("15/08/2025", formatter)!!,
    travelItinerary = mutableListOf(t11_i1, t11_i2),
    travelCompanions = mutableListOf(
        TravelCompanion(user_me),
        TravelCompanion(user_ec)
    ),
    maxPeople = 6,
    travelReviews = listOf()
)

val t11_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "6",
    author = user_ec,
    trip = travel11,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hi there! Spend some days in Spain it's one of my dream, it would be great if you accept my request",
    responseMessage = "Sure! I want people like you to spend some days in Spain!"
)

val t11_req2 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "6",
    author = user_ob,
    trip = travel11,
    isAccepted = false,
    isRefused = false,
    spots = 2,
    reqMessage = "Hi there! Spend some days in Spain it's one of my dream",
)



val t12_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Rio",
    startDate = LocalDate.parse("10/11/2025", formatter)!!,
    places = listOf("Rio de Janeiro, Brazil"),
    description = "Arrive in Rio and explore the vibrant city life.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit Christ the Redeemer", false, "sightseeing"),
        Activity("Relax at Copacabana beach", true, "relaxation")
    )
)

val t12_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Colonial Escape to Paraty",
    startDate = LocalDate.parse("11/11/2025", formatter)!!,
    places = listOf("Paraty, Brazil"),
    description = "Take a scenic trip to the charming town of Paraty.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Explore the historic center", true, "culture"),
        Activity("Boat tour to tropical islands", false, "adventure")
    )
)

val travel12 = Travel(
    travelId = "12",
    creator = user_sl,
    title = "Brazilian Adventure: From Rio to Paraty",
    description = "Experience Brazil’s mix of city energy and tropical serenity in this South American escape.",
    country = "Brazil",
    travelTypes = mutableListOf("Adventure", "Culture", "Beach"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t12_image1),
        TravelImage.Resource(R.drawable.t12_image2)
    ),
    priceMin = 1000,
    priceMax = 1600,
    status = AVAILABLE,
    distance = "500 km",
    startDate = LocalDate.parse("10/11/2025", formatter)!!,
    endDate = LocalDate.parse("15/11/2025", formatter)!!,
    travelItinerary = mutableListOf(t12_i1, t12_i2),
    travelCompanions = mutableListOf(
        TravelCompanion(user_sl),
        TravelCompanion(user_ob),
        TravelCompanion(user_me)
    ),
    maxPeople = 8,
    travelReviews = listOf(),
    travelChat = listOf(
        ChatMessage(system_light, LocalDateTime.parse("2025-06-16 12:03", firebaseChatFormatter)!!, getSystemMessageJoined(user_ob)),
        ChatMessage(system_light, LocalDateTime.parse("2025-06-16 15:30", firebaseChatFormatter)!!, getSystemMessageJoined(user_me)),
        ChatMessage(user_me, LocalDateTime.parse("2025-06-17 17:20", firebaseChatFormatter)!!, "I have great plans for this trip"),
        ChatMessage(user_ob, LocalDateTime.parse("2025-06-17 17:40", firebaseChatFormatter)!!, "Me too, can't wait"),
    )
)

val t12_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "7",
    author = user_ob,
    trip = travel12,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hey! Exploring Brazil with this group sounds like an unforgettable experience—count me in!",
    responseMessage = "Absolutely! Brazil is better with good company, welcome aboard!"
)

val t12_req2 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "7",
    author = user_me,
    trip = travel12,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hey! Exploring Brazil with this group sounds like an unforgettable experience—count me in!",
    responseMessage = "Absolutely! Brazil is better with good company, welcome aboard!"
)





val t13_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Delhi",
    startDate = LocalDate.parse("09/12/2025", formatter)!!,
    places = listOf("Delhi, India"),
    description = "Arrive in Delhi, settle into the hotel, and explore the vibrant streets.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Arrival and Hotel Check-in", true, "hotel"),
        Activity("Evening visit to India Gate", false, "monument")
    )
)

val t13_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Old Delhi & Cultural Sights",
    startDate = LocalDate.parse("10/12/2025", formatter)!!,
    places = listOf("Delhi, India"),
    description = "Dive into Delhi’s history and culture with guided tours.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Explore Red Fort", false, "monument"),
        Activity("Visit Jama Masjid", false, "religious"),
        Activity("Rickshaw ride in Chandni Chowk", true, "adventure")
    )
)

val travel13 = Travel(
    travelId = "13",
    creator = user_eh,
    title = "Incredible India: Delhi Discovery",
    description = "Experience the rich heritage and dynamic atmosphere of India's capital.",
    country = "Delhi, India",
    travelTypes = mutableListOf("Culture", "City", "Adventure"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t13_image1),
        TravelImage.Resource(R.drawable.t13_image2)
    ),
    priceMin = 600,
    priceMax = 1200,
    status = AVAILABLE,
    distance = "1,500 km",
    startDate = LocalDate.parse("09/12/2025", formatter)!!,
    endDate = LocalDate.parse("10/12/2025", formatter)!!,
    travelItinerary = mutableListOf(t13_i1, t13_i2),
    travelCompanions = mutableListOf(
        TravelCompanion(user_eh),
        TravelCompanion(user_dw),
        TravelCompanion(user_sl)
    ),
    maxPeople = 9,
    travelReviews = listOf()
)

val t13_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "8",
    author = user_dw,
    trip = travel13,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "India has always fascinated me—I'd love to join this adventure through Delhi!",
    responseMessage = "We're thrilled to have you on board!"
)

val t13_req2 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "8",
    author = user_sl,
    trip = travel13,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "India has always fascinated me",
    responseMessage = "Delhi awaits with all its colors and history!"
)

val t13_req3 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "8",
    author = user_ec,
    trip = travel13,
    isAccepted = false,
    isRefused = false,
    spots = 1,
    reqMessage = "India has always fascinated me—I'd love to join this adventure through Delhi!",
)




val t14_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in New York City",
    startDate = LocalDate.parse("16/09/2025", formatter)!!,
    places = listOf("New York, NY"),
    description = "Arrive in NYC, check in, and enjoy a first taste of the city that never sleeps.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Hotel Check-in", true, "hotel"),
        Activity("Evening walk in Times Square", false, "walking")
    )
)

val t14_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Exploring Manhattan",
    startDate = LocalDate.parse("17/09/2025", formatter)!!,
    places = listOf("New York, NY"),
    description = "Discover iconic Manhattan landmarks and cultural highlights.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit the Empire State Building", false, "monument"),
        Activity("Stroll through Central Park", true, "walking"),
        Activity("Explore the Museum of Modern Art", false, "museum")
    )
)

val t14_i3 = Itinerary(
    itineraryId = 3,
    name = "Day 3: Day Trip to Washington, D.C.",
    startDate = LocalDate.parse("18/09/2025", formatter)!!,
    places = listOf("Washington, D.C."),
    description = "Take a fast train to D.C. to visit monuments and museums.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit the Lincoln Memorial", false, "monument"),
        Activity("Tour the Smithsonian Museums", false, "museum"),
        Activity("Explore the National Mall", true, "walking")
    )
)

val t14_i4 = Itinerary(
    itineraryId = 4,
    name = "Day 4: Brooklyn Vibes",
    startDate = LocalDate.parse("19/09/2025", formatter)!!,
    places = listOf("Brooklyn, NY"),
    description = "Discover the art, food, and culture scene in Brooklyn.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Walk the Brooklyn Bridge", true, "walking"),
        Activity("Visit local galleries in Williamsburg", false, "art"),
        Activity("Taste food at Smorgasburg Market", false, "food")
    )
)

val t14_i5 = Itinerary(
    itineraryId = 5,
    name = "Day 5: Farewell NYC",
    startDate = LocalDate.parse("20/09/2025", formatter)!!,
    places = listOf("New York, NY"),
    description = "Wrap up the trip with last-minute shopping and views before heading home.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Morning shopping in SoHo", false, "shopping"),
        Activity("Observation deck at One World Trade Center", false, "viewpoint"),
        Activity("Departure", true, "transport")
    )
)

val travel14 = Travel(
    travelId = "14",
    creator = user_me,
    title = "Urban Escape: NYC & Beyond",
    description = "Experience the energy of New York City and a touch of D.C. in this 5-day urban getaway.",
    country = "United States",
    travelTypes = mutableListOf("City", "Culture", "Food"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t14_image1),
        TravelImage.Resource(R.drawable.t14_image2),
        TravelImage.Resource(R.drawable.t14_image3),
        TravelImage.Resource(R.drawable.t14_image4),
        TravelImage.Resource(R.drawable.t14_image5)
    ),
    priceMin = 1000,
    priceMax = 1800,
    status = AVAILABLE,
    distance = "3,000 km",
    startDate = LocalDate.parse("16/09/2025", formatter)!!,
    endDate = LocalDate.parse("20/09/2025", formatter)!!,
    travelItinerary = mutableListOf(t14_i1, t14_i2, t14_i3, t14_i4, t14_i5),
    travelCompanions = mutableListOf(
        TravelCompanion(user_me),
        TravelCompanion(user_dw, 2)
    ),
    maxPeople = 6,
    travelReviews = listOf()
)

val t14_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "9",
    author = user_dw,
    trip = travel14,
    isAccepted = true,
    isRefused = false,
    spots = 3,
    reqMessage = "Hey! This NYC adventure looks incredible—I'd love to tag along if there's room!",
    responseMessage = "Thanks for your interest, but I prefer to travel with people I know well."
)

val t14_req2 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "9",
    author = user_sl,
    trip = travel14,
    isAccepted = false,
    isRefused = true,
    spots = 6,
    reqMessage = "Hey! This NYC adventure looks incredible, I have lots of friends interested",
    responseMessage = "You guys are too many"
)




val t15_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival in Saint Petersburg",
    startDate = LocalDate.parse("03/10/2025", formatter)!!,
    places = listOf("Saint Petersburg, Russia"),
    description = "Arrive and enjoy your first evening in Russia’s imperial capital.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Check-in and rest", true, "hotel"),
        Activity("Evening walk along Nevsky Prospekt", false, "walking")
    )
)

val t15_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Palaces and Canals",
    startDate = LocalDate.parse("04/10/2025", formatter)!!,
    places = listOf("Saint Petersburg, Russia"),
    description = "Dive into the opulent past with a day among palaces and river canals.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit the Winter Palace and Hermitage Museum", false, "museum"),
        Activity("Canal cruise through the historic center", false, "relaxation"),
        Activity("Dinner in a traditional Russian tavern", true, "food")
    )
)

val t15_i3 = Itinerary(
    itineraryId = 3,
    name = "Day 3: Tsarskoye Selo and Farewell",
    startDate = LocalDate.parse("05/10/2025", formatter)!!,
    places = listOf("Pushkin, Russia"),
    description = "Trip to Catherine Palace in Pushkin before saying goodbye to Russia.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Tour of Catherine Palace and Amber Room", false, "monument"),
        Activity("Walk in the autumn gardens", true, "nature")
    )
)

val travel15 = Travel(
    travelId = "15",
    creator = user_ec,
    title = "Autumn Lights in Saint Petersburg",
    description = "Experience the elegance and history of Saint Petersburg dressed in fall colors.",
    country = "Russia",
    travelTypes = mutableListOf("Culture", "History", "City"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t15_image1),
        TravelImage.Resource(R.drawable.t15_image2),
        TravelImage.Resource(R.drawable.t15_image3)
    ),
    priceMin = 1200,
    priceMax = 1900,
    status = AVAILABLE,
    distance = "2,400 km",
    startDate = LocalDate.parse("03/10/2025", formatter)!!,
    endDate = LocalDate.parse("06/10/2025", formatter)!!,
    travelItinerary = mutableListOf(t15_i1, t15_i2, t15_i3),
    travelCompanions = mutableListOf(
        TravelCompanion(user_ec),
        TravelCompanion(user_dw),
        TravelCompanion(user_sl, 1)
    ),
    maxPeople = 5,
    travelReviews = listOf()
)

val t15_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "9",
    author = user_dw,
    trip = travel15,
    isAccepted = true,
    isRefused = false,
    spots = 1,
    reqMessage = "Hey! This  Saint Petersburg dressed in fall colors looks incredible—I'd love to tag along if there's room!",
    responseMessage = "Thanks for your interest, but I prefer to travel with people I know well."
)

val t15_req2 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "9",
    author = user_sl,
    trip = travel15,
    isAccepted = true,
    isRefused = false,
    spots = 2,
    reqMessage = "Hey! This looks incredible, I have one friends interested",
    responseMessage = "Thanks for your interest, i love to have you and your friend"
)

val t15_req3 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "9",
    author = user_ob,
    trip = travel15,
    isAccepted = false,
    isRefused = false,
    spots = 1,
    reqMessage = "Hey! This adventure looks incredible—I'd love to tag along if there's room!",
)


val t16_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Reykjavik Arrival",
    startDate = LocalDate.parse("12/10/2025", formatter)!!,
    places = listOf("Reykjavik, Iceland"),
    description = "Arrive and explore the colorful capital of Iceland.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Walk along Laugavegur Street", true, "walking"),
        Activity("Check-in and dinner at a local bistro", false, "food")
    )
)

val t16_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Golden Circle Adventure",
    startDate = LocalDate.parse("13/10/2025", formatter)!!,
    places = listOf("Thingvellir, Geysir, Gullfoss"),
    description = "Visit Iceland’s most iconic natural wonders in one day.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Thingvellir National Park", false, "nature"),
        Activity("Geysir geothermal area", false, "nature"),
        Activity("Gullfoss waterfall", false, "viewpoint")
    )
)

val t16_i3 = Itinerary(
    itineraryId = 3,
    name = "Day 3: Blue Lagoon & Departure",
    startDate = LocalDate.parse("14/10/2025", formatter)!!,
    places = listOf("Grindavík, Iceland"),
    description = "Relax at the Blue Lagoon before flying back home.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Blue Lagoon spa morning", true, "relaxation"),
        Activity("Departure from Reykjavik", true, "transport")
    )
)

val travel16 = Travel(
    travelId = "16",
    creator = user_eh,
    title = "Icelandic Escape: Nature and Relaxation",
    description = "Breathe in Iceland’s raw beauty from geysers to glaciers, and unwind in the Blue Lagoon.",
    country = "Iceland",
    travelTypes = mutableListOf("Nature", "Relaxation", "Adventure"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t16_image1),
        TravelImage.Resource(R.drawable.t16_image2),
        TravelImage.Resource(R.drawable.t16_image3)
    ),
    priceMin = 900,
    priceMax = 1600,
    status = AVAILABLE,
    distance = "1,200 km",
    startDate = LocalDate.parse("18/10/2025", formatter)!!,
    endDate = LocalDate.parse("21/10/2025", formatter)!!,
    travelItinerary = mutableListOf(t16_i1, t16_i2, t16_i3),
    travelCompanions = mutableListOf(
        TravelCompanion(user_eh)
    ),
    maxPeople = 4,
    travelReviews = listOf()
)





val t17_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival and Classic Venice",
    startDate = LocalDate.parse("01/07/2025", formatter)!!,
    places = listOf("Venice, Italy"),
    description = "Arrive in Venice and explore its timeless charm through canals and landmarks.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Hotel check-in and orientation", true, "hotel"),
        Activity("Gondola ride along the Grand Canal", false, "relaxation"),
        Activity("Visit St. Mark’s Basilica and Square", false, "monument")
    )
)

val t17_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: Art and Hidden Corners",
    startDate = LocalDate.parse("02/07/2025", formatter)!!,
    places = listOf("Venice, Italy"),
    description = "Discover Venice’s art, history, and charming alleyways before departure.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Visit the Peggy Guggenheim Collection", false, "museum"),
        Activity("Lunch near the Rialto Market", true, "food"),
        Activity("Stroll through Dorsoduro district", true, "walking")
    )
)

val travel17 = Travel(
    travelId = "17",
    creator = user_ob,
    title = "Summer Escape to Venice",
    description = "A quick but magical getaway through the canals, art, and history of Venice.",
    country = "Italy",
    travelTypes = mutableListOf("Culture", "City", "Romance"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t17_image1),
        TravelImage.Resource(R.drawable.t17_image2)
    ),
    priceMin = 400,
    priceMax = 750,
    status = AVAILABLE,
    distance = "300 km",
    startDate = LocalDate.parse("01/07/2025", formatter)!!,
    endDate = LocalDate.parse("02/07/2025", formatter)!!,
    travelItinerary = mutableListOf(t17_i1, t17_i2),
    travelCompanions = mutableListOf(
        TravelCompanion(user_ob)
    ),
    maxPeople = 4,
    travelReviews = listOf()
)

val t17_req1 = Request(
    lastUpdate = LocalDate.parse("16/06/2025", formatter)!!,
    id = "10",
    author = user_sl,
    trip = travel17,
    isAccepted = false,
    isRefused = false,
    spots = 2,
    reqMessage = "Hey! I've always wanted to see Venice in summer—hope you'll let me join this quick getaway!"
)





val t18_i1 = Itinerary(
    itineraryId = 1,
    name = "Day 1: Arrival & Museum Island",
    startDate = LocalDate.parse("04/01/2026", formatter)!!,
    places = listOf("Berlin, Germany"),
    description = "Arrive in Berlin and dive into its rich cultural offerings.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Check into hotel", true, "accommodation"),
        Activity("Visit Pergamon Museum", false, "culture")
    )
)

val t18_i2 = Itinerary(
    itineraryId = 2,
    name = "Day 2: History & City Life",
    startDate = LocalDate.parse("05/01/2026", formatter)!!,
    places = listOf("Berlin, Germany"),
    description = "Explore Berlin's landmarks and enjoy local food.",
    itineraryImages = mutableListOf(),
    activities = mutableListOf(
        Activity("Walk along the Berlin Wall Memorial", true, "history"),
        Activity("Dinner in Kreuzberg", false, "food")
    )
)

val travel18 = Travel(
    travelId = "18",
    creator = user_me,
    title = "Berlin City Break",
    description = "A quick escape to Germany's capital full of history, culture, and food.",
    country = "Berlin, Germany",
    travelTypes = mutableListOf("Culture", "History", "Urban"),
    travelImages = listOf(
        TravelImage.Resource(R.drawable.t18_image1),
        TravelImage.Resource(R.drawable.t18_image2),
        TravelImage.Resource(R.drawable.t18_image3)
    ),
    priceMin = 500,
    priceMax = 900,
    status = FULL,
    distance = "200 km",
    startDate = LocalDate.parse("04/01/2026", formatter)!!,
    endDate = LocalDate.parse("06/01/2026", formatter)!!,
    travelItinerary = mutableListOf(t18_i1, t18_i2),
    travelCompanions = mutableListOf(
        TravelCompanion(user_me)
    ),
    maxPeople = 3,
    travelReviews = listOf()
)

