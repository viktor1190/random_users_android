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
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.UsersRemoteDataSource
import com.example.android.architecture.blueprints.randomuser.data.source.remote.response.BaseResponse
import com.example.android.architecture.blueprints.randomuser.data.source.remote.retrofit.RandomUserApi
import com.example.android.architecture.blueprints.randomuser.data.source.remote.retrofit.ResponseDataMapper
import com.example.android.architecture.blueprints.randomuser.data.succeeded
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the data source that adds a latency simulating network.
 */
@Singleton
class UsersRemoteDataSourceImpl @Inject constructor(
        private val usersApi: RandomUserApi,
        private val mapper: ResponseDataMapper<BaseResponse, List<User>>
) : UsersRemoteDataSource {

    private var seed: String? = null

    override suspend fun getUsers(page: Int, pageSize: Int): Result<List<User>> {
        val result = usersApi.getUsers(
                page = page,
                limit = pageSize,
                seed = seed
        )
        return if (result.succeeded) {
            val data = (result as Result.Success).data
            result.data.info.seed?.let {
                this.seed = it
            }
            Result.Success(mapper.mapToModel(data))
        } else
            result as Result.Error
    }
}
