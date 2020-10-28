package fba.abadi.bahtera.fajar.kotlin.kitakeren

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment.LoginFragment
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor

class LoginActivity : AppCompatActivity(), AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (SaveSharedPreference.getStatus(this)) {
            startActivity(intentFor<MainActivity>().clearTask().clearTop())
            finish()
        } else {
            addFragment(LoginFragment())
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLogin, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}