package com.privin.notificationdemo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.os.Build

class App : Application() {

    companion object{
        const val GROUP1 = "GROUP1"
        const val CHANNEL1_ID = "CHANNEL1"
        const val CHANNEL2_ID = "CHANNEL2"
        const val CHANNEL3_ID = "CHANNEL3"
    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(CHANNEL1_ID,
                "Channel 1",NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "This is Channel 1"
            val group1 = NotificationChannelGroup(GROUP1,
                "Group 1")
            val channel2 = NotificationChannel(CHANNEL2_ID,
                "Channel 2",NotificationManager.IMPORTANCE_HIGH)
            channel2.description = "This is Channel 2"
            channel2.group = GROUP1
            val channel3 = NotificationChannel(CHANNEL3_ID,
                "Channel 3",NotificationManager.IMPORTANCE_HIGH)
            channel2.description = "This is Channel 3"
            channel2.group = GROUP1
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannelGroup(group1)
            manager?.createNotificationChannel(channel1)
            manager?.createNotificationChannel(channel2)
            manager?.createNotificationChannel(channel3)
        }
    }
}