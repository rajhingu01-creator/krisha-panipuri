package com.dadaschatpos.data.model

data class BackupPayload(
    val exportedAt: Long,
    val users: List<UserEntity>,
    val items: List<ItemEntity>,
    val orders: List<OrderEntity>,
    val orderItems: List<OrderItemEntity>
)
