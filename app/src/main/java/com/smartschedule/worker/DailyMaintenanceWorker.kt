package com.smartschedule.worker

import android.content.Context
import androidx.work.*
import com.smartschedule.data.db.AppDatabase
import com.smartschedule.data.repository.TaskRepository
import com.smartschedule.util.Constants
import com.smartschedule.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DailyMaintenanceWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val repository = TaskRepository(db.taskDao())
                repository.deleteOldOverdueTasks(DateUtils.getThreeDaysAgoNormalized())
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }

    companion object {
        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<DailyMaintenanceWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(1, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WORK_NAME_DAILY_MAINTENANCE,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
