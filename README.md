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

# 难点重点
## 类构造函数，初始化，属性构造
### 嵌套类，内部类inner，匿名内部类
## 泛型
## 协程
Continutiaon续体表示挂起协程的在挂起点时的状态，可以用“剩余计算”来称呼。
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

### 状态机
协程在挂起前，会先保存所有的局部变量以及在下次resume后要执行的代码片段（根据lable的值判断），这个保存状态和局部变量的对象就叫状态机
考虑用状态机来实现协程是尽可能少的创建类和对象
