fun main() {
    val geodes = solve19(32)
    val res = geodes.take(3).reduce(Int::times)
    println(res)
}
