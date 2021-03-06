package com.privin.notificationdemo

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.privin.notificationdemo.App.Companion.CHANNEL1_ID
import com.privin.notificationdemo.App.Companion.CHANNEL2_ID
import com.privin.notificationdemo.NotificationFactory.Companion.ACTIONBUTTON
import com.privin.notificationdemo.NotificationFactory.Companion.BIGCONTENT
import com.privin.notificationdemo.NotificationFactory.Companion.BIGPICTURE
import com.privin.notificationdemo.NotificationFactory.Companion.DEFAULT
import com.privin.notificationdemo.NotificationFactory.Companion.DOWNLOAD
import com.privin.notificationdemo.NotificationFactory.Companion.INBOX
import com.privin.notificationdemo.NotificationFactory.Companion.MEDIA
import com.privin.notificationdemo.NotificationFactory.Companion.MESSAGING
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object{
        fun sendMessage(context: Context){

            val activityIntent = Intent(context,MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context,0,activityIntent,0)
            val actionIntent = Intent(context,NotificationReceiver::class.java)
            actionIntent.putExtra("type", MESSAGING)
            val actionPendingIntent = PendingIntent.getBroadcast(context,0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            val notification = NotificationFactory.Builder(context, CHANNEL1_ID)
                .setContentIntent(pendingIntent)
                .setActionIntent(actionPendingIntent)
                .setType(MESSAGING)
                .build()

            NotificationManagerCompat.from(context).notify(1,notification)
        }
    }
    private val types = arrayOf(DEFAULT,ACTIONBUTTON,INBOX, BIGCONTENT, BIGPICTURE, MEDIA,MESSAGING,DOWNLOAD)
    private val channels = arrayOf(CHANNEL1_ID, CHANNEL2_ID)
    private var notificationManager : NotificationManagerCompat?= null
    private var notificationType = ""
    private var mediaSession : MediaSessionCompat? = null
    private var channel = channels[0]
    private var id = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManager = NotificationManagerCompat.from(this)
        val adapter = ArrayAdapter.createFromResource(this,R.array.list,android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        radio_grp.setOnCheckedChangeListener{ rg, _ ->
            val r = findViewById<RadioButton>(rg.checkedRadioButtonId)
            channel = if(r.text.contains("1")){
                channels[0]
            }else{
                channels[1]
            }
        }
        mediaSession = MediaSessionCompat(this,"media")
    }

    fun send1(view: View) {
        notificationManager?.let {
            if (!it.areNotificationsEnabled()){
                openSettings()
                return
            }
        }
        val title = if(notification_title.text.isNotEmpty()) notification_title.text.toString() else "Title"
        val description = if(desc.text.isNotEmpty()) desc.text.toString() else "Description"
        val activityIntent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val actionIntent = Intent(this,NotificationReceiver::class.java)
        actionIntent.putExtra("type",notificationType)
        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.greenday_cover)
        val user = Person.Builder().setName("SuperMan").setIcon(IconCompat.createWithResource(this,R.drawable.superman)).build()
        NotificationFactory.MESSAGES.clear()
        NotificationFactory.MESSAGES.add(Message("Hey! What's up", user))
        val actionPendingIntent = PendingIntent.getBroadcast(this,0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationFactory.Builder(this, channel)
            .setTitle(title)
            .setDescription(description)
            .setContentIntent(pendingIntent)
            .setLargeIcon(bitmap)
            .setActionIntent(actionPendingIntent)
            .setMediaSession(mediaSession)
            .setType(notificationType)
            .build()
        notification.flags = Notification.FLAG_AUTO_CANCEL

        if (notificationType== DOWNLOAD || notificationType== MESSAGING){
            notificationManager?.notify(1,notification)
            return
        }
        notificationManager?.notify(id++,notification)
    }

    private fun openSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE,packageName)
            startActivity(intent)
        }else{
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        notificationType = ""
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        notificationType = types[position]
        when(notificationType){
            MEDIA -> {
                if(notification_title.text.isEmpty())
                    notification_title.setText(getString(R.string.dummy_song_name))
                if (desc.text.isEmpty())
                    desc.setText(getString(R.string.dummy_artist))
            }
        }
    }
}
