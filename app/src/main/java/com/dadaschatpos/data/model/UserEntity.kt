package com.dadaschatpos.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val shopName: String,
    val mobile: String,
    val email: String,
    val password: String,
    val address: String = "Main Road, Your City"
)
