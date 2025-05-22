import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.daffa_34076492.nutritrack.data.MotivationalMessageRepository
import com.daffa_34076492.nutritrack.workers.NotificationHelper
import java.util.Calendar

class DailyTipWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)

            val prompt = when {
                hour in 6..11 -> "Good morning! Give me a short, motivating health tip for the start of the day."
                hour in 12..17 -> "Good afternoon! Give me a quick health tip to keep up the energy."
                else -> "Good evening! Share a brief tip for winding down and staying healthy."
            }

            val repository = MotivationalMessageRepository.getInstance(applicationContext)
            val message = repository.generateMotivationalTip(prompt)

            NotificationHelper.showNotification(
                context = applicationContext,
                title = "Your Health Tip",
                message = message
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
