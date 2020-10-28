package fba.abadi.bahtera.fajar.kotlin.kitakeren

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment.DashboardFragment
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.getLang
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.getPhoto
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.getname
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.logout
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.setLang
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.link.Companion.push
import ir.zadak.zadaknotify.notification.ZadakNotification
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {
    private var lg = 0
    private val CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment(DashboardFragment())
        login()
        when(getLang(this)) {
            0 -> {
                Glide.with(this)
                    .load(R.drawable.ind)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(language)
            }
            1 -> {
                Glide.with(this)
                    .load(R.drawable.en)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(language)
            }
        }
        language.setOnClickListener {
            lg = if (lg == 0) {
                setLang(this, 1)
                Glide.with(this)
                    .load(R.drawable.en)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(language)
                1
            } else {
                setLang(this, 0)
                Glide.with(this)
                    .load(R.drawable.ind)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(language)
                0
            }
        }
//        loginAc.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        //notif

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My app notification channel"
            val description = "Description for this channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        //pusher
//        val pusher = Pusher("1fac292d0f7a131ee7c5", PusherOptions().setCluster("mt1"))
//        pusher.connect(object : ConnectionEventListener {
//            override fun onConnectionStateChange(change: ConnectionStateChange) {
//                Log.e(
//                    "Pusher",
//                    "State changed from ${change.previousState} to ${change.currentState}"
//                )
//            }
//
//            override fun onError(
//                message: String,
//                code: String,
//                e: Exception
//            ) {
//                Log.e(
//                    "Pusher",
//                    "There was a problem connecting! code ($code), message ($message), exception($e)"
//                )
//            }
//        }, ConnectionState.ALL)

        val channel = push().subscribe("my-channel")
        channel.bind("my-event") { event ->
//            Toast.makeText(, "Received event with data: $event", Toast.LENGTH_LONG).show()
            ZadakNotification.with(this)
                .load()
                .notificationChannelId(CHANNEL_ID)
                .title("notif")
                .message(event.data)
                .smallIcon(R.drawable.ic_launcher)
                .largeIcon(R.drawable.ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build()
            Log.e("Pusher", "Received event with data: ${event.data}")
        }
    }

    private fun login() {
//        Log.e("photo", getPhoto(this).toString())
//        Log.e("logout", getgoogleapi(this)!!)
        if (getPhoto(this) != "null") {
            Glide.with(this)
                .load(getPhoto(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(loginAc)
        }
        txname.text = getname(this)

        loginAc.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            Auth.GoogleSignInApi.signOut(GoogleSignIn.getClient(this, gso).asGoogleApiClient()).setResultCallback {
//                Log.e("status", it.status.statusMessage!!)
                if (it.isSuccess) {
                    logout(this)
                    startActivity(intentFor<LoginActivity>().clearTask().clearTop())
                    finish()
                }
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}