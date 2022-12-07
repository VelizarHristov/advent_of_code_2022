import java.io.File

fun main() {
    abstract class Tree {
        abstract fun size(): Int
    }
    // File is already a built-in class
    class AFile(val name: String, val size: Int) : Tree() {
        override fun size(): Int = size
    }
    class Dir(val name: String, var files: Set<Tree> = setOf()) : Tree() {
        override fun size(): Int = files.sumOf { it.size() }
        fun descendants(): List<Dir> = files.flatMap {
            if (it is Dir)
                it.descendants().plus(it)
            else
                listOf()
        }
    }
    var parents = listOf(Dir("/"))

    val inputString = File("7.txt").bufferedReader().use { it.readText() }
    inputString.split("\n").drop(1).dropLast(1).forEach {
        if (it.startsWith("$ cd ")) {
            val dir = it.drop(5)
            if (dir == "..") {
                parents = parents.dropLast(1)
            } else {
                val nextDir = parents.last().files.find { it is Dir && it.name == dir }!! as Dir
                parents = parents + nextDir
            }
        } else if (it.startsWith("dir ")) {
            val name = it.drop(4)
            parents.last().files = parents.last().files.plus(Dir(name))
        } else if (!it.startsWith("$")) {
            val (size, filename) = it.split(" ")
            parents.last().files = parents.last().files.plus(AFile(filename, size.toInt()))
        }
    }

    val res = parents.first().descendants().map { it.size() }.filter { it < 100000 }.sum()
    println(res)
}
