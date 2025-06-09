import json
from itertools import combinations


# run: ./automate_index_creation.py

# firebase firestore:indexes > firestore_indexes_current.json


# run: ./check_difference_indexes.py 
# check difference with the current indexes

# firebase deploy --only firestore:indexes

indexes = []



travel_fields = [
    {"fieldPath": "creatorId", "order": "ASCENDING"},
    {"fieldPath": "travelId", "order": "ASCENDING"},
    {"fieldPath": "status", "order": "ASCENDING"},
    {"fieldPath": "startDate", "order": "ASCENDING"},
    {"fieldPath": "endDate", "order": "ASCENDING"},
    {"fieldPath": "minPrice", "order": "ASCENDING"},
    {"fieldPath": "maxPrice", "order": "ASCENDING"},
]
for r in range(1, len(travel_fields) + 1):
    for combo in combinations(travel_fields, r):
        if not any(f.get("fieldPath") == "startDate" for f in combo):
            combo = list(combo) + [{"fieldPath": "startDate", "order": "ASCENDING"}]
        
        comboList = list(combo)
        if len(comboList)>1:
            indexes.append({
                "collectionGroup": "Travels",
                "queryScope": "COLLECTION",
                "fields": comboList
            })


indexes.append({
    "collectionGroup": "Travels",
    "queryScope": "COLLECTION",
    "fields": [
        {
          "fieldPath": "creatorId",
          "order": "ASCENDING"
        },
        {
          "fieldPath": "startDate", 
          "order": "ASCENDING"
        }
      ]
})

indexes.append({
    "collectionGroup": "Travels",
    "queryScope": "COLLECTION",
    "fields": [
        
        {
            "fieldPath": "status", 
            "order": "ASCENDING"
            },
        {
          "fieldPath": "creatorId",
          "order": "ASCENDING"
        },
        {
          "fieldPath": "startDate", 
          "order": "ASCENDING"
        }
      ]
})



request_fields = [
    {"fieldPath": "authorId", "order": "ASCENDING"},
    {"fieldPath": "refused", "order": "ASCENDING"},
    {"fieldPath": "accepted", "order": "ASCENDING"},
    {"fieldPath": "tripId", "order": "ASCENDING"},
]


for r in range(1, len(request_fields) + 1):
    for combo in combinations(request_fields, r):
        comboList = list(combo)
        if len(comboList)>1:
            indexes.append({
                "collectionGroup": "Requests",
                "queryScope": "COLLECTION",
                "fields": comboList
            })




firestore_indexes = {
    "indexes": indexes,
    "fieldOverrides": []
}
print(f"Generated {len(indexes)} indexes.")

with open("firestore_indexes_generated.json", "w") as f:
    json.dump(firestore_indexes, f, indent=2)
