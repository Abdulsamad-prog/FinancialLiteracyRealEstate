package com.example.financialliteracyrealestate.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialliteracyrealestate.data.local.SavingsGoalEntity
import java.text.NumberFormat
import java.util.Locale

private data class PropertyPreset(
    val title: String,
    val location: String,
    val category: String,
    val defaultTargetKes: String,
    val defaultMonthlyKes: String
)

private val kenyanPropertyPresets = listOf(
    PropertyPreset("Acorn Student I-REIT", "Nairobi (NSE)", "REIT", "50000", "5000"),
    PropertyPreset("Ruiru 1/8-Acre Plot", "Ruiru, Kiambu", "Land", "650000", "25000"),
    PropertyPreset("Kilimani 1BR Off-Plan Deposit", "Kilimani, Nairobi", "Apartment", "900000", "35000")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SavingsGoalsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var goalForDeposit by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property & REIT Goals") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Goal") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
        ) {
            item {
                PortfolioSummaryCard(
                    totalSavedKes = uiState.totalSavedKes,
                    totalTargetKes = uiState.totalTargetKes,
                    totalMonthlyTargetKes = uiState.totalMonthlyTargetKes,
                    goalCount = uiState.goals.size
                )
            }

            if (uiState.goals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "No property savings goals yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tap + New Goal to start saving toward a Kenyan REIT, land parcel, or apartment deposit.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(uiState.goals, key = { it.id }) { goal ->
                    SavingsGoalCard(
                        goal = goal,
                        onDepositClick = { goalForDeposit = goal },
                        onDeleteClick = { viewModel.deleteGoal(goal) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSavingsGoalDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, location, category, target, monthly, initial, mpesaRef ->
                viewModel.addGoal(title, location, category, target, monthly, initial, mpesaRef)
                showAddDialog = false
            }
        )
    }

    goalForDeposit?.let { selectedGoal ->
        LogDepositDialog(
            goal = selectedGoal,
            onDismiss = { goalForDeposit = null },
            onConfirm = { amount, mpesaRef ->
                viewModel.logDeposit(selectedGoal, amount, mpesaRef)
                goalForDeposit = null
            }
        )
    }
}

@Composable
private fun PortfolioSummaryCard(
    totalSavedKes: Double,
    totalTargetKes: Double,
    totalMonthlyTargetKes: Double,
    goalCount: Int
) {
    val overallProgress = if (totalTargetKes > 0.0) {
        (totalSavedKes / totalTargetKes).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Total Property Capital Saved",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = formatKes(totalSavedKes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$goalCount goal(s) • ${formatKes(totalMonthlyTargetKes)}/mo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Target: ${formatKes(totalTargetKes)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SavingsGoalCard(
    goal: SavingsGoalEntity,
    onDepositClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val progress = if (goal.targetAmountKes > 0.0) {
        (goal.savedAmountKes / goal.targetAmountKes).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val percentText = "${(progress * 100).toInt()}%"

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(goal.category) }
                        )
                        Text(
                            text = goal.location,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete goal",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${formatKes(goal.savedAmountKes)} / ${formatKes(goal.targetAmountKes)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$percentText funded • Plan: ${formatKes(goal.monthlyTargetKes)}/mo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(onClick = onDepositClick) {
                    Text("+ Deposit")
                }
            }
        }
    }
}

@Composable
private fun AddSavingsGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        location: String,
        category: String,
        targetKes: Double,
        monthlyKes: Double,
        initialKes: Double,
        mpesaRef: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("REIT") }
    var targetInput by remember { mutableStateOf("") }
    var monthlyInput by remember { mutableStateOf("") }
    var initialDepositInput by remember { mutableStateOf("") }
    var mpesaRefInput by remember { mutableStateOf("") }
    val categories = listOf("Land", "Apartment", "REIT")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Property Savings Goal") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Quick Kenyan Presets:",
                    style = MaterialTheme.typography.labelMedium
                )
                kenyanPropertyPresets.forEach { preset ->
                    OutlinedButton(
                        onClick = {
                            title = preset.title
                            location = preset.location
                            category = preset.category
                            targetInput = preset.defaultTargetKes
                            monthlyInput = preset.defaultMonthlyKes
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(preset.title, maxLines = 1)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (e.g., Ruiru, Kilimani)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text(item) }
                        )
                    }
                }

                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    label = { Text("Target Amount (KES)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = monthlyInput,
                    onValueChange = { monthlyInput = it },
                    label = { Text("Monthly Savings Target (KES)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = initialDepositInput,
                    onValueChange = { initialDepositInput = it },
                    label = { Text("Initial Deposit (KES, optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if ((initialDepositInput.toDoubleOrNull() ?: 0.0) > 0.0) {
                    OutlinedTextField(
                        value = mpesaRefInput,
                        onValueChange = { mpesaRefInput = it },
                        label = { Text("M-Pesa Ref (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val target = targetInput.toDoubleOrNull() ?: 0.0
                    val monthly = monthlyInput.toDoubleOrNull() ?: 0.0
                    val initial = initialDepositInput.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && target > 0.0) {
                        onConfirm(title, location, category, target, monthly, initial, mpesaRefInput)
                    }
                }
            ) {
                Text("Save Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LogDepositDialog(
    goal: SavingsGoalEntity,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    var mpesaRefInput by remember { mutableStateOf("") }
    val remaining = (goal.targetAmountKes - goal.savedAmountKes).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deposit to ${goal.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Remaining to target: ${formatKes(remaining)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Deposit Amount (KES)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mpesaRefInput,
                    onValueChange = { mpesaRefInput = it },
                    label = { Text("M-Pesa Ref Code (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Logs a DEPOSIT transaction linked to this goal in your ledger.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    if (amount > 0.0) {
                        onConfirm(amount, mpesaRefInput)
                    }
                }
            ) {
                Text("Confirm Deposit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatKes(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }
    return "KES ${formatter.format(amount)}"
}