package com.atg.autonexo.features.iam.data.di

import android.content.Context
import androidx.room.Room
import com.atg.autonexo.core.data.UserPreferences
import com.atg.autonexo.core.network.ConnectivityTest
import com.atg.autonexo.features.iam.data.local.dao.UserDao
import com.atg.autonexo.features.iam.data.local.database.IamDatabase
import com.atg.autonexo.features.iam.data.remote.services.AuthService
import com.atg.autonexo.features.iam.data.repositories.AuthRepositoryImpl
import com.atg.autonexo.features.iam.domain.repositories.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideIamDatabase(@ApplicationContext context: Context): IamDatabase {
        return Room.databaseBuilder(
            context,
            IamDatabase::class.java,
            IamDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: IamDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideConnectivityTest(@ApplicationContext context: Context): ConnectivityTest {
        return ConnectivityTest(context)
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        userDao: UserDao,
        userPreferences: UserPreferences,
        connectivityTest: ConnectivityTest
    ): AuthRepository {
        return AuthRepositoryImpl(authService, userDao, userPreferences, connectivityTest)
    }
}
