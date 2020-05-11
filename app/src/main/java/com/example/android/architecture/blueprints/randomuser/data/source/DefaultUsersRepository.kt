package com.example.android.architecture.blueprints.randomuser.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.succeeded
import com.example.android.architecture.blueprints.randomuser.di.ApplicationModule.LocalDataSource
import com.example.android.architecture.blueprints.randomuser.di.ApplicationModule.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

class DefaultUsersRepository @Inject constructor(
        @RemoteDataSource private val usersRemoteDataSource: UsersRemoteDataSource,
        @LocalDataSource private val usersLocalDataSource: UsersDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsersRepository {

    /**
     * This cache is used to store only the users coming from the network, in this way the user can get into the details of the selected contact
     */
    private var memoryCachedUsers: ConcurrentMap<String, User>? = null

    override fun getSavedUsers(): LiveData<List<User>> = usersLocalDataSource.getUsers()

    override suspend fun getNewUsers(page: Int, pageSize: Int): Result<List<User>> {
        return withContext(ioDispatcher) {
            val newUsers = usersRemoteDataSource.getUsers(page, pageSize)

            // Refresh the cache with the new users
            (newUsers as? Result.Success)?.let { refreshCache(it.data) }

            newUsers
        }
    }

    override suspend fun getUser(userId: String): Result<User> {
        return withContext(ioDispatcher) {

            // first checks if the user is from database
            val localUser: Result<User> = usersLocalDataSource.getUser(userId)
            if (localUser.succeeded) {
                return@withContext localUser
            }


            memoryCachedUsers?.get(userId)?.let {
                return@withContext Result.Success(it)
            }

            // if the user wasn't found in the database, it should be at the cache
            return@withContext Result.Error(Exception("User not found"))
        }
    }

    override suspend fun saveUser(user: User): Result<Long> {
        return withContext(ioDispatcher) {
            return@withContext usersLocalDataSource.saveUser(user)
        }
    }

    override suspend fun deleteUser(userId: String) {
        coroutineScope {
            launch { usersLocalDataSource.deleteUser(userId) }
        }
    }

    override suspend fun isFavoriteUser(userId: String): Boolean {
        return withContext(ioDispatcher) { usersLocalDataSource.getUser(userId).succeeded }
    }

    private fun refreshCache(users: List<User>) {
        users.sortedBy { it.id }.forEach {
            cacheAndPerform(it) {}
        }
    }

    private fun cacheUser(user: User): User {
        val cachedUser = user.copy()
        // Create if it doesn't exist.
        if (memoryCachedUsers == null) {
            memoryCachedUsers = ConcurrentHashMap()
        }
        memoryCachedUsers?.put(cachedUser.id, cachedUser)
        return cachedUser
    }

    private inline fun cacheAndPerform(user: User, perform: (User) -> Unit) {
        val cachedUser = cacheUser(user)
        perform(cachedUser)
    }
}
