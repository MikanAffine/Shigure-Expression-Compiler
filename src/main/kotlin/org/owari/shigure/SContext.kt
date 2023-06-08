package org.owari.shigure

import org.owari.shigure.runtime.ArithmeticFunction
import org.owari.shigure.runtime.BuiltinFunctions

class SContext(
    private val parent: SContext? = null,
) {
    @JvmOverloads
    constructor(parent: SContext? = null, vars: Map<String, Double>, fns: Map<String, ArithmeticFunction>) : this(parent) {
        this.vars.putAll(vars)
        this.fns.putAll(fns)
    }

    private val vars = hashMapOf<String, Double>()
    private val fns = hashMapOf<String, ArithmeticFunction>()
    init {
        vars.putAll(BuiltinFunctions.vars)
        fns.putAll(BuiltinFunctions.fns)
    }

    fun getVar(name: String): Double = vars.getOrDefault(name, 0.0)
    fun putVar(name: String, value: Double) {
        vars[name] = value
    }
    fun getFn(name: String): ArithmeticFunction = fns.getOrDefault(name, object : ArithmeticFunction {
        override fun call(args: DoubleArray): Double = 0.0
    })
    fun putFn(name: String, value: ArithmeticFunction) {
        fns[name] = value
    }

    inline operator fun get(name: String): Double = getVar(name)
    inline operator fun set(name: String, value: Double) = putVar(name, value)
    inline operator fun set(name: String, fn: ArithmeticFunction) = putFn(name, fn)
    inline operator fun set(name: String, noinline fn: (DoubleArray) -> Double) = putFn(name, object : ArithmeticFunction {
        override fun call(args: DoubleArray): Double = fn(args)
    })

    inline fun eval(e: SExpression): Double = e.eval(this)

    companion object {
        @JvmStatic
        fun of(vararg vars: Pair<String, Double>): SContext {
            val ctx = SContext()
            vars.forEach { ctx[it.first] = it.second }
            return ctx
        }
    }
}