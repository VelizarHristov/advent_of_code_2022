import java.io.File
import java.util.PriorityQueue

fun main() {
    val inputString = File("16.txt").bufferedReader().use { it.readText() }
    val graph = inputString.split("\n").dropLast(1).associate {
        val name = it.drop("Valve ".length).take("AA".length)
        val flowRate = it.drop("Valve PL has flow rate=".length).takeWhile { it.isDigit() }.toInt()
        val neighbors = it.dropWhile { it != ';' }
            .dropWhile { !it.isUpperCase() }
            .split(", ")
        name to Pair(flowRate, neighbors)
    }
    val allValves = graph.values.filter { it.first != 0 }

    // Branch & bound algorithm
    data class State(
        val curPos: String,
        val openValves: Set<String>,
        val remainingTime: Int,
        val score: Int)
    fun scoreIncrease(openValves: Set<String>): Int = openValves.sumOf { graph[it]!!.first }
    val maxScorePerTurn = allValves.sumOf { it.first }

    val nextStates = PriorityQueue<State>(compareBy { -(it.remainingTime * maxScorePerTurn + it.score) })
    nextStates.add(State("AA", setOf(), 30, 0))
    val visited = mutableMapOf<Triple<String, Set<String>, Int>, Int>()
    while (nextStates.isNotEmpty()) {
        val (curPos, openValves, remainingTime, score) = nextStates.remove()
        val state = Triple(curPos, openValves, remainingTime)
        if (remainingTime == 0) {
            println(score)
            return
        } else if (!visited.contains(state) || visited[state]!! < score) {
            visited[state] = score
            val nextScore = score + scoreIncrease(openValves)
            val moveStates = graph[curPos]!!.second.map {
                State(it, openValves, remainingTime - 1, nextScore)
            }
            val newStates = if (openValves.contains(curPos) || graph[curPos]!!.first == 0) {
                moveStates
            } else {
                val openValve = State(curPos, openValves + curPos, remainingTime - 1, nextScore)
                moveStates + openValve
            }
            nextStates.addAll(newStates)
        }
    }
    println("Error")
}
