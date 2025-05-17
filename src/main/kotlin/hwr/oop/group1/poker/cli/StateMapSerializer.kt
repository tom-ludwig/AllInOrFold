package hwr.oop.group1.poker.cli

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull


object StateMapSerializer : KSerializer<Map<String, Any>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StateMap")

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
        val jsonObject = buildJsonObject {
            value.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, JsonPrimitive(value))
                    is Number -> put(key, JsonPrimitive(value.toInt()))
                    is Boolean -> put(key, JsonPrimitive(value))
                    is List<*> -> put(key, JsonArray(value.map { serializeAny(it!!) }))
                    is Map<*, *> -> put(key, serializeMap(value as Map<String, Any>))
                    else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
                }
            }
        }
        (encoder as JsonEncoder).encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any> {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        if (element !is JsonObject) {
            throw IllegalArgumentException("Expected JsonObject for Map deserialization")
        }
        return deserializeJsonObject(element)
    }

    private fun serializeAny(value: Any): JsonElement = when (value) {
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Map<*, *> -> serializeMap(value as Map<String, Any>)
        is List<*> -> JsonArray(value.map { serializeAny(it!!) })
        else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
    }

    private fun serializeMap(map: Map<String, Any>): JsonObject = buildJsonObject {
        map.forEach { (key, value) ->
            put(key, serializeAny(value))
        }
    }

    private fun deserializeJsonObject(jsonObject: JsonObject): Map<String, Any> =
        jsonObject.mapValues { (_, element) -> deserializeJsonElement(element) }

    private fun deserializeJsonElement(element: JsonElement): Any = when (element) {
        is JsonPrimitive -> when {
            element.isString -> element.content
            element.intOrNull != null -> element.intOrNull ?: element.content.toInt()
            element.booleanOrNull != null -> element.booleanOrNull ?: element.content.toBoolean()
            else -> element.content
        }
        is JsonArray -> element.map { deserializeJsonElement(it) }
        is JsonObject -> deserializeJsonObject(element)
    }
}
