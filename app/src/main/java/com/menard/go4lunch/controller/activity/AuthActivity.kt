package com.menard.go4lunch.controller.activity

import com.menard.go4lunch.Constants.Companion.RC_SIGN_IN
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.*
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.viewmodel.RequestCodes.GOOGLE_PROVIDER
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.math.sign
import android.util.Base64

class AuthActivity : AppCompatActivity(), View.OnClickListener {

    /** Button to connect with Google  */
    private lateinit var googleConnect: Button
    /** Button to connect with Facebook  */
    private lateinit var facebookConnect: Button
    private lateinit var signOut: Button

    private lateinit var layout: LinearLayout
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        googleConnect = findViewById(R.id.auth_activity_google)
        googleConnect.setOnClickListener(this)
        facebookConnect = findViewById(R.id.auth_activity_facebook)
        facebookConnect.setOnClickListener(this)
        signOut = findViewById(R.id.sign_out)
        signOut.setOnClickListener(this)
        layout = findViewById(R.id.auth_activity_layout)
        auth = FirebaseAuth.getInstance()

        try {
            val info = packageManager.getPackageInfo(
                    "com.menard.go4lunch",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }

    }

    override fun onClick(v: View?) {
        when(v) {
            googleConnect -> googleSignIn()
            facebookConnect -> facebookSignIn()
            signOut -> signOut()
        }
    }


    //-- CONFIGURE GOOGLE SIGN IN --
    private fun googleSignIn(){
        startActivityForResult(getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(IdpConfig.GoogleBuilder().build()))
                .build(), RC_SIGN_IN)
    }

    //-- CONFIGURE FACEBOOK SIGN IN --
    private fun facebookSignIn(){
        startActivityForResult(getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(IdpConfig.FacebookBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(), RC_SIGN_IN)
    }
    //-- GET RESPONSE --
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.handleResponseAfterSignIn(requestCode, resultCode, data)

    }

    private fun handleResponseAfterSignIn( requestCode: Int, resultCode: Int, data: Intent?){

        if(requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                showSnackBar(layout, "Connection succeed")
                startMainActivity()
            }else{
                when {
                    response == null -> showSnackBar(layout, "Authentification canceled")
                    response.error!!.errorCode == ErrorCodes.NO_NETWORK -> showSnackBar(layout, "No internet")
                    response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR -> showSnackBar(layout, "Unknown error")
                }
            }
        }
    }

    private fun showSnackBar(linearLayout: LinearLayout, message:String){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun signOut(){
        getInstance()
                .signOut(this)
                .addOnCompleteListener{
                    showSnackBar(layout, "Signed out")
                }
    }


}
