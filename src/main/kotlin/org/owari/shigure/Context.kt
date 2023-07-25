package org.owari.shigure

import org.owari.shigure.impl.*

interface Context {
    operator fun get(name: String): Double
    operator fun set(name: String, value: Double)

    fun call(funcName: String, args: DoubleArray): Double
    fun register(funcName: String, func: MathFunction)

    companion object {
        fun empty() = buildContext()
        fun noBuiltin() = buildContext(false)

        fun of(vars: Map<String, Double>) = buildContext { setAll(vars) }
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

    fun set(name: String, value: Double) {
        vars[name] = value
    }
    fun setAll(m: Map<String, Double>) {
        vars += m
    }
    fun get(name: String) = vars[name]
    fun remove(name: String) {
        vars.remove(name)
    }

    fun register(funcName: String, func: MathFunction) {
        fns[funcName] = func
    }
    inline fun register(funcName: String, crossinline func: (DoubleArray) -> Double) {
        register(funcName, MathFunction(func))
    }
    fun registerAll(m: Map<String, MathFunction>) {
        fns += m
    }
    inline fun registerAll(m: Map<String, (DoubleArray) -> Double>) {
        registerAll(m.mapValues { (k, v) -> MathFunction(v) })
    }
    fun unregister(funcName: String) {
        fns.remove(funcName)
    }
    fun getFunction(funcName: String) = fns[funcName]

    fun build(): Context = ContextImpl(vars, fns)
}
