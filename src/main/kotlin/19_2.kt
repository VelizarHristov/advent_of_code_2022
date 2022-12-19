import java.io.File
import java.util.*
import kotlin.math.max

fun main() {
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

            fun canAffordOreBot(): Boolean = buildOreBot().isValid()
            fun canAffordClayBot(): Boolean = buildClayBot().isValid()
            fun canAffordObsBot(): Boolean = buildObsBot().isValid()
            fun canAffordGeodeBot(): Boolean = buildGeodeBot().isValid()
        }

        val maxOreCost = max(max(max(bp.oreBotCost, bp.clayBotCost), bp.obsBotCost), bp.geodeBotCost)

        val nextStates = PriorityQueue<State>(compareBy {
            var state = it
            while (state.remainingTime > 0) {
                if (state.canAffordGeodeBot())
                    state = state.buildGeodeBot().copy(ore = state.ore, geodes = state.geodes - 1)
                if (state.canAffordObsBot())
                    state = state.buildObsBot().copy(ore = state.ore, obs = state.obs - 1)
                state = state.tick()
                state = state.copy(
                    oreBots = state.oreBots + 1,
                    clayBots = state.clayBots + 1)
            }
            -state.geodes
        })
        nextStates.add(State(1, 0, 0, 0, 0, 0, 0, 0, 32))
        while (nextStates.isNotEmpty()) {
            val state = nextStates.remove()!!
            val (oreBots, _, _, _, _, _, _, geodes, remainingTime) = state
            if (remainingTime == 0) {
                println(state)
                return geodes
            }
            val ticked = state.tick()
            nextStates.add(ticked)
            if (state.canAffordOreBot() && oreBots < maxOreCost)
                nextStates.add(ticked.buildOreBot())
            if (state.canAffordClayBot())
                nextStates.add(ticked.buildClayBot())
            if (state.canAffordObsBot())
                nextStates.add(ticked.buildObsBot())
            if (state.canAffordGeodeBot())
                nextStates.add(ticked.buildGeodeBot())
        }
        throw IllegalStateException("nextStates is empty")
    }

    val geodes = blueprints.map { calcOptimalMining(it) }
    val res = geodes.take(3).reduce(Int::times)
    println(res)
}
