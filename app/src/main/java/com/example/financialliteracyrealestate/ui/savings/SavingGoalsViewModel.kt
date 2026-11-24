package com.example.financialliteracyrealestate.ui.savings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialliteracyrealestate.data.local.AppDatabase
import com.example.financialliteracyrealestate.data.local.SavingsGoalEntity
import com.example.financialliteracyrealestate.data.local.TransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SavingsUiState(
    val goals: List<SavingsGoalEntity> = emptyList(),
    val totalSavedKes: Double = 0.0,
    val totalTargetKes: Double = 0.0,
    val totalMonthlyTargetKes: Double = 0.0
)

class SavingsGoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val savingsGoalDao = database.savingsGoalDao()
    private val transactionDao = database.transactionDao()

    val uiState: StateFlow<SavingsUiState> = savingsGoalDao.getAllGoals()
        .map { goals ->
            SavingsUiState(
                goals = goals,
                totalSavedKes = goals.sumOf { it.savedAmountKes },
                totalTargetKes = goals.sumOf { it.targetAmountKes },
                totalMonthlyTargetKes = goals.sumOf { it.monthlyTargetKes }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SavingsUiState()
        )

    fun addGoal(
        title: String,
        location: String,
        category: String,
        targetAmountKes: Double,
        monthlyTargetKes: Double,
        initialDepositKes: Double = 0.0,
        mpesaRef: String = ""
    ) {
        if (title.isBlank() || targetAmountKes <= 0.0) return

        viewModelScope.launch {
            val cleanTitle = title.trim()
            val cleanLocation = location.trim().ifBlank { "Nairobi" }
            val safeInitial = initialDepositKes.coerceAtLeast(0.0)

            val goal = SavingsGoalEntity(
                title = cleanTitle,
                location = cleanLocation,
                targetAmountKes = targetAmountKes,
                savedAmountKes = safeInitial,
                monthlyTargetKes = monthlyTargetKes.coerceAtLeast(0.0),
                category = category
            )
            savingsGoalDao.insertGoal(goal)

            if (safeInitial > 0.0) {
                transactionDao.insertTransaction(
                    TransactionEntity(
                        goalId = null,
                        title = "Initial Deposit: $cleanTitle",
                        amountKes = safeInitial,
                        type = "DEPOSIT",
                        mpesaRef = mpesaRef.trim().ifBlank { generateMpesaRef() },
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun logDeposit(goal: SavingsGoalEntity, depositAmountKes: Double, mpesaRef: String = "") {
        if (depositAmountKes <= 0.0) return

        viewModelScope.launch {
            val updatedGoal = goal.copy(
                savedAmountKes = goal.savedAmountKes + depositAmountKes
            )
            savingsGoalDao.updateGoal(updatedGoal)

            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = goal.id,
                    title = "Deposit: ${goal.title}",
                    amountKes = depositAmountKes,
                    type = "DEPOSIT",
                    mpesaRef = mpesaRef.trim().ifBlank { generateMpesaRef() },
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            savingsGoalDao.deleteGoal(goal)
        }
    }

    private fun generateMpesaRef(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val randomCode = (1..8).map { chars.random() }.joinToString("")
        return "MP$randomCode"
    }
}