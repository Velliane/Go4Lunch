package com.menard.go4lunch.controller.activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.detailsrequest.ResultDetails
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.utils.GooglePlacesStreams
import com.menard.go4lunch.utils.getProgressDrawableSpinner
import com.menard.go4lunch.utils.loadRestaurantPhoto
import io.reactivex.disposables.CompositeDisposable
import saschpe.android.customtabs.CustomTabsHelper
import saschpe.android.customtabs.WebViewFallback

class LunchActivity : BaseActivity(), View.OnClickListener {


    /** FloatingActionButton to select the restaurant */
    private lateinit var selectingButton: FloatingActionButton
    /** Contact */
    private lateinit var call: LinearLayout
    private lateinit var like: LinearLayout
    private lateinit var website: LinearLayout
    /** Infos */
    private lateinit var nameRestaurant: TextView
    private lateinit var addressRestaurant: TextView
    lateinit var photo: ImageView
    /** Recycler view */
    private lateinit var listWorkmates: RecyclerView
    /** Place_id */
    private lateinit var idRestaurant: String
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        selectingButton = findViewById(R.id.activity_lunch_button)
        selectingButton.setOnClickListener(this)
        //-- Contact --
        call = findViewById(R.id.contact_phone)
        call.setOnClickListener(this)
        like = findViewById(R.id.contact_like)
        like.setOnClickListener(this)
        website = findViewById(R.id.contact_website)
        website.setOnClickListener(this)
        //-- Infos --
        nameRestaurant = findViewById(R.id.infos_name)
        addressRestaurant = findViewById(R.id.infos_address)
        photo = findViewById(R.id.activity_lunch_restaurant_photo)
        //-- Recycler view --
        listWorkmates = findViewById(R.id.activity_lunch_list_workmates)

        //-- Get restaurant's infos --
        idRestaurant = intent.getStringExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER)
        findRestaurantInfos(idRestaurant)

    }

    //-- REQUEST FOR THE DETAILS --
    /**
     * Make request to get details of the selected restaurant, according to his place_id
     * @param id place_id
     */
    private fun findRestaurantInfos(id: String) {
        val disable: CompositeDisposable? = CompositeDisposable()
        disable?.add(GooglePlacesStreams.getDetails(id, Constants.FIELD_FOR_DETAILS, BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError))
    }

    /**
     * Handle response of the request
     */
    private fun handleResponse(detailsRequest: DetailsRequest) {
        val result: ResultDetails = detailsRequest.result!!

        nameRestaurant.text = result.name
        addressRestaurant.text = result.formattedAddress

        //-- Use Place Photos to show according to its reference --
        if (result.photos != null) {
            val reference: String? = result.photos!![0].photoReference
            val url:String = this.getString(R.string.photos_lunch_activity, reference, BuildConfig.api_key_google)
            photo.loadRestaurantPhoto(url, null, getProgressDrawableSpinner(this))
        } else {
            photo.loadRestaurantPhoto(null, R.drawable.no_image_available_64, getProgressDrawableSpinner(this))
        }

        //-- Check if restaurant is already selected and update FloatingButton --
        if (sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, "") == idRestaurant) {
            Glide.with(this).load(R.drawable.selected_24).into(selectingButton)
        } else {
            Glide.with(this).load(R.drawable.select_24).into(selectingButton)
        }

        // Set tags to button
        website.tag = result.website
        call.tag = result.formattedPhoneNumber
        like.tag = result.placeId
    }

    /**
     * Handle error
     */
    private fun handleError(error: Throwable) {
        Log.d(ContentValues.TAG, error.localizedMessage)
    }

    // -- CLICK ON THE CONTACT INFOS --
    override fun onClick(v: View?) {
        when (v) {
            call -> if (call.tag != null) startCall(call.tag.toString()) else showSnackBar("No phone number available")
            like -> UserHelper.addFavorites(getCurrentUser().uid, like.tag.toString())
            website -> if(website.tag != null)  openCustomTabs() else showSnackBar("No website available")
            selectingButton -> saveRestaurantInSharedPreferences()
        }
    }

    /**
     * Save the restaurant selected in Shared Preferences or delete it
     */
    private fun saveRestaurantInSharedPreferences() {
        if (sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null) != idRestaurant) {
            UserHelper.updateRestaurant(getCurrentUser().uid, nameRestaurant.text.toString())
            sharedPreferences.edit().putString(Constants.PREF_RESTAURANT_SELECTED, idRestaurant).apply()
            //-- Update FloatingButton --
            Glide.with(this).load(R.drawable.selected_24).into(selectingButton)
            Toast.makeText(this, "Restaurant selected", Toast.LENGTH_SHORT).show()
        } else {
            UserHelper.updateRestaurant(getCurrentUser().uid, null)
            sharedPreferences.edit().putString(Constants.PREF_RESTAURANT_SELECTED, null).apply()
            //-- Update FloatingButton --
            Glide.with(this).load(R.drawable.select_24).into(selectingButton)
            Toast.makeText(this, "Restaurant unselected", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Open Custom Tabs with restaurant's website
     */
    private fun openCustomTabs(){
        val customTabsIntent = CustomTabsIntent.Builder().addDefaultShareMenuItem()
                .setToolbarColor(this.resources.getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build()
        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent)
        CustomTabsHelper.openCustomTab(this, customTabsIntent, Uri.parse(website.tag.toString()), WebViewFallback())
    }

    /**
     * Show a SnackBar
     */
    private fun showSnackBar(message:String){
        Snackbar.make(findViewById(R.id.lunch_activity_container), message, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Start a call phone after checking for permissions
     */
    private fun startCall(number:String){
        if (checkPermissionForCall()) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            startActivity(intent)
        }
    }

    /**
     * Check permissions for Phone Call
     */
    private fun checkPermissionForCall(): Boolean{
        return if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            true
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), Constants.REQUEST_CODE_CALL_PHONE)
            false
        }
    }
}