package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    val date: String,
    val items: String,
    val notes: String = "",
    val status: String = "جديد", // "جديد" (New), "قيد التوصيل" (In Delivery), "مكتمل" (Completed), "ملغي" (Cancelled)
    val price: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
