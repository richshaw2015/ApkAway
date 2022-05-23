package org.dinorss.apkaway

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ApkBlockerService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action.equals(
                MainActivity.ACTION_STOP_SERVICE, ignoreCase = true)) {
            stopForeground(true)
            stopSelf()
        }
        genNotification()

        // 监听下载文件夹
        val observer = RecursiveApkObserver()
        observer.startWatching()

        // 监听应用列表
//        val uninstallIntent = Intent(Intent.ACTION_DELETE)
//        uninstallIntent.data = Uri.parse("package:org.dinorss.idiomle")
//        uninstallIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        ContextCompat.startActivity(this, uninstallIntent, null)

        return START_STICKY
    }

    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 911

    private fun genNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intentMainLanding = Intent(this, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intentMainLanding, FLAG_IMMUTABLE)

            if (mNotificationManager == null) {
                mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                mNotificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("chats_group", "Chats")
                )
                val notificationChannel =
                    NotificationChannel("service_channel", "Service Notifications",
                        NotificationManager.IMPORTANCE_MIN)
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(this, "service_channel")

            builder.setContentTitle(StringBuilder("小卓･正在守护中").toString())
                .setSmallIcon(R.drawable.logo)
                .setColorized(true)
                .setColor(getColor(R.color.notification))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)

            notification = builder.build()
            startForeground(mNotificationId, notification)
        }

    }
}