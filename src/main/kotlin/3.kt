import java.io.File

fun main() {
    fun priority(a: Char): Int {
        val isUpper = if (a.isUpperCase()) 26 else 0
        return a.lowercaseChar().code - 'a'.code + 1 + isUpper
    }

    val inputString = File("3.txt").bufferedReader().use { it.readText() }
    val res = inputString.split("\n").dropLast(1).map {
        val first = it.take(it.length / 2)
        val second = it.drop(it.length / 2)
        val commonItem = first.toSet().intersect(second.toSet()).first()
        priority(commonItem)
    }.sum()

    println(res)
}
