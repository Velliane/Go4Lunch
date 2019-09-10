package com.menard.go4lunch.controller.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.getInstance
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.menard.go4lunch.utils.Constants.Companion.RC_SIGN_IN
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper

class AuthActivity : BaseActivity(), View.OnClickListener {

    /** Button to sign in  */
    private lateinit var signIn: Button
    /** Layout */
    private lateinit var layout: LinearLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        signIn = findViewById(R.id.auth_activity_connect_btn)
        signIn.setOnClickListener(this)

        layout = findViewById(R.id.auth_activity_layout)
    }

    override fun onClick(v: View?) {
        when(v) {
            signIn -> signIn()
        }
    }


    //-- CONFIGURE GOOGLE, FACEBOOK AND TWITTER SIGN IN --
    private fun signIn(){
        startActivityForResult(getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.SignInScreen)
                .setAvailableProviders(listOf(IdpConfig.GoogleBuilder().build(), IdpConfig.FacebookBuilder().build(), IdpConfig.TwitterBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.icon1)
                .setTosAndPrivacyPolicyUrls("https://openclassrooms.com/fr/terms-conditions","https://openclassrooms.com/fr/privacy-policy")
                .build(),
                RC_SIGN_IN)
    }

    //-- GET RESPONSE --
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        this.handleResponseAfterSignIn(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun handleResponseAfterSignIn( requestCode: Int, resultCode: Int, data: Intent?){

        if(requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                showSnackBar(layout, "Connection succeed")
                //-- Add new user to firestore
                UserHelper.createUser(getCurrentUser().uid, getCurrentUser().displayName!!, getCurrentUser().photoUrl?.toString(), null,null ).addOnFailureListener(onFailureListener())
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


}
