package ng.max.vams.service.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ng.max.vams.R
import ng.max.vams.data.manager.AppManager
import ng.max.vams.data.manager.UserManager
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import ng.max.vams.ui.MainActivity
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var vehicleService: VehicleService

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())


    private val notificationManager
            by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onNewToken(token: String) {
        if(UserManager.getUser() != null){
            val tokenBody = HashMap<String, String>().apply {
                this["user_id"] = UserManager.getUser()!!.id
                this["registration_token"] = token
            }
            coroutineScope.launch {
                try {
                    val response = vehicleService.saveToken(tokenBody)
                    if (response.isSuccessful) {
                        Result.Success(response.body()!!)
                    }else{
                        Result.Error("Something went wrong")
                    }
                } catch (ex: Exception) {
                    Log.d(TAG, "Error saving token to server")
                }
            }
        }else{
            AppManager.saveMessagingServiceToken(token)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.notification != null) {
            Log.d(TAG, "No Notification Received")
            showNotification(remoteMessage.notification!!)
        } else {
            Log.d(TAG, "No Notification Received")
        }

    }

    private fun showNotification(remoteMessage: RemoteMessage.Notification) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val title = remoteMessage.title
            val message = remoteMessage.body

            if (title == null || message == null) return@post

            val pattern = longArrayOf(0, 4000)

            val timestamp = System.currentTimeMillis()

            val launcherIntent = Intent(this, MainActivity::class.java).apply {
                this.putExtra(NOTIFICATION, true)
            }
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val openIntent = PendingIntent.getActivity(
                this,
                0,
                launcherIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val channelId = createChannel()
            val builder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(openIntent)
                .setShowWhen(true)
                .setWhen(timestamp)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setVibrate(pattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.FLAG_AUTO_CANCEL)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .build()

            notificationManager.notify(
                FIREBASE_NOTIFICATION_CHANNEL_NAME_TAG,
                FIREBASE_NOTIFICATION_ID,
                builder
            )
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(): String {
        // check build version to determine what type of notification channel to build
        val isPreAndroidO = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1
        // if pre oreo, just return channelId
        return if (isPreAndroidO) {
            FIREBASE_CHANNEL_ID
        } else {
            // Create a channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                FIREBASE_CHANNEL_ID,
                FIREBASE_NOTIFICATION_CHANNEL_NAME_TAG,
                importance
            )

            // sound uri
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            // set audio attributes
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            notificationChannel.setSound(soundUri, audioAttributes)

            notificationManager.createNotificationChannel(notificationChannel)
            FIREBASE_CHANNEL_ID
        }
    }


    companion object {
        private const val TAG = "FirebaseMessaging"
        const val FIREBASE_CHANNEL_ID = "ai.maxdrive.doms.firebase_messaging_service"
        const val FIREBASE_NOTIFICATION_ID = 1022
        const val FIREBASE_NOTIFICATION_CHANNEL_NAME_TAG = "Notification"
        const val NOTIFICATION = "NOTIFICATION"
    }
}