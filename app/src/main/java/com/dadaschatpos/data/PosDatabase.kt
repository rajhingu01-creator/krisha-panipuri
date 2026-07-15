package com.dadaschatpos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dadaschatpos.data.dao.ItemDao
import com.dadaschatpos.data.dao.OrderDao
import com.dadaschatpos.data.dao.UserDao
import com.dadaschatpos.data.model.ItemEntity
import com.dadaschatpos.data.model.OrderEntity
import com.dadaschatpos.data.model.OrderItemEntity
import com.dadaschatpos.data.model.UserEntity
@Database(
    entities = [UserEntity::class, ItemEntity::class, OrderEntity::class, OrderItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PosDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: PosDatabase? = null

        fun getDatabase(context: Context): PosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PosDatabase::class.java,
                    "dadas_chat_pos.db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
