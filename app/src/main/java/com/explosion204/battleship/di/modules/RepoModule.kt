package com.explosion204.battleship.di.modules

import com.explosion204.battleship.data.repos.GameResultRepository
import com.explosion204.battleship.data.repos.SessionRepository
import com.explosion204.battleship.data.repos.UserRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(fireStore: FirebaseFirestore, storage: FirebaseStorage) =
        UserRepository(fireStore, storage)

    @Provides
    @Singleton
    fun provideSessionRepository(firebaseDatabase: FirebaseDatabase) =
        SessionRepository(firebaseDatabase)

    @Provides
    @Singleton
    fun providesGameResultRepository(fireStore: FirebaseFirestore) = GameResultRepository(fireStore)
}