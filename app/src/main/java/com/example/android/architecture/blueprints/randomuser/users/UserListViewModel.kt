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
package com.example.android.architecture.blueprints.randomuser.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.randomuser.Event
import com.example.android.architecture.blueprints.randomuser.data.Result.Success
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.UsersDataSource
import com.example.android.architecture.blueprints.randomuser.data.source.UsersRepository
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

/**
 * ViewModel for the user list screen.
 */
class UserListViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<User>>().apply { value = emptyList() }
    val items: LiveData<List<User>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openUserEvent = MutableLiveData<Event<String>>()
    val openUserEvent: LiveData<Event<String>> = _openUserEvent

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        loadUsers(true)
    }

    /**
     * Called by Data Binding.
     */
    fun openUser(userId: String) {
        _openUserEvent.value = Event(userId)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [UsersDataSource]
     */
    fun loadUsers(forceUpdate: Boolean) {
        _dataLoading.value = true

        viewModelScope.launch {
            val usersResult = usersRepository.getUsers(forceUpdate)

            if (usersResult is Success) {
                val users = usersResult.data
                isDataLoadingError.value = false
                _items.value = ArrayList(users)
            } else {
                isDataLoadingError.value = false
                _items.value = emptyList()
                // TODO victor.valencia showSnackbarMessage(R.string.loading_users_error)
            }

            _dataLoading.value = false
        }
    }

    fun refresh() {
        loadUsers(true)
    }
}
