package com.ufoscout.vertxk.kodein

import com.ufoscout.vertxk.kodein.stub.SimpleVerticle
import com.ufoscout.vertxk.kodein.stub.VerticleWithDependencies
import org.junit.Assert
import org.junit.Test
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import org.kodein.di.jxinject.jxInjectorModule
import java.util.*

class VertxkKodeinVerticleFactoryTest {

    @Test
    fun shouldReturnTheExpectedPrefix() {
        val kodein = Kodein {}
        val factory = VertxkKodeinVerticleFactory(kodein)
        Assert.assertEquals(VertxkKodeinVerticleFactory.PREFIX, factory.prefix())
    }

    @Test
    fun shouldCreateASimpleVerticleInstance() {
        val kodein = Kodein {
            import(jxInjectorModule)
        }

        val factory = VertxkKodeinVerticleFactory(kodein)

        val instance = factory.createVerticle(SimpleVerticle::class.java!!.getName(), this.javaClass.classLoader)
        Assert.assertNotNull(instance)
        Assert.assertTrue(instance is SimpleVerticle)
    }

    @Test
    fun shouldCreateAVerticleInstanceWithDependencies() {
        val name = UUID.randomUUID().toString()
        val kodein = Kodein {
            import(jxInjectorModule)
            bind<String>() with singleton { name }
        }

        val factory = VertxkKodeinVerticleFactory(kodein)

        val instance = factory.createVerticle(VerticleWithDependencies::class.java!!.getName(), this.javaClass.classLoader)
        Assert.assertNotNull(instance)
        Assert.assertTrue(instance is VerticleWithDependencies)

        Assert.assertEquals(name, (instance as VerticleWithDependencies).name)

    }


}