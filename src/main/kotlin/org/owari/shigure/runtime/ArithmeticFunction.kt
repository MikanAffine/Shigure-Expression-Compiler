package org.owari.shigure.runtime

interface ArithmeticFunction : (DoubleArray) -> Double {
    override fun invoke(p1: DoubleArray): Double = call(p1)
    fun call(args: DoubleArray): Double
}
