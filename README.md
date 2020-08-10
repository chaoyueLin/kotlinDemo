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
## 引用上下文对象的方式
run、with 以及 apply 通过关键字 this 引用上下文对象。let 及 also 将上下文对象作为 lambda 表达式参数用it.
## 返回值
apply 及 also 的返回值是上下文对象本身。因此，它们可以作为辅助步骤包含在调用链中：你可以继续在同一个对象上进行链式函数调用。let、run 及 with 返回 lambda 表达式的结果。
# 难点重点
## 类构造函数，初始化，属性构造
### 嵌套类，内部类inner，匿名内部类
## 泛型
