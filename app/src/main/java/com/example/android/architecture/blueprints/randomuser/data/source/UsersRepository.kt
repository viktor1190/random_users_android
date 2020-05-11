package com.example.android.architecture.blueprints.randomuser.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.User

/**
 * Facade interface to the data layer.
 */
interface UsersRepository {

    fun getSavedUsers(): LiveData<List<User>>

    suspend fun getNewUsers(page: Int, pageSize: Int): Result<List<User>>

    suspend fun getUser(userId: String): Result<User>

    suspend fun saveUser(user: User): Result<Long>

    suspend fun deleteUser(userId: String)

    suspend fun isFavoriteUser(userId: String): Boolean
}