package com.atg.autonexo.features.iam.data.local.dao

import androidx.room.*
import com.atg.autonexo.features.iam.data.local.models.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de usuario en Room
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?
    
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?
}
