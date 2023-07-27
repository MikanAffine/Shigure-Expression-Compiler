package org.owari.shigure.impl

import org.owari.shigure.Shigure

/**
 * @author Mochizuki Haruka
 * @since 1.0 - release
 *
 * Parser, 直接输出字节码序列
 */
class Parser(val src: Tokenizer) {
    private val result = mutableListOf<Opcode>()
    private val usedVars = mutableSetOf<String>()

    fun parse(): OpcodeSet {
        src.reset()
        result.clear()
        usedVars.clear()
        if(!src.hasNext()) return OpcodeSet(emptyList(), emptyList())
        emitAddSub()
        return OpcodeSet(result, usedVars.toList())
    }

    private inline fun emit(i: Opcode) {
        result += i
    }

    private fun emitAddSub() {
        emitMulDiv()
        while(src.test(TokenType.ADD) || src.test(TokenType.SUB)) {
            if (src.testAndSkip(TokenType.ADD)) {
                emitMulDiv()
                emit(Opcode.operator(Opcode.ADD))
            } else if (src.testAndSkip(TokenType.SUB)) {
                emitMulDiv()
                emit(Opcode.operator(Opcode.SUB))
            }
        }
    }
    private fun emitMulDiv() {
        emitPow()
        while(src.test(TokenType.MUL) || src.test(TokenType.DIV) || src.test(TokenType.MOD) || src.test(TokenType.FLOORDIV)) {
            if (src.testAndSkip(TokenType.MUL)) {
                emitPow()
                emit(Opcode.operator(Opcode.MUL))
            } else if (src.testAndSkip(TokenType.DIV)) {
                emitPow()
                emit(Opcode.operator(Opcode.DIV))
            } else if (src.testAndSkip(TokenType.MOD)) {
                emitPow()
                emit(Opcode.operator(Opcode.MOD))
            } else if (src.testAndSkip(TokenType.FLOORDIV)) {
                emitPow()
                emit(Opcode.operator(Opcode.FLOORDIV))
            }
        }
    }
    // 特别地, power 运算符 具有右结合性
    private fun emitPow() {
        emitUnary()
        var count = 0
        if(src.testAndSkip(TokenType.POW)) {
            count++
            emitUnary()
        }
        repeat(count) { emit(Opcode.operator(Opcode.POW)) }
    }
    private fun emitUnary() {
        if(src.testAndSkip(TokenType.SUB)) {
            emitAtom()
            emit(Opcode.operator(Opcode.NEG))
            return
        } else src.testAndSkip(TokenType.ADD)
        emitAtom()
    }
    private fun emitAtom() {
        if(src.test(TokenType.NUMBER)) {
            result += Opcode.push(src.next().text.toDouble())
        } else if(src.test(TokenType.ID)) {
            val id = src.next().text
            if(src.testAndSkip(TokenType.LPAREN)) {
                val a = Opcode.preCall(id)
                emit(a)
                if(src.testAndSkip(TokenType.RPAREN)) {
                    emit(Opcode.call(id, 0))
                    return
                }
                var count = 0
                do {
                    emit(Opcode.preArg(count))
                    emitAddSub()
                    emit(Opcode.operator(Opcode.ENDARG))
                    count++
                }
                while (src.hasNext() && src.testAndSkip(TokenType.COMMA))
                src.expect(TokenType.RPAREN)
                a.argI = count
                emit(Opcode.call(id, count))
            } else {
                usedVars += id
                emit(Opcode.load(id))
            }
        } else if(src.testAndSkip(TokenType.LPAREN)) {
            emitAddSub()
            src.expect(TokenType.RPAREN)
        }
    }
}
