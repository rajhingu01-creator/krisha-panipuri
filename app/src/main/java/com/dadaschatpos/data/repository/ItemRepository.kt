package com.dadaschatpos.data.repository

import com.dadaschatpos.data.DefaultData
import com.dadaschatpos.data.dao.ItemDao
import com.dadaschatpos.data.model.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepository(private val itemDao: ItemDao) {
    private val menuOrder = DefaultData.defaultItems()
        .mapIndexed { index, item -> item.name to index }
        .toMap()

    fun observeItems(): Flow<List<ItemEntity>> = itemDao.observeItems()
        .map { items -> items.sortedForMenu() }

    fun searchItems(query: String): Flow<List<ItemEntity>> = if (query.isBlank()) {
        observeItems()
    } else {
        itemDao.searchItems(query.trim()).map { items -> items.sortedForMenu() }
    }

    suspend fun save(item: ItemEntity): Long {
        return if (item.id == 0L) {
            itemDao.insert(item)
        } else {
            itemDao.update(item)
            item.id
        }
    }

    suspend fun delete(item: ItemEntity) = itemDao.delete(item)

    private fun List<ItemEntity>.sortedForMenu(): List<ItemEntity> {
        return sortedWith(
            compareBy<ItemEntity> { menuOrder[it.name] ?: Int.MAX_VALUE }
                .thenBy { it.name }
        )
    }
}
