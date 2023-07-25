package org.owari.shigure.impl

import org.owari.shigure.CalculateFunc
import org.owari.shigure.Shigure
import java.util.concurrent.atomic.AtomicLong

class Assembler : ClassLoader(Shigure.javaClass.classLoader) {
    private val counter = AtomicLong(0L)
    fun newClassName(): String = "${counter.getAndIncrement()}"

    fun <T> assemble(b: ByteArray): Class<T>
        where T : CalculateFunc {
        return defineClass()
    }
}