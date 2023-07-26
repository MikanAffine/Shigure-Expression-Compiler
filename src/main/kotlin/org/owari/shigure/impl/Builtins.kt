package org.owari.shigure.impl

import org.owari.shigure.MathFunction
import kotlin.math.*
import kotlin.random.Random

val RANDOM_GENERATOR = Random(System.currentTimeMillis() xor System.nanoTime())

// 内置变量
const val PI = Math.PI
const val E = Math.E
val RADIAN_CONVERT_RATIO = Math.toRadians(1.0)
val GOLDEN_RATIO = (1 + sqrt(5.0)) / 2

// 内置函数
val ZERO = MathFunction { 0.0 }
val ONE = MathFunction { 1.0 }

val ABS = MathFunction { abs(it[0]) }
val SIGN = MathFunction { sign(it[0]) }

val SQUARE = MathFunction { it[0] * it[0] }
val CUBE = MathFunction { it[0] * it[0] * it[0] }

val MAX = MathFunction { it.max() }
val MIN = MathFunction { it.min() }
val AVG = MathFunction { it.average() }
val SUM = MathFunction { it.sum() }
// 求所有参数的乘积
val PROD = MathFunction { it.reduce { acc, d -> acc * d } }
// 平方和
val SQSUM = MathFunction { it.sumOf { d -> d * d } }

// 注意, Shigure 中三角函数默认使用角度制
val SIN = MathFunction { sin(Math.toRadians(it[0])) }
val COS = MathFunction { cos(Math.toRadians(it[0])) }
val TAN = MathFunction { tan(Math.toRadians(it[0])) }
val ASIN = MathFunction { Math.toDegrees(asin(it[0])) }
val ACOS = MathFunction { Math.toDegrees(acos(it[0])) }
val ATAN = MathFunction { Math.toDegrees(atan(it[0])) }
val ATAN2 = MathFunction { Math.toDegrees(atan2(it[0], it[1])) }
val SINH = MathFunction { sinh(it[0]) }
val COSH = MathFunction { cosh(it[0]) }
val TANH = MathFunction { tanh(it[0]) }
val ASINH = MathFunction { asinh(it[0]) }
val ACOSH = MathFunction { acosh(it[0]) }
val ATANH = MathFunction { atanh(it[0]) }

// 角度转弧度
val RAD = MathFunction { Math.toRadians(it[0]) }
// 弧度转角度
val DEG = MathFunction { Math.toDegrees(it[0]) }
// 求斜边长
val HYPOT = MathFunction { hypot(it[0], it[1]) }

// 幂函数, pow(base, exponent) = base ^ exponent
@Deprecated("已经内置了 power 运算符...", ReplaceWith("a ^ b"), DeprecationLevel.WARNING)
val POW = MathFunction { it[0].pow(it[1]) }
// 标准库的 sqrt 是 native 实现, 更快
val SQRT = MathFunction { sqrt(it[0]) }
// 理论上, 使用标准库的 cbrt 是更快的
val CBRT = MathFunction { cbrt(it[0]) }
// 开方函数, root(base, exponent) = value ^ (1 / exponent)
@Deprecated("已经内置了 power 运算符...", ReplaceWith("a ^ (1 / b)"), DeprecationLevel.WARNING)
val ROOT = MathFunction { it[0].pow(1 / it[1]) }

val EXP = MathFunction { exp(it[0]) }
val LN = MathFunction { ln(it[0]) }
val LOG10 = MathFunction { log10(it[0]) }
val LOG2 = MathFunction { log2(it[0]) }
// log(value, base)
val LOG = MathFunction { log(it[0], it[1]) }

val CEIL = MathFunction { ceil(it[0]) }
val FLOOR = MathFunction { floor(it[0]) }
val ROUND = MathFunction { round(it[0]) }
// 去除小数部分, 保留整数部分
val TRUNCATE = MathFunction { truncate(it[0]) }

// 随机小数
val RANDOM = MathFunction { RANDOM_GENERATOR.nextDouble() }
// 随机整数
val RANDINT = MathFunction { RANDOM_GENERATOR.nextLong().toDouble() }

val builtinVariables = mapOf(
    "pi" to PI,
    "π" to PI,
    "e" to E,
    "φ" to GOLDEN_RATIO,
    "rad" to RADIAN_CONVERT_RATIO,
)

val builtinFunctions = mapOf(
    "zero" to ZERO,
    "one" to ONE,

    "abs" to ABS,
    "sign" to SIGN,

    "square" to SQUARE,
    "cube" to CUBE,

    "max" to MAX,
    "min" to MIN,
    "avg" to AVG,
    "sum" to SUM,
    "prod" to PROD,
    "sqsum" to SQSUM,

    "sin" to SIN,
    "cos" to COS,
    "tan" to TAN,
    "asin" to ASIN,
    "acos" to ACOS,
    "atan" to ATAN,
    "atan2" to ATAN2,
    "sinh" to SINH,
    "cosh" to COSH,
    "tanh" to TANH,
    "asinh" to ASINH,
    "acosh" to ACOSH,
    "atanh" to ATANH,

    "rad" to RAD,
    "deg" to DEG,
    "hypot" to HYPOT,

    "pow" to POW,
    "sqrt" to SQRT,
    "cbrt" to CBRT,
    "root" to ROOT,

    "exp" to EXP,
    "ln" to LN,
    "log10" to LOG10,
    "log2" to LOG2,
    "log" to LOG,

    "ceil" to CEIL,
    "floor" to FLOOR,
    "round" to ROUND,
    "truncate" to TRUNCATE,

    "random" to RANDOM,
    "randint" to RANDINT,
)
