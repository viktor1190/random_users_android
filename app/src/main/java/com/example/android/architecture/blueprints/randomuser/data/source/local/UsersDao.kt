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

package com.example.android.architecture.blueprints.randomuser.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.architecture.blueprints.randomuser.data.User

/**
 * Data Access Object for the users table.
 */
@Dao
interface UsersDao {

    /**
     * Select all users from the user table.
     *
     * @return all users.
     */
    @Query("SELECT * FROM User")
    suspend fun getUsers(): List<User>

    /**
     * Select a user by id.
     *
     * @param userId the user id.
     * @return the user with userId.
     */
    @Query("SELECT * FROM User WHERE id = :userId")
    suspend fun getUserById(userId: String): User?


    /**
     * Delete a user by id.
     *
     * @return the number of users deleted. This should always be 1.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long


    /**
     * Delete a user by id.
     *
     * @return the number of users deleted. This should always be 1.
     */
    @Query("DELETE FROM User WHERE id = :userId")
    suspend fun deleteUserById(userId: String): Int

}
