import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlinx.coroutines.channels.Channel
import kotlin.system.measureTimeMillis


fun main() {

    runBlocking {
        launch {
            work()
        }
        println("Hello")
        withTimeout(3000L){
            repeat(3){
                println("with time out $it")
            }
        }

        withTimeoutOrNull(3000L){

        }
    }
    println("end")


    //Cancellation and timeouts
    thread {
        runBlocking {
            val job = launch {
                try {
                    repeat(1000){ i ->
                        println("job: I'm sleeping $i ...")
                        delay(500L)
                    }
                }finally {
                    println("I'm running finally")
                    //Run non-cancellable block
                    withContext(NonCancellable){
                        println("I'm running non cancellable finally")
                        delay(1000L)
                        println("I've just delayed for 1 sec because I'm non-cancellable")
                    }
                }
            }
            delay(500L)
            println("I'm tried of waiting")
//            job.cancel()
//            job.join()
            job.cancelAndJoin()
            println("Now I can quit")
        }
    }

    thread {
        runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (isActive) { // computation loop, just wastes CPU
                    // print a message twice a second
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }
    }

//    GlobalScope.async {
//
//    }

    runBlocking {
        val time = measureTimeMillis {
            println("The answer is ${concurrentSum()}")
        }
        println("Completed in $time ms")

        try {
            failedConcurrentSum()
        } catch(e: ArithmeticException) {
            println("Computation failed with ArithmeticException")
        }
    }
}

suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE) // Emulates very long computation
            42
        } finally {
            println("First child was cancelled")
        }
    }
    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }
    one.await() + two.await()
}

suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}

fun startCoroutines(){
    thread {
        runBlocking {
            //Channel 就像一个 线程安全的队列
            /**
             * | 类型                                 | 特点                                   |
             * | ---------------------------------- | ------------------------------------ |
             * | **Rendezvous Channel** (默认)        | 没有缓冲区，`send` 会挂起，直到接收方 `receive` 来接收 |
             * | **Buffered Channel**               | 有缓冲区，可以暂存 N 个元素，发送可能不会立即挂起           |
             * | **Conflated Channel**              | 只保留最新一个元素，旧的被覆盖                      |
             * | **Unlimited / LinkedList Channel** | 无限缓冲，不会挂起发送（除非内存耗尽）                  |
             *
             */
            val channel = Channel<Int>(capacity = 2)

            val job = launch {
                delay(1000L)
                println("running --->")
                channel.send(123243423)
            }

            job.join()
            val job2 = launch {
                val result = channel.receive()
                println("launch run ---> $result")
            }

            println("job run end")

            val deferred = async {
                delay(1000L)
                println("async running --->")
                1000
            }
            println("value:-->${deferred.await()}")



            val deferredList: List<Deferred<Int>> = (1..3).map {
                async {
                    delay(1000L * it)
                    println("Loading $it")
                    it
                }
            }

            println("sum: ${deferredList.awaitAll().sum()}")


        }
    }
}

suspend fun work() {
    delay(1000L)
    println("world!")
}


interface SendChannel<in E> {
    suspend fun send(element: E)
    fun close(): Boolean
}

interface ReceiveChannel<out E> {
    suspend fun receive(): E
}

interface Channel<E> : SendChannel<E>, ReceiveChannel<E>


