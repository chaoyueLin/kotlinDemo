# koilin学习体验
* java的语法是简单的，但是为了避免空指针需要些很多判断。kotlin可以优雅的表达，编译期间就检查知否空指针
* effetctive java中是java高效使用java的典范，kotlin直接吸收默认实现的，比如class默认就是final的，组合优于继承
* collections的使用扩展常用方法，如过滤filter,转换map
* 常用设计模式的实现，如用于流的produce是生产消费者模式，with对一个对象实例调用多个方法有点像建造者模式
* lamda表达式极简使用，扩展函数有点像装饰模式
* 协程引入

# 参考
[https://www.kotlincn.net/docs/reference/](https://www.kotlincn.net/docs/reference/)
## kotlin-android-extensions

# when主语捕获到变量中
	fun Request.getBody() =
		when (val response = executeRequest()) {
			is Success -> response.body
			is HttpError -> throw HttpException(response.status)
		}
# 创建单例

	object Resource {
		val name = "Name"
	}

## 伴生对象
类内部的对象声明可以⽤ companion 关键字标记：

	class MyClass {
		companion object Factory {
			fun create(): MyClass = MyClass()
		}
	}

可以省略伴⽣对象的名称，在这种情况下将使⽤名称 Companion ：
	class MyClass {
		companion object { }
	}
	val x = MyClass.Companion

## 静态static
接⼝中伴⽣对象的 @JvmStatic 与 @JvmField

	interface Foo {
		companion object {
			@JvmField
			val answer: Int = 42
			@JvmStatic
			fun sayHello() {
				println("Hello, world!")
			}
		}
	}
相当于这段 Java 代码：

	interface Foo {
		public static int answer = 42;
		public static void sayHello() {
		// ……
		}
	}

## by,notNull
	class App : Application() {
		companion object {
			var instance: App by Delegates.notNull()
		}
		override fun onCreate() {
			super.onCreate()
			instance = this
		}
	}



# 非空
## If not null 缩写

	val files = File("Test").listFiles()
	println(files?.size)

## If not null and else 缩写
	val files = File("Test").listFiles()
	println(files?.size ?: "empty")

## if null 执行一个语句
	val values = ……
	val email = values["email"] ?: throw IllegalStateException("Email is missing!")

## if not null 执行代码
	val value = ……
	value?.let {
	…… // 代码会执⾏到此处, 假如data不为null
	}

# 扩展
Kotlin 能够扩展⼀个类的新功能⽽⽆需继承该类或者使⽤像装饰者这样的设计模式。 这通过叫做扩展的特殊声明完成。
## 扩展函数

# 作用域函数 apply/with/run/also/let
唯一目的是在对象的上下文中执行代码块。当对一个对象调用这样的函数并提供一个 lambda 表达式时，它会形成一个临时作用域。在此作用域中，可以访问该对象而无需其名称。这些函数称为作用域函数。

run, with, let, also, apply  都是作用域函数，这些作用域函数如何使用，以及如何区分呢，我们将从以下三个方面来区分它们。

* 是否是扩展函数。
* 作用域函数的参数（this、it）。
* 作用域函数的返回值（调用本身、其他类型即最后一行）。

首先我们来看一下 with 和 T.run，这两个函数非常的相似，他们的区别在于 with 是个普通函数，T.run 是个扩展函数，来看一下下面的例子。

	val name: String? = null
	with(name){
	    val subName = name!!.substring(1,2)
	}
	
	// 使用之前可以检查它的可空性
	name?.run { val subName = name.substring(1,2) }?:throw IllegalArgumentException("name must not be null")

作用域函数的参数（this、it）


T.run 和 T.let，它们都是扩展函数，但是他们的参数不一样 T.run 的参数是 this, T.let 的参数是 it。
	val name: String? = "hi-dhl.com"
	
	// 参数是 this，可以省略不写
	name?.run {
	    println("The length  is ${this.length}  this 是可以省略的 ${length}")
	}
	
	// 参数 it
	name?.let {
	    println("The length  is  ${it.length}")
	}
	
	// 自定义参数名字
	name?.let { str ->
	    println("The length  is  ${str.length}")
	}

T.let 和 T.also 它们接受的参数都是 it, 但是它们的返回值是不同的 T.let 返回最后一行，T.also 返回调用本身。
	var name = "hi-dhl"
	
	// 返回调用本身
	name = name.also {
	    val result = 1 * 1
	    "juejin"
	}
	println("name = ${name}") // name = hi-dhl
	
	// 返回的最后一行
	name = name.let {
	    val result = 1 * 1
	    "hi-dhl.com"
	}
	println("name = ${name}") // name = hi-dhl.com

从上面的例子来看 T.also 似乎没有什么意义，细想一下其实是非常有意义的，在使用之前可以进行自我操作，结合其他的函数，功能会更强大。

	fun makeDir(path: String) = path.let{ File(it) }.also{ it.mkdirs() }


![](./zuoyongyu.png)

## 引用上下文对象的方式
run、with 以及 apply 通过关键字 this 引用上下文对象。let 及 also 将上下文对象作为 lambda 表达式参数用it.
## 返回值
apply 及 also 的返回值是上下文对象本身。因此，它们可以作为辅助步骤包含在调用链中：你可以继续在同一个对象上进行链式函数调用。let、run 及 with 返回 lambda 表达式的结果。

# 接口

	interface MyInterface {
    	fun onLocationMeasured(location: Location)
	}
	val obj = object : MyInterface {
    	override fun onLocationMeasured(location: Location) { ... }
	}

# 类构造函数，初始化，属性构造
## 嵌套类，内部类inner，匿名内部类
# 泛型
# 协程
协程是一种脱离语言的概念，它是一种编程思想，并不局限于特定的语言。本质上来说。协程就是一段程序，它能够被挂起，待会儿再恢复执行。语言会对协程有自己的实现。

协程(coroutine)的概念根据Donald Knuth的说法早在1958年就由Melvin Conway提出了，对应wikipedia的定义如下

	Coroutines are computer program components that generalize subroutines for non-preemptive multitasking, by allowing execution to be suspended and resumed. Coroutines are well-suited for implementing familiar program components such as cooperative tasks, exceptions, event loops, iterators, infinite lists and pipes.

子例程(subroutine)是一个概括性的术语，子例程可以是整个程序中的一个代码区块，当它被主程序调用的时候就会进入运行。例如函数就是子例程中的一种。协程相比子例程更加的灵活，允许执行过程中被挂起和恢复，多个协程可以一起相互协作执行任务。从协程(co + routine)名字上来拆解为支持协作(cooperate)的例程。
## 进程、线程、协程的关系和比较

* 进程是资源分配的最小单位，会拥有独立的地址空间以及对应的内存空间，还有网络和文件资源等，不同进程之间资源都是独立的，可以通过进程间通信（管道、共享内存、信号量等方式）来进行交互。
* 线程为CPU调度的基本单位，除了拥有运行中必不可少的信息(如程序计数器、一组寄存器和栈)以外，本身并不拥有系统资源，所有线程会共享进程的资源，比如会共享堆资源。
* 协程可以认为是运行在线程上的代码块，协程提供的挂起操作会使协程暂停执行，而不会导致线程阻塞。一个线程内部可以创建几千个协程都没有任何问题。
* 进程的切换和线程切换中都包含了对应上下文的切换，这块都涉及到了内核来完成，即一次用户态到内核态的切换和一次内核态到用户态的切换。因为进程上下文切换保存的信息更多，所以进程切换代价会比线程切换代价更大。
* 协程是一个纯用户态的并发机制，同一时刻只会有一个协程在运行，其他协程挂起等待；不同协程之间的切换不涉及内核，只用在用户态切换即可，所以切换代价更小，更轻量级，适合IO密集型的场景。

## kotlin协程
在JVM的平台上，并灭有提供对协程的原生支持，完全依赖编译器技术支持。Kotlin协程在代码层面实现要基于线程池的工具API,所以Kotlin协程不属于广义上的协程，更像是一个线程框架。

## kotlin协程原理
* 挂起函数：suspend修饰标记的函数。挂起函数不能再常规代码中被调用，只能在其他挂起函数或是挂起lambda表达式中
* 协程构建器：使用一些挂起lambda表达式作为参数来创建的一个协程的函数，如launch(),async()

### CPS(Continuation-Passing-Style Transformation)
	//编译前
	suspend fun request():Int
	//编译后
	fun requesT(continuation:Continuation<T>):Any?

* suspend函数都有一个Continuation类型的隐式参数，并且都能通过这个参数拿到一个Continuation类型对象
* 返回类型变成Any,因为挂起函数被花旗，会返回COROUTINE_SUSPENDED;否则执行完毕直接返回一个结果或是异常，需要一个联合标识

### Continutiaon续体,表示挂起协程的在挂起点时的状态，可以用“剩余计算”来称呼。
剩余计算

	val str =1024.toString()//#1
	val length=str.length//#2
	println(length)//#3

加入吧#1，#2和#3分成两部分代码段，#2，#3的代码可以合并表示为println(x.length),然后改写成一个lambda表达式

	{s:String->println(s.length)}
这样，我们就可以通过把#1的结果传递给lambda来重新构建原始的形式：

	{s:String->println(s.length)}.invoke(1024.toString())
这个lambda表达式就是#1的Continuation,通过它的invoke方法可以执行剩余计算

### 状态机
挂起点：协成执行过程中可能被挂起的位置。可理解为剩余计算各种可能点。每一个挂起点和初始挂起点对应的Continuation都会转为为一种状态，协程恢复只是跳到下一种状态中。

协程在挂起前，会先保存所有的局部变量以及在下次resume后要执行的代码片段（根据lable的值判断），这个保存状态和局部变量的对象就叫状态机
考虑用状态机来实现协程是尽可能少的创建类和对象

### 总结
1.在执行suspend函数时，（CPS传递Continuation参数）挂起，暂时不执行剩下的协程代码

2.当suspend函数执行完毕，通过Continuation参数的resume()进行回调，继续执行
	
	@SinceKotlin("1.3")
	public interface Continuation<in T> {
	    /**
	     * The context of the coroutine that corresponds to this continuation.
	     */
	    public val context: CoroutineContext
	
	    /**
	     * Resumes the execution of the corresponding coroutine passing a successful or failed [result] as the
	     * return value of the last suspension point.
	     */
	    public fun resumeWith(result: Result<T>)
	}
	
	/**
	 * Classes and interfaces marked with this annotation are restricted when used as receivers for extension
	 * `suspend` functions. These `suspend` extensions can only invoke other member or extension `suspend` functions on this particular
	 * receiver and are restricted from calling arbitrary suspension functions.
	 */
	@SinceKotlin("1.3")
	@Target(AnnotationTarget.CLASS)
	@Retention(AnnotationRetention.BINARY)
	public annotation class RestrictsSuspension
	
	/**
	 * Resumes the execution of the corresponding coroutine passing [value] as the return value of the last suspension point.
	 */
	@SinceKotlin("1.3")
	@InlineOnly
	public inline fun <T> Continuation<T>.resume(value: T): Unit =
	    resumeWith(Result.success(value))
	
	/**
	 * Resumes the execution of the corresponding coroutine so that the [exception] is re-thrown right after the
	 * last suspension point.
	 */
	@SinceKotlin("1.3")
	@InlineOnly
	public inline fun <T> Continuation<T>.resumeWithException(exception: Throwable): Unit =
	    resumeWith(Result.failure(exception))

# 空安全
不可空

	//在其方法体中我们获取了传入字符串的长度
	fun m1(str: String) {
	    str.length
	}

字节码可知，该方法的入参会被加上非空注解，之后，kotlin编译器内部调用了是否为null的检查，这就是为什么我们传入null的时候会编译报错

	public final static m1(Ljava/lang/String;)V
	    @Lorg/jetbrains/annotations/NotNull;() // invisible, parameter 0
	   L0
	    ALOAD 0
	    LDC "str"
	    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkParameterIsNotNull (Ljava/lang/Object;Ljava/lang/String;)V
	   L1
	    LINENUMBER 6 L1
	    ALOAD 0
	    INVOKEVIRTUAL java/lang/String.length ()I
	    POP
	   L2
	    LINENUMBER 7 L2
	    RETURN
	   L3
	    LOCALVARIABLE str Ljava/lang/String; L0 L3 0
	    MAXSTACK = 2
	    MAXLOCALS = 1

可空

	//在其方法体中我们采用了安全调用操作符 ?. 来获取传入字符串的长度
	fun m2(str: String?) {
	    str?.length
	}
字节码，m2的入参被加上了可为null的注解，kotlin编译器对该场景做了如下处理：如果为null则什么都不做，否则直接调用str的length方法
	// 
	  public final static m2(Ljava/lang/String;)V
	    @Lorg/jetbrains/annotations/Nullable;() // invisible, parameter 0
	   L0
	    LINENUMBER 10 L0
	    ALOAD 0
	    DUP
	    IFNULL L1
	    INVOKEVIRTUAL java/lang/String.length ()I
	    POP
	    GOTO L2
	   L1
	    POP
	   L2
	   L3
	    LINENUMBER 11 L3
	    RETURN
	   L4
	    LOCALVARIABLE str Ljava/lang/String; L0 L4 0
	    MAXSTACK = 2
	    MAXLOCALS = 1

强制非空

	fun m3(str: String?) {
    	str!!.length
	}
字节码入参同样被标注为了可为null，传入为null的字符串直接抛出空指针异常，否则调用其length方法

	public final static m3(Ljava/lang/String;)V
	    @Lorg/jetbrains/annotations/Nullable;() // invisible, parameter 0
	   L0
	    LINENUMBER 15 L0
	    ALOAD 0
	    DUP
	    IFNONNULL L1
	    INVOKESTATIC kotlin/jvm/internal/Intrinsics.throwNpe ()V
	   L1
	    INVOKEVIRTUAL java/lang/String.length ()I
	    POP
	   L2
	    LINENUMBER 16 L2
	    RETURN
	   L3
	    LOCALVARIABLE str Ljava/lang/String; L0 L3 0
	    MAXSTACK = 3
	    MAXLOCALS = 1