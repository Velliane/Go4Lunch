package com.menard.go4lunch.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.menard.go4lunch.R;
import com.menard.go4lunch.api.UserHelper;

import java.util.Arrays;

import static com.firebase.ui.auth.AuthUI.getInstance;
import static com.menard.go4lunch.utils.Constants.REQUEST_CODE_SIGN_IN;

/**
 * The AuthActivity
 */

public class AuthActivity extends BaseActivity implements View.OnClickListener{

    /** Button to sign in  */
    private Button signIn;
    /** Layout */
    private LinearLayout layout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        signIn = findViewById(R.id.auth_activity_connect_btn);
        signIn.setOnClickListener(this);

        layout = findViewById(R.id.auth_activity_layout);
    }

    @Override
    public void onClick(View v) {
        if(v == signIn){
            signIn();
        }
    }

    //-----------------------------------------------------------//
    //-- CONFIGURE GOOGLE, FACEBOOK, TWITTER AND EMAIL SIGN IN --//
    //-----------------------------------------------------------//
    private void signIn(){
        startActivityForResult(getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.SignInScreen)
                .setAvailableProviders(Arrays.asList(new IdpConfig.GoogleBuilder().build(), new IdpConfig.FacebookBuilder().build(), new IdpConfig.TwitterBuilder().build(), new IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.icon1)
                .setTosAndPrivacyPolicyUrls(getString(R.string.term_condition),getString(R.string.privacy_policy))
                .build(),
                REQUEST_CODE_SIGN_IN);
    }

    //------------------//
    //-- GET RESPONSE --//
    //------------------//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void handleResponseAfterSignIn(int requestCode,int resultCode,Intent data){

        if(requestCode == REQUEST_CODE_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == Activity.RESULT_OK){
                showSnackBar(layout, getString(R.string.connection_succeed));
                //-- Add new user to firestore
                UserHelper.createUser(getCurrentUser().getUid(), getCurrentUser().getDisplayName(), getCurrentUser().getPhotoUrl().toString(), "", "", null, null).addOnFailureListener(onFailureListener());
                startMainActivity();
            }else{
                    if(response == null){
                        showSnackBar(layout, getString(R.string.authentication_canceled));
                    } else {
                        if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                            showSnackBar(layout, getString(R.string.no_internet));
                        }
                        if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                            showSnackBar(layout, getString(R.string.unkown_error_occurs));
                        }
                    }
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
