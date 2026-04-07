package com.example.breify20.data


fun cosineSimilarity(query1: List<Float>, query2: List<Float>): Float {
    if (query1.size != query2.size) {
        throw IllegalArgumentException("Vectors must be of the same length")
    }

    var dotProduct = 0f
    var normA = 0f
    var normB = 0f

    for (i in query1.indices) {
        dotProduct += query1[i] * query2[i]
        normA += query1[i] * query1[i]
        normB += query2[i] * query2[i]
    }

    return if (normA != 0f && normB != 0f) {
        dotProduct / (kotlin.math.sqrt(normA) * kotlin.math.sqrt(normB))
    } else {
        0f
    }
}