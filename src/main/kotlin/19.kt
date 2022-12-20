import java.io.File
import java.util.PriorityQueue

import kotlin.math.ceil
import kotlin.math.max

fun solve19(time: Int): List<Int> {
    val inputString = File("19.txt").bufferedReader().use { it.readText() }
    data class Blueprint(
        val oreBotCost: Int,
        val clayBotCost: Int,
        val obsBotCost: Int,
        val obsBotClayCost: Int,
        val geodeBotCost: Int,
        val geodeBotObsCost: Int)
    val blueprints = inputString.split("\n").dropLast(1).map {
        var line = it.dropWhile { it != ':' }
        val nums = (1..6).map {
            line = line.dropWhile { it.isDigit() }.dropWhile { !it.isDigit() }
            line.takeWhile { it.isDigit() }.toInt()
        }
        Blueprint(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5])
    }

    // Branch & bound algorithm
    fun calcOptimalMining(bp: Blueprint): Int {
        data class BotCost(val oreCost: Int, val clayCost: Int, val obsCost: Int)
        val oreBotCost = BotCost(bp.oreBotCost, 0, 0)
        val clayBotCost = BotCost(bp.clayBotCost, 0, 0)
        val obsBotCost = BotCost(bp.obsBotCost, bp.obsBotClayCost, 0)
        val geodeBotCost = BotCost(bp.geodeBotCost, 0, bp.geodeBotObsCost)

        data class State(
            val oreBots: Int,
            val clayBots: Int,
            val obsBots: Int,
            val geodeBots: Int,
            val ore: Int,
            val clay: Int,
            val obs: Int,
            val geodes: Int,
            val remainingTime: Int) {
            fun tick(): State {
                return State(oreBots, clayBots, obsBots, geodeBots,
                    ore + oreBots, clay + clayBots, obs + obsBots, geodes + geodeBots,
                    remainingTime - 1)
            }
            fun tickNTimes(n: Int): State {
                return State(oreBots, clayBots, obsBots, geodeBots,
                    ore + oreBots * n, clay + clayBots * n, obs + obsBots * n, geodes + geodeBots * n,
                    remainingTime - n)
            }
            fun tickUntilAffordable(cost: BotCost): State? {
                val (oreCost, clayCost, obsCost) = cost
                val n1 = ceil((oreCost - ore) / oreBots.toDouble()).toInt()
                val n2 = ceil((clayCost - clay) / clayBots.toDouble()).toInt()
                val n3 = ceil((obsCost - obs) / obsBots.toDouble()).toInt()
                val s = tickNTimes(max(max(max(n1, n2), n3), 0) + 1)
                return if (s.remainingTime > 0) s else null
            }

            private fun isValid(): Boolean = ore >= 0 && clay >= 0 && obs >= 0

            fun buildOreBot(): State = copy(
                ore = ore - bp.oreBotCost,
                oreBots = oreBots + 1)
            fun buildClayBot(): State = copy(
                ore = ore - bp.clayBotCost,
                clayBots = clayBots + 1)
            fun buildObsBot(): State = copy(
                ore = ore - bp.obsBotCost,
                clay = clay - bp.obsBotClayCost,
                obsBots = obsBots + 1)
            fun buildGeodeBot(): State = copy(
                ore = ore - bp.geodeBotCost,
                obs = obs - bp.geodeBotObsCost,
                geodeBots = geodeBots + 1)

            fun canAffordClayBot(): Boolean = buildClayBot().isValid()
            fun canAffordObsBot(): Boolean = buildObsBot().isValid()
            fun canAffordGeodeBot(): Boolean = buildGeodeBot().isValid()
        }

        val maxOreCost = max(max(max(bp.oreBotCost, bp.clayBotCost), bp.obsBotCost), bp.geodeBotCost)

        fun heuristic(s: State): Int {
            var state = s
            while (state.remainingTime > 0) {
                if (state.canAffordGeodeBot())
                    state = state.buildGeodeBot().copy(ore = state.ore, geodes = state.geodes - 1)
                if (state.canAffordObsBot())
                    state = state.buildObsBot().copy(ore = state.ore, obs = state.obs - 1)
                if (state.canAffordClayBot())
                    state = state.buildClayBot().copy(ore = state.ore, clay = state.clay - 1)
                state = state.tick()
            }
            return -state.geodes
        }
        val nextStates = PriorityQueue<State>(compareBy { heuristic(it) })
        nextStates.add(State(1, 0, 0, 0, 0, 0, 0, 0, time))
        while (nextStates.isNotEmpty()) {
            val state = nextStates.remove()!!
            if (state.remainingTime == 0) {
                println(state)
                return state.geodes
            }
            val newStates = mutableListOf<State?>()
            if (state.oreBots < maxOreCost)
                newStates.add(state.tickUntilAffordable(oreBotCost)?.buildOreBot())
            newStates.add(state.tickUntilAffordable(clayBotCost)?.buildClayBot())
            if (state.clayBots > 0)
                newStates.add(state.tickUntilAffordable(obsBotCost)?.buildObsBot())
            if (state.obsBots > 0)
                newStates.add(state.tickUntilAffordable(geodeBotCost)?.buildGeodeBot())
            val validNewStates = newStates.filterNotNull()
            nextStates.addAll(validNewStates)
            if (validNewStates.isEmpty())
                nextStates.add(state.tickNTimes(state.remainingTime))
        }
        throw IllegalStateException("nextStates is empty")
    }

    return blueprints.map { calcOptimalMining(it) }
}

fun main() {
    val geodes = solve19(24)
    val res = geodes.mapIndexed { i, gd -> (i + 1) * gd }.sum()
    println(res)
}
