import java.io.File

fun main() {
    val inputString = File("5.txt").bufferedReader().use { it.readText() }
    val numStacks = Math.ceil((inputString.split("\n")[0].length / 4.0)).toInt()
    val stacks = Array(numStacks) { ArrayList<Char>() }
    inputString.split("\n").takeWhile {
        !it[1].isDigit()
    }.forEach {
        var idx = 0
        it.chunked(4).forEach {
            if (it[1] != ' ')
                stacks[idx].add(it[1])
            idx++
        }
    }

    inputString.split("\n").dropLast(1).dropWhile {
        !it.startsWith("move ")
    }.forEach {
        val count = it.drop(5).takeWhile { it.isDigit() }.toInt()
        val (from, to) = it.dropWhile { it != 'f' }.filter { it.isDigit() }.map { it.toString().toInt() }
        stacks[to - 1].addAll(0, stacks[from - 1].take(count))
        stacks[from - 1] = ArrayList(stacks[from - 1].drop(count))
    }

    println(stacks.map { it.first() }.joinToString(""))
}
