import java.io.File

fun main() {
    val inputString = File("6.txt").bufferedReader().use { it.readText() }
    val str = inputString.windowed(4).first { it.toList().distinct().size == 4 }
    val res = inputString.indexOf(str) + 4

    println(res)
}
