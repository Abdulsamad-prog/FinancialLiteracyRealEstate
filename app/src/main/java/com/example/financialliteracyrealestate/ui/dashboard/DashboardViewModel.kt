package com.example.financialliteracyrealestate.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financialliteracyrealestate.data.local.AppDatabase
import com.example.financialliteracyrealestate.data.local.SavingsGoalEntity
import com.example.financialliteracyrealestate.data.local.TransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalSavedKes: Double = 0.0,
    val totalTargetKes: Double = 0.0,
    val totalDepositsKes: Double = 0.0,
    val totalExpensesKes: Double = 0.0,
    val netCashflowKes: Double = 0.0,
    val goals: List<SavingsGoalEntity> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(private val db: AppDatabase) : ViewModel() {

    private val goalDao = db.savingsGoalDao()
    private val transactionDao = db.transactionDao()

    val uiState: StateFlow<DashboardUiState> = combine(
        goalDao.getAllGoals(),
        transactionDao.getAllTransactions(),
        transactionDao.getTotalDeposits(),
        transactionDao.getTotalExpenses()
    ) { goals, transactions, deposits, expenses ->
        val totalSaved = goals.sumOf { it.savedAmountKes }
        val totalTarget = goals.sumOf { it.targetAmountKes }
        val safeDeposits = deposits ?: 0.0
        val safeExpenses = expenses ?: 0.0

        DashboardUiState(
            totalSavedKes = totalSaved,
            totalTargetKes = totalTarget,
            totalDepositsKes = safeDeposits,
            totalExpensesKes = safeExpenses,
            netCashflowKes = safeDeposits - safeExpenses,
            goals = goals,
            recentTransactions = transactions,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    fun logTransaction(
        title: String,
        amountKes: Double,
        type: String,
        mpesaRef: String,
        goalId: Int?
    ) {
        if (title.isBlank() || amountKes <= 0.0) return

        viewModelScope.launch {
            val cleanRef = mpesaRef.trim().uppercase().ifBlank { generateSampleMpesaRef() }
            transactionDao.insertTransaction(
                TransactionEntity(
                    goalId = if (type == "DEPOSIT") goalId else null,
                    title = title.trim(),
                    amountKes = amountKes,
                    type = type,
                    mpesaRef = cleanRef,
                    timestamp = System.currentTimeMillis()
                )
            )

            if (type == "DEPOSIT" && goalId != null) {
                goalDao.addFundsToGoal(goalId = goalId, amount = amountKes)
            }
        }
    }

    private fun generateSampleMpesaRef(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return "Q" + (1..9).map { chars.random() }.joinToString("")
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(AppDatabase.getDatabase(context)) as T
        }
    }
}