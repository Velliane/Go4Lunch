package com.menard.go4lunch.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.menard.go4lunch.BuildConfig;
import com.menard.go4lunch.R;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.controller.activity.LunchActivity;
import com.menard.go4lunch.model.nearbysearch.NearbySearch;
import com.menard.go4lunch.model.nearbysearch.Result;
import com.menard.go4lunch.utils.Constants;
import com.menard.go4lunch.utils.GooglePlacesStreams;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static com.menard.go4lunch.utils.RestaurantUtilsKt.setMarker;

public class MapviewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnCameraMoveListener {


    public static MapviewFragment newInstance() {
        return new MapviewFragment();
    }

    /** Map View */
    private MapView mapView;
    /** Google Map */
    private GoogleMap mGoogleMap;
    /** FusedLocation */
    private FusedLocationProviderClient fusedLocationProviderClient;
    /** Progress Bar */
    private ProgressBar progressBar;
    /** Error message */
    private TextView errorTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapview_fragment, container, false);

        try {
            MapsInitializer.initialize(requireActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        progressBar = view.findViewById(R.id.map_view_progress);
        errorTextView = view.findViewById(R.id.map_view_error_message);

        //-- Check is fragment is added to MainActivity --
        if (isAdded()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            //-- Google Places SDK initialization --
            Places.initialize(requireActivity(), BuildConfig.api_key_google);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().isZoomControlsEnabled();
        mGoogleMap.isMyLocationEnabled();
        //-- Check Permissions for ACCESS_FINE_LOCATION
        if (checkPermissions()) {
            mGoogleMap.setMyLocationEnabled(true);
            GPSUpdateLocation();
        }
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setOnCameraMoveListener (this);
    }

    //-------------------------------------//
    //-- UPDATE MAP WITH USER'S LOCATION --//
    //-------------------------------------//
    /**
     * Update Map with new location of the user
     */
    private void GPSUpdateLocation() {
        LocationRequest locationRequest = setLocationRequest();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                LatLng lastLocation = onLocationChanged(locationResult.getLastLocation());
                String latitude = String.valueOf(lastLocation.latitude);
                String longitude = String.valueOf(lastLocation.longitude);

                UserHelper.updateLocation(getCurrentUser().getUid(), latitude, longitude);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 14F));
                getResult(latitude + "," + longitude);

            }},null);
    }

    @Override
    public void onCameraMove() {
        LatLng centerMap = mGoogleMap.getCameraPosition().target;
        getResult(centerMap.latitude + "," + centerMap.longitude);

    }
    //------------------------------//
    //-- REQUEST ON NEARBY SEARCH --//
    //------------------------------//
    /**
     * Get result of NearbySearch
     * @param location the user's location
     */
    private void getResult(String location) {
        CompositeDisposable disable= new CompositeDisposable();
        disable.add(GooglePlacesStreams.getListRestaurant(location, "6000", "restaurant", BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError));
    }

    /**
     * Add markers according to result of request
     */
    private void handleResponse(NearbySearch nearbySearch) {
        progressBar.setVisibility(View.GONE);
        List<Result> listResults = nearbySearch.getResults();

        for (int i = 0; i <listResults.size(); i++) {
            LatLng latLng = new LatLng(listResults.get(i).getGeometry().getLocation().getLat(), listResults.get(i).getGeometry().getLocation().getLng());
            String opening;
            if (listResults.get(i).getOpeningHours() != null) {
                if (listResults.get(i).getOpeningHours().getOpenNow()) {
                    opening = getString(R.string.restaurant_open);
                } else {
                    opening = getString(R.string.restaurant_closed);
                }
            } else {
                opening = getString(R.string.restaurant_no_opening_available);
            }
            setMarker(listResults.get(i).getPlaceId(), opening, mGoogleMap, latLng, listResults.get(i).getName());
        }
    }

    private void handleError(Throwable error) {
        Log.d("TAG", error.getLocalizedMessage());
        mapView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
    }

    //---------------------------------//
    //-- ACTION WHEN CLICK ON MARKER --//
    //---------------------------------//
    @Override
    public boolean onMarkerClick(Marker marker) {
        //-- Return false to centered and open the marker's info window
        return false;
    }

    //--------------------------------------//
    //-- ACTION WHEN CLICK IN INFO WINDOW --//
    //--------------------------------------//
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(requireActivity(), LunchActivity.class);
        intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, marker.getTag().toString());
        startActivity(intent);
    }

    //------------------------//
    //-- FRAGMENT LIFECYCLE --//
    //------------------------//

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}




