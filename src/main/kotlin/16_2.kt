import java.io.File
import java.util.PriorityQueue

// takes 450 ms
fun main() {
    val inputString = File("16.txt").bufferedReader().use { it.readText() }
    val graph = inputString.split("\n").dropLast(1).associate {
        val name = it.drop("Valve ".length).take("AA".length)
        val flowRate = it.drop("Valve PL has flow rate=".length).takeWhile { it.isDigit() }.toInt()
        val neighbors = it.dropWhile { it != ';' }
            .dropWhile { !it.isUpperCase() }
            .split(", ")
            .toSet()
        name to Pair(flowRate, neighbors)
    }
    fun dist(from: Set<String>, to: String): Int {
        return if (from.contains(to)) {
            1 // to open the valve
        } else {
            1 + dist(from.flatMap { graph[it]!!.second }.toSet(), to)
        }
    }
    fun distance(from: String, to: String): Int = dist(setOf(from), to)
    val allValves = graph.entries
        .filter { it.value.first != 0 || it.key == "AA" }
        .map { it.key }
        .sortedBy { -graph[it]!!.first }
    val allValvesI = allValves.indices.toList()
    val smallerGraph = allValves.associate { valve ->
        val asInt = allValves.indexOf(valve)
        val flowRate = graph[valve]!!.first
        val neighbors = allValves.map {
            Pair(allValves.indexOf(it), distance(valve, it))
        }.toSet()
        asInt to Pair(flowRate, neighbors)
    }

    data class State(
        val curPos: Int,
        val elePos: Int,
        val openValves: Set<Int>,
        val remainingTime: Int,
        val eleRemainingTime: Int,
        val score: Int)
    fun scoreIncrease(openValves: Set<Int>): Int = openValves.sumOf { smallerGraph[it]!!.first }
    fun allMoves(curPos: Int, openValves: Set<Int>): List<Pair<Int, Int>> =
        smallerGraph[curPos]!!.second.filter { !openValves.contains(it.first) }
    val dists = allValvesI.map {
        smallerGraph[it]!!.second.toList().sortedBy { it.first }.map { it.second }.toIntArray()
    }.toTypedArray()
    fun heuristic(state: State): Int {
        val remainingValves = (allValvesI - state.openValves).toMutableList()
        var moves = state.remainingTime - remainingValves.minBy { dists[state.curPos][it] } + 3
        var eleMoves = state.eleRemainingTime - remainingValves.minBy { dists[state.elePos][it] } + 3
        var score = state.score
        // Note: the smallest distance between two valves is 2, +1 for opening = 3
        while ((moves >= 3 || eleMoves >= 3) && remainingValves.isNotEmpty()) {
            if (moves >= 3) {
                moves -= 3
                score += moves * smallerGraph[remainingValves.removeFirst()]!!.first
            }
            if (eleMoves >= 3 && moves < eleMoves && remainingValves.isNotEmpty()) {
                eleMoves -= 3
                score += eleMoves * smallerGraph[remainingValves.removeFirst()]!!.first
            }
        }
        return score + state.remainingTime * scoreIncrease(state.openValves)
    }
    val nextStates = PriorityQueue<State>(compareBy { -heuristic(it) })
    val aaAsInt = allValves.indexOf("AA")
    nextStates.add(State(aaAsInt, aaAsInt, setOf(aaAsInt), 26, 26, 0))
    while (nextStates.isNotEmpty()) {
        val state = nextStates.remove()
        val (curPos, elePos, openValves, remainingTime, eleTime, score) = state
        if (remainingTime == 0 && eleTime == 0) {
            println(score)
            return
        } else {
            val newStates = if (remainingTime >= eleTime) {
                allMoves(curPos, openValves).filter { it.second <= remainingTime }
                    .map { (nextPos, timeElapsed) ->
                        State(
                            nextPos,
                            elePos,
                            openValves + nextPos,
                            remainingTime - timeElapsed,
                            eleTime,
                            score + timeElapsed * scoreIncrease(openValves))
                    }.ifEmpty {
                        listOf(State(curPos, elePos, openValves, 0, eleTime,
                            score + remainingTime * scoreIncrease(openValves)))
                    }
            } else {
                allMoves(elePos, openValves).filter { it.second <= eleTime }
                    .map { (nextElePos, timeElapsed) ->
                        val openAt = eleTime - timeElapsed
                        State(
                            curPos,
                            nextElePos,
                            openValves + nextElePos,
                            remainingTime,
                            eleTime - timeElapsed,
                            score + (openAt - remainingTime) * scoreIncrease(setOf(nextElePos)))
                    }.ifEmpty {
                        listOf(State(curPos, elePos, openValves, remainingTime, 0, score))
                    }
            }
            nextStates.addAll(newStates)
        }
    }
    println("Error")
}
