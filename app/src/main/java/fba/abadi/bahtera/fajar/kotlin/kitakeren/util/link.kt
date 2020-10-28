package fba.abadi.bahtera.fajar.kotlin.kitakeren.util

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange


class link {
    companion object {
        fun newInstagramProfileIntent(pm: PackageManager, url: String): Intent? {
            var url = url
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                if (pm.getPackageInfo("com.instagram.android", 0) != null) {
                    if (url.endsWith("/")) {
                        url = url.substring(0, url.length - 1)
                    }
                    val username = url.substring(url.lastIndexOf("/") + 1)
                    // http://stackoverflow.com/questions/21505941/intent-to-open-instagram-user-profile-on-android
                    intent.data = Uri.parse("http://instagram.com/_u/$username")
                    intent.setPackage("com.instagram.android")
                    return intent
                }
            } catch (ignored: Exception) {
                Log.e("gagal", ignored.message.toString())
            }
            intent.data = Uri.parse(url)
            return intent
        }

        fun newFacebookIntent(pm: PackageManager, url: String): Intent? {
            var uri = Uri.parse(url)
            try {
                val applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0)
                if (applicationInfo.enabled) {
                    // http://stackoverflow.com/a/24547437/1048340
                    uri = Uri.parse("fb://facewebmodal/f?href=$url")
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
            return Intent(Intent.ACTION_VIEW, uri)
        }

        fun newWebsiteIntent(url: String): Intent? {
            var uri = Uri.parse(url)
            return Intent(Intent.ACTION_VIEW, uri)
        }

        fun newWhatappIntent(pm: PackageManager, number: String, message: String): Intent? {
            val num = number.replaceRange(0, 0, "")
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                if (pm.getPackageInfo("com.whatsapp", 0) != null) {
                    intent.data = Uri.parse("http://api.whatsapp.com/send?phone=+62$num&text=$message")
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
                Log.e("gggaalll", ignored.toString())
                if (pm.getPackageInfo("com.aero", 0) != null) {
                    intent.data = Uri.parse("http://api.whatsapp.com/send?phone=+62$num&text=$message")
                }
            }
            return intent
        }

        fun newMapsIntent(pm: PackageManager, address: String): Intent? {
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                if (pm.getPackageInfo("com.google.android.apps.maps", 0) != null) {
                    intent.data = Uri.parse("http://maps.google.co.in/maps?q=$address")
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
                Log.e("gggaalll", ignored.message!!)
            }
            return intent
        }

        fun newEmailIntent(pm: PackageManager, email: String): Intent? {
            val intent = Intent(Intent.ACTION_SENDTO)
            try {
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            } catch (e: Exception) {
                Log.e("gggaalll", e.message!!)
            }
            return intent
        }

        fun push() : Pusher {
            val pusher = Pusher("1fac292d0f7a131ee7c5", PusherOptions().setCluster("mt1"))
            pusher.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {
                    Log.e(
                        "Pusher",
                        "State changed from ${change.previousState} to ${change.currentState}"
                    )
                }

                override fun onError(
                    message: String,
                    code: String,
                    e: Exception
                ) {
                    Log.e(
                        "Pusher",
                        "There was a problem connecting! code ($code), message ($message), exception($e)"
                    )
                }
            }, ConnectionState.ALL)
            return pusher
        }
    }
}