package org.leandreck.kingbob.viewers

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import java.util.*

/**
 * Created by kowalzik on 30.01.2017.
 */


class SimpleTestObject : AbstractVerticle() {

    override fun start(fut: Future<Void>) {
        val router = Router.router(vertx)
        router.route().handler {
            routingContext ->
            val random = Random().nextInt(750).toLong()
            vertx.setTimer(random) { handler ->
                routingContext.response().putHeader("content-type", "text/html").end("Hello World!")
            }
        }
        vertx.createHttpServer().requestHandler({ router.accept(it) }).listen(9090)
    }
}