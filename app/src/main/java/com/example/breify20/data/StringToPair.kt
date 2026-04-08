package com.example.breify20.data

fun convertStringToPairs(pairsString: String): List<Pair<String, String>> {
    if (pairsString.isEmpty()) return emptyList()

    return pairsString.split("||").mapNotNull {
        val parts = it.split("::")
        if (parts.size == 2) Pair(parts[0], parts[1]) else null
    }
}