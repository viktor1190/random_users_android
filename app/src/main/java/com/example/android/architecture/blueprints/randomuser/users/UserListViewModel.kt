package com.example.android.architecture.blueprints.randomuser.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.android.architecture.blueprints.randomuser.Event
import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.UsersRepository
import com.example.android.architecture.blueprints.randomuser.data.succeeded
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the user list screen.
 */
class UserListViewModel @Inject constructor(val usersRepository: UsersRepository) : ViewModel() {

    val randomUsers: LiveData<PagedList<User>>
    // This LiveData depends on another so we can use a transformation.
    private val _empty: MutableLiveData<Boolean> = MutableLiveData()
    val emptyRandomUsers: LiveData<Boolean> = _empty

    private val _liveDataSource: MutableLiveData<PageKeyedDataSource<Int, User>>

    // TODO victor.valencia Use this to manage the errors with a showSnackbarMessage(R.string.loading_users_error)
    /**
     * This private LiveData object is used to observe the request status and emmit the errors and the loading indicators.
     */
    private val _requestStatusObserver = MutableLiveData<Result<Nothing?>>()
    val dataLoading: LiveData<Boolean> = Transformations.map(_requestStatusObserver) { it == Result.Loading }

    private val _openUserEvent = MutableLiveData<Event<String>>()
    val openUserEvent: LiveData<Event<String>> = _openUserEvent

    private val _savedUsers = MutableLiveData<List<User>>().apply { value = emptyList() }
    val savedUsers: LiveData<List<User>> = _savedUsers
    val emptySavedUsers: LiveData<Boolean> = Transformations.map(_savedUsers) {
        it.isEmpty()
    }

    init {
        val dataSourceFactory = UserRemoteDataSourceFactory(usersRepository, viewModelScope, _requestStatusObserver)
        _liveDataSource = dataSourceFactory.getUserLiveDataSource()
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(80)
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(3)
                .build()
        randomUsers = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<User>() {
                    override fun onZeroItemsLoaded() {
                        _empty.value = false
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: User) {
                        _empty.value = true
                    }
                })
                .build()
        loadSavedUsers()
    }

    private fun loadSavedUsers() {
        viewModelScope.launch {
            val savedUsersResult = usersRepository.getSavedUsers()

            if (savedUsersResult.succeeded) {
                _savedUsers.value = (savedUsersResult as Result.Success).data
            } else {
                // TODO victor.valencia manage local data storage exceptions
            }
        }
    }

    /**
     * Called by Data Binding.
     */
    fun openUser(userId: String) {
        _openUserEvent.value = Event(userId)
    }

    fun refresh() {
        _liveDataSource.value?.invalidate()
    }
}
