package br.ufpe.cin.if710.rss

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log

class BackgroundReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        criarCanal(notificationManager)

        val notificacao = NotificationCompat.Builder(context, "id do canal")
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle("RSS")
                .setContentText("News")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(longArrayOf(0, 200, 500, 1000))
                .build()

        notificationManager.notify(R.string.app_name, notificacao)
    }

    fun criarCanal(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O &&
                notificationManager.getNotificationChannel("id do canal")==null) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("id do canal", "RSS", importance)
            channel.apply {
                description = "descricao do canal de RSS"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
