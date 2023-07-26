package org.owari.shigure.impl

import org.owari.shigure.Context
import org.owari.shigure.util.DoubleArrayStack
import kotlin.math.floor
import kotlin.math.pow

/**
 * 内码解释器
 */
object Interpreter {
    @JvmStatic
    fun eval(os: OpcodeSet, context: Context): Double {
        val opcodes = os.opcodes
        if(opcodes.isEmpty()) return 0.0

        val stack = DoubleArrayStack(12)

        opcodes.forEach {
            when (it.op) {
                Opcode.PUSH -> stack.push(it.argD)
                Opcode.LOADVAR -> stack.push(context[it.argN])
                Opcode.CALL -> stack.push(context.call(it.argN, stack.popN(it.argI)))
                Opcode.ADD -> stack.push(stack.pop() + stack.pop())

                Opcode.SUB -> {
                    val a = stack.pop()
                    stack.push(stack.pop() - a)
                }

                Opcode.MUL -> stack.push(stack.pop() * stack.pop())

                Opcode.DIV -> {
                    val a = stack.pop()
                    stack.push(stack.pop() / a)
                }

                Opcode.MOD -> {
                    val a = stack.pop()
                    stack.push(stack.pop() % a)
                }

                Opcode.FLOORDIV -> {
                    val a = stack.pop()
                    stack.push(floor(stack.pop() / a))
                }

                Opcode.POW -> {
                    val a = stack.pop()
                    stack.push(stack.pop().pow(a))
                }

                Opcode.NEG -> stack.push(-stack.pop())
                Opcode.PRECALL, Opcode.PUSHARG, Opcode.ENDARG -> {}
                else -> throw UnsupportedOperationException("Unknown opcode: ${it.op}")
            }
        }
        return stack.pop()
    }
}

class Opcode(val op: Int, val argD: Double, val argN: String, var argI: Int) {
    companion object {
        const val PUSH = 0
        const val CALL = 1
        const val LOADVAR = 2
        const val ADD = 3
        const val SUB = 4
        const val MUL = 5
        const val DIV = 6
        const val MOD = 7
        const val FLOORDIV = 8
        const val POW = 9
        const val NEG = 10 // 仅会出现在对变量使用中, 带符号数字已在 Tokenize 阶段被解析
        const val PRECALL = 11 // 编译到 java 时使用的
        const val PUSHARG = 12 // 编译到 java 时使用的
        const val ENDARG = 13 // 编译到 java 时使用的

        fun push(n: Double) = Opcode(PUSH, n, "", 0)
        fun operator(t: Int) = Opcode(t, 0.0, "", 0)
        fun call(name: String, argc: Int) = Opcode(CALL, 0.0, name, argc)
        fun load(name: String) = Opcode(LOADVAR, 0.0, name, 0)
        fun preCall(name: String) = Opcode(PRECALL, 0.0, name, 0)
        fun preArg(c: Int) = Opcode(PUSHARG, 0.0, "", c)
    }
    override fun toString() = "[$op, $argD, $argN, $argI]"
}

class OpcodeSet(val opcodes: List<Opcode>, val usedVars: List<String>)