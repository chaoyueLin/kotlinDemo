# Kotlin 类初始化与单例模式

## 类构造函数，初始化，属性构造
### lateinit var 

* lateinit var只能用来修饰类属性，不能用来修饰局部变量，并且只能用来修饰对象，不能用来修饰基本类型(因为基本类型的属性在类加载后的准备阶段都会被初始化为默认值)。
* lateinit var的作用也比较简单，就是让编译期在检查时不要因为属性变量未被初始化而报错。
* Kotlin相信当开发者显式使用lateinit var 关键字的时候，他一定也会在后面某个合理的时机将该属性对象初始化的(然而，谁知道呢，也许他用完才想起还没初始化)。

### lazy
lazy 应用于单例模式(if-null-then-init-else-return)，而且当且仅当变量被第一次调用的时候，委托方法才会执行。

lazy()是接受一个 lambda 并返回一个 Lazy <T> 实例的函数，返回的实例可以作为实现延迟属性的委托： 第一次调用 get() 会执行已传递给 lazy() 的 lambda 表达式并记录结果， 后续调用 get() 只是返回记录的结果。

	val lazyValue: String by lazy {
	    println("computed!")
	    "Hello"
	}
	
	fun main(args: Array<String>) {
	    println(lazyValue)
	    println(lazyValue)
	}
	
	打印结果
	computed！
	Hello
	
	Hello


lazy 传入的是一个lambda表达式，其中UnsafeLazyImpl是不安全的，SynchronizedLazyImpl用Synchronize实现安全，SynchronizedLazyImpl是通过AtomicReferenceFieldUpdater实现安全

	public actual fun <T> lazy(mode: LazyThreadSafetyMode, initializer: () -> T): Lazy<T> =
	    when (mode) {
	        LazyThreadSafetyMode.SYNCHRONIZED -> SynchronizedLazyImpl(initializer)SafePublicationLazyImpl
	        LazyThreadSafetyMode.PUBLICATION -> SafePublicationLazyImpl(initializer)
	        LazyThreadSafetyMode.NONE -> UnsafeLazyImpl(initializer)
	    }


	private class SafePublicationLazyImpl<out T>(initializer: () -> T) : Lazy<T>, Serializable {
	    @Volatile private var initializer: (() -> T)? = initializer
	    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
	    // this final field is required to enable safe publication of constructed instance
	    private val final: Any = UNINITIALIZED_VALUE
	
	    override val value: T
	        get() {
	            val value = _value
	            if (value !== UNINITIALIZED_VALUE) {
	                @Suppress("UNCHECKED_CAST")
	                return value as T
	            }
	
	            val initializerValue = initializer
	            // if we see null in initializer here, it means that the value is already set by another thread
	            if (initializerValue != null) {
	                val newValue = initializerValue()
	                if (valueUpdater.compareAndSet(this, UNINITIALIZED_VALUE, newValue)) {
	                    initializer = null
	                    return newValue
	                }
	            }
	            @Suppress("UNCHECKED_CAST")
	            return _value as T
	        }
	
	    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE
	
	    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
	
	    private fun writeReplace(): Any = InitializedLazyImpl(value)
	
	    companion object {
	        private val valueUpdater = java.util.concurrent.atomic.AtomicReferenceFieldUpdater.newUpdater(
	            SafePublicationLazyImpl::class.java,
	            Any::class.java,
	            "_value"
	        )
	    }
	}


### 单例模式
#### 饿汉式实现
	//Java实现
	public class SingletonDemo {
	    private static SingletonDemo instance=new SingletonDemo();
	    private SingletonDemo(){
	
	    }
	    public static SingletonDemo getInstance(){
	        return instance;
	    }
	}
	//Kotlin实现
	object SingletonDemo

#### 懒汉式

	//Java实现
	public class SingletonDemo {
	    private static SingletonDemo instance;
	    private SingletonDemo(){}
	    public static SingletonDemo getInstance(){
	        if(instance==null){
	            instance=new SingletonDemo();
	        }
	        return instance;
	    }
	}
	//Kotlin实现
	class SingletonDemo private constructor() {
	    companion object {
	        private var instance: SingletonDemo? = null
	            get() {
	                if (field == null) {
	                    field = SingletonDemo()
	                }
	                return field
	            }
	        fun get(): SingletonDemo{
	        //细心的小伙伴肯定发现了，这里不用getInstance作为为方法名，是因为在伴生对象声明时，内部已有getInstance方法，所以只能取其他名字
	         return instance!!
	        }
	    }
	}


#### 双重校验锁式（Double Check)

	//Java实现
	public class SingletonDemo {
	    private volatile static SingletonDemo instance;
	    private SingletonDemo(){} 
	    public static SingletonDemo getInstance(){
	        if(instance==null){
	            synchronized (SingletonDemo.class){
	                if(instance==null){
	                    instance=new SingletonDemo();
	                }
	            }
	        }
	        return instance;
	    }
	}
	//kotlin实现
	class SingletonDemo private constructor() {
	    companion object {
	        val instance: SingletonDemo by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
	        SingletonDemo() }
	    }
	}

#### 静态内部类

	//Java实现
	public class SingletonDemo {
	    private static class SingletonHolder{
	        private static SingletonDemo instance=new SingletonDemo();
	    }
	    private SingletonDemo(){
	        System.out.println("Singleton has loaded");
	    }
	    public static SingletonDemo getInstance(){
	        return SingletonHolder.instance;
	    }
	}
	//kotlin实现
	class SingletonDemo private constructor() {
	    companion object {
	        val instance = SingletonHolder.holder
	    }
	
	    private object SingletonHolder {
	        val holder= SingletonDemo()
	    }
	
	}
