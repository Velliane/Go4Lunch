package com.menard.go4lunch.controller.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.menard.go4lunch.BuildConfig;
import com.menard.go4lunch.R;
import com.menard.go4lunch.adapter.ListViewAdapter;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.model.detailsrequest.DetailsRequest;
import com.menard.go4lunch.utils.Constants;
import com.menard.go4lunch.utils.GooglePlacesStreams;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class ListViewFragment extends BaseFragment {


    /**
     * RecyclerView
     */
    private RecyclerView recyclerView;
    /**
     * Progress Bar
     */
    private ProgressBar progressBar;
    /**
     * FusedLocation
     */
    private FusedLocationProviderClient fusedLocationProviderClient;
    /**
     * Places Client
     */
    private PlacesClient placesClient;

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_fragment, container, false);

        recyclerView = view.findViewById(R.id.listview_fragment_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        progressBar = view.findViewById(R.id.list_view_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        //-- Google Places SDK initialization --
        Places.initialize(requireActivity(), BuildConfig.api_key_google);
        placesClient = Places.createClient(requireActivity());

        //-- Layout manager --
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(layoutManager);
        listOfRestaurant();

        return view;
    }

    private void listOfRestaurant() {
        LocationRequest locationRequest = setLocationRequest();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                LatLng lastLocation = onLocationChanged(locationResult.getLastLocation());
                String latitude = String.valueOf(lastLocation.latitude);
                String longitude = String.valueOf(lastLocation.longitude);

                UserHelper.updateLocation(getCurrentUser().getUid(), latitude, longitude);
                getResultWithRXJAVA(latitude + "," + longitude);

            }},null);
    }


    private void getResultWithRXJAVA(String location) {
        CompositeDisposable disable = new CompositeDisposable();
        disable.add(GooglePlacesStreams.getDetailsOfSelectedRestaurant(location, "10000", "restaurant", Constants.FIELD_FOR_DETAILS, BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError));
    }

    private void handleResponse(List<DetailsRequest> detailsRequestList) {
        progressBar.setVisibility(View.GONE);
        ListViewAdapter listViewAdapter = new ListViewAdapter(detailsRequestList, requireActivity());
        recyclerView.setAdapter(listViewAdapter);

    }

    private void handleError(Throwable error) {
        Log.d("TAG", "Error");
    }


}