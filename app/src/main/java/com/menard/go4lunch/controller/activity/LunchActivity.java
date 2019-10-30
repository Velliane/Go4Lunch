package com.menard.go4lunch.controller.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.menard.go4lunch.BuildConfig;
import com.menard.go4lunch.R;
import com.menard.go4lunch.adapter.WorkmatesAdapter;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.model.User;
import com.menard.go4lunch.model.detailsrequest.DetailsRequest;
import com.menard.go4lunch.model.detailsrequest.ResultDetails;
import com.menard.go4lunch.utils.Constants;
import com.menard.go4lunch.utils.GooglePlacesStreams;
import com.menard.go4lunch.utils.NotificationWorker;

import org.threeten.bp.LocalDateTime;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

import static com.menard.go4lunch.utils.DateUtilsKt.setNotificationsTime;
import static com.menard.go4lunch.utils.PhotoUtilsKt.getProgressDrawableSpinner;
import static com.menard.go4lunch.utils.PhotoUtilsKt.loadRestaurantPhoto;

public class LunchActivity extends BaseActivity implements View.OnClickListener{


    /** FloatingActionButton to select the restaurant */
    private FloatingActionButton selectingButton;
    /** Contact */
    private LinearLayout call;
    private LinearLayout like;
    private LinearLayout website;
    /** Infos */
    private TextView nameRestaurant;
    private TextView addressRestaurant;
    private ImageView photo;
    private ImageView stars;
    /** Recycler view */
    private RecyclerView listWorkmates;
    /** Place_id */
    private String idRestaurant;
    /** Shared Preferences */
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);
        AndroidThreeTen.init(this);

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        //-- Views --
        selectingButton = findViewById(R.id.activity_lunch_button);
        selectingButton.setOnClickListener(this);
        call = findViewById(R.id.contact_phone);
        call.setOnClickListener(this);
        like = findViewById(R.id.contact_like);
        like.setOnClickListener(this);
        website = findViewById(R.id.contact_website);
        website.setOnClickListener(this);
        nameRestaurant = findViewById(R.id.infos_name);
        addressRestaurant = findViewById(R.id.infos_address);
        photo = findViewById(R.id.activity_lunch_restaurant_photo);
        stars = findViewById(R.id.infos_star);

        //-- Get restaurant's infos --
        idRestaurant = getIntent().getStringExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER);
        findRestaurantInfos(idRestaurant);

        //-- Recycler view --
        listWorkmates = findViewById(R.id.activity_lunch_list_workmates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listWorkmates.setLayoutManager(layoutManager);

    }

    //-- REQUEST FOR THE DETAILS --//
    /**
     * Make request to get details of the selected restaurant, according to his place_id
     * @param id place_id
     */
    private void findRestaurantInfos(String id){
        CompositeDisposable disable = new CompositeDisposable();
        disable.add(GooglePlacesStreams.getDetails(id, Constants.FIELD_FOR_DETAILS, BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError));
    }

    /**
     * Handle response of the request
     */
    private void handleResponse(DetailsRequest detailsRequest) {
        ResultDetails result = detailsRequest.getResult();

        assert result != null;
        nameRestaurant.setText(result.getName());
        addressRestaurant.setText(result.getFormattedAddress());

        //-- Use Place Photos to show according to its reference --
        if (result.getPhotos() != null) {
            String reference = result.getPhotos().get(0).getPhotoReference();
            String url = this.getString(R.string.photos_lunch_activity, reference, BuildConfig.api_key_google);
            //Glide.with(this).setDefaultRequestOptions(new RequestOptions().centerCrop().placeholder(getProgressDrawableSpinner(this))).load(url).into(photo);
            loadRestaurantPhoto(photo, url, null, getProgressDrawableSpinner(this));
        } else {
            //Glide.with(this).setDefaultRequestOptions(new RequestOptions().centerCrop().placeholder(getProgressDrawableSpinner(this))).load(R.drawable.no_image_available_64).into(photo);
            loadRestaurantPhoto(photo,null, R.drawable.no_image_available_64, getProgressDrawableSpinner(this));
        }

        //-- Check if restaurant is already selected and update FloatingButton --
        if (Objects.requireNonNull(sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, "")).equals(idRestaurant)) {
            Glide.with(this).load(R.drawable.selected_24).into(selectingButton);
        } else {
            Glide.with(this).load(R.drawable.select_24).into(selectingButton);
        }

        //-- Set tags to button --
        website.setTag(result.getWebsite());
        call.setTag(result.getFormattedPhoneNumber());
        like.setTag(result.getPlaceId());

        //-- Get List of workmates --
        Query query = UserHelper.getUserAccordingToRestaurant(Objects.requireNonNull(result.getPlaceId()));
        FirestoreRecyclerOptions<User> list = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class)
                .setLifecycleOwner(this).build();
        WorkmatesAdapter workmatesAdapter = new WorkmatesAdapter(this, list,false);
        listWorkmates.setAdapter(workmatesAdapter);

        setFavorites();

    }

    /**
     * Handle error
     */
    private void handleError(Throwable error) {
        String err = (error.getMessage()==null)? "getDetails failed":error.getMessage();
        Log.d(ContentValues.TAG, err);
    }

    // -- CLICK ON THE CONTACT INFOS --//
    @Override
    public void onClick(View v) {
        if (v == call) {
            if (call.getTag() != null){
                startCall(call.getTag().toString());
            }
            else {
                showSnackBar(getString(R.string.no_website));
            }
        }
        if(v == like){
            if(stars.getVisibility() == View.VISIBLE){

                            UserHelper.deleteFavorites(getCurrentUser().getUid(), like.getTag().toString());
                            stars.setVisibility(View.INVISIBLE);
                        }else {
                            UserHelper.addFavorites(getCurrentUser().getUid(), like.getTag().toString()).addOnFailureListener(onFailureListener());
                            stars.setVisibility(View.VISIBLE);
                        }
                    }



        if(v == website){
            if(website.getTag() != null){
                openCustomTabs();
            } else{
                showSnackBar(getString(R.string.no_phone));
            }

        }
        if(v == selectingButton){
            updateSharedPreferencesAndFirestore();
        }
    }

    /**
     * Open Custom Tabs with restaurant's website
     */
    private void openCustomTabs(){
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().addDefaultShareMenuItem()
                .setToolbarColor(this.getResources().getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build();
        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent);
        CustomTabsHelper.openCustomTab(this, customTabsIntent, Uri.parse(website.getTag().toString()), new WebViewFallback());
    }


    //-- SHARED PREFERENCES AND FIRESTORE --//
    /**
     * Save the restaurant selected in Shared Preferences or delete it
     */
    private void updateSharedPreferencesAndFirestore() {
        if (sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null) != (idRestaurant)) {
            selected();
            //-- Activate Notification --
            if(sharedPreferences.getBoolean(Constants.PREF_ENABLED_NOTIFICATIONS, true)) {
                Data data = new Data.Builder()
                        .putString(Constants.DATA_RESTAURANT_ID, idRestaurant)
                        .putString(Constants.DATA_USER, getCurrentUser().getDisplayName())
                        .putString(Constants.DATA_RESTAURANT_NAME, nameRestaurant.getText().toString())
                        .putString(Constants.DATA_RESTAURANT_ADDRESS, addressRestaurant.getText().toString())
                        .build();
                //-- Check if hours of notifications have been choosed by user, else set default 12:00 --
                int hours = sharedPreferences.getInt(Constants.PREF_NOTIFICATIONS_HOURS, 12);
                int minute = sharedPreferences.getInt(Constants.PREF_NOTIFICATIONS_MINUTES, 0);
                NotificationWorker.scheduleReminder(data, setNotificationsTime(LocalDateTime.now(), hours, minute, 0));
            }
        } else {
            unselected();
            //-- Cancel Notification --
            NotificationWorker.cancelReminder();
        }
    }

    /**
     * When the restaurant is selected
     */
    private void selected(){
        UserHelper.updateRestaurant(getCurrentUser().getUid(), nameRestaurant.getText().toString(), idRestaurant).addOnFailureListener(onFailureListener());
        sharedPreferences.edit().putString(Constants.PREF_RESTAURANT_SELECTED, idRestaurant).apply();
        //-- Update FloatingButton --
        Glide.with(this).load(R.drawable.selected_24).into(selectingButton);
        showSnackBar(getString(R.string.selected));
    }

    /**
     * When the restaurant is unselected
     */
    private void unselected(){
        UserHelper.updateRestaurant(getCurrentUser().getUid(), "", "").addOnFailureListener(onFailureListener());
        sharedPreferences.edit().putString(Constants.PREF_RESTAURANT_SELECTED, null).apply();
        //-- Update FloatingButton --
        Glide.with(this).load(R.drawable.select_24).into(selectingButton);
        showSnackBar(getString(R.string.unselected));
    }

    private void showSnackBar(String message){
        Snackbar.make(findViewById(R.id.lunch_activity_container), message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Check if the selected restaurant is saved in Favorites in Firebase
     * Is so, then make star visible
     */
    private void setFavorites() {
        UserHelper.getFavorites(getCurrentUser().getUid()).addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                List docs = queryDocumentSnapshots.getDocuments();

                for (Object obj : docs) {
                    DocumentSnapshot doc = (DocumentSnapshot) obj;
                    String id = doc.getString("placeId");
                    assert id != null;
                    if (id.equals(like.getTag())) {
                        stars.setVisibility(View.VISIBLE);
                    }
                }
            }
        })
        .addOnFailureListener(onFailureListener());
    }

    //-- PHONE CALL --//
    /**
     * Start a call phone after checking for permissions
     */
    private void startCall(String number){
        if (checkPermissionForCall()) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(intent);
        }
    }

    /**
     * Check permissions for Phone Call
     */
    private boolean checkPermissionForCall(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CALL_PHONE}, Constants.REQUEST_CODE_CALL_PHONE);
            return false;
        }
    }

}