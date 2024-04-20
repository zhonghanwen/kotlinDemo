fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    print("100 + 200 = ")
    println(sum(100, 200 ))

    print("20 + 20 = ")
    println(sum1(20, 20))

    println(sum2(1, 33))

}

//带有两个 Int 参数、返回 Int 的函数。
fun sum(a: Int, b: Int ): Int {
    return a + b
}

//函数体可以是表达式。其返回类型可以推断出来。
fun sum1(a: Int, b: Int) = a + b

//返回无意义的值的函数 Unit 返回类型可以省略。
fun sum2(a: Int, b: Int): Unit {
    println("sum of $a and $b is ${a +b}")
}
