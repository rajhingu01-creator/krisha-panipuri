package com.dadaschatpos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dadaschatpos.data.model.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name")
    fun observeItems(): Flow<List<ItemEntity>>

    @Query("""
        SELECT * FROM items
        WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'
        ORDER BY name
    """)
    fun searchItems(query: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): ItemEntity?

    @Query("SELECT COUNT(*) FROM items WHERE name = :name")
    suspend fun countByName(name: String): Int

    @Query("SELECT COUNT(*) FROM items WHERE name IN (:names) AND image LIKE 'ic_food_%'")
    suspend fun countLegacyDefaultImages(names: List<String>): Int

    @Query("""
        UPDATE items
        SET price = :price, image = :image, category = :category
        WHERE name = :name
    """)
    suspend fun updateMenuItemByName(name: String, price: Double, image: String, category: String): Int

    @Query("DELETE FROM items WHERE name IN (:names)")
    suspend fun deleteByNames(names: List<String>)

    @Query("SELECT COUNT(*) FROM items")
    suspend fun count(): Int

    @Query("SELECT * FROM items ORDER BY name")
    suspend fun getAll(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)

    @Update
    suspend fun update(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)
}
