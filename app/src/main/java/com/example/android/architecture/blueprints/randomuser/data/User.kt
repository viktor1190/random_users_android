package com.example.android.architecture.blueprints.randomuser.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Immutable model class for a User. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 */
@Entity
data class User @JvmOverloads constructor(
        @PrimaryKey val id: String, // java.util.UUID.randomUUID().toString(),
        val userName: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val phone: String,
        val cellphone: String,
        val gender: String,
        val pictureThumbnailUrl: String,
        val pictureMediumUrl: String,
        val pictureLargeUrl: String
) {

    val fullName: String
        get() = "$firstName $lastName"
}
