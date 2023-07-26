package org.owari.shigure

import org.owari.shigure.impl.*

abstract class Context {
    abstract operator fun get(name: String): Double
    abstract operator fun set(name: String, value: Double)

    abstract fun call(funcName: String, args: DoubleArray): Double
    abstract fun register(funcName: String, func: MathFunction)

    companion object {
        fun empty() = buildContext()
        fun noBuiltin() = buildContext(false)

        fun of(vars: Map<String, Double>) = buildContext { setAll(vars) }

        fun of(vararg vars: Pair<String, Double>) = buildContext { setAll(*vars) }
    }
}

fun buildContext(withBuiltin: Boolean = true) = ContextBuilder(withBuiltin).build()
fun buildContext(withBuiltin: Boolean = true, init: ContextBuilder.() -> Unit) = ContextBuilder(withBuiltin).apply(init).build()

class ContextBuilder(withBuiltin: Boolean) {

    private val vars = mutableMapOf<String, Double>()
    private val fns = mutableMapOf<String, MathFunction>()

    init {
        if (withBuiltin) {
            vars += builtinVariables
            fns += builtinFunctions

            buildString {  }
        }
    }

    inline fun set(name: String, value: Int) {
        set(name, value.toDouble())
    }
    fun set(name: String, value: Double) {
        vars[name] = value
    }
    fun setAll(m: Map<String, Double>) {
        vars += m
    }
    fun setAll(vararg pair: Pair<String, Double>) {
        vars += pair
    }
    fun get(name: String) = vars[name]
    fun remove(name: String) {
        vars.remove(name)
    }

    fun register(funcName: String, func: MathFunction) {
        fns[funcName] = func
    }
    inline fun register(funcName: String, noinline func: (DoubleArray) -> Double) {
        register(funcName, MathFunction(func))
    }
    fun registerAll(m: Map<String, MathFunction>) {
        fns += m
    }
    fun registerAll(vararg pair: Pair<String, MathFunction>) {
        fns += pair
    }
    @JvmName("registerAll2")
    inline fun registerAll(vararg pair: Pair<String, (DoubleArray) -> Double>) {
        registerAll(*pair.map { (k, v) -> k to MathFunction(v) }.toTypedArray())
    }
    fun unregister(funcName: String) {
        fns.remove(funcName)
    }
    fun getFunction(funcName: String) = fns[funcName]

    fun build(): Context = ContextImpl(vars, fns)
}
