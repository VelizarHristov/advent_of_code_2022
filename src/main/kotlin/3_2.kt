import java.io.File

fun main() {
    fun priority(a: Char): Int {
        val isUpper = if (a.isUpperCase()) 26 else 0
        return a.lowercaseChar().code - 'a'.code + 1 + isUpper
    }

    val inputString = File("3.txt").bufferedReader().use { it.readText() }
    val res = inputString.split("\n").dropLast(1).chunked(3).map {
        val commonItem = it.map { it.toSet() }.reduce { a, b -> a.intersect(b) }.first()
        priority(commonItem)
    }.sum()

    println(res)
}
