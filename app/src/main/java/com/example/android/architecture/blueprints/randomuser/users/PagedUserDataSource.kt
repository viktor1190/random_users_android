package com.example.android.architecture.blueprints.randomuser.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.UsersRepository
import com.example.android.architecture.blueprints.randomuser.data.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val PAGE_SIZE = 50
const val FIRST_PAGE = 1

class PagedUserDataSource constructor(
        private val usersRepository: UsersRepository,
        private val coroutineScope: CoroutineScope,
        private val requestStatusObserver: MutableLiveData<Result<List<User>>>,
        private val ioDispatcher: CoroutineDispatcher
): PageKeyedDataSource<Int, User>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, User>) {
        managedUserRequest(FIRST_PAGE, params.requestedLoadSize) {
            callback.onResult(it, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        managedUserRequest(params.key, params.requestedLoadSize) {
            // incrementing the next page number
            val key = params.key + 1
            callback.onResult(it, key)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        managedUserRequest(params.key, params.requestedLoadSize) {
            //if the current page is greater than one we are decrementing the page number else there is no previous page
            val key = if (params.key > 1) params.key - 1 else null
            callback.onResult(it, key)
        }
    }

    private fun managedUserRequest(page: Int, pageSize: Int, callback: (List<User>) -> Unit) {
        coroutineScope.launch {
            requestStatusObserver.value = Result.Loading
            val usersResult = withContext(ioDispatcher) { usersRepository.getNewUsers(page, pageSize) }
            if (usersResult.succeeded) {
                val users = (usersResult as Result.Success).data
                requestStatusObserver.value = Result.Success(users)
                callback(users)
            } else {
                requestStatusObserver.value = (usersResult as Result.Error).copy()
            }
        }
    }
}

class UserRemoteDataSourceFactory(
        private val usersRepository: UsersRepository,
        private val coroutineScope: CoroutineScope,
        private val requestStatusObserver: MutableLiveData<Result<List<User>>>,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): DataSource.Factory<Int, User>() {

    private val userLiveDataSource: MutableLiveData<PageKeyedDataSource<Int, User>> = MutableLiveData()

    override fun create(): DataSource<Int, User> {
        val dataSource = PagedUserDataSource(usersRepository, coroutineScope, requestStatusObserver, ioDispatcher)

        userLiveDataSource.postValue(dataSource)

        return dataSource
    }

    fun getUserLiveDataSource() = userLiveDataSource

}