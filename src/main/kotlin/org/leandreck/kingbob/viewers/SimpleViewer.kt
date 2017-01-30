package org.leandreck.kingbob.viewers

/**
 * Created by kowalzik on 30.01.2017.
 */
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

class SimpleViewer : AbstractVerticle() {

    override fun start(fut: Future<Void>) {
        vertx
                .createHttpServer()
                .requestHandler({ r -> r.response().end("""<h1>Hello from "King Bob"</h1>""") })
                .listen(8080, { result ->
                    if (result.succeeded()) {
                        fut.complete()
                    } else {
                        fut.fail(result.cause())
                    }
                })
    }
}