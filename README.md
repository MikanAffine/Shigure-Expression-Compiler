# 时雨 表达式编译计算引擎

![English Version here](README_en.md)

---

### 使用便捷



__简洁的API:__ 寥寥几行代码即可达成目标



__内置大量函数, 还可自定义函数:__ 随心定制你的 表达式环境

```kotlin
val expr = Shigure.createExpr("a + b * c")
val context = buildContext {
    set("a", 1.2)
    set("b", 2.5)
    set("c", 3.0)
}
val result: Double = expr.invoke(context)
```

---

#### 性能先进



__可编译到 Java字节码:__ 性能获得巨大提升


__自带 JIT 策略:__ 无论是只执行几次, 还是大量重复执行, 都能获得最佳的性能表现.



__虽然是动态取用变量和函数, 但编译后性能不输直接写在 Java 代码内的表达式__


__解释模式下, 字节码解释器也有很好的性能, 只需 23 秒即可执行 1 亿次 计算__

```text
[(Baseline) Java Eval 100M] Elapsed time: 2830 ms
[Interpreted Eval 100M] Elapsed time: 23254 ms
[Compiled Eval 100M] Elapsed time: 5712 ms
```

---

#### 赶快使用吧!

[![](https://jitpack.io/v/KouyouX/Shigure-Expression-Compiler.svg)](https://jitpack.io/#KouyouX/Shigure-Expression-Compiler)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KouyouX:Shigure-Expression-Compiler:v1.0.0'
}
```
