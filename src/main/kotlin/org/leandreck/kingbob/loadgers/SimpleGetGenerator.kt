package org.leandreck.kingbob.loadgers

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import org.leandreck.kingbob.msgs.DurationMessage
import org.leandreck.kingbob.msgs.RequestMessage

/**
 * Created by kowalzik on 30.01.2017.
 */
class SimpleGetGenerator : AbstractVerticle() {

    override fun start(future: Future<Void>) {
        vertx.eventBus().consumer<RequestMessage>("send.request.GET") { it -> receiveRequest(it) }
    }

    fun receiveRequest(message: Message<RequestMessage>) {
        println("I have received a message: ${message.body()}")

        val theRequest = message.body()
        val start = System.currentTimeMillis()
        println("Start=$start")
        vertx.createHttpClient()
                .getNow(theRequest.port, theRequest.host, theRequest.uri, {
                    response ->
                    val duration = System.currentTimeMillis() - start
                    println("Received response with status code ${response.statusCode()} and took ${duration}ms")
                    vertx.eventBus().send("request.durations",
                            DurationMessage(theRequest.msgId, theRequest.host, theRequest.port, theRequest.uri, duration),
                            DeliveryOptions().setCodecName("DurationMessageCodec"))
                })
    }
}