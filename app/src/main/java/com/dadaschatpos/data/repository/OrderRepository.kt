package com.dadaschatpos.data.repository

import androidx.room.withTransaction
import com.dadaschatpos.data.PosDatabase
import com.dadaschatpos.data.model.CartItem
import com.dadaschatpos.data.model.OrderEntity
import com.dadaschatpos.data.model.OrderItemEntity
import com.dadaschatpos.data.model.OrderWithItems
import com.dadaschatpos.data.model.ReportFilter
import com.dadaschatpos.data.model.ReportSummary
import com.dadaschatpos.util.DateTimeUtils
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val database: PosDatabase) {
    private val orderDao = database.orderDao()

    suspend fun createOrder(cart: List<CartItem>): Long = database.withTransaction {
        require(cart.isNotEmpty()) { "Cart is empty" }
        val total = cart.sumOf { it.amount }
        val orderId = orderDao.insertOrder(
            OrderEntity(
                date = System.currentTimeMillis(),
                total = total
            )
        )
        val orderItems = cart.map { cartItem ->
            OrderItemEntity(
                orderId = orderId,
                itemId = cartItem.item.id,
                itemName = cartItem.item.name,
                qty = cartItem.quantity,
                price = cartItem.item.price,
                category = cartItem.item.category
            )
        }
        orderDao.insertOrderItems(orderItems)
        orderId
    }

    fun observeOrder(orderId: Long): Flow<OrderWithItems?> = if (orderId > 0) {
        orderDao.observeOrderWithItems(orderId)
    } else {
        orderDao.observeLatestOrderWithItems()
    }

    suspend fun report(filter: ReportFilter): ReportSummary {
        val (start, end) = DateTimeUtils.rangeFor(filter)
        return ReportSummary(
            totalSales = orderDao.totalSales(start, end),
            totalBills = orderDao.totalBills(start, end),
            totalItemsSold = orderDao.totalItemsSold(start, end),
            averageBillValue = orderDao.averageBillValue(start, end),
            topProducts = orderDao.topProducts(start, end),
            salesChart = orderDao.salesChart(start, end),
            categoryChart = orderDao.categorySales(start, end)
        )
    }
}
