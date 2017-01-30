package org.leandreck.kingbob

import io.vertx.core.AbstractVerticle
import org.leandreck.kingbob.viewers.SimpleViewer

/**
 * Created by kowalzik on 30.01.2017.
 */
class Supervisor : AbstractVerticle() {
    override fun start() {
        println("Starting King Bob...")
        vertx.deployVerticle(SimpleViewer::class.java.name)
        println("King Bob started!")
    }
}