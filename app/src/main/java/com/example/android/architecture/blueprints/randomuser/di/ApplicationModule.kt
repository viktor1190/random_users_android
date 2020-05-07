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

package com.example.android.architecture.blueprints.randomuser.di

import android.content.Context
import androidx.room.Room
import com.example.android.architecture.blueprints.randomuser.data.source.DefaultUsersRepository
import com.example.android.architecture.blueprints.randomuser.data.source.UsersDataSource
import com.example.android.architecture.blueprints.randomuser.data.source.UsersRepository
import com.example.android.architecture.blueprints.randomuser.data.source.local.UsersDatabase
import com.example.android.architecture.blueprints.randomuser.data.source.local.UsersLocalDataSource
import com.example.android.architecture.blueprints.randomuser.data.source.remote.UsersRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME


@Module(includes = [ApplicationModuleBinds::class])
object ApplicationModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class UsersRemoteDataSource

    @Qualifier
    @Retention(RUNTIME)
    annotation class UsersLocalDataSource

    @JvmStatic
    @Singleton
    @UsersRemoteDataSource
    @Provides
    fun provideUsersRemoteDataSource(): UsersDataSource {
        return UsersRemoteDataSource
    }

    @JvmStatic
    @Singleton
    @UsersLocalDataSource
    @Provides
    fun provideUsersLocalDataSource(
            database: UsersDatabase,
            ioDispatcher: CoroutineDispatcher
    ): UsersDataSource {
        return UsersLocalDataSource(
                database.userDao(), ioDispatcher
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): UsersDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UsersDatabase::class.java,
            "Users.db"
        ).build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

@Module
abstract class ApplicationModuleBinds {

    @Singleton
    @Binds
    abstract fun bindRepository(repo: DefaultUsersRepository): UsersRepository
}
