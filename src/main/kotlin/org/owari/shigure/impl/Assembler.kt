package org.owari.shigure.impl

import org.owari.shigure.Shigure
import java.util.concurrent.atomic.AtomicLong

class Assembler : ClassLoader(Shigure.javaClass.classLoader) {
    private val counter = AtomicLong(0L)
    fun newClassName(): String = "org.owari.shigure.generated.CalcFuncImpl$${counter.getAndIncrement()}"

    fun assemble(n: String, b: ByteArray): Class<*> {
        val clazz = defineClass(n, b, 0, b.size)
        assert(CalcFunc::class.java.isAssignableFrom(clazz))
        return clazz
    }
}
