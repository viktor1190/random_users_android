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
import com.example.android.architecture.blueprints.randomuser.data.Result.Error
import com.example.android.architecture.blueprints.randomuser.data.Result.Success
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.di.ApplicationModule.LocalDataSource
import com.example.android.architecture.blueprints.randomuser.di.ApplicationModule.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

/**
 * Concrete implementation to load users from the data sources into a cache.
 *
 * To simplify the sample, this repository only uses the local data source only if the remote
 * data source fails. Remote is the source of truth.
 */
class DefaultUsersRepository @Inject constructor(
        @RemoteDataSource private val usersRemoteDataSource: UsersDataSource,
        @LocalDataSource private val usersLocalDataSource: UsersDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsersRepository {

    private var cachedUsers: ConcurrentMap<String, User>? = null

    override suspend fun getUsers(forceUpdate: Boolean): Result<List<User>> {

        return withContext(ioDispatcher) {
            // Respond immediately with cache if available and not dirty
            if (!forceUpdate) {
                cachedUsers?.let { cachedUsers ->
                    return@withContext Success(cachedUsers.values.sortedBy { it.id })
                }
            }

            val newUsers = fetchUsersFromRemoteOrLocal(forceUpdate)

            // Refresh the cache with the new users
            (newUsers as? Success)?.let { refreshCache(it.data) }

            cachedUsers?.values?.let { users ->
                return@withContext Success(users.sortedBy { it.id })
            }

            (newUsers as? Success)?.let {
                if (it.data.isEmpty()) {
                    return@withContext Success(it.data)
                }
            }

            return@withContext Error(Exception("Illegal state"))
        }
    }

    private suspend fun fetchUsersFromRemoteOrLocal(forceUpdate: Boolean): Result<List<User>> {
        // Remote first
        val remoteUsers = usersRemoteDataSource.getUsers()
        when (remoteUsers) {
            is Error -> Timber.w("Remote data source fetch failed: ${remoteUsers.exception}")
            is Success -> {
                return remoteUsers
            }
            else -> throw IllegalStateException()
        }

        // Don't read from local if it's forced
        if (forceUpdate) {
            return Error(Exception("Can't force refresh: remote data source is unavailable"))
        }

        // Local if remote fails
        val localUsers = usersLocalDataSource.getUsers()
        if (localUsers is Success) return localUsers
        return Error(Exception("Error fetching from remote and local"))
    }

    /**
     * Relies on [getUsers] to fetch data and picks the user with the same ID.
     */
    override suspend fun getUser(userId: String, forceUpdate: Boolean): Result<User> {

        return withContext(ioDispatcher) {
            // Respond immediately with cache if available
            if (!forceUpdate) {
                getUserWithId(userId)?.let {
                    return@withContext Success(it)
                }
            }

            val newUser = fetchUserFromRemoteOrLocal(userId, forceUpdate)

            // Refresh the cache with the new users
            (newUser as? Success)?.let { cacheUser(it.data) }

            return@withContext newUser
        }
    }

    private suspend fun fetchUserFromRemoteOrLocal(
            userId: String,
            forceUpdate: Boolean
    ): Result<User> {
        // Remote first
        val remoteUser = usersRemoteDataSource.getUser(userId)
        when (remoteUser) {
            is Error -> Timber.w("Remote data source fetch failed")
            is Success -> {
                return remoteUser
            }
            else -> throw IllegalStateException()
        }

        // Don't read from local if it's forced
        if (forceUpdate) {
            return Error(Exception("Refresh failed"))
        }

        // Local if remote fails
        val localUsers = usersLocalDataSource.getUser(userId)
        if (localUsers is Success) return localUsers
        return Error(Exception("Error fetching from remote and local"))
    }

    override suspend fun deleteUser(userId: String) {
        coroutineScope {
            launch { usersRemoteDataSource.deleteUser(userId) }
            launch { usersLocalDataSource.deleteUser(userId) }
        }

        cachedUsers?.remove(userId)
    }

    private fun refreshCache(users: List<User>) {
        cachedUsers?.clear()
        users.sortedBy { it.id }.forEach {
            cacheAndPerform(it) {}
        }
    }

    private fun getUserWithId(id: String) = cachedUsers?.get(id)

    private fun cacheUser(user: User): User {
        val cachedUser = User(user.id, user.userName, user.firstName, user.lastName, user.email, user.phone, user.cellphone, user.gender,
                user.pictureThumbnailUrl, user.pictureMediumUrl, user.pictureLargeUrl)
        // Create if it doesn't exist.
        if (cachedUsers == null) {
            cachedUsers = ConcurrentHashMap()
        }
        cachedUsers?.put(cachedUser.id, cachedUser)
        return cachedUser
    }

    private inline fun cacheAndPerform(user: User, perform: (User) -> Unit) {
        val cachedUser = cacheUser(user)
        perform(cachedUser)
    }
}
