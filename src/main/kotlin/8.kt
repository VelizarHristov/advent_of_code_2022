import java.io.File

fun main() {
    val inputString = File("8.txt").bufferedReader().use { it.readText() }
    val length = inputString.split("\n")[0].length
    val grid = Array(length) { IntArray(length) }
    val visited = Array(length) { BooleanArray(length) }
    inputString.split("\n").dropLast(1).forEachIndexed { i, it ->
        grid[i] = it.map { it.toString().toInt() }.toIntArray()
    }
    for (i in 0 until length) {
        var maxTreeFromLeft = -1
        var maxTreeFromRight = -1
        var maxTreeFromTop = -1
        var maxTreeFromBottom = -1
        for (j in 0 until length) {
            if (grid[i][j] > maxTreeFromLeft) {
                maxTreeFromLeft = grid[i][j]
                visited[i][j] = true
            }
            if (grid[j][i] > maxTreeFromTop) {
                maxTreeFromTop = grid[j][i]
                visited[j][i] = true
            }
        }
        for (j in length-1 downTo 0) {
            if (grid[i][j] > maxTreeFromRight) {
                maxTreeFromRight = grid[i][j]
                visited[i][j] = true
            }
            if (grid[j][i] > maxTreeFromBottom) {
                maxTreeFromBottom = grid[j][i]
                visited[j][i] = true
            }
        }
    }

    val res = visited.sumOf { it.count { it } }
    println(res)
}
