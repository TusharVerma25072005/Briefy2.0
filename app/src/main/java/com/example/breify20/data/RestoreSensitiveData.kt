package com.example.breify20.data

fun restoreSensitiveData(text: String, pairs: List<Pair<String, String>>): String {
    var result = text
    pairs.forEach { (original, hash) ->
        val placeholder = "<PRIVATE_$hash>"
        result = result.replace(placeholder, original)
    }
    return result
}