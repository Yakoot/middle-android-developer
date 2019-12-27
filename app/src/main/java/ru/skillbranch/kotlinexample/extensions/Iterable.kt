package ru.skillbranch.kotlinexample.extensions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    val lastIndex = this.indexOfLast(predicate)
    return if (lastIndex > 0) {
        this.dropLast(size - lastIndex)
    } else {
        this
    }
}