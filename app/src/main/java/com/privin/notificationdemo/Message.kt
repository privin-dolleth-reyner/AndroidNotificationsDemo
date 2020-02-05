package com.privin.notificationdemo

import androidx.core.app.Person
import java.util.*

class Message(val message: CharSequence,val sender: Person?) {
    val timestamp = Calendar.getInstance().timeInMillis
}