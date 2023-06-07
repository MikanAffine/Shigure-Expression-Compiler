package org.owari.shigure.tokenize

class STokenStream @JvmOverloads constructor(private val tokens: List<SToken>, var offset: Int = 0) {
    fun toList() = ArrayList(tokens)

    fun hasMore() = offset < tokens.size

    fun peek() = tokens[offset]
    fun next() = tokens[offset++]

    fun skip() { offset++ }
    fun skip(n: Int) { offset += n }
    fun reset() { offset = 0 }

    inline fun expect(s: String): SToken = next().apply { if(text != s) throw RuntimeException("Expected [ANY : $s], but got [ANY : $text]") }
    inline fun expect(t: Int): SToken = next().apply { if(type != t) throw RuntimeException("Expected [$t : ANY], but got [$type : ANY]") }
    inline fun expect(s: String, t: Int): SToken = next().apply { if(text != s || type != t) throw RuntimeException("Expected [$t : $s], but got [$type : $text]") }

    inline fun test(s: String): Boolean = hasMore() && peek().text == s
    inline fun test(t: Int): Boolean = hasMore() && peek().type == t
    inline fun test(s: String, t: Int): Boolean = hasMore() && peek().run { text == s && type == t }

    inline fun testAndSkip(s: String): Boolean = if(test(s)) { skip(); true } else false
    inline fun testAndSkip(t: Int): Boolean = if(test(t)) { skip(); true } else false
    inline fun testAndSkip(s: String, t: Int): Boolean = if(test(s, t)) { skip(); true } else false
}