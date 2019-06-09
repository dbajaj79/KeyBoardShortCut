package com.example.keyboardshortcut

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import com.example.keyboardshortcut.customextensions.showToast
import com.example.keyboardshortcut.databinding.ActivityMainBinding
import com.example.keyboardshortcut.model.ShortCut
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    lateinit var keys: MutableList<String>
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_save -> saveKey()
            R.id.bt_addkey -> changeKey()
        }
    }

    fun changeKey() {
        if (TextUtils.isEmpty(activityMainBinding.mainContainer.actKeys.text)) {
            showToast("Please Enter Key")
        } else {
            val changeKeys = activityMainBinding.mainContainer.actKeys.text.toString()
            val newKey = activityMainBinding.mainContainer.textView.text.toString() + changeKeys + " "
            keys.add(changeKeys)
            activityMainBinding.mainContainer.textView.text = newKey
            activityMainBinding.mainContainer.actKeys.text = null
        }
    }

    fun setDataBaseChangeListener() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference()
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val childs = p0.children
                for (child in childs) {
                    val shortCut = child.getValue<ShortCut>(ShortCut::class.java)
                    showToast(shortCut.toString())
                }
            }
        }
        databaseReference.child("root").addValueEventListener(valueEventListener)


    }

    fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun createNotificationChannel() {
        val notificationChannel =
            NotificationChannel("NOTIFICATION_CHANNEL_ID", "Shortcuts", NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.description = "Descriptions of ShortCuts"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

    }

    fun showSimpleNotification() {
        val notificationCompatBuilder = androidx.core.app.NotificationCompat.Builder(this, "NOTIFICATION_CHANNEL_ID")
        notificationCompatBuilder.setContentTitle("Notification Sample")
        notificationCompatBuilder.setSmallIcon(R.drawable.ic_add)
        notificationCompatBuilder.setContentText("hello Notification")
        if(isPie())
        {
            val imageDecoderSource = ImageDecoder.createSource(resources,R.drawable.ic_add)
            val bitmap = ImageDecoder.decodeBitmap(imageDecoderSource)
            notificationCompatBuilder.setStyle(NotificationCompat.BigPictureStyle().bigLargeIcon(bitmap))
        }
        else
        {
            val bitmap = BitmapFactory.decodeResource(resources,R.drawable.notify)
            notificationCompatBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(1, notificationCompatBuilder.build())
    }

    fun isPie() = Build.VERSION.SDK_INT==Build.VERSION_CODES.P


    fun saveKey() {
        when {
            TextUtils.isEmpty(activityMainBinding.mainContainer.etshortcutkeyname.text) -> showToast("Please Enter ShortCut Name")
            TextUtils.isEmpty(activityMainBinding.mainContainer.textView.text) -> showToast("Please Add Keys")
            else -> {
                val shortcut = ShortCut(activityMainBinding.mainContainer.etshortcutkeyname.text.toString(), keys)
                keys = ArrayList<String>()
                activityMainBinding.mainContainer.textView.text = null
                activityMainBinding.mainContainer.etshortcutkeyname.text = null
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val databaseReference = firebaseDatabase.reference
                databaseReference.child("root").push().setValue(shortcut)
            }
        }
    }

    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.onClickListener = this
        keys = ArrayList<String>()
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            showSimpleNotification()
        }
        setDataBaseChangeListener()
        if (isOreo()) {
            createNotificationChannel()

        }
    }

}
