import java.io.File

fun main() {
    fun snafuToDecimal(s: String): Long {
        if (s.isEmpty()) {
            return 0
        }
        val firstNum = when (s.last()) {
            '=' -> -2
            '-' -> -1
            else -> s.last().toString().toInt()
        }
        return 5 * snafuToDecimal(s.dropLast(1)) + firstNum
    }
    fun decimalToSnafu(i: Long): String {
        return if (i < 3) {
            i.toString()
        } else {
            val nextDigit = when (i % 5) {
                3L -> "="
                4L -> "-"
                else -> (i % 5).toString()
            }
            decimalToSnafu((i + 2) / 5) + nextDigit
        }
    }

    val inputString = File("25.txt").bufferedReader().use { it.readText() }
    val res = inputString.split("\n").dropLast(1).sumOf { snafuToDecimal(it) }

    println(res)
    if (res != snafuToDecimal(decimalToSnafu(res))) {
        println("Inconsistency! snafuToDecimal(decimalToSnafu(res)) = " + snafuToDecimal(decimalToSnafu(res)))
    }
    println(decimalToSnafu(res))
}
