package org.owari.shigure

@FunctionalInterface
interface MathFunction : (DoubleArray) -> Double {
    override fun invoke(p1: DoubleArray): Double
}

inline operator fun MathFunction.invoke(vararg p1: Double) = invoke(p1)

inline fun MathFunction(crossinline fn: (DoubleArray) -> Double) = object : MathFunction {
    override fun invoke(p1: DoubleArray) = fn(p1)
}
