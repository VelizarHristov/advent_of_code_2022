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

    // returns null if s has "humn" as its descendant, else returns s's value
    fun evaluate(s: String): Long? {
        if (s == "humn")
            return null
        return when (val value = variables[s]) {
            is Number -> value.i
            is Derived -> {
                val i1 = evaluate(value.s1)
                val i2 = evaluate(value.s2)
                if (i1 != null && i2 != null) {
                    when (value.op) {
                        '+' -> i1 + i2
                        '-' -> i1 - i2
                        '*' -> i1 * i2
                        '/' -> i1 / i2
                        else -> throw IllegalStateException("Unexpected operator: " + value.op)
                    }
                } else {
                    null
                }
            }
            else -> throw IllegalStateException("Value not in map: $s results in $value")
        }
    }

    /**
     * s - one of its descendants must be "humn"
     * Output: the value which "humn" needs to become in order for desiredValue to
     *   be equal to the value of s
     */
    fun mustEqual(desiredValue: Long, s: String): Long {
        if (s == "humn")
            return desiredValue
        return when (val value = variables[s]) {
            is Number -> value.i
            is Derived -> {
                // One of the calls will return null and do unnecessary work in the process
                // The total amount of unnecessary work is equal to evaluate() on the
                //   entire tree, so improving the performance of this will only
                //   speed up the entire program by 2x at most
                val i1 = evaluate(value.s1)
                val i2 = evaluate(value.s2)
                val (nullStr, nextValue) = if (i1 == null) {
                    Pair(value.s1, i2!!)
                } else {
                    Pair(value.s2, i1)
                }
                val nextDesiredValue = when {
                    value.op == '+' -> desiredValue - nextValue
                    value.op == '*' -> desiredValue / nextValue
                    value.op == '/' && i1 == null -> nextValue * desiredValue
                    value.op == '/' && i1 != null -> nextValue / desiredValue
                    value.op == '-' && i1 == null -> nextValue + desiredValue
                    value.op == '-' && i1 != null -> nextValue - desiredValue
                    else -> throw IllegalStateException("Unexpected operator: " + value.op)
                }
                mustEqual(nextDesiredValue, nullStr)
            }
            else -> throw IllegalStateException("Value not in map: $s results in $value")
        }
    }

    val curRoot = variables["root"] as Derived
    variables["root"] = Derived(curRoot.s1, '-', curRoot.s2)
    println(mustEqual(0L, "root"))
}
