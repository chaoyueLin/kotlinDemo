# Kotlin 学习笔记

Kotlin 语法要点、协程与 Flow 的知识梳理，以及高频面试题整理。

## 目录

| 文档 | 主要内容 |
| --- | --- |
| [Kotlin 基础语法与空安全](<./Kotlin 基础语法与空安全.md>) | `when` 主语捕获、伴生对象与 `@JvmStatic`、委托 `by`、可空性 `?.` `?:` `!!`、接口、嵌套类、泛型，以及空安全背后的字节码实现 |
| [Kotlin 作用域函数与扩展](<./Kotlin 作用域函数与扩展.md>) | 扩展函数，`apply` / `with` / `run` / `also` / `let` 的参数、返回值与选用场景 |
| [Kotlin 类初始化与单例模式](<./Kotlin 类初始化与单例模式.md>) | `lateinit`、`lazy` 的三种线程安全模式，单例的四种 Kotlin 实现（饿汉、懒汉、双重校验、静态内部类） |
| [Kotlin 协程基础知识点与高频面试题](<./Kotlin 协程基础知识点与高频面试题.md>) | 进程/线程/协程对比、CPS 与状态机、CoroutineContext/Job/Dispatcher/Scope、结构化并发、异常处理与取消、Android 应用，26 道高频面试题 |
| [Kotlin Channel、Flow基础知识点与高频面试题](<./Kotlin Flow基础知识点与高频面试题.md>) | 冷流与热流、常用运算符、StateFlow 与 SharedFlow 对比、Channel 容量与关闭、select 多路复用、Mutex/Semaphore 并发安全，22 道高频面试题 |

## 学习心得

* java的语法是简单的，但是为了避免空指针需要些很多判断。kotlin可以优雅的表达，编译期间就检查知否空指针
* effetctive java中是java高效使用java的典范，kotlin直接吸收默认实现的，比如class默认就是final的，组合优于继承
* collections的使用扩展常用方法，如过滤filter,转换map
* 常用设计模式的实现，如用于流的produce是生产消费者模式，with对一个对象实例调用多个方法有点像建造者模式
* lamda表达式极简使用，扩展函数有点像装饰模式
* 协程引入

## 参考

* [Kotlin 中文文档](https://www.kotlincn.net/docs/reference/)
