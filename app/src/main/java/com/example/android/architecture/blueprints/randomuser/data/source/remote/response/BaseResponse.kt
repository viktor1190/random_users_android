package com.example.android.architecture.blueprints.randomuser.data.source.remote.response

data class BaseResponse(val results: List<UserResponse>, val info: Info)

data class Info(val seed: String, val results: Int, val page: Int, val version: String)