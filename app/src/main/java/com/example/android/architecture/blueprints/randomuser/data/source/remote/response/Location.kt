package com.example.android.architecture.blueprints.randomuser.data.source.remote.response

data class Location (
        val street : Street,
        val city : String,
        val state : String,
        val postcode : String,
        val coordinates : Coordinates,
        val timezone : Timezone
)

data class Street (
        val number: Int,
        val name: String
)