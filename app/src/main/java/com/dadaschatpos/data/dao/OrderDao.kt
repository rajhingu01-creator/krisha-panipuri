package com.dadaschatpos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dadaschatpos.data.model.CategorySale
import com.dadaschatpos.data.model.ChartPoint
import com.dadaschatpos.data.model.OrderEntity
import com.dadaschatpos.data.model.OrderItemEntity
import com.dadaschatpos.data.model.OrderWithItems
import com.dadaschatpos.data.model.TopProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrdersReplace(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItemsReplace(items: List<OrderItemEntity>)

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun observeOrderWithItems(orderId: Long): Flow<OrderWithItems?>

    @Transaction
    @Query("SELECT * FROM orders ORDER BY date DESC LIMIT 1")
    fun observeLatestOrderWithItems(): Flow<OrderWithItems?>

    @Query("SELECT * FROM orders ORDER BY date")
    suspend fun getAllOrders(): List<OrderEntity>

    @Query("SELECT * FROM order_items ORDER BY orderId, id")
    suspend fun getAllOrderItems(): List<OrderItemEntity>

    @Query("SELECT IFNULL(SUM(total), 0) FROM orders WHERE date BETWEEN :start AND :end")
    suspend fun totalSales(start: Long, end: Long): Double

    @Query("SELECT COUNT(*) FROM orders WHERE date BETWEEN :start AND :end")
    suspend fun totalBills(start: Long, end: Long): Int

    @Query("""
        SELECT IFNULL(SUM(order_items.qty), 0)
        FROM order_items
        INNER JOIN orders ON orders.id = order_items.orderId
        WHERE orders.date BETWEEN :start AND :end
    """)
    suspend fun totalItemsSold(start: Long, end: Long): Int

    @Query("SELECT IFNULL(AVG(total), 0) FROM orders WHERE date BETWEEN :start AND :end")
    suspend fun averageBillValue(start: Long, end: Long): Double

    @Query("""
        SELECT itemName AS name,
               CAST(SUM(qty) AS INTEGER) AS qty,
               SUM(qty * price) AS total
        FROM order_items
        INNER JOIN orders ON orders.id = order_items.orderId
        WHERE orders.date BETWEEN :start AND :end
        GROUP BY itemName
        ORDER BY qty DESC
        LIMIT 10
    """)
    suspend fun topProducts(start: Long, end: Long): List<TopProduct>

    @Query("""
        SELECT strftime('%d/%m', orders.date / 1000, 'unixepoch', 'localtime') AS label,
               SUM(total) AS total
        FROM orders
        WHERE date BETWEEN :start AND :end
        GROUP BY label
        ORDER BY MIN(date)
    """)
    suspend fun salesChart(start: Long, end: Long): List<ChartPoint>

    @Query("""
        SELECT category AS category,
               SUM(qty * price) AS total
        FROM order_items
        INNER JOIN orders ON orders.id = order_items.orderId
        WHERE orders.date BETWEEN :start AND :end
        GROUP BY category
        ORDER BY total DESC
    """)
    suspend fun categorySales(start: Long, end: Long): List<CategorySale>
}
