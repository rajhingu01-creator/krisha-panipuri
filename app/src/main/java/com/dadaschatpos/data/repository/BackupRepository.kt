package com.dadaschatpos.data.repository

import androidx.room.withTransaction
import com.dadaschatpos.data.PosDatabase
import com.dadaschatpos.data.model.BackupPayload
import com.google.gson.Gson

class BackupRepository(private val database: PosDatabase) {
    private val gson = Gson()

    suspend fun exportJson(): String {
        val payload = BackupPayload(
            exportedAt = System.currentTimeMillis(),
            users = database.userDao().getAll(),
            items = database.itemDao().getAll(),
            orders = database.orderDao().getAllOrders(),
            orderItems = database.orderDao().getAllOrderItems()
        )
        return gson.toJson(payload)
    }

    suspend fun importJson(json: String) {
        val payload = gson.fromJson(json, BackupPayload::class.java)
            ?: error("Invalid backup file")
        database.withTransaction {
            database.userDao().insertAll(payload.users)
            database.itemDao().insertAll(payload.items)
            database.orderDao().insertOrdersReplace(payload.orders)
            database.orderDao().insertOrderItemsReplace(payload.orderItems)
        }
    }
}
