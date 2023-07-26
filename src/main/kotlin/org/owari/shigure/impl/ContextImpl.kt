package org.owari.shigure.impl

import org.owari.shigure.*

class ContextImpl() : Context() {
    constructor(initVars: Map<String, Double>, initFns: Map<String, MathFunction>) : this() {
        vars += initVars
        fns += initFns
    }

    private val vars = hashMapOf<String, Double>()
    private val fns = hashMapOf<String, MathFunction>()

    override fun get(name: String): Double = vars.getOrDefault(name, 0.0)
    override fun set(name: String, value: Double) {
        vars[name] = value
    }

    override fun call(funcName: String, vararg args: Double): Double = fns[funcName]?.invoke(args)
        ?: throw RuntimeException("Function not found: $funcName")

    override fun register(funcName: String, func: MathFunction) {
        fns[funcName] = func
    }
}
