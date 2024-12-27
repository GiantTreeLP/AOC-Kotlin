package common

inline fun <A> Pair<A, A>.count(predicate: (A) -> Boolean): Int {
    var count = 0
    if (predicate(first)) {
        count++
    }
    if (predicate(second)) {
        count++
    }
    return count
}