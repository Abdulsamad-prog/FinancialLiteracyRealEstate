package com.example.financialliteracyrealestate.ui.calculator

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mortgage & ROI Calculator") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Kenyan Financing Presets",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    kenyanCalculatorPresets.forEach { preset ->
                        FilterChip(
                            selected = false,
                            onClick = { viewModel.applyPreset(preset) },
                            label = { Text(preset.label) }
                        )
                    }
                }
            }

            MortgageAndRoiSummaryCard(uiState = uiState)

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Property & Mortgage Terms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.propertyPriceInput,
                        onValueChange = viewModel::onPropertyPriceChange,
                        label = { Text("Property Price (KES)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.depositPercentInput,
                            onValueChange = viewModel::onDepositPercentChange,
                            label = { Text("Deposit (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.interestRateInput,
                            onValueChange = viewModel::onInterestRateChange,
                            label = { Text("Interest (% p.a.)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.loanTermYearsInput,
                            onValueChange = viewModel::onLoanTermYearsChange,
                            label = { Text("Years") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "2. Rental Income & Operating Costs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.monthlyRentInput,
                            onValueChange = viewModel::onMonthlyRentChange,
                            label = { Text("Monthly Rent (KES)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.monthlyExpensesInput,
                            onValueChange = viewModel::onMonthlyExpensesChange,
                            label = { Text("Service/Maint. (KES)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            BreakdownCard(uiState = uiState)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MortgageAndRoiSummaryCard(uiState: CalculatorUiState) {
    val cashflowPositive = uiState.monthlyNetCashflowKes >= 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Estimated Monthly Mortgage",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "${formatKes(uiState.monthlyMortgageKes)} / mo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = "Net Rental Yield",
                    value = formatPercent(uiState.netYieldPercent)
                )
                MetricItem(
                    label = "Cash-on-Cash ROI",
                    value = formatPercent(uiState.cashOnCashRoiPercent)
                )
                MetricItem(
                    label = "Monthly Cashflow",
                    value = (if (cashflowPositive) "+" else "") + formatKes(uiState.monthlyNetCashflowKes)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun BreakdownCard(uiState: CalculatorUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Kenyan Closing & Loan Breakdown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            DetailRow("Down Payment (Deposit)", formatKes(uiState.depositAmountKes))
            DetailRow("Est. Stamp Duty (4% Urban)", formatKes(uiState.stampDutyKes))
            DetailRow("Total Upfront Cash Needed", formatKes(uiState.totalUpfrontCashKes), bold = true)
            HorizontalDivider()
            DetailRow("Mortgage Principal", formatKes(uiState.loanPrincipalKes))
            DetailRow("Total Interest Over Term", formatKes(uiState.totalInterestKes))
            DetailRow("Gross Rental Yield", formatPercent(uiState.grossYieldPercent))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

private fun formatKes(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }
    return "KES ${formatter.format(amount)}"
}

private fun formatPercent(value: Double): String {
    return String.format(Locale.US, "%.1f%%", value)
}