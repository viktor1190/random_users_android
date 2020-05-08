package com.example.android.architecture.blueprints.randomuser.data.source.remote.retrofit

import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.data.source.remote.response.BaseResponse
import com.example.android.architecture.blueprints.randomuser.data.source.remote.response.UserResponse
import javax.inject.Inject

interface ResponseDataMapper<in R, out T> {

    fun mapToModel(response: R): T
}

class BaseResponseDataMapper @Inject constructor(
        private val userMapper: UsersResponseDataMapper
) : ResponseDataMapper<BaseResponse, List<User>> {

    override fun mapToModel(response: BaseResponse): List<User> {
        return response.results.map { userMapper.mapToModel(it) }
    }
}

class UsersResponseDataMapper @Inject constructor() : ResponseDataMapper<UserResponse, User> {

    override fun mapToModel(response: UserResponse): User {
        return User(
                response.id.getUserId(),
                response.login.username,
                response.name.first,
                response.name.last,
                response.email,
                response.phone,
                response.cell,
                response.gender,
                response.picture.thumbnail,
                response.picture.medium,
                response.picture.large)
    }
}