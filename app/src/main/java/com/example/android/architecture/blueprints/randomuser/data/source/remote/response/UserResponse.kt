package com.example.android.architecture.blueprints.randomuser.data.source.remote.response

data class UserResponse (
        val gender : String,
        val name : Name,
        val location : Location,
        val email : String,
        val login : Login,
        val registered : Registered,
        val phone : String,
        val cell : String,
        val id : Id,
        val picture : Picture,
        val nat : String
)