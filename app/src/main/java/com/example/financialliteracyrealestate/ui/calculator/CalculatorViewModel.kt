package com.example.financialliteracyrealestate.ui.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

data class CalculatorPreset(
    val label: String,
    val propertyPrice: String,
    val depositPercent: String,
    val interestRate: String,
    val loanTermYears: String,
    val monthlyRent: String,
    val monthlyExpenses: String
)

val kenyanCalculatorPresets = listOf(
    CalculatorPreset(
        label = "KMRC 2BR (9.5% Fixed)",
        propertyPrice = "6500000",
        depositPercent = "10",
        interestRate = "9.5",
        loanTermYears = "20",
        monthlyRent = "50000",
        monthlyExpenses = "6000"
    ),
    CalculatorPreset(
        label = "Kilimani 1BR (Bank 15.5%)",
        propertyPrice = "8500000",
        depositPercent = "20",
        interestRate = "15.5",
        loanTermYears = "15",
        monthlyRent = "75000",
        monthlyExpenses = "10000"
    ),
    CalculatorPreset(
        label = "Ruiru Rental Unit",
        propertyPrice = "4200000",
        depositPercent = "25",
        interestRate = "14.0",
        loanTermYears = "15",
        monthlyRent = "38000",
        monthlyExpenses = "4000"
    )
)

data class CalculatorUiState(
    val propertyPriceInput: String = "6500000",
    val depositPercentInput: String = "10",
    val interestRateInput: String = "9.5",
    val loanTermYearsInput: String = "20",
    val monthlyRentInput: String = "50000",
    val monthlyExpensesInput: String = "6000",
    val depositAmountKes: Double = 650000.0,
    val stampDutyKes: Double = 260000.0,
    val totalUpfrontCashKes: Double = 910000.0,
    val loanPrincipalKes: Double = 5850000.0,
    val monthlyMortgageKes: Double = 54529.0,
    val totalInterestKes: Double = 7236960.0,
    val grossYieldPercent: Double = 9.23,
    val netYieldPercent: Double = 8.12,
    val monthlyNetCashflowKes: Double = -10529.0,
    val cashOnCashRoiPercent: Double = -13.88
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(recalculate(CalculatorUiState()))
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onPropertyPriceChange(value: String) = updateInputs { it.copy(propertyPriceInput = value) }
    fun onDepositPercentChange(value: String) = updateInputs { it.copy(depositPercentInput = value) }
    fun onInterestRateChange(value: String) = updateInputs { it.copy(interestRateInput = value) }
    fun onLoanTermYearsChange(value: String) = updateInputs { it.copy(loanTermYearsInput = value) }
    fun onMonthlyRentChange(value: String) = updateInputs { it.copy(monthlyRentInput = value) }
    fun onMonthlyExpensesChange(value: String) = updateInputs { it.copy(monthlyExpensesInput = value) }

    fun applyPreset(preset: CalculatorPreset) {
        updateInputs {
            it.copy(
                propertyPriceInput = preset.propertyPrice,
                depositPercentInput = preset.depositPercent,
                interestRateInput = preset.interestRate,
                loanTermYearsInput = preset.loanTermYears,
                monthlyRentInput = preset.monthlyRent,
                monthlyExpensesInput = preset.monthlyExpenses
            )
        }
    }

    private fun updateInputs(transform: (CalculatorUiState) -> CalculatorUiState) {
        _uiState.update { current -> recalculate(transform(current)) }
    }

    private fun recalculate(state: CalculatorUiState): CalculatorUiState {
        val price = (state.propertyPriceInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
        val depositPct = (state.depositPercentInput.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 100.0)
        val annualRate = (state.interestRateInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
        val termYears = (state.loanTermYearsInput.toIntOrNull() ?: 0).coerceIn(0, 40)
        val monthlyRent = (state.monthlyRentInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
        val monthlyExpenses = (state.monthlyExpensesInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)

        val depositAmount = price * (depositPct / 100.0)
        val stampDuty = price * 0.04 // Standard 4% Kenyan urban stamp duty
        val totalUpfrontCash = depositAmount + stampDuty
        val principal = (price - depositAmount).coerceAtLeast(0.0)

        val totalMonths = termYears * 12
        val monthlyRate = (annualRate / 100.0) / 12.0

        val monthlyMortgage = when {
            principal <= 0.0 || totalMonths <= 0 -> 0.0
            monthlyRate <= 0.0 -> principal / totalMonths
            else -> {
                val factor = (1.0 + monthlyRate).pow(totalMonths)
                principal * (monthlyRate * factor) / (factor - 1.0)
            }
        }

        val totalRepayment = monthlyMortgage * totalMonths
        val totalInterest = (totalRepayment - principal).coerceAtLeast(0.0)

        val annualGrossRent = monthlyRent * 12.0
        val annualNetRent = ((monthlyRent - monthlyExpenses) * 12.0).coerceAtLeast(0.0)

        val grossYield = if (price > 0.0) (annualGrossRent / price) * 100.0 else 0.0
        val netYield = if (price > 0.0) (annualNetRent / price) * 100.0 else 0.0

        val monthlyNetCashflow = monthlyRent - monthlyExpenses - monthlyMortgage
        val cashOnCashRoi = if (totalUpfrontCash > 0.0) {
            ((monthlyNetCashflow * 12.0) / totalUpfrontCash) * 100.0
        } else {
            0.0
        }

        return state.copy(
            depositAmountKes = depositAmount,
            stampDutyKes = stampDuty,
            totalUpfrontCashKes = totalUpfrontCash,
            loanPrincipalKes = principal,
            monthlyMortgageKes = monthlyMortgage,
            totalInterestKes = totalInterest,
            grossYieldPercent = grossYield,
            netYieldPercent = netYield,
            monthlyNetCashflowKes = monthlyNetCashflow,
            cashOnCashRoiPercent = cashOnCashRoi
        )
    }
}