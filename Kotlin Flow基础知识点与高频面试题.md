# Kotlin Channel,Flow基础知识点与高频面试题
## 一、Flow基础概念
Flow是基于协程实现的异步数据流框架，可依次发射多个值，具备冷流特性：若无收集者，上游代码不会执行；每次触发收集都会完整执行一次生产者逻辑。
1. 分类
    - 普通Flow：无状态，每次collect都会重执行生产逻辑，仅主动发送数据。
    - StateFlow：热流，始终保存最新值，新订阅者可立刻获取缓存值，必须设置初始值，仅有一个订阅者时无需配置额外参数，多用于UI状态托管。
    - SharedFlow：热流，支持多订阅，可自定义重放数值、缓冲区策略，无强制初始值，适配一次性事件。
2. 核心构成
    生产者：flow{}、MutableStateFlow、MutableSharedFlow等构建数据流；
    中间运算符：转换、过滤数据流，属于惰性执行，如map、filter、flatMap系列；
    末端运算符：触发数据流执行，如collect、collectLatest、first等。
3. 冷流与热流区分
    冷流：flow构建的常规Flow，订阅时才运行，多订阅会多次执行上游逻辑，无内存常驻数据；
    热流：StateFlow、SharedFlow，创建后常驻内存发射数据，订阅与否都会运行，多订阅共享同一份数据源。

### 1.1 冷流：不收集不执行，重复收集重复执行

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    val cold = flow {
        println("生产者执行")
        emit(1)
        emit(2)
    }
    println("创建 flow 对象，此时生产者还没有运行")
    cold.collect { println("第一次收集 $it") }
    cold.collect { println("第二次收集 $it") }     // 第二次收集会重新执行一遍上游
}
```

输出：

```
创建 flow 对象，此时生产者还没有运行
生产者执行
第一次收集 1
第一次收集 2
生产者执行
第二次收集 1
第二次收集 2
```

### 1.2 三类流的使用方式

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // 普通 Flow：冷流，collect 时执行，收完自动结束
    flowOf(1, 2, 3).collect { println("普通 Flow 收到 $it") }

    // StateFlow：热流，必须给初始值，始终缓存最新值；收集永不结束，take(1) 收一次即停
    val state = MutableStateFlow("加载中")
    state.value = "加载成功"
    println("StateFlow 不订阅也能读：${state.value}")
    state.take(1).collect { println("新订阅者立刻拿到缓存值 $it") }

    // SharedFlow：热流，支持多订阅；replay = 1 表示给新订阅者重放最近 1 条
    val shared = MutableSharedFlow<String>(replay = 1)
    shared.emit("最新通知")
    shared.take(1).collect { println("SharedFlow 新订阅者收到重放值 $it") }
}
```

输出：

```
普通 Flow 收到 1
普通 Flow 收到 2
普通 Flow 收到 3
StateFlow 不订阅也能读：加载成功
新订阅者立刻拿到缓存值 加载成功
SharedFlow 新订阅者收到重放值 最新通知
```

### 1.3 冷流与热流的多订阅对比

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // 冷流：两个订阅者各自触发一次完整执行，生产逻辑跑了两遍
    val cold = flow {
        println("冷流上游执行")
        emit("数据")
    }
    launch { cold.collect { println("订阅者 A 收到 $it") } }
    launch { cold.collect { println("订阅者 B 收到 $it") } }
    delay(50)
    println("---")

    // 热流：只有一份数据源，多个订阅者共享，同一值各收到一份
    val hot = MutableStateFlow("当前状态")
    launch { hot.collect { println("订阅者 C 收到 $it") } }
    launch { hot.collect { println("订阅者 D 收到 $it") } }
    delay(50)
    hot.value = "状态更新"
    delay(50)                                    // 等订阅者收到更新
    println("C、D 收到的是同一条更新")
    coroutineContext.cancelChildren()            // 热流收集永不结束，退出前取消子协程
}
```

输出：

```
冷流上游执行
订阅者 A 收到 数据
冷流上游执行
订阅者 B 收到 数据
---
订阅者 C 收到 当前状态
订阅者 D 收到 当前状态
订阅者 C 收到 状态更新
订阅者 D 收到 状态更新
C、D 收到的是同一条更新
```

## 二、常用运算符
1. 转换类
map：逐个转换发射的数据；
transform：灵活发送多个值，自由度高于map；
flatMapConcat：串行处理上游数据，等上一条处理完毕再执行下一条；
flatMapMerge：并发处理多条上游数据；
flatMapLatest：新数据抵达时取消上一条正在执行的任务，搜索场景核心算子。
2. 过滤类
filter：筛选满足条件的数据；
distinctUntilChanged：过滤连续重复的值，StateFlow自带该特性；
take(n)：仅获取前n条数据。
3. 生命周期管控
repeatOnLifecycle：绑定页面生命周期，生命周期不达标时直接取消收集释放资源；
launchWhenStarted：仅暂停协程，上游持续运行，存在资源损耗。
4. 收集算子
collect：完整接收每一条数据；
collectLatest：丢弃未执行完的旧任务，只执行最新数据逻辑。

### 2.1 map 与 transform

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    flowOf(1, 2, 3)
        .map { it * it }                          // 一对一转换
        .collect { println("map 结果 $it") }

    flowOf("A", "B")
        .transform { value ->                     // 一对多，想发几条发几条
            emit("$value-开始")
            emit("$value-结束")
        }
        .collect { println("transform 结果 $it") }
}
```

输出：

```
map 结果 1
map 结果 4
map 结果 9
transform 结果 A-开始
transform 结果 A-结束
transform 结果 B-开始
transform 结果 B-结束
```

### 2.2 flatMapConcat / flatMapMerge / flatMapLatest

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    fun task(value: Int) = flow {
        println("开始处理 $value")
        delay(100)
        emit("处理完 $value")
    }

    println("flatMapConcat：串行排队，下一个要等上一个跑完（总耗时约 300ms）")
    flowOf(1, 2, 3).flatMapConcat { task(it) }.collect { println(it) }

    println("flatMapMerge：并发执行，三个同时跑（总耗时约 100ms）")
    flowOf(1, 2, 3).flatMapMerge { task(it) }.collect { println(it) }
}
```

输出：

```
flatMapConcat：串行排队，下一个要等上一个跑完（总耗时约 300ms）
开始处理 1
处理完 1
开始处理 2
处理完 2
开始处理 3
处理完 3
flatMapMerge：并发执行，三个同时跑（总耗时约 100ms）
开始处理 1
开始处理 2
开始处理 3
处理完 1
处理完 2
处理完 3
```

flatMapLatest：新数据一来就取消上一条正在跑的流，只保留最新的一个，搜索防抖就是靠它：

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    flow {
        emit("k"); delay(50)                      // 模拟用户快速输入
        emit("ko"); delay(50)
        emit("kot"); delay(50)
        emit("kotlin")
    }.flatMapLatest { keyword ->
        flow {
            println("发起 [$keyword] 的请求")
            delay(80)                             // 模拟 80ms 的网络请求
            emit("关键词 [$keyword] 的搜索结果")
        }
    }.collect { println(it) }
}
```

输出：

```
发起 [k] 的请求
发起 [ko] 的请求
发起 [kot] 的请求
发起 [kotlin] 的请求
关键词 [kotlin] 的搜索结果
```

前三个请求都在返回前被新关键词取消，只有最后一个能返回并渲染，所以不用再手写防抖延时。

### 2.3 filter / distinctUntilChanged / take

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    flowOf(1, 2, 3, 4, 5, 6)
        .filter { it % 2 == 0 }
        .collect { print("$it ") }
    println()                                     // 2 4 6

    flowOf(1, 1, 2, 2, 2, 3, 1)
        .distinctUntilChanged()                   // 只去连续重复，不保证全局唯一
        .collect { print("$it ") }
    println()                                     // 1 2 3 1

    flowOf(1, 2, 3, 4, 5)
        .take(2)                                  // 取够 n 条后自动取消上游
        .collect { print("$it ") }
    println()                                     // 1 2
}
```

输出：

```
2 4 6
1 2 3 1
1 2
```

### 2.4 生命周期感知收集（Android 代码，不能脱离 Android 运行）

```kotlin
// 依赖 androidx.lifecycle:lifecycle-runtime-ktx
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state -> render(state) }   // 低于 STARTED 直接取消收集，回到 STARTED 重新收集
    }
}
```

launchWhenStarted 只是把协程挂起，上游照跑不误，资源照样消耗，已经废弃，不要再用。

### 2.5 collect 与 collectLatest

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    fun source() = flow {
        emit(1); delay(30)
        emit(2); delay(30)
        emit(3)
    }

    source().collect { value ->
        delay(50)                                 // 每条都要处理 50ms，比发射间隔长
        println("collect 处理完 $value")
    }
    println("---")
    source().collectLatest { value ->
        delay(50)                                 // 新数据一来，上一轮没跑完的处理被取消
        println("collectLatest 处理完 $value")
    }
}
```

输出：

```
collect 处理完 1
collect 处理完 2
collect 处理完 3
---
collectLatest 处理完 3
```

collect 一条不落全处理完；collectLatest 前两轮都在 delay 中被新数据打断取消，只有最后一条跑完，避免旧请求返回覆盖新结果。

## 三、StateFlow与SharedFlow详细对比
1. StateFlow
    - 强制初始值，内部永久缓存最新值；
    - 等效自带distinctUntilChanged，相同值不会重复下发；
    - 默认重放数量为1，新订阅者直接拿到缓存状态；
    - 适合界面状态：加载中、成功、失败、列表数据等，屏幕旋转可恢复状态。
2. SharedFlow
    - 无需初始值，默认无缓存；
    - 可自定义replay重放条数、extraBufferCapacity缓冲区；
    - 相同值允许重复推送，不会自动去重；
    - 适配一次性事件：弹窗、页面路由、吐司，规避重建重复消费问题。
3. 一次性事件解决方案
若强行用StateFlow传递事件，配置包装类Event<T>，通过isConsumed标记事件是否已消费；更简洁方案是使用SharedFlow，replay设为0避免重建重放。

### 3.1 StateFlow：初始值、缓存值与自带去重

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    val state = MutableStateFlow("加载中")          // 必须给初始值
    val job = launch {
        state.collect { println("订阅者收到 $it") }  // 先收到缓存值，之后持续接收更新
    }
    yield()                                        // 先让订阅者订阅上

    state.value = "成功"
    yield()
    state.value = "成功"                            // 值相同，自带 distinctUntilChanged，不会重复下发
    yield()

    println("不订阅也能读：${state.value}")
    job.cancel()                                   // 收集永不结束，要手动取消
}
```

输出：

```
订阅者收到 加载中
订阅者收到 成功
不订阅也能读：成功
```

### 3.2 SharedFlow：多订阅、不重放历史事件

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // 一次性事件的推荐配置：不重放、1 条缓冲、缓冲满时丢最旧的
    val events = MutableSharedFlow<String>(
        replay = 0,                                // 新订阅者拿不到历史事件
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    events.emit("订阅前的旧事件")                   // replay = 0，没有订阅者时直接丢弃

    val a = launch { events.collect { println("订阅者 A 收到 $it") } }
    yield()
    events.emit("弹窗：网络异常")
    yield()

    val b = launch { events.collect { println("订阅者 B 收到 $it") } }
    yield()
    events.emit("跳转：详情页")                     // A、B 各收到一份
    yield()

    a.cancel(); b.cancel()
}
```

输出：

```
订阅者 A 收到 弹窗：网络异常
订阅者 A 收到 跳转：详情页
订阅者 B 收到 跳转：详情页
```

和 StateFlow 不同，SharedFlow 不去重，连续发两次"跳转：详情页"，两个订阅者都会各收到两次；A 是在旧事件之后才订阅的，所以拿不到旧事件，正好满足"一次性事件不重放"的要求。

### 3.3 一次性事件：Event 包装类

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class Event<T>(private val content: T) {
    var isConsumed = false
        private set
    fun consume(): T? = if (isConsumed) null else { isConsumed = true; content }
}

fun main() = runBlocking {
    val event = Event("弹出登录过期提示")
    println("第一次消费：${event.consume()}")       // 弹窗正常弹出
    println("第二次消费：${event.consume()}")       // null，已消费，屏幕旋转重建也不会重复弹
}
```

输出：

```
第一次消费：弹出登录过期提示
第二次消费：null
```

## 四、Channel

Channel 是协程间的并发安全消息队列（协程版 BlockingQueue），遵循 CSP 模型：不靠共享内存来通信，而是靠通信来共享内存。与 Flow 的区别：Channel 是热流、点对点，一个元素只会被一个接收者消费，适合协程通信与任务分发；Flow 面向数据流变换，普通 Flow 是冷流且支持多订阅。

### 4.1 容量与迭代

Channel(capacity) 的容量决定 send 的行为：

| capacity 常量 | 值 | 行为 |
| --- | --- | --- |
| RENDEZVOUS | 0（默认） | 无缓冲区，send 挂起直到有接收者取走 |
| BUFFERED | -2 | 使用默认缓冲 64（可通过 `kotlinx.coroutines.channels.defaultBuffer` 属性调整），满则 send 挂起 |
| CONFLATED | -1 | 缓冲区只保留最新值，旧值被覆盖，send 永不挂起 |
| UNLIMITED | Int.MAX_VALUE | 无界队列，send 永不挂起，注意内存风险 |
| 正整数 n | n | 缓冲 n 条，满则 send 挂起 |

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    val channel = Channel<Int>()            // 默认 RENDEZVOUS，无缓冲区
    launch {
        repeat(3) { channel.send(it) }      // 没有接收者时挂起
        channel.close()
    }
    for (value in channel) {                // 迭代直到 Channel 关闭，循环自然结束
        println("收到 $value")
    }
}
```

输出：

```
收到 0
收到 1
收到 2
```

迭代：`for (x in channel)` 是最常用的消费方式，等价于循环 `receive()` 并在关闭时退出；`consumeEach {}` 也可以，区别是它会在消费结束后取消 Channel。

### 4.2 produce 与 actor

produce = launch + Channel + 自动关闭：启动生产者协程并返回 ReceiveChannel，block 执行完（或异常）自动 close；actor = launch + Channel：启动消费者协程并返回 SendChannel。二者都受结构化并发约束，父作用域取消则一起取消，未捕获异常按 launch 规则向上传播。

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun main() = runBlocking {
    val actor = actor<Int> {                     // 消费者协程，返回 SendChannel
        for (value in channel) {                 // 消费到 Channel 关闭
            println("处理 $value")
        }
    }
    repeat(3) { actor.send(it) }
    actor.close()

    val producer = produce {                     // 生产者协程，返回 ReceiveChannel
        repeat(3) { send(it * it) }
    }                                            // 生产完自动 close
    for (value in producer) {
        println("消费 $value")
    }
}
```

输出：

```
处理 0
处理 1
处理 2
消费 0
消费 1
消费 4
```

- produce/actor 的默认 capacity 均为 RENDEZVOUS，实际使用建议显式指定容量；
- send/receive 是挂起函数，trySend/tryReceive 是即试的非挂起版本，返回 ChannelResult；
- 注意 API 状态：produce 目前标注 @ExperimentalCoroutinesApi，actor 标注 @ObsoleteCoroutinesApi，使用时要加 @OptIn（新代码更推荐直接用 Channel() 加 launch）。

### 4.3 Channel 的关闭

- close()：关闭发送端且幂等，之后 isClosedForSend 为 true，再 send 抛 ClosedSendChannelException；
- 缓冲区中尚未取走的数据仍会被消费完，取空后再 receive 抛 ClosedReceiveChannelException，`for` 循环正常结束；
- close(cause)：带异常关闭，接收方会抛出该异常；用 receiveCatching() 拿 ChannelResult 判断（isSuccess/isClosed/exceptionOrNull），可优雅处理关闭而不抛异常；
- cancel()：立即取消，缓冲区数据直接丢弃，接收方抛 CancellationException；
- produce 中生产者抛异常，框架会用该异常关闭 Channel；
- 用 isClosedForReceive/isClosedForSend 判断状态（这两个是 delicate API，需 @OptIn(DelicateCoroutinesApi::class)），invokeOnClose {} 监听关闭，produce 中可用 awaitClose {} 做关闭清理（callbackFlow 必须调用）。

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    val channel = Channel<Int>(Channel.BUFFERED)
    launch {
        repeat(3) { channel.send(it) }
        channel.close()                     // 关闭发送端
    }
    for (value in channel) {
        println("取出 $value")              // 关闭前缓冲的数据仍能取完
    }
    println("for 循环正常结束，没有抛异常")
    // 关闭后再 send 会抛 ClosedSendChannelException
    // 取空后再 receive 会抛 ClosedReceiveChannelException
}
```

输出：

```
取出 0
取出 1
取出 2
for 循环正常结束，没有抛异常
```

### 4.4 BroadcastChannel

广播通道：一个元素会分发给所有订阅者（各自收到一份），用于一对多事件分发，openSubscription() 获取订阅通道。该 API 自 1.7.0 起已废弃（WARNING 级别），官方推荐用 SharedFlow/StateFlow 替代，新代码不要使用：

- 广播事件：MutableSharedFlow(replay = 0, extraBufferCapacity = 1)；
- 需要向新订阅者重放最新状态：StateFlow。

## 五、多路复用

select 是协程的多路复用：同时等待多个挂起事件，谁先就绪就执行哪个分支，类比 Java NIO 的 Selector、Go 的 select。select 是挂起函数，由若干子句（SelectClause）组成，命中一个后其余注册自动撤销。

1. 复用多个 await

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.*

fun main() = runBlocking {
    val a = async { delay(300); "A" }
    val b = async { delay(100); "B" }
    val result = select<String> {
        a.onAwait { it }                         // A 要 300ms
        b.onAwait { it }                         // B 只要 100ms
    }
    println("先返回的是 $result")
}
```

输出：

```
先返回的是 B
```

- 谁先完成取谁，已完成的 Deferred 会立即命中；
- 未命中的 Deferred 不会被取消，之后仍可正常 await（这里 a 仍会跑完，runBlocking 会等它）。

2. 复用多个 Channel

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.*

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val a = produce { delay(300); send("A 的数据") }
    val b = produce { delay(100); send("B 的数据") }
    val result = select<String> {
        a.onReceive { it }                       // A 要 300ms
        b.onReceive { it }                       // B 只要 100ms
    }
    println("先收到 $result")
    a.cancel()                                   // 没被选中的生产者要取消，否则它的 send 会一直挂起

    val slow = produce<String> { delay(500); send("慢") }
    val timeout = select<String> {
        slow.onReceive { it }
        onTimeout(200) { "等待超时" }            // select 还能搭配超时分支
    }
    println(timeout)
    slow.cancel()
}
```

输出：

```
先收到 B 的数据
等待超时
```

- onReceive 是 SelectClause1；Channel 已关闭时该子句会抛异常，改用 onReceiveCatching {}（拿到 ChannelResult）可把"关闭"也作为结果参与竞争；
- 发送端同样能 select：channel.onSend(value) {} 是 SelectClause2，哪个 Channel 先腾出空位就往哪发；
- 注意：没被选中的生产者如果不取消，它后面的 send 会一直挂起，runBlocking 会永远等下去。

3. SelectClause

select 的子句按返回值形态分三类：

- SelectClause0：无返回值，如 job.onJoin、onTimeout {}；
- SelectClause1：一个返回值，如 deferred.onAwait、channel.onReceive；
- SelectClause2：一个返回值加一个额外参数，如 channel.onSend(value)、mutex.onLock；
- select 至少需要一个子句；只有 else 分支时直接执行 else 不挂起；所有子句都不可用且无 else 时挂起等待；
- select 等待期间可被取消（抛 CancellationException）。

4. Flow 实现多路复用

Flow 没有可参与 select 的子句，需要降级到 Channel 或改用合并算子：

- flow.produceIn(scope)：把 Flow 转成 ReceiveChannel，之后就能用 onReceive 参与 select（注意作用域结束要 cancel）；
- merge(flowA, flowB)：并发收集多个流，谁先发射谁先到，只关心合并结果时最简单；
- combine/zip 用于组合而非竞争：combine 取各流最新值两两组合，zip 按顺序配对。

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    val flowA = flow {
        emit("A1")
        delay(200)
        emit("A2")
    }
    val flowB = flow {
        delay(100)
        emit("B1")
    }
    merge(flowA, flowB).collect { println(it) }
}
```

输出：

```
A1
B1
A2
```

## 六、并发安全

协程在单线程调度器下天然串行，但在多线程调度器或跨线程切换时，共享可变状态仍会出现竞态。

1. Mutex 互斥锁

- 协程版互斥锁，lock/unlock 是挂起函数，等待锁时挂起协程而非阻塞线程，可替代 synchronized/ReentrantLock；
- 推荐 mutex.withLock {}，异常时也能正确释放；
- Mutex 不可重入：同一协程重复加锁会死锁（synchronized 可重入）；
- 还提供 onLock 子句，可参与 select。

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

fun main() = runBlocking {
    val mutex = Mutex()
    var counter = 0
    val jobs = List(1000) {
        launch(Dispatchers.Default) {
            mutex.withLock { counter++ }         // 同一时刻只有一个协程能进来
        }
    }
    jobs.forEach { it.join() }
    println("counter = $counter")
}
```

输出：

```
counter = 1000
```

去掉 withLock 后 counter 通常会小于 1000（读-改-写不是原子操作），这就是必须加锁的原因。

2. Semaphore 信号量

- 控制同时执行的并发数：acquire() 获取许可，无许可则挂起；release() 归还；
- 推荐 semaphore.withPermit {} 自动归还；
- Mutex 相当于许可数为 1 的 Semaphore，但语义不同：Mutex 强调持有者，Semaphore 不绑定协程、任意协程都能 release，且许可可以有多个；
- 典型场景：限制并发请求数、限流、资源池；tryAcquire 非挂起，拿不到立即返回 false。

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

fun main() = runBlocking<Unit> {
    val semaphore = Semaphore(1)             // 许可数为 1，效果等同互斥；写成 2 就是"最多 2 个并发"
    launch {
        semaphore.withPermit {
            println("A 进入")
            delay(100)                       // 持有许可期间 B 只能等着
            println("A 离开")
        }
    }
    launch {
        semaphore.withPermit {
            println("B 进入")
            println("B 离开")
        }
    }
}
```

输出：

```
A 进入
A 离开
B 进入
B 离开
```

限制并发请求数就是把许可数设为上限，例如 `Semaphore(5)` 表示同时最多 5 个请求在跑。

3. 其他并发安全手段

- 单线程约束：Dispatchers.Default.limitedParallelism(1)，把并发改为串行；
- 原子类：AtomicInteger/AtomicReference 的 CAS；
- StateFlow 用 update {}（内部 CAS 重试），不要写 value = value + 1（读-改-写非原子）；
- volatile 只保证可见性，不保证复合操作原子性。

## 七、高频面试问题及解答
1. Flow冷流特性是什么？带来什么优缺点？
Flow属于冷流，只有调用末端收集算子时，上游生产者代码才会执行，每一次collect都会从头执行生产逻辑。优点是节约资源，无订阅时不会发起网络、数据库请求；缺点是多次collect会重复执行耗时操作，多订阅场景推荐改用SharedFlow。

2. Flow和LiveData区别？
LiveData是生命周期感知的热数据流，依附Android框架，仅支持单个值存储，只能在主线程更新；Flow基于协程，跨平台可用，支持多值连续发射，具备丰富运算符，可灵活调度线程。StateFlow可替代LiveData，兼具生命周期感知与协程优势，且LiveData存在转换繁琐、异步能力薄弱的短板。

3. StateFlow为什么必须提供初始值？
StateFlow的设计初衷是承载界面当前状态，页面初始化时界面必须拿到一个有效状态，因此强制初始化。而SharedFlow面向瞬时事件，不存在初始状态的需求，无该约束。

4. 使用SharedFlow传递事件时replay、buffer、onBufferOverflow参数如何配置？
replay代表向新订阅者重放历史数据条数，事件场景设0，防止页面重建重复推送弹窗；extraBufferCapacity设置缓冲区大小，应对短时间高频发送；onBufferOverflow配置缓冲区溢出策略，可选丢弃最新、丢弃最旧、挂起阻塞。常规事件场景配置：MutableSharedFlow(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)。

5. repeatOnLifecycle优于launchWhenStarted的原因？
launchWhenStarted仅暂停协程，上游数据流持续运行，网络请求、数据库查询不会中断，持续消耗流量与内存；repeatOnLifecycle在生命周期低于指定状态时直接取消协程，上游同步停止执行，彻底释放资源，杜绝无效请求，避免内存泄漏。

6. collectLatest工作原理，适用场景？
每次上游发射新数据，collectLatest会取消上一轮未完成的收集逻辑，只运行最新数据对应的代码。适用于输入实时校验、列表下拉刷新、接口防抖，防止旧请求延迟返回覆盖最新结果。

7. flatMapLatest实现搜索防抖的原理？
上游每收到新的搜索关键词，flatMapLatest会取消上一个关键词发起的网络请求，立刻发起新检索，最终仅有最新关键词的请求能够返回并渲染页面，省去手动防抖延时逻辑。

8. Flow切换线程的两种方式flowOn与dispatchIn/collect的区别？
flowOn作用于上游整个数据流，修改生产者执行的调度器，多次调用flowOn分段切换线程；collect、launch中指定的调度器仅作用于下游收集逻辑，不会影响上游生产代码。

9. StateFlow没有生命周期感知，如何安全在UI层收集？
借助viewModelScope.launch搭配repeatOnLifecycle绑定Fragment/Activity生命周期，页面进入STOPED状态自动取消收集；若直接collect，页面销毁后数据流持续持有页面引用，极易引发内存泄漏。

10. Flow如何处理异常？try-catch、catch算子区别？
在flow{}内部使用try-catch可捕获单段逻辑异常；catch中间运算符捕获上游抛出的异常，同时可发送备用值，不会中断整个数据流。末端collect中捕获异常仅能捕获下游收集阶段错误，无法拦截上游生产异常。

11. MutableStateFlow赋值用value和emit的差异？
value是同步赋值，主线程子线程均可调用；emit是挂起函数，只能在协程内调用。二者都会更新缓存状态并分发数据，无本质效果区别，仅调用环境不同。

12. 多订阅普通Flow会出现什么问题？怎么解决？
普通冷Flow每次订阅都会重新执行上游逻辑，多订阅会重复发起多次请求。解决方案：使用shareIn转换为SharedFlow或者stateIn转为StateFlow，转化为热流实现多订阅共享数据源。

13. shareIn、stateIn三个参数（scope、started、initialValue/replay）如何选择？
started参数三种枚举：
- SharingStarted.Eagerly：作用域创建后立刻启动数据流，常驻消耗资源；
- SharingStarted.Lazily：首个订阅者出现时启动，最后一个订阅者取消后持续持有；
- WhileSubscribed：无订阅者时停止上游，释放资源，UI场景首选。

14. distinctUntilChanged的作用，StateFlow是否自带？
该算子过滤连续重复的值，避免重复刷新UI。StateFlow分发数据时内部自动执行distinctUntilChanged，值无变更不会通知订阅者；SharedFlow默认没有，需要手动添加。

15. Flow会不会造成内存泄漏？常见泄漏场景？
会。常见场景：未绑定生命周期直接collect、作用域提前销毁但数据流未取消、全局SharedFlow持有页面实例。规避方案：repeatOnLifecycle绑定生命周期、使用viewModelScope管控数据流、页面销毁主动关闭订阅。

16. Channel与Flow的区别？
Channel是热的有缓冲并发队列，点对点：一个元素只会被一个接收者消费，收发都是挂起函数，用于协程间通信与任务分发；Flow面向数据流，普通Flow是冷流，一个数据可被多个订阅者各自收集，算子丰富，适合数据变换与UI状态。需要"传递"用Channel，需要"变换、订阅"用Flow；channelFlow/callbackFlow是二者的桥接。

17. Channel的容量类型有哪些，send何时挂起？
RENDEZVOUS(0)：无缓冲，send必须等到有接收者；BUFFERED(-2)：默认64条缓冲，满则挂起；CONFLATED(-1)：只保留最新值，send永不挂起，旧值被覆盖；UNLIMITED(Int.MAX_VALUE)：无界队列，send永不挂起但要注意内存；正整数n：缓冲n条，满则挂起。RENDEZVOUS能提供最天然的背压。

18. Channel关闭后缓冲区里的数据还能取到吗？
能。close()只关闭发送端，剩余数据仍会被接收方依次消费，取空后再receive抛ClosedReceiveChannelException，for循环正常结束；若close(cause)带异常，接收方会抛出该异常，用receiveCatching()拿ChannelResult可优雅处理而不抛异常。

19. produce与actor的作用和区别？
produce启动生产者协程返回ReceiveChannel供别处消费，actor启动消费者协程返回SendChannel供别处投递消息；本质都是"launch+Channel"，共享结构化并发与异常传播规则，block执行完自动关闭Channel。produce/actor默认容量都是RENDEZVOUS，建议显式指定。

20. 为什么在协程里用Mutex而不是synchronized？
synchronized会阻塞线程，而协程挂起时本应释放线程去执行其他任务，用synchronized容易造成线程浪费甚至死锁；Mutex.lock是挂起函数，等待时不占用线程，withLock还能自动释放。注意Mutex不可重入，同一协程重复加锁会死锁。

21. Semaphore和Mutex的区别与适用场景？
Mutex是互斥量，许可数为1，保证临界区串行执行；Semaphore可指定多个许可，用于限制并发数（如同时最多5个请求）实现限流。Mutex有明确的持有者语义，Semaphore不绑定协程、任意协程都可release。二者等待时都挂起而不阻塞线程。

22. BroadcastChannel为什么被废弃？替代方案是什么？
BroadcastChannel缓存与背压语义含糊，且长期处于实验状态，1.7.0起已废弃，官方推荐SharedFlow/StateFlow：广播事件用MutableSharedFlow(replay=0, extraBufferCapacity=1)，需要向新订阅者重放最新状态用StateFlow。
