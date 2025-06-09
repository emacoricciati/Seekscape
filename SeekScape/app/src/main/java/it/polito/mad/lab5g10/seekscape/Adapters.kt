package it.polito.mad.lab5g10.seekscape

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TravelImageAdapter : JsonDeserializer<TravelImage>, JsonSerializer<TravelImage> {

    override fun serialize(src: TravelImage, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        when (src) {
            is TravelImage.Url -> {
                jsonObject.addProperty("type", "url")
                jsonObject.addProperty("value", src.value)
            }
            is TravelImage.Resource -> {
                jsonObject.addProperty("type", "resource")
                jsonObject.addProperty("resId", src.resId)
            }
        }
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): TravelImage {
        val jsonObject = json.asJsonObject

        return if (jsonObject.has("type")) {
            when (val type = jsonObject["type"].asString) {
                "url" -> context.deserialize<TravelImage.Url>(json, TravelImage.Url::class.java)
                "resource" -> context.deserialize<TravelImage.Resource>(json, TravelImage.Resource::class.java)
                else -> throw JsonParseException("Unknown TravelImage type: $type")
            }
        } else {
            throw JsonParseException("Invalid TravelImage data: 'type' field is missing")
        }
    }
}

class ProfilePicAdapter : JsonDeserializer<ProfilePic>, JsonSerializer<ProfilePic> {

    override fun serialize(src: ProfilePic, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        when (src) {
            is ProfilePic.Url -> {
                jsonObject.addProperty("type", "url")
                jsonObject.addProperty("value", src.value)
            }
            is ProfilePic.Resource -> {
                jsonObject.addProperty("type", "resource")
                jsonObject.addProperty("resId", src.resId)
            }
        }
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): ProfilePic {
        val jsonObject = json.asJsonObject

        return if (jsonObject.has("type")) {
            when (val type = jsonObject["type"].asString) {
                "url" -> context.deserialize<ProfilePic.Url>(json, ProfilePic.Url::class.java)
                "resource" -> context.deserialize<ProfilePic.Resource>(json, ProfilePic.Resource::class.java)
                else -> throw JsonParseException("Unknown ProfilePic type: $type")
            }
        } else {
            throw JsonParseException("Invalid ProfilePic data: 'type' field is missing")
        }
    }
}


class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src.format(formatter))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
        return try {
            LocalDate.parse(json.asString, formatter)
        } catch (e: Exception) {
            throw JsonParseException("Invalid date format")
        }
    }
}
