package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<MutableList<Pair<Int, Int>>> {
    return bounds.map { boundsGroup ->
        this.filter { it.first < boundsGroup.second && it.second > boundsGroup.first }
            .map {
                when {
                    it.first < boundsGroup.first -> Pair(boundsGroup.first, it.second)
                    it.second > boundsGroup.second -> Pair(it.first, boundsGroup.second)
                    else -> it
                }
            }
            .toMutableList()
    }
}