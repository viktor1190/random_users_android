/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    val titleForList: String
        get() = "$firstName $lastName"
}
