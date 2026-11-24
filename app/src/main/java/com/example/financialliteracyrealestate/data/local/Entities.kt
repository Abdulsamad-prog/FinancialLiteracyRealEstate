package com.example.financialliteracyrealestate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val location: String,
    val targetAmountKes: Double,
    val savedAmountKes: Double,
    val monthlyTargetKes: Double,
    val category: String // e.g., "Land", "Apartment", "REIT"
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int? = null,
    val title: String,
    val amountKes: Double,
    val type: String, // "DEPOSIT" or "EXPENSE"
    val mpesaRef: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val content: String,
    val isCompleted: Boolean = false
)