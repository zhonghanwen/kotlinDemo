
//普通函数 不带参数的lambda expression
val greet : () -> Unit = { println("How are you?") }

//普通函数 带参数的lambda expression
val greetWithParams : (String) -> Unit = { name -> println("$name, how are you?") }

//普通函数 带参数的lambda expression 有返回类型的
val cal : (Int) -> (Int) = {count -> count * 2}


data class MenuItem(val name: String)

data class Menu(val name: String) {
    val items = mutableListOf<MenuItem>()

    fun item(name: String) {
        items.add(MenuItem(name))
    }
}

//带接收者的
fun menu(name: String, init: Menu.() -> Unit): Menu {
    // Creates an instance of the Menu class
    val menu = Menu(name)
    // Calls the lambda expression with receiver init() on the class instance
    menu.init()
    return menu
}


class Button {
    fun onEvent(action: ButtonEvent.() -> Unit) {
        // Simulate a double-click event (not a right-click)
        val event = ButtonEvent(isRightClick = false, amount = 2, position = Position(100, 200))
        event.action() // Trigger the event callback
    }
}

data class ButtonEvent(
    val isRightClick: Boolean,
    val amount: Int,
    val position: Position
)

data class Position(
    val x: Int,
    val y: Int
)

fun main() {
    greet()
    greetWithParams("Han")
    cal(100)

    val clothes = menu("clothes"){
        item("man")
        item("woman")
        item("kid")
    }
    println(clothes.name)
    println(clothes.items)


    val button = Button()

    button.onEvent {
        // Write your code here
        // Double click!
        if(!isRightClick && amount == 2){
            println("Double click!")
        }

    }
}

