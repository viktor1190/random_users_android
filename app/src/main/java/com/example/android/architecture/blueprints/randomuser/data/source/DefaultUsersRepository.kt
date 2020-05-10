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

import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.di.ApplicationModule.LocalDataSource
import com.example.android.architecture.blueprints.randomuser.di.ApplicationModule.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultUsersRepository @Inject constructor(
        @RemoteDataSource private val usersRemoteDataSource: UsersRemoteDataSource,
        @LocalDataSource private val usersLocalDataSource: UsersDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsersRepository {

    override suspend fun getSavedUsers(): Result<List<User>> {
        return withContext(ioDispatcher) { usersLocalDataSource.getUsers() }
    }

    override suspend fun getNewUsers(page: Int, pageSize: Int): Result<List<User>> {
        return withContext(ioDispatcher) { usersRemoteDataSource.getUsers(page, pageSize) }
    }

    override suspend fun getUser(userId: String): Result<User> {
        return withContext(ioDispatcher) { usersLocalDataSource.getUser(userId) }
    }

    override suspend fun deleteUser(userId: String) {
        coroutineScope {
            launch { usersLocalDataSource.deleteUser(userId) }
        }
    }
}
