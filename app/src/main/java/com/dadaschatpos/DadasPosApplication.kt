package com.dadaschatpos

import android.app.Application
import androidx.room.withTransaction
import com.dadaschatpos.data.DefaultData
import com.dadaschatpos.data.PosDatabase
import com.dadaschatpos.data.repository.AuthRepository
import com.dadaschatpos.data.repository.BackupRepository
import com.dadaschatpos.data.repository.ItemRepository
import com.dadaschatpos.data.repository.OrderRepository
import com.dadaschatpos.util.NotificationScheduler
import com.dadaschatpos.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DadasPosApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val sessionManager by lazy { SessionManager(this) }
    val database by lazy { PosDatabase.getDatabase(this) }
    val authRepository by lazy { AuthRepository(database.userDao(), sessionManager) }
    val itemRepository by lazy { ItemRepository(database.itemDao()) }
    val orderRepository by lazy { OrderRepository(database) }
    val backupRepository by lazy { BackupRepository(database) }

    override fun onCreate() {
        super.onCreate()
        NotificationScheduler.scheduleDailySalesSummary(this)
        syncDefaultMenu()
    }

    private fun syncDefaultMenu() {
        applicationScope.launch {
            val itemDao = database.itemDao()
            val defaultItems = DefaultData.defaultItems()
            val defaultNames = defaultItems.map { it.name }
            val hasLegacyDefaultImages = itemDao.countLegacyDefaultImages(defaultNames) > 0
            val shouldSync = sessionManager.defaultMenuVersion < DefaultData.MENU_VERSION || hasLegacyDefaultImages
            if (!shouldSync) return@launch

            database.withTransaction {
                val removedItems = DefaultData.removedDefaultItemNames()
                if (removedItems.isNotEmpty()) itemDao.deleteByNames(removedItems)

                defaultItems.forEach { item ->
                    val updatedRows = itemDao.updateMenuItemByName(
                        name = item.name,
                        price = item.price,
                        image = item.image,
                        category = item.category
                    )
                    if (updatedRows == 0) itemDao.insert(item)
                }
            }
            sessionManager.saveDefaultMenuVersion(DefaultData.MENU_VERSION)
        }
    }
}
