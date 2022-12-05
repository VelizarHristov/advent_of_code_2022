import java.io.File

fun main() {
    val inputString = File("1.txt").bufferedReader().use { it.readText() }
    val res = inputString.split("\n\n").map {
        it.split("\n").sumOf { it.toInt() }
    }.max()

    println(res)
}
