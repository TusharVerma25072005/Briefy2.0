package com.example.breify20.data

fun convertPairsToString(pairs: List<Pair<String,String>>): String {
    return pairs.joinToString(",") {
        "<${it.first},${it.second}>"
    }
}