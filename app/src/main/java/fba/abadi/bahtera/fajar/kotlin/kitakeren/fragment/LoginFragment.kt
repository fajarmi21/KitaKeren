package fba.abadi.bahtera.fajar.kotlin.kitakeren.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fba.abadi.bahtera.fajar.kotlin.kitakeren.MainActivity
import fba.abadi.bahtera.fajar.kotlin.kitakeren.R
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.BackEndApi
import fba.abadi.bahtera.fajar.kotlin.kitakeren.network.WebServiceClient
import fba.abadi.bahtera.fajar.kotlin.kitakeren.util.SaveSharedPreference.setLoggedIn
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    private val disposables = CompositeDisposable()
    private var googleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 0
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        btnGoogle()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun btnGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_api))
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(context!!)
            .enableAutoManage(requireActivity(), GoogleApiClient.OnConnectionFailedListener { })
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        btnGoogle.setOnClickListener {
//            RxSocialLogin.login(PlatformType.GOOGLE)
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
//                Log.d("tg", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                Log.w("tg", "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    setLoggedIn(
                        context,
                        true,
                        auth.currentUser!!.displayName!!,
                        auth.currentUser!!.email!!,
                        auth.currentUser!!.photoUrl.toString()
                    )

                    val observable =
                        WebServiceClient.client.create(BackEndApi::class.java).Login(
                            auth.currentUser!!.displayName!!,
                            auth.currentUser!!.email!!
                        )
                    observable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, MainActivity::class.java))
                            Log.e("sukses", it.message)
                        }, {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            Log.e("gagal 1", it.message!!)
                        })
                } else {
                    Toast.makeText(context, "Login Gagal", Toast.LENGTH_SHORT).show()
                    Log.e("gagal 1", task.exception.toString())
                }
            }
    }
}