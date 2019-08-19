package com.menard.go4lunch

import Constants.Companion.RC_SIGN_IN
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI.*
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class AuthActivity : AppCompatActivity(), View.OnClickListener {

    /** Button to connect with Google  */
    private lateinit var googleConnect: Button
    /** Button to connect with Facebook  */
    private lateinit var facebookConnect: Button

    private lateinit var layout: LinearLayout
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        googleConnect = findViewById(R.id.auth_activity_google)
        googleConnect.setOnClickListener(this)
        facebookConnect = findViewById(R.id.auth_activity_facebook)
        facebookConnect.setOnClickListener(this)
        layout = findViewById(R.id.auth_activity_layout)
        auth = FirebaseAuth.getInstance()

    }

    override fun onClick(v: View?) {
       if(v == googleConnect){
           googleSignIn()
       }

       if(v == facebookConnect){

       }
    }


    //-- CONFIGURE GOOGLE SIGN IN --


    private fun googleSignIn(){
        startActivityForResult(getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(IdpConfig.Builder(GOOGLE_PROVIDER).build()))
                .setIsSmartLockEnabled(false, true)
                .build(), RC_SIGN_IN)
    }

    private fun signOut(){
        auth.signOut()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.handleResponseAfterSignIn(requestCode, resultCode, data)

    }

    private fun showSnackBar(linearLayout: LinearLayout, message:String){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun handleResponseAfterSignIn( requestCode: Int, resultCode: Int, data: Intent?){

        if(requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                showSnackBar(layout, "Connection succeed")
            }else{
                when {
                    response == null -> showSnackBar(layout, "Authentification canceled")
                    response.errorCode == ErrorCodes.NO_NETWORK -> showSnackBar(layout, "No internet")
                    response.errorCode == ErrorCodes.UNKNOWN_ERROR -> showSnackBar(layout, "Unknown error")
                }
            }
        }
    }

}
