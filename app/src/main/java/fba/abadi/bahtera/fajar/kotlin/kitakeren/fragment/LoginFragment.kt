package fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.MainActivity
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.BackEndApi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.setLoggedIn
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {
    private val disposables = CompositeDisposable()
    private var googleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 1
    private lateinit var callbackManager : CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        callbackManager = CallbackManager.Factory.create()
//        btnFacebook()
        btnGoogle()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

//    private fun btnFacebook() {
//        callbackManager = CallbackManager.Factory.create()
//        LoginManager.getInstance().registerCallback(callbackManager,
//            object: FacebookCallback<LoginResult> {
//                override fun onSuccess(result: LoginResult?) {
//                    Log.e("facebook sukses", result!!.accessToken.userId)
//                }
//
//                override fun onCancel() {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onError(error: FacebookException?) {
//                    Log.e("facebook error", error!!.message!!)
//                }
//            })
//        btnFacebook.setOnClickListener {
//            LoginManager.getInstance().logInWithReadPermissions(activity, listOf("public_profile", "user_friends"))
////            Auth.GoogleSignInApi.signOut(googleApiClient)
//
//        }
//    }

    private fun btnGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(context!!)
            .enableAutoManage(requireActivity(), this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        btnGoogle.setOnClickListener {
            Auth.GoogleSignInApi.signOut(googleApiClient)
//            RxSocialLogin.login(PlatformType.GOOGLE)
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result!!)
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("CheckResult")
    private fun handleSignInResult(result: GoogleSignInResult) {
        try {
            if (result.isSuccess) {
                Log.e("photo", result.signInAccount!!.photoUrl.toString())
                setLoggedIn(
                    context,
                    true,
                    result.signInAccount!!.displayName!!,
                    result.signInAccount!!.email!!,
                    result.signInAccount!!.photoUrl.toString()
                )

                val observable =
                    WebServiceClient.client.create(BackEndApi::class.java).Login(
                        result.signInAccount!!.displayName!!,
                        result.signInAccount!!.email!!
                    )
                observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        startActivity(Intent(context, MainActivity::class.java))
                        Log.e("sukses", it.message)
                    }, {
                        Log.e("gagal 1", it.message!!)
                    })
            } else {
                Log.e("gagal 1", result.toString())
            }
        } catch (e: Exception) {
            Log.e("gagal 1", e.message.toString())
        }
//        if (result.isSuccess) {
//        } else {
////            Toast.makeText(context, "Sign in cancel", Toast.LENGTH_LONG).show()
//            Log.e("gagal 1", result.toString())
//            Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show()
//        }
    }

    private fun addFragment(fragment: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLogin, fragment, fragment.javaClass.simpleName)
            .disallowAddToBackStack()
            .commit()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}