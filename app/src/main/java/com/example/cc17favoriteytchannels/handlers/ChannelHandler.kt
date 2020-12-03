package com.example.cc17favoriteytchannels.handlers

import com.example.cc17favoriteytchannels.models.Channel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChannelHandler {
    var database: FirebaseDatabase
    var channelRef: DatabaseReference

    init{
        database = FirebaseDatabase.getInstance()
        channelRef = database.getReference("channels")

    }

    fun create (channel: Channel): Boolean{
        val id = channelRef.push().key
        channel.id = id

        channelRef.child(id!!).setValue(channel)
        return true
    }

    fun update (channel: Channel):Boolean{
        channelRef.child(channel.id!!).setValue(channel)
        return true
    }
    fun delete (channel: Channel):Boolean{
        channelRef.child(channel.id!!).removeValue()
        return true
    }

}