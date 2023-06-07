package org.owari.shigure.runtime

import org.owari.shigure.Shigure

object BuiltinFunctions {
    @JvmStatic
    val ZERO: ArithmeticFunction = { 0.0 }

    @JvmStatic
    val table = hashMapOf(
        "zero" to ZERO,
    )
}