package com.example.financialliteracyrealestate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SavingsGoalEntity::class,
        TransactionEntity::class,
        LessonEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun transactionDao(): TransactionDao
    abstract fun lessonDao(): LessonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finlit_realestate_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val goalDao = db.savingsGoalDao()
            val txDao = db.transactionDao()
            val lessonDao = db.lessonDao()

            goalDao.insertGoal(
                SavingsGoalEntity(
                    title = "Juja 50x100 Serviced Plot",
                    location = "Juja, Kiambu",
                    targetAmountKes = 850_000.0,
                    savedAmountKes = 320_000.0,
                    monthlyTargetKes = 25_000.0,
                    category = "Land"
                )
            )
            goalDao.insertGoal(
                SavingsGoalEntity(
                    title = "Syokimau 2BR Apartment Deposit",
                    location = "Syokimau, Machakos",
                    targetAmountKes = 1_500_000.0,
                    savedAmountKes = 450_000.0,
                    monthlyTargetKes = 40_000.0,
                    category = "Apartment"
                )
            )
            goalDao.insertGoal(
                SavingsGoalEntity(
                    title = "Acorn I-REIT Income Portfolio",
                    location = "Nairobi Securities Exchange",
                    targetAmountKes = 250_000.0,
                    savedAmountKes = 110_000.0,
                    monthlyTargetKes = 10_000.0,
                    category = "REIT"
                )
            )

            txDao.insertTransaction(
                TransactionEntity(
                    goalId = 1,
                    title = "M-Pesa Deposit - Juja Plot",
                    amountKes = 25_000.0,
                    type = "DEPOSIT",
                    mpesaRef = "QK84L9P2X1"
                )
            )
            txDao.insertTransaction(
                TransactionEntity(
                    goalId = 2,
                    title = "M-Pesa Deposit - Syokimau Deposit",
                    amountKes = 40_000.0,
                    type = "DEPOSIT",
                    mpesaRef = "QL19M7R4T8"
                )
            )
            txDao.insertTransaction(
                TransactionEntity(
                    goalId = null,
                    title = "Ardhisasa Land Search Fee",
                    amountKes = 500.0,
                    type = "EXPENSE",
                    mpesaRef = "QM52N3W6Y0"
                )
            )

            lessonDao.insertLessons(
                listOf(
                    LessonEntity(
                        title = "How to Verify a Title Deed on Ardhisasa",
                        category = "Land Due Diligence",
                        durationMinutes = 4,
                        content = "Before paying any deposit for land in Kenya, obtain a copy of the title deed and national ID from the seller. Log into the Ardhisasa portal to conduct an official search, confirming the registered owner, acreage, and whether any caution, charge, or encumbrance exists on the parcel."
                    ),
                    LessonEntity(
                        title = "50/30/20 Rule for Property Down Payments",
                        category = "Personal Budgeting",
                        durationMinutes = 3,
                        content = "Allocate 50% of net monthly income to essential living costs, 30% directly toward your property deposit or SACCO shares, and 20% to flexible personal spending. Automating a standing order on payday prevents lifestyle creep from delaying home ownership."
                    ),
                    LessonEntity(
                        title = "Real Estate Investment Trusts (REITs) Explained",
                        category = "Passive Real Estate",
                        durationMinutes = 5,
                        content = "REITs allow you to invest in income-generating student housing, malls, or offices without buying an entire building. Income REITs (I-REITs) distribute at least 80% of net profits to unit holders as dividends, offering liquidity and low entry minimums."
                    ),
                    LessonEntity(
                        title = "SACCO Loans vs. Bank Mortgages",
                        category = "Property Financing",
                        durationMinutes = 6,
                        content = "SACCOs typically lend 3x to 4x your deposits at stable reducing-balance interest rates (around 12%–14% p.a.), while commercial mortgages offer longer repayment periods up to 20–25 years. Combining SACCO savings for the 20% deposit with a fixed-rate mortgage minimizes total interest."
                    )
                )
            )
        }
    }
}