import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.cancellation.CancellationException

fun simple() : List<Int> = listOf(1, 2, 3)

fun simple2() : Sequence<Int> = sequence {
    for (i in 1..3){
        Thread.sleep(100)
        yield(i)
    }
}

suspend fun simple3(): List<Int> {
    delay(100L)
    return listOf(4,5,6)
}

fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3){
        delay(100L)
        emit(i)
    }
}

fun simpleException(): Flow<String> = flow {
    for (i in 1..3){
        delay(100L)
        emit(i)
    }
}.map {value ->
    check(value <= 1) { "Crashed on $value" }
    "string $value"
}

fun simpleExceptionDeclaratively(): Flow<Int> = flow {
    for (i in 1..3){
        delay(100L)
        emit(i)
    }
}

fun onEvent(): Flow<Int> = (1..3).asFlow().onEach { delay(100L) }

suspend fun performRequest(request: Int) : String {
    delay(1000)
    return "response $request"
}

fun numbers(): Flow<Int> = flow {
    try {
        emit(1)
        emit(2)
        println("This line will not execute")
        emit(3)
    } finally {
        println("Finally in numbers")
    }
}


fun main() {
    simple().forEach { action -> println(action) }

    simple2().forEach { action ->  println(action)}

    runBlocking {
        println("-----")
        simple3().forEach { action ->  println(action)}

        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..3) {
                println("I'm not blocked $k")
                delay(100)
//                Thread.sleep(100L)
            }
        }

        // Collect the flow
        try {
            simpleFlow().collect { value ->
                println(value)
                check(value <= 1){ "Collected $value"}
            }
        }catch (e: Throwable){
            println("Caught: $e")
        }finally {
            println("Flow finally Done!!!")
        }

        simpleException().catch { e -> emit("Caught $e") }.collect { value -> println("simple exception: $value") }

        simpleExceptionDeclaratively().onEach { value ->
            check(value <= 2)
            println("ExceptionDeclaratively value: $value") }.catch { e ->
                println("ExceptionDeclaratively e: $e") }.onCompletion { println("ExceptionDeclaratively Done!!!") }
            .collect { value ->
                println("ExceptionDeclaratively value: $value") }

        (1..3).asFlow().collect { value -> println(value) }

        (1..3).asFlow().map { request -> performRequest(request) }.collect { value -> println(value) }

        (1..3).asFlow().transform { request ->
            emit("Making request $request")
            emit(performRequest(request))
        }.collect { value -> println(value) }

        numbers().take(2).collect { value -> println(value) }

        val numbers = listOf(1, 2, 3, 4)
        // fold: 你提供初始值 = 0
        val sumFold = numbers.fold(1) { acc, elem -> acc + elem }
        println("fold = $sumFold") // 10


        val job = onEvent().onEach { value -> println("luanchIn value:$value") }.launchIn(this)



    }


//    runBlocking {
//        try {
//            (1..5).asFlow().cancellable().collect { value ->
//                if (value == 3) cancel()
//                println(value)
//            }
//        } catch (e: CancellationException) {
//            println("Flow was cancelled at value=3")
//        }
//    }





}