package org.owari.shigure

class SContext(
    private val parent: SContext? = null,
) {
    @JvmOverloads
    constructor(parent: SContext? = null, vars: Map<String, Double>, fns: Map<String, (DoubleArray) -> Double>) : this(parent) {
        this.vars.putAll(vars)
        this.fns.putAll(fns)
    }

    private val vars = hashMapOf<String, Double>()
    private val fns = hashMapOf<String, (DoubleArray) -> Double>()

    fun getVar(name: String): Double = vars.getOrDefault(name, 0.0)
    fun putVar(name: String, value: Double) {
        vars[name] = value
    }
    fun getFn(name: String): (DoubleArray) -> Double = fns.getOrDefault(name) { 0.0 }
    fun putFn(name: String, value: (DoubleArray) -> Double) {
        fns[name] = value
    }
    inline operator fun get(name: String): Double = getVar(name)
    inline operator fun set(name: String, value: Double) = putVar(name, value)
    inline operator fun set(name: String, noinline fn: (DoubleArray) -> Double) = putFn(name, fn)

    inline fun eval(e: SExpression): Double = e.eval(this)
}