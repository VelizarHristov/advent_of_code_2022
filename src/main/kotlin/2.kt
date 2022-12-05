import java.io.File

fun main() {
    val rps = mapOf(
        "Rock" to "Scissors",
        "Scissors" to "Paper",
        "Paper" to "Rock")

    val shapeOrder = listOf("Rock", "Paper", "Scissors")

    fun opponentToRps(a: Char): String = shapeOrder.get(listOf('A', 'B', 'C').indexOf(a))
    fun meToRps(a: Char): String = shapeOrder.get(listOf('X', 'Y', 'Z').indexOf(a))

    fun rockPaperScissors(a: Char, b: Char): Int {
        val s1 = opponentToRps(a)
        val s2 = meToRps(b)
        return if (s1 == s2) {
            3
        } else if (rps.get(s2) == s1) {
            6
        } else {
            0
        }
    }

    fun shapeScore(b: Char): Int {
        return when (b) {
            'X' -> 1
            'Y' -> 2
            'Z' -> 3
            else -> throw IllegalArgumentException("input: $b")
        }
    }

    val inputString = File("2.txt").bufferedReader().use { it.readText() }
    val res = inputString.split("\n").dropLast(1).sumOf { it ->
        val (opponent, me) = it.split(" ").map { it[0] }
        rockPaperScissors(opponent, me) + shapeScore(me)
    }

    println(res)
}
