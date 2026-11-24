package com.example.financialliteracyrealestate.ui.learn

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    viewModel: LearnViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Study Guides", "Readiness Quiz")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Literacy Hub") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                tabs.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = uiState.selectedTabIndex == index,
                        onClick = { viewModel.selectTab(index) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                        label = { Text(label) }
                    )
                }
            }

            if (uiState.selectedTabIndex == 0) {
                StudyGuidesSection(
                    uiState = uiState,
                    onSelectCategory = viewModel::selectCategory,
                    onToggleExpand = viewModel::toggleModuleExpanded,
                    onToggleComplete = viewModel::toggleModuleCompleted
                )
            } else {
                ReadinessQuizSection(
                    uiState = uiState,
                    onSelectOption = viewModel::selectQuizOption,
                    onNextQuestion = viewModel::nextQuestion,
                    onRestartQuiz = viewModel::restartQuiz
                )
            }
        }
    }
}

@Composable
private fun StudyGuidesSection(
    uiState: LearnUiState,
    onSelectCategory: (String) -> Unit,
    onToggleExpand: (String) -> Unit,
    onToggleComplete: (String) -> Unit
) {
    val categories = listOf("All", "Due Diligence", "Closing Costs", "Investing", "Taxation")
    val visibleModules = if (uiState.selectedCategory == "All") {
        kenyanLiteracyModules
    } else {
        kenyanLiteracyModules.filter { it.category == uiState.selectedCategory }
    }

    val completedCount = uiState.completedModuleIds.size
    val totalCount = kenyanLiteracyModules.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
    ) {
        item {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Buyer Readiness Progress",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$completedCount / $totalCount completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onSelectCategory(category) },
                        label = { Text(category) }
                    )
                }
            }
        }

        items(visibleModules, key = { it.id }) { module ->
            val isExpanded = module.id in uiState.expandedModuleIds
            val isCompleted = module.id in uiState.completedModuleIds

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .clickable { onToggleExpand(module.id) }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text(module.category) }
                            )
                            Text(
                                text = "${module.readTimeMinutes} min read",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }

                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = module.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isExpanded) {
                        HorizontalDivider()
                        Text(
                            text = "Key Takeaways:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        module.keyTakeaways.forEach { point ->
                            Text(
                                text = "• $point",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "Kenya Pro Tip: ${module.kenyaProTip}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            FilledTonalButton(onClick = { onToggleComplete(module.id) }) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isCompleted) "Completed" else "Mark as Read")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadinessQuizSection(
    uiState: LearnUiState,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (uiState.quizFinished) {
            val total = kenyanReadinessQuiz.size
            val passed = uiState.score >= 4

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (passed) "Property Buyer Ready!" else "Keep Reviewing the Guides",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "You scored ${uiState.score} out of $total on the Kenyan Real Estate Readiness Quiz.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Button(onClick = onRestartQuiz) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake Quiz")
                    }
                }
            }
        } else {
            val question = kenyanReadinessQuiz[uiState.currentQuestionIndex]
            val total = kenyanReadinessQuiz.size
            val progress = (uiState.currentQuestionIndex + 1).toFloat() / total

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Question ${uiState.currentQuestionIndex + 1} of $total",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Score: ${uiState.score}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    question.options.forEachIndexed { idx, optionText ->
                        val isAnswered = uiState.selectedOptionIndex != null
                        val isSelected = uiState.selectedOptionIndex == idx
                        val isCorrect = idx == question.correctOptionIndex

                        OutlinedButton(
                            onClick = { onSelectOption(idx) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = when {
                                isAnswered && isCorrect -> ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                                isAnswered && isSelected -> ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                                else -> ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = optionText,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isAnswered && isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct")
                                } else if (isAnswered && isSelected) {
                                    Icon(Icons.Default.Close, contentDescription = "Incorrect")
                                }
                            }
                        }
                    }

                    if (uiState.selectedOptionIndex != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = onNextQuestion) {
                                Text(
                                    if (uiState.currentQuestionIndex + 1 == total) "See Results"
                                    else "Next Question"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}