package com.example.breify20.data

import com.example.breify20.model.email.Header

fun getHeader(headers: List<Header>, name: String): String? {
    return headers.find { it.name.equals(name, true) }?.value
}

fun parseSender(sender: String?): Pair<String,String> {
    if(sender == null) return Pair("Unknown","")
    val regex = Regex("(.*)<(.*)>")
    val match = regex.find(sender)
    return if(match != null){
        Pair(match.groupValues[1].trim(), match.groupValues[2].trim())
    }else{
        Pair(sender,sender)
    }
}

fun decodeBody(encoded:String?):String{

    if(encoded == null) return ""

    val bytes =
        android.util.Base64.decode(encoded, android.util.Base64.URL_SAFE)

    return String(bytes)
}