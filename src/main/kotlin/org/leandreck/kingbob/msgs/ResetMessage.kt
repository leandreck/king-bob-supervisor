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
data class ResetMessage
@JsonCreator
constructor(@JsonProperty("msgId") val msgId: String)

class ResetMessageCodec : MessageCodec<ResetMessage, ResetMessage> {

    override fun name(): String = "ResetMessageCodec"

    override fun systemCodecID(): Byte = -1

    override fun transform(s: ResetMessage?): ResetMessage? = s?.copy()

    override fun encodeToWire(buffer: Buffer, message: ResetMessage) {
        val strBytes = Json.encode(message).toByteArray(CharsetUtil.UTF_8)
        buffer.appendInt(strBytes.size)
        buffer.appendBytes(strBytes)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): ResetMessage {
        val length = buffer.getInt(pos)
        val startPayload = pos + 4
        val bytes = buffer.getBytes(startPayload, startPayload + length)
        return Json.decodeValue(String(bytes, CharsetUtil.UTF_8), ResetMessage::class.java)
    }
}