package com.example.android.architecture.blueprints.randomuser.data.source.local

import androidx.lifecycle.LiveData
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

    override fun getUsers(): LiveData<List<User>> = usersDao.getUsers()

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

    override suspend fun saveUser(user: User): Result<Long> = withContext(ioDispatcher) {
        try {
            val long = usersDao.insertUser(user)
            return@withContext Success(long)
        } catch (e: java.lang.Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun deleteUser(userId: String) {
        usersDao.deleteUserById(userId)
    }
}
