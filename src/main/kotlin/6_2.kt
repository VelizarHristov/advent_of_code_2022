import java.io.File

fun main() {
    val inputString = File("6.txt").bufferedReader().use { it.readText() }
    val str = inputString.windowed(14).first { it.toList().distinct().size == 14 }
    val res = inputString.indexOf(str) + 14

    println(res)
}
