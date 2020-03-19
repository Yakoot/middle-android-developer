package ru.skillbranch.skillarticles.extensions

import android.util.Log

fun String?.indexesOf(query: String, ignoreCase: Boolean = true): List<Int> {
    if (query.isBlank()) return emptyList()
    val results: MutableList<Int> = mutableListOf()
    var index: Int = this?.indexOf(query, 0, ignoreCase) ?: -1
    while(index >= 0) {
        Log.d("testtest", index.toString())
        results.add(index)
        index = this?.indexOf(query, index + 1, ignoreCase) ?: -1
    }
    return results
}
