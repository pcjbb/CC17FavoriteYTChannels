package com.example.cc17favoriteytchannels

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.cc17favoriteytchannels.handlers.ChannelHandler
import com.example.cc17favoriteytchannels.models.Channel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var channelNameET: EditText
    lateinit var channelLinkET: EditText
    lateinit var channelRankET: EditText
    lateinit var reasonET: EditText
    lateinit var addChannelBtn: Button
    lateinit var channelHandler: ChannelHandler
    lateinit var channels: ArrayList<Channel>
    lateinit var channelsLV: ListView
    lateinit var updatedChannel: Channel

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        channelNameET = findViewById(R.id.channelNameET)
        channelLinkET = findViewById(R.id.channelLinkET)
        channelRankET = findViewById(R.id.channelRankET)
        reasonET = findViewById(R.id.reasonET)
        addChannelBtn = findViewById(R.id.addChannelBtn)
        channelHandler = ChannelHandler()
        channels = ArrayList()
        channelsLV = findViewById(R.id.channelsLV)

        addChannelBtn.setOnClickListener {
            val name = channelNameET.text.toString()
            val link = channelLinkET.text.toString()
            val rank = channelRankET.text.toString().toInt()
            val reason = reasonET.text.toString()

            val channel = Channel(name = name, link = link, rank = rank, reason = reason)
            if (addChannelBtn.text.toString() == "ADD CHANNEL") {
                if (channelHandler.create(channel)) {
                    Toast.makeText(
                        applicationContext,
                        "Successfully added YouTube Channel.",
                        Toast.LENGTH_LONG
                    ).show()
                    clearFields()
                }
            } else if (addChannelBtn.text.toString() == "UPDATE CHANNEL") {
                val channel = Channel(
                    id = updatedChannel.id,
                    name = name,
                    link = link,
                    rank = rank,
                    reason = reason
                )
                if (channelHandler.update(channel)) {
                    Toast.makeText(
                        applicationContext,
                        "Successfully updated YouTube Channel.",
                        Toast.LENGTH_LONG
                    ).show()
                    clearFields()
                }
            }

        }
        registerForContextMenu(channelsLV)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = menuInflater
        inflater.inflate(R.menu.channel_options, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.edit_channel -> {
                updatedChannel = channels[info.position]
                channelNameET.setText(updatedChannel.name)
                channelLinkET.setText(updatedChannel.link)
                channelRankET.setText(updatedChannel.rank.toString())
                reasonET.setText(updatedChannel.reason)
                addChannelBtn.text = "UPDATE CHANNEL"
                true
            }
            R.id.delete_channel -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Do you want to delete the channel?")
                    .setCancelable(false)
                    //when Ok is selected
                    .setPositiveButton("OK") { _, _ ->
                        if (channelHandler.delete(channels[info.position])) {
                            Toast.makeText(
                                applicationContext,
                                "YouTube Channel Deleted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //get notif service as notif manager
                        notificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationChannel = NotificationChannel(
                                channelId, description, NotificationManager.IMPORTANCE_HIGH
                            )
                            notificationChannel.enableLights(true)
                            notificationChannel.lightColor = Color.CYAN
                            notificationChannel.enableVibration(true)
                            notificationManager.createNotificationChannel(notificationChannel)

                            builder = Notification.Builder(this, channelId)
                                .setContentTitle("Channel Removed")
                                .setContentText("Successfully deleted channel.")
                                .setSmallIcon(R.drawable.ic_launcher_background)

                        } else {
                            builder = Notification.Builder(this)
                                .setContentTitle("Channel Removed")
                                .setContentText("Successfully deleted channel.")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                        }
                        //calls manager
                        notificationManager.notify(0, builder.build())
                        //when cancel is pressed
                    }.setNegativeButton("CANCEL") { dialog, _ ->
                        dialog.cancel()
                    }
                val alert = dialogBuilder.create()
                alert.show()
                true
            }
            else -> super.onContextItemSelected(item)
        }

    }

    override fun onStart() {
        super.onStart()
        channelHandler.channelRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                channels.clear()
                snapshot.children.forEach {
                    val channel = it.getValue(Channel::class.java)
                    channels.add(channel!!)
                }
                channels.sort()
                val adapter = ArrayAdapter<Channel>(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    channels
                )
                channelsLV.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    fun clearFields() {
        channelNameET.text.clear()
        channelLinkET.text.clear()
        channelRankET.text.clear()
        reasonET.text.clear()
        addChannelBtn.text = "ADD CHANNEL"
    }
}


