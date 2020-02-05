package com.privin.notificationdemo

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat

class NotificationFactory {

    companion object{
        const val DEFAULT = "DEFAULT"
        const val ACTIONBUTTON = "ACTIONBUTTON"
        const val BIGCONTENT = "BIGCONTENT"
        const val BIGPICTURE = "BIGPICTURE"
        const val INBOX = "INBOX"
        const val MEDIA = "MEDIA"
        const val MESSAGING = "MESSAGING"
        const val DOWNLOAD = "DOWNLOAD"

        val MESSAGES = mutableListOf<Message>()
    }
    class Builder(private val context: Context, private val channel : String) {
        private var notificationTitle : String = ""
        private var notificationMessage : String = ""
        private var notificationType : String = DEFAULT
        private var notificationPendingIntent : PendingIntent? = null
        private var notificationActionPendingIntent : PendingIntent? = null
        private var largeIcon : Bitmap? = null
        private var notificationMediaSession : MediaSessionCompat? = null

        fun setTitle(title:String) : Builder{
            notificationTitle = title
            return this
        }
        fun setDescription(msg:String) : Builder{
            notificationMessage = msg
            return this
        }
        fun setMediaSession(mediaSession: MediaSessionCompat?) : Builder{
            notificationMediaSession = mediaSession
            return this
        }
        fun setType(type:String) : Builder{
            notificationType = type
            return this
        }
        fun setLargeIcon(icon : Bitmap) : Builder{
            largeIcon = icon
            return this
        }
        fun setContentIntent(pendingIntent:PendingIntent) : Builder{
            notificationPendingIntent = pendingIntent
            return this
        }
        fun setActionIntent(pendingIntent:PendingIntent) : Builder{
            notificationActionPendingIntent = pendingIntent
            return this
        }
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun build() : Notification{
            return when(notificationType){
                ACTIONBUTTON -> actionNotification()
                INBOX -> inboxNotification()
                BIGCONTENT -> bigContentNotification()
                BIGPICTURE -> bigPictureNotification()
                MEDIA -> mediaNotification()
                MESSAGING -> messageNotification()
                DOWNLOAD -> downloadNotification()
                else -> defaultNotification()
            }
        }

        private fun makeBuilder() : NotificationCompat.Builder{
            return NotificationCompat.Builder(context, channel)
                .setSmallIcon(if (channel.contains("1")) R.drawable.ic_error else R.drawable.ic_launcher_foreground)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroup("Test Group")
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
        }
        private fun defaultNotification() : Notification{
            val builder =  makeBuilder()
            return builder.build()
        }

        private fun actionNotification() : Notification{
            val builder =  makeBuilder()
            notificationPendingIntent?.let {
                builder.setContentIntent(it)
                    .addAction(R.drawable.ic_launcher_foreground,"Toast",notificationActionPendingIntent)
            }
            return builder.build()
        }

        private fun inboxNotification() : Notification{
            return makeBuilder()
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setBigContentTitle("Inbox")
                        .addLine("Line 1")
                        .addLine("Line 2")
                        .addLine("Line 3")
                        .addLine("Line 4"))
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
                .build()
        }

        private fun bigContentNotification() : Notification{
            val builder = makeBuilder()
                .setStyle(NotificationCompat.BigTextStyle()
                .bigText(context.getString(R.string.dummy_text))
                .setBigContentTitle("Big Content")
                .setSummaryText("Summary text"))
            largeIcon?.let {
                builder .setLargeIcon(it)
            }
            return builder.build()
        }
        private fun bigPictureNotification() : Notification{
            val builder = makeBuilder()

            largeIcon?.let {
                builder .setLargeIcon(it)
                    .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(largeIcon)
                        .setBigContentTitle("Big Content")
                        .setSummaryText("Summary text")
                        .bigLargeIcon(null))
            }
            return builder.build()
        }
        private fun mediaNotification():Notification{

            val builder = makeBuilder()
                .addAction(R.drawable.ic_add,"Add to Playlist",null)
                .addAction(R.drawable.ic_skip_previous,"Previous",null)
                .addAction(R.drawable.ic_pause,"Pause",null)
                .addAction(R.drawable.ic_skip_next,"Next",null)
                .setSubText("Sub text")
            largeIcon?.let {
                builder .setLargeIcon(it)
            }
            notificationMediaSession?.let {
                builder
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1,2,3)
                        .setMediaSession(it.sessionToken))
            }
            return builder.build()
        }
        private fun messageNotification() : Notification{
            val builder = makeBuilder()
            val remoteInput = RemoteInput.Builder(MESSAGING)
                .setLabel("reply")
                .build()
            val replyAction= NotificationCompat.Action.Builder(R.drawable.send,
                "Reply",
                notificationActionPendingIntent)
                .addRemoteInput(remoteInput)
                .build()
            val messagingStyle = NotificationCompat.MessagingStyle("You")


            for (msg in MESSAGES){
                messagingStyle.addMessage(msg.message,msg.timestamp,msg.sender)
            }

            builder.setStyle(messagingStyle)
                .addAction(replyAction)
            return builder.build()
        }

        private fun downloadNotification() : Notification{
            val builder = makeBuilder().setProgress(100,0,false)
                .setContentTitle("Download")
                .setContentText("downloading...")
            startDownload(builder)
            return builder.build()
        }

        private fun startDownload(builder:NotificationCompat.Builder) {
            Log.d("DOWNLOAD","startDownload")
            Runnable {
                SystemClock.sleep(2000)
                var i =0
                while (i<=100){
                    Log.d("DOWNLOAD","startDownload $i % completed")
                    i+=10
                    builder.setProgress(100,i,false)
                    NotificationManagerCompat.from(context).notify(1,builder.build())
                    SystemClock.sleep(1000)
                }
                builder.setContentText("Download finished!")
                    .setProgress(0,0,false)
                NotificationManagerCompat.from(context).notify(1,builder.build())
            }.run()
        }
    }


}