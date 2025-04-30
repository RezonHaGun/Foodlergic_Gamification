package com.maranatha.foodlergic.data.di

import com.maranatha.foodlergic.data.repository.FirebaseAuthRepository
import com.maranatha.foodlergic.data.repository.FirebaseUserProfileRepository
import com.maranatha.foodlergic.data.repository.FirebaseUserRepository
import com.maranatha.foodlergic.domain.repository.IAuthRepository
import com.maranatha.foodlergic.domain.repository.IUserProfileRepository
import com.maranatha.foodlergic.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(repository: FirebaseAuthRepository): IAuthRepository

    @Binds
    abstract fun bindUserProfileRepository(repository: FirebaseUserProfileRepository): IUserProfileRepository

    @Binds
    abstract fun bindUserRepository(repository: FirebaseUserRepository): UserRepository


}