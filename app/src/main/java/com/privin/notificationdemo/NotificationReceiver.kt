package com.privin.notificationdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput
import com.privin.notificationdemo.NotificationFactory.Companion.MESSAGING

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val type = intent?.extras?.getString("type",NotificationFactory.DEFAULT)
        type?.let{
            when(it){
                MESSAGING -> handleMessage(context,intent)
                else -> showToast(context)
            }
        }
    }

    private fun showToast(context: Context?){
        context?.let {
            Toast.makeText(it,"Toast Clicked!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleMessage(context: Context?, intent: Intent) {
        val remoteBundle = RemoteInput.getResultsFromIntent(intent)
        val replyText = remoteBundle?.getCharSequence(MESSAGING)
        replyText?.let{
            NotificationFactory.MESSAGES.add(Message(it,null))
        }
        context?.let{
            MainActivity.sendMessage(it)
        }

    }
}