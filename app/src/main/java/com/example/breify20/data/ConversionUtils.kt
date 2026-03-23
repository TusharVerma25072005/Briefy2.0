package com.example.breify20.data


fun FloatArray.toDbString(): String {
    return joinToString(",")
}

fun String.toFloatArray(): FloatArray {
    return split(",").map { it.toFloat() }.toFloatArray()
}