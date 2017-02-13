package org.leandreck.kingbob.viewers

/**
 * Created by kowalzik on 30.01.2017.
 */
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.BridgeEventType
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.PermittedOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler


class SimpleViewer : AbstractVerticle() {

    override fun start(fut: Future<Void>) {
        // Allow outbound traffic to the news-feed address
        val options = BridgeOptions().addOutboundPermitted(PermittedOptions().setAddress("stats-feed"))

        val router = Router.router(vertx)
        router.route("/eventbus/*").handler(
                SockJSHandler.create(vertx).bridge(options, { event ->

                    // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
                    // You can use this for monitoring or logging, or to change the raw messages in-flight.
                    // It can also be used for fine grained access control.
                    if (event.type() === BridgeEventType.SOCKET_CREATED) {
                        println("A socket was created")
                    }
                    // This signals that it's ok to process the event
                    event.complete(true)
                }))

        // Serve the static resources
        router.route().handler(StaticHandler.create())

        vertx.createHttpServer().requestHandler { t -> router.accept(t) }.listen(8080)
        vertx.eventBus().consumer<String>("viewer.duration") { it -> receiveDuration(it) }

    }

    private fun receiveDuration(message: Message<String>) {
        print("Received a avg-duration message: $message")
        val durationMessage = message.body()
        vertx.eventBus().publish("stats-feed", "Request: $durationMessage")
    }


}