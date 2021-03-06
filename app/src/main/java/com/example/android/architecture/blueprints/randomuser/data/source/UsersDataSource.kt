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
package com.example.android.architecture.blueprints.randomuser.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.User

/**
 * Main entry point for accessing users data from local storage.
 */
interface UsersDataSource {

    fun getUsers(): LiveData<List<User>>

    suspend fun getUser(userId: String): Result<User>

    suspend fun saveUser(user: User): Result<Long>

    suspend fun deleteUser(userId: String)
}

/**
 * Main entry point for accessing users data from the remote data source
 */
interface UsersRemoteDataSource {

    suspend fun getUsers(page: Int, pageSize: Int): Result<List<User>>
}
