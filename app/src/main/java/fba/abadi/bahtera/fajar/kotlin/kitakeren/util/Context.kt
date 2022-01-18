package fba.abadi.bahtera.fajar.kotlin.kitakeren.util

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Picasso
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient

fun Context.showShortToast(textRes: Int) {
    showShortToast(getString(textRes))
}

fun Context.showShortToast(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}


fun Context.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable? {
    return AppCompatResources.getDrawable(this, drawableRes)
}

fun Context.sendShareIntent(text: String) {
    startActivity(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    })
}

fun ImageView.loadImage(url: String?) =
        Glide.with(context!!)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this)
//    Picasso.get().load(url).into(this)