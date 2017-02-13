package org.leandreck.kingbob.stats

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import org.leandreck.kingbob.msgs.DurationMessage
import org.leandreck.kingbob.msgs.ResetMessage

/**
 * Created by kowalzik on 30.01.2017.
 */
class ResponseTimeCollector : AbstractVerticle() {

    var count: Long = 0
    var avg: Long = 0

    override fun start(future: Future<Void>) {
        val eventBus = vertx.eventBus()
        eventBus.consumer<DurationMessage>("request.durations") { it -> receiveRequest(it) }
        eventBus.consumer<ResetMessage>("stats.reset") { it -> reset(it) }
    }

    fun receiveRequest(message: Message<DurationMessage>) {
        println("I have received a DurationMessage: ${message.body()}")
        val durationMessage = message.body()
        avg = (count * avg + durationMessage.duration) / ++count

        println("Sending avg-duration message: Count: $count, Last Duration: ${durationMessage.duration}, Avg: $avg")
        vertx.eventBus().send("viewer.duration", "Count: $count, Last Duration: ${durationMessage.duration}, Avg: $avg")
    }

    fun reset(message: Message<ResetMessage>) {
        println("I have received a ResetMessage: ${message.body()}")
        count = 0
        avg = 0
    }

}