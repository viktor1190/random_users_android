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
package com.example.android.architecture.blueprints.randomuser.data.source.remote

import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.Result.Error
import com.example.android.architecture.blueprints.randomuser.data.Result.Success
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.UsersDataSource
import kotlinx.coroutines.delay

/**
 * Implementation of the data source that adds a latency simulating network.
 */
object UsersRemoteDataSource : UsersDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L
    private var USERS_SERVICE_DATA = LinkedHashMap<String, User>(2)

    init {
        val user1 = User("1", "user1", "victor", "valencia", "cytrux@gmail.com", "55555",
                "33333", "male", "https://randomuser.me/api/portraits/thumb/men/75.jpg", "", "")
        USERS_SERVICE_DATA.put(user1.id, user1)
        val user2 = User("2", "user2", "Jhon", "Doe", "jhondoe@gmail.com", "55555",
                "33333", "male", "https://randomuser.me/api/portraits/thumb/men/75.jpg", "", "")
        USERS_SERVICE_DATA.put(user2.id, user2)
    }

    /**
     * Note: [LoadTasksCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override suspend fun getUsers(): Result<List<User>> {
        // Simulate network by delaying the execution.
        val users = USERS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Success(users)
    }

    /**
     * Note: [GetTaskCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override suspend fun getUser(userId: String): Result<User> {

        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        USERS_SERVICE_DATA[userId]?.let {
            return Success(it)
        }
        return Error(Exception("User not found"))
    }

    override suspend fun deleteUser(userId: String) {
        USERS_SERVICE_DATA.remove(userId)
    }
}
