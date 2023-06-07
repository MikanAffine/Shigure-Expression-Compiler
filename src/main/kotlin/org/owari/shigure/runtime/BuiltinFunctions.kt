package org.owari.shigure.runtime

import org.owari.shigure.Shigure

object BuiltinFunctions {
    @JvmStatic
    val ZERO = { 0.0 } as ArithmeticFunction

    @JvmStatic
    val table = hashMapOf(
        "zero" to ZERO,
    )
}