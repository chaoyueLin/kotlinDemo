# Kotlin 基础语法与空安全

## when主语捕获到变量中
	fun Request.getBody() =
		when (val response = executeRequest()) {
			is Success -> response.body
			is HttpError -> throw HttpException(response.status)
		}

## 创建单例

	object Resource {
		val name = "Name"
	}

### 伴生对象
类内部的对象声明可以⽤ companion 关键字标记：

	class MyClass {
		companion object Factory {
			fun create(): MyClass = MyClass()
		}
	}

可以省略伴⽣对象的名称，在这种情况下将使⽤名称 

	class MyClass {
		companion object { }
	}
	val x = MyClass.Companion

### 静态static
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

### by,notNull
	class App : Application() {
		companion object {
			var instance: App by Delegates.notNull()
		}
		override fun onCreate() {
			super.onCreate()
			instance = this
		}
	}

## 非空
### If not null 缩写

	val files = File("Test").listFiles()
	println(files?.size)

### If not null and else 缩写
	val files = File("Test").listFiles()
	println(files?.size ?: "empty")

### if null 执行一个语句
	val values = ……
	val email = values["email"] ?: throw IllegalStateException("Email is missing!")

### if not null 执行代码
	val value = ……
	value?.let {
	…… // 代码会执⾏到此处, 假如data不为null
	}

## 接口

	interface MyInterface {
    	fun onLocationMeasured(location: Location)
	}
	val obj = object : MyInterface {
    	override fun onLocationMeasured(location: Location) { ... }
	}

## 嵌套类，内部类inner，匿名内部类

待补充。

## 泛型

待补充。

## 空安全
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
