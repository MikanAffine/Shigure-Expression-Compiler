package org.owari.shigure.runtime

@FunctionalInterface
interface ArithmeticFunction : (DoubleArray) -> Double {
    override fun invoke(p1: DoubleArray): Double = call(p1)
    fun call(args: DoubleArray): Double

    companion object {
        inline fun of(crossinline fn: (DoubleArray) -> Double) = object : ArithmeticFunction {
            override fun call(args: DoubleArray): Double = fn(args)
        }
    }
}
