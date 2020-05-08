package com.example.android.architecture.blueprints.randomuser.data.source.remote.retrofit

import com.example.android.architecture.blueprints.randomuser.data.Result
import com.example.android.architecture.blueprints.randomuser.data.source.remote.response.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Doc from https://randomuser.me/documentation
 * Seeds allow you to always generate the same set of users. For example, the seed "foobar" will always
 * return results for Becky Sims (for version 1.0). Seeds can be any string or sequence of characters.
 */
interface RandomUserApi {

    @GET("api")
    suspend fun getUsers(
            @Query("page") page: Int? = null,
            @Query("seed") seed: String? = null,
            @Query("results") limit: Int = 50
    ): Result<BaseResponse>
}