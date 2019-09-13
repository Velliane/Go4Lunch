package com.menard.go4lunch.controller.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R
import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.detailsrequest.Result
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.utils.GooglePlacesStreams
import io.reactivex.disposables.CompositeDisposable

class LunchActivity : AppCompatActivity(), View.OnClickListener {


    /** FloatingActionButton to select the restaurant */
    lateinit var selectingButton:FloatingActionButton
    /** Contact */
    lateinit var call: LinearLayout
    lateinit var like: LinearLayout
    lateinit var website: LinearLayout
    /** Infos */
    lateinit var nameRestaurant: TextView
    lateinit var addressRestaurant: TextView
    lateinit var photo: ImageView
    /** Recycler view */
    lateinit var listWorkmates: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)

        selectingButton = findViewById(R.id.activity_lunch_button)
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


        val idRestaurant: String = intent.getStringExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER)
        findRestaurantInfos(idRestaurant)

    }

    //-- REQUEST FOR THE DETAILS --
    /**
     * Make request to get details of the selected restaurant, according to his place_id
     * @param id place_id
     */
    fun findRestaurantInfos(id: String){
        val disable: CompositeDisposable? = CompositeDisposable()
        disable?.add(GooglePlacesStreams.getDetails(id, Constants.FIELD_FOR_DETAILS, BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError))
    }

    /**
     * Handle response of the request
     */
    fun handleResponse(detailsRequest: DetailsRequest){
        val result: Result = detailsRequest.result

        nameRestaurant.text = result.name
        addressRestaurant.text = result.formattedAddress

        //-- Use Place Photos to show according to its reference --
        if(result.photos != null){
            val reference: String = result.photos[0].photoReference
            Glide.with(this).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference="+reference+"&key="+BuildConfig.api_key_google).centerCrop().into(photo)
        }else{
            Glide.with(this).load(R.drawable.no_image_available_64).centerCrop().into(photo)
        }
    }

    /**
     * Handle error
     */
    private fun handleError(error: Throwable) {
        Log.d(ContentValues.TAG, error.localizedMessage)
    }

    // -- CLICK ON THE CONTACT INFOS --
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.contact_phone -> TODO()
            R.id.contact_like -> TODO()
            R.id.contact_website -> TODO()
        }
    }

}