package org.leandreck.kingbob.msgs

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.netty.util.CharsetUtil
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.Json

/**
 * Created by kowalzik on 12.02.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class DurationMessage
@JsonCreator
constructor(@JsonProperty("msgId") val msgId: String,
            @JsonProperty("host") val host: String,
            @JsonProperty("port") val port: Int,
            @JsonProperty("uri") val uri: String,
            @JsonProperty("duration") val duration: Long)

class DurationMessageCodec : MessageCodec<DurationMessage, DurationMessage> {

    override fun name(): String = "DurationMessageCodec"

    override fun systemCodecID(): Byte = -1

    override fun transform(message: DurationMessage?): DurationMessage? = message?.copy()

    override fun encodeToWire(buffer: Buffer, message: DurationMessage) {
        val strBytes = Json.encode(message).toByteArray(CharsetUtil.UTF_8)
        buffer.appendInt(strBytes.size)
        buffer.appendBytes(strBytes)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): DurationMessage {
        val length = buffer.getInt(pos)
        val startPayload = pos + 4
        val bytes = buffer.getBytes(startPayload, startPayload + length)
        return Json.decodeValue(String(bytes, CharsetUtil.UTF_8), DurationMessage::class.java)
    }
}