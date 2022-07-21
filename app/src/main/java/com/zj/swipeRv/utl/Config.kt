package com.zj.swipeRv.utl

interface Config {

    fun getApiHost(): String

    fun getUserId(): Long

    fun getToken(): String

    fun getNickName(): String

    fun getAvatar(): String

    fun getHeader(): Map<String, String>

}