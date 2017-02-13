package org.leandreck.kingbob

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import org.leandreck.kingbob.loadgers.SimpleGetGenerator
import org.leandreck.kingbob.msgs.*
import org.leandreck.kingbob.stats.ResponseTimeCollector
import org.leandreck.kingbob.viewers.SimpleTestObject
import org.leandreck.kingbob.viewers.SimpleViewer
import java.util.*

/**
 * Created by kowalzik on 30.01.2017.
 */
class Supervisor : AbstractVerticle() {

    var periodicTimerId: Long = 0

    override fun start() {
        println("Starting King Bob...")
        vertx.deployVerticle(SimpleTestObject::class.java.name)
        vertx.deployVerticle(SimpleViewer::class.java.name)
        vertx.deployVerticle(ResponseTimeCollector::class.java.name)
        vertx.deployVerticle(SimpleGetGenerator::class.java.name)
        println("King Bob started!")

        val eventBus = vertx.eventBus()
        eventBus.registerCodec(RequestMessageCodec())
        eventBus.registerCodec(DurationMessageCodec())
        eventBus.registerCodec(ResetMessageCodec())

        eventBus.consumer<RequestMessage>("supervisor") { it -> startNewRequests(it) }
        eventBus.send("supervisor", RequestMessage(msgId = "Start", port = 9090, uri = "/", host = "localhost"),
                DeliveryOptions().setCodecName("RequestMessageCodec"))
    }

    fun startNewRequests(message: Message<RequestMessage>) {
        println("King Bob is going to test: ${message.body()}")
        val eventBus = vertx.eventBus()
        vertx.cancelTimer(periodicTimerId)
        eventBus.send("stats.reset", ResetMessage("Kotlin-${UUID.randomUUID()}"), DeliveryOptions().setCodecName("ResetMessageCodec"))
        val theRequest = message.body()
        periodicTimerId = vertx.setPeriodic(1000, { it ->
            sendMessage(theRequest.port, theRequest.host, theRequest.uri)
        })
    }

    fun sendMessage(port: Int, host: String, uri: String) {
        val requestMessage = RequestMessage(msgId = "Kotlin-${UUID.randomUUID()}", host = host, port = port, uri = uri)
        println(requestMessage)
        vertx.eventBus().send("send.request.GET", requestMessage, DeliveryOptions().setCodecName("RequestMessageCodec"))
    }
}