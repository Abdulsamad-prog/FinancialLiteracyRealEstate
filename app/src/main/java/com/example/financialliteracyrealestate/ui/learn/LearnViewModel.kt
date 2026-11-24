package com.example.financialliteracyrealestate.ui.learn

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LiteracyModule(
    val id: String,
    val category: String,
    val title: String,
    val readTimeMinutes: Int,
    val summary: String,
    val keyTakeaways: List<String>,
    val kenyaProTip: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

val kenyanLiteracyModules = listOf(
    LiteracyModule(
        id = "ardhisasa_due_diligence",
        category = "Due Diligence",
        title = "Title Deed Verification & Ardhisasa Search",
        readTimeMinutes = 4,
        summary = "Before paying any deposit on Kenyan land or an apartment, verify ownership, encumbrances, and boundary maps directly through official Ministry of Lands channels.",
        keyTakeaways = listOf(
            "Request a copy of the Title Deed and seller ID/KRA PIN, then conduct an official search on Ardhisasa (KES 1,000 fee).",
            "Confirm whether the title is Freehold (absolute ownership) or Leasehold (typically 33, 50, or 99 years with annual land rent).",
            "Check the Registry Index Map (RIM) with a licensed surveyor to verify the physical beacons match the deed.",
            "For agricultural land, ensure Land Control Board (LCB) consent is obtained before transfer."
        ),
        kenyaProTip = "Never pay land fees or stamp duty in cash to an agent—all Ministry of Lands fees must be paid against an official ArdhiPay/eCitizen invoice."
    ),
    LiteracyModule(
        id = "stamp_duty_closing_costs",
        category = "Closing Costs",
        title = "Stamp Duty, Legal Fees & Hidden Transfer Costs",
        readTimeMinutes = 3,
        summary = "Budgeting only for the purchase price is the most common mistake first-time Kenyan buyers make. Closing costs add 6% to 8% on top of the property price.",
        keyTakeaways = listOf(
            "Stamp Duty is 4% of market value in cities, municipalities, and gazetted towns (e.g., Nairobi, Ruiru, Kitengela) and 2% for rural agricultural land.",
            "Stamp duty is charged on the higher of the agreed sale price or the Government Valuer's assessment.",
            "Advocate conveyancing fees follow the Advocates Remuneration Order (roughly 1%–2% plus 16% VAT).",
            "First-time buyers under the government Affordable Housing Program (AHP) qualify for stamp duty exemption."
        ),
        kenyaProTip = "On a KES 5,000,000 urban plot, set aside at least KES 200,000 for 4% stamp duty plus ~KES 100,000 for legal, valuation, and registration fees."
    ),
    LiteracyModule(
        id = "reits_vs_plots",
        category = "Investing",
        title = "NSE REITs vs. Buying Physical Plots",
        readTimeMinutes = 4,
        summary = "You do not need millions of shillings to invest in Kenyan real estate. Regulated Real Estate Investment Trusts (REITs) let you own institutional property with small capital.",
        keyTakeaways = listOf(
            "Income REITs (I-REITs) like Acorn Student Accommodation I-REIT distribute rental income as dividends to unit holders.",
            "Development REITs (D-REITs) pool investor capital to construct commercial or residential projects for sale or lease.",
            "Physical plots offer direct control and capital appreciation in growth corridors, but carry illiquidity and fencing/security costs.",
            "Combining monthly I-REIT contributions with a long-term land/deposit goal balances liquidity and growth."
        ),
        kenyaProTip = "Compare a property's Net Rental Yield in the Calculator tab against NSE I-REIT dividend yields and Treasury bond rates before locking up capital."
    ),
    LiteracyModule(
        id = "kra_property_taxes",
        category = "Taxation",
        title = "KRA Rental Income Tax (MRI) & Capital Gains Tax",
        readTimeMinutes = 3,
        summary = "Understanding Kenyan landlord and seller taxes protects your net ROI from penalties and surprises at exit.",
        keyTakeaways = listOf(
            "Residential landlords earning between KES 288,000 and KES 15,000,000 per year pay Monthly Rental Income (MRI) tax at 7.5% on gross rent.",
            "MRI returns and payments are due on iTax on or before the 20th day of the following month.",
            "Commercial rental income and landlords earning over KES 15M/year file annual income tax returns where allowable expenses are deductible.",
            "When selling property, the seller pays Capital Gains Tax (CGT) at 15% on the net profit."
        ),
        kenyaProTip = "Always factor the 7.5% KRA MRI tax and service charges into your monthly expenses when calculating net cashflow."
    )
)

val kenyanReadinessQuiz = listOf(
    QuizQuestion(
        id = 1,
        question = "What is the statutory Stamp Duty rate for residential property inside a Kenyan municipality or gazetted town?",
        options = listOf("2% of purchase price", "4% of assessed value", "7.5% of purchase price", "15% of net gain"),
        correctOptionIndex = 1,
        explanation = "Urban and municipal properties attract 4% stamp duty on the higher of the purchase price or government valuation, whereas rural agricultural land attracts 2%."
    ),
    QuizQuestion(
        id = 2,
        question = "Which official digital platform is used in Kenya to conduct a land title search and pay stamp duty invoices?",
        options = listOf("iTax Only", "Ardhisasa / ArdhiPay", "Boma Yangu", "NSE Portal"),
        correctOptionIndex = 1,
        explanation = "Ardhisasa (linked with ArdhiPay/eCitizen) is the Ministry of Lands platform for official land searches, valuations, and stamp duty payments."
    ),
    QuizQuestion(
        id = 3,
        question = "What is the KRA Monthly Rental Income (MRI) tax rate for eligible residential landlords in Kenya?",
        options = listOf("4% on net rent", "7.5% on gross rent", "15% on gross rent", "30% on net rent"),
        correctOptionIndex = 1,
        explanation = "Resident landlords earning between KES 288,000 and KES 15M annually pay 7.5% MRI tax on gross residential rent by the 20th of the next month."
    ),
    QuizQuestion(
        id = 4,
        question = "Who is legally responsible for paying the 15% Capital Gains Tax (CGT) when a Kenyan property is sold?",
        options = listOf("The Buyer", "The Seller", "The County Government", "The Buyer's Advocate"),
        correctOptionIndex = 1,
        explanation = "Capital Gains Tax (15% on the net gain) is paid by the seller upon transfer of the property."
    ),
    QuizQuestion(
        id = 5,
        question = "How do Income REITs (I-REITs) on the Nairobi Securities Exchange generate returns for investors?",
        options = listOf(
            "By selling unverified rural plots",
            "By distributing rental income from income-generating properties as dividends",
            "By guaranteeing fixed bank loan interest",
            "By waiving KRA income taxes on salaries"
        ),
        correctOptionIndex = 1,
        explanation = "I-REITs own stabilized, income-producing real estate (such as student hostels or offices) and distribute rental profits to unit holders."
    )
)

data class LearnUiState(
    val selectedTabIndex: Int = 0, // 0 = Guides, 1 = Readiness Quiz
    val selectedCategory: String = "All",
    val expandedModuleIds: Set<String> = setOf("ardhisasa_due_diligence"),
    val completedModuleIds: Set<String> = emptySet(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val score: Int = 0,
    val quizFinished: Boolean = false
)

class LearnViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LearnUiState())
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun toggleModuleExpanded(moduleId: String) {
        _uiState.update { state ->
            val updated = if (moduleId in state.expandedModuleIds) {
                state.expandedModuleIds - moduleId
            } else {
                state.expandedModuleIds + moduleId
            }
            state.copy(expandedModuleIds = updated)
        }
    }

    fun toggleModuleCompleted(moduleId: String) {
        _uiState.update { state ->
            val updated = if (moduleId in state.completedModuleIds) {
                state.completedModuleIds - moduleId
            } else {
                state.completedModuleIds + moduleId
            }
            state.copy(completedModuleIds = updated)
        }
    }

    fun selectQuizOption(optionIndex: Int) {
        _uiState.update { state ->
            if (state.selectedOptionIndex != null) return@update state
            val currentQuestion = kenyanReadinessQuiz[state.currentQuestionIndex]
            val isCorrect = optionIndex == currentQuestion.correctOptionIndex
            state.copy(
                selectedOptionIndex = optionIndex,
                score = if (isCorrect) state.score + 1 else state.score
            )
        }
    }

    fun nextQuestion() {
        _uiState.update { state ->
            val nextIdx = state.currentQuestionIndex + 1
            if (nextIdx >= kenyanReadinessQuiz.size) {
                state.copy(quizFinished = true)
            } else {
                state.copy(
                    currentQuestionIndex = nextIdx,
                    selectedOptionIndex = null
                )
            }
        }
    }

    fun restartQuiz() {
        _uiState.update {
            it.copy(
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                score = 0,
                quizFinished = false
            )
        }
    }
}