import java.io.File

fun main() {
    fun contains(startA: Int, endA: Int, startB: Int, endB: Int): Boolean {
//        return startA >= startB && endA <= endB
        return startA in startB..endB && endA in startB..endB
    }

    val inputString = File("4.txt").bufferedReader().use { it.readText() }
    val res = inputString.split("\n").dropLast(1).count {
        val (startA, endA, startB, endB) = it.split(",").flatMap { it.split("-") }.map { it.toInt() }
        contains(startA, endA, startB, endB) ||
                contains(startB, endB, startA, endA)
    }

    println(res)
}
