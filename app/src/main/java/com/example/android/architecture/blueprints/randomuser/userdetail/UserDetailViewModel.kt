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
package com.example.android.architecture.blueprints.randomuser.userdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.randomuser.Event
import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.Result.Success
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.UsersRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Details screen.
 */
class UserDetailViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _addUserToContactsCommand = MutableLiveData<Event<Unit>>()
    val addUserToContactsCommand: LiveData<Event<Unit>> = _addUserToContactsCommand

    private val _deleteUserCommand = MutableLiveData<Event<Unit>>()
    val deleteUserCommand: LiveData<Event<Unit>> = _deleteUserCommand

    private val userId: String?
        get() = _user.value?.id


    fun deleteUser() = viewModelScope.launch {
        userId?.let {
            usersRepository.deleteUser(it)
            _deleteUserCommand.value = Event(Unit)
        }
    }

    fun start(userId: String?, forceRefresh: Boolean = false) {
        if (_isDataAvailable.value == true && !forceRefresh || _dataLoading.value == true) {
            return
        }

        // Show loading indicator
        _dataLoading.value = true

        viewModelScope.launch {
            if (userId != null) {
                usersRepository.getUser(userId, false).let { result ->
                    if (result is Success) {
                        onUserLoaded(result.data)
                    } else {
                        onDataNotAvailable(result)
                    }
                }
            }
            _dataLoading.value = false
        }
    }

    fun addUserToContacts() {
        _addUserToContactsCommand.value = Event(Unit)
    }

    private fun setUser(user: User?) {
        this._user.value = user
        _isDataAvailable.value = user != null
    }

    private fun onUserLoaded(user: User) {
        setUser(user)
    }

    private fun onDataNotAvailable(result: Result<User>) {
        _user.value = null
        _isDataAvailable.value = false
    }

    fun refresh() {
        userId?.let { start(it, true) }
    }
}
