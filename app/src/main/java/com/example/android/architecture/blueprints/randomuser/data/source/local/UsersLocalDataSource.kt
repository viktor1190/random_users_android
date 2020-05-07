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

import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.Result.Error
import com.example.android.architecture.blueprints.randomuser.data.Result.Success
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.UsersDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class UsersLocalDataSource internal constructor(
        private val usersDao: UsersDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsersDataSource {

    override suspend fun getUsers(): Result<List<User>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(usersDao.getUsers())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User> = withContext(ioDispatcher) {
        try {
            val user = usersDao.getUserById(userId)
            if (user != null) {
                return@withContext Success(user)
            } else {
                return@withContext Error(Exception("User not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun deleteUser(userId: String) {
        usersDao.deleteUserById(userId)
    }
}
