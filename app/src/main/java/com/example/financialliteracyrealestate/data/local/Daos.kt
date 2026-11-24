package com.example.financialliteracyrealestate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :id")
    suspend fun getGoalById(id: Int): SavingsGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoalEntity)

    @Update
    suspend fun updateGoal(goal: SavingsGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: SavingsGoalEntity)

    @Query("UPDATE savings_goals SET savedAmountKes = savedAmountKes + :amount WHERE id = :goalId")
    suspend fun addFundsToGoal(goalId: Int, amount: Double)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT SUM(amountKes) FROM transactions WHERE type = 'DEPOSIT'")
    fun getTotalDeposits(): Flow<Double?>

    @Query("SELECT SUM(amountKes) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double?>
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY id ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :completed WHERE id = :lessonId")
    suspend fun setLessonCompleted(lessonId: Int, completed: Boolean)
}