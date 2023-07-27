# Shigure Expression Compiler
[Chinese Version here](README.md)

## Shigure Expression Compiler

#### Easy to use
__Simple API:__ A few lines of code can achieve the goal
__Built-in functions, customizable functions:__ Customize your expression environment at will
____
```kotlin
val expr = Shigure.createExpr("a + b * c")
val context = buildContext {
    set("a", 1.2)
    set("b", 2.5)
    set("c", 3.0)
}
val result: Double = expr.invoke(context)
```

#### Advanced performance
__Can be compiled to Java bytecode:__ Performance is greatly improved
__Comes with JIT strategy:__ Whether it is executed only a few times or executed repeatedly, the best performance can be obtained.

__Although expression is executed dynamically, the performance after compilation is close to the expression written directly in the Java code__
__In the interpretation mode, the bytecode interpreter also has good performance, and it only takes 23 seconds to execute 100 million calculations__
```text
[(Baseline) Java Eval 100M] Elapsed time: 2830 ms
[Interpreted Eval 100M] Elapsed time: 23254 ms
[Compiled Eval 100M] Elapsed time: 5712 ms
```

#### Use it now!

[![](https://jitpack.io/v/KouyouX/Shigure-Expression-Compiler.svg)](https://jitpack.io/#KouyouX/Shigure-Expression-Compiler)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KouyouX:Shigure-Expression-Compiler:v1.0.1'
}
```
