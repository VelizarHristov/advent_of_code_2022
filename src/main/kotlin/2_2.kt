import java.io.File

fun main() {
    val rps = mapOf(
        "Rock" to "Scissors",
        "Scissors" to "Paper",
        "Paper" to "Rock")
    val reversedRps = rps.entries.associate { (k, v) -> v to k }

    val shapeOrder = listOf("Rock", "Paper", "Scissors")

    fun opponentToRps(a: Char): String = shapeOrder.get(listOf('A', 'B', 'C').indexOf(a))

    fun shapeScore(a: Char, b: Char): Int {
        val s = opponentToRps(a)
        val me = when (b) {
            'X' -> rps.get(s)
            'Y' -> s
            'Z' -> reversedRps.get(s)
            else -> throw IllegalArgumentException("input: $b")
        }
        return shapeOrder.indexOf(me) + 1
    }

    fun resultScore(b: Char): Int {
        return when (b) {
            'X' -> 0
            'Y' -> 3
            'Z' -> 6
            else -> throw IllegalArgumentException("input: $b")
        }
    }

    val inputString = File("2.txt").bufferedReader().use { it.readText() }
    val res = inputString.split("\n").dropLast(1).sumOf { it ->
        val (opponent, me) = it.split(" ").map { it[0] }
        shapeScore(opponent, me) + resultScore(me)
    }

    println(res)
}
