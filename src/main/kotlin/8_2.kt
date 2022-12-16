import java.io.File

fun main() {
    val inputString = File("8.txt").bufferedReader().use { it.readText() }
    val length = inputString.split("\n")[0].length
    val grid = Array(length) { IntArray(length) }
    inputString.split("\n").dropLast(1).forEachIndexed { i, it ->
        grid[i] = it.map { it.toString().toInt() }.toIntArray()
    }
    var maxScore = 1
    for (i in 1 until length-1) {
        for (j in 1 until length-1) {
            var score = 1

            var nextI = i + 1
            while (grid[nextI][j] < grid[i][j] && nextI < length-1) {
                nextI += 1
            }
            score *= nextI - i

            nextI = i - 1
            while (grid[nextI][j] < grid[i][j] && nextI > 0) {
                nextI -= 1
            }
            score *= i - nextI

            var nextJ = j + 1
            while (grid[i][nextJ] < grid[i][j] && nextJ < length-1) {
                nextJ += 1
            }
            score *= nextJ - j

            nextJ = j - 1
            while (grid[i][nextJ] < grid[i][j] && nextJ > 0) {
                nextJ -= 1
            }
            score *= j - nextJ

            if (score > maxScore) {
                maxScore = score
            }
        }
    }

    println(maxScore)
}
