package com.lovelycatv.vertex.ai.mcp.codec

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.lovelycatv.vertex.ai.mcp.MCPFunction
import java.lang.reflect.Type

/**
 * @author lovelycat
 * @since 2025-12-16 20:46
 * @version 1.0
 */
class PropertyDeserializer : JsonDeserializer<MCPFunction.InputSchema.Property> {
    override fun deserialize(
        json: JsonElement,
        p1: Type,
        context: JsonDeserializationContext
    ): MCPFunction.InputSchema.Property {
        val obj = json.asJsonObject
        val t = obj.get("type")?.asString ?: throw JsonParseException("Missing 'type' in Property")

        return when (t) {
            "string"  -> context.deserialize(obj, MCPFunction.InputSchema.Property.StringProperty::class.java)
            "integer" -> context.deserialize(obj, MCPFunction.InputSchema.Property.IntegerProperty::class.java)
            "number"  -> context.deserialize(obj, MCPFunction.InputSchema.Property.NumberProperty::class.java)
            "boolean" -> context.deserialize(obj, MCPFunction.InputSchema.Property.BooleanProperty::class.java)
            "object"  -> context.deserialize(obj, MCPFunction.InputSchema.Property.ObjectProperty::class.java)
            "array"   -> context.deserialize(obj, MCPFunction.InputSchema.Property.ArrayProperty::class.java)
            "null"    -> context.deserialize(obj, MCPFunction.InputSchema.Property.NullProperty::class.java)
            else      -> throw JsonParseException("Unknown property type: $t")
        }
    }
}