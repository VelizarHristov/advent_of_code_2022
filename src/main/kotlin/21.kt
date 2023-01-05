import java.io.File

fun main() {
    abstract class Value
    class Number(val i: Long): Value()
    class Derived(val s1: String, val op: Char, val s2: String): Value()

    val inputString = File("21.txt").bufferedReader().use { it.readText() }
    val variables = inputString.split("\n").dropLast(1).associate {
        val (name, rawValue) = it.split(": ")
        val value = when (val parsedValue = rawValue.toLongOrNull()) {
            null -> Derived(rawValue.take(4), rawValue[5], rawValue.drop(7))
            else -> Number(parsedValue)
        }
        name to value
    }.toMutableMap()

    fun evaluate(s: String): Long {
        return when (val value = variables[s]) {
            is Number -> value.i
            is Derived -> {
                val i1 = evaluate(value.s1)
                val i2 = evaluate(value.s2)
                when (value.op) {
                    '+' -> i1 + i2
                    '-' -> i1 - i2
                    '*' -> i1 * i2
                    '/' -> i1 / i2
                    else -> throw IllegalStateException("Unexpected operator: " + value.op)
                }
            }
            else -> throw IllegalStateException("Value not in map: $s results in $value")
        }
        // variables[s] = Number(result above) // unnecessary because every value is read only once
    }

    println(evaluate("root"))
}
