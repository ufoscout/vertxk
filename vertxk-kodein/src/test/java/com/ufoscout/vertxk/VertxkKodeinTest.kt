package com.ufoscout.vertxk

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.jxinject.JxInjector
import com.github.salomonbrys.kodein.jxinject.jx
import com.github.salomonbrys.kodein.jxinject.jxInjectorModule
import com.github.salomonbrys.kodein.singleton
import com.ufoscout.vertxk.stub.CoroutinesVerticle
import com.ufoscout.vertxk.stub.SimpleVerticle
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.file.FileSystem
import io.vertx.core.shareddata.SharedData
import io.vertx.core.spi.VerticleFactory
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.util.*

class VertxkKodeinTest: BaseTest() {

    @Test
    fun shouldReturnCanonicalVertxVerticleName() {
        val name = VertxkKodein.getFullVerticleName<SimpleVerticle>()
        println("Found name [$name]")
        Assert.assertEquals(VertxkKodein.PREFIX + ":" + SimpleVerticle::class.java.name, name)
    }

    @Test
    fun shouldRegisterTheVertxkKodeinFactory() {
        val spiedVertx = Mockito.spy(vertx)
        val kodein = Kodein {}

        VertxkKodein.registerFactory(spiedVertx, kodein)

        Mockito.verify(spiedVertx, Mockito.times(1)).registerVerticleFactory(Mockito.any())
    }

    @Test
    fun shouldRegisterVertxkComponentsInKodein() {
        val kodein = Kodein {
            import(VertxkKodein.module(vertx))
        }

        Assert.assertNotNull(kodein.instance<Vertx>())
        Assert.assertNotNull(kodein.instance<EventBus>())
        Assert.assertNotNull(kodein.instance<FileSystem>())
        Assert.assertNotNull(kodein.instance<SharedData>())
    }

    @Test
    fun shouldDeployACoroutineBasedVerticle() = runBlocking<Unit> {
        val name = UUID.randomUUID().toString()
        val kodein = Kodein {
            import(jxInjectorModule)
            bind<String>() with singleton { name }
        }

        CoroutinesVerticle.NAME = ""
        CoroutinesVerticle.STARTED = false

        VertxkKodein.registerFactory(vertx, kodein)
        awaitResult<String> {
            VertxkKodein.deployVerticle<CoroutinesVerticle>(vertx, it)
        }

        Assert.assertEquals(name, CoroutinesVerticle.NAME)
        Assert.assertTrue(CoroutinesVerticle.STARTED)

    }

}