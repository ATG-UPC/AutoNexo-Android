package com.atg.autonexo.features.iam.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atg.autonexo.features.iam.data.local.dao.UserDao
import com.atg.autonexo.features.iam.data.local.models.UserEntity

/**
 * Base de datos Room para el módulo IAM
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class IamDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    
    companion object {
        const val DATABASE_NAME = "iam_database"
    }
}
