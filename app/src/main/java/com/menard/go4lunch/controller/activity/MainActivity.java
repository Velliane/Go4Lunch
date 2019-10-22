package com.menard.go4lunch.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.menard.go4lunch.BuildConfig;
import com.menard.go4lunch.R;
import com.menard.go4lunch.adapter.AutocompleteAdapter;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.controller.fragment.ChatFragment;
import com.menard.go4lunch.controller.fragment.ListViewFragment;
import com.menard.go4lunch.controller.fragment.MapviewFragment;
import com.menard.go4lunch.controller.fragment.WorkmatesFragment;
import com.menard.go4lunch.model.User;
import com.menard.go4lunch.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    /**
     * Toolbar
     */
    private Toolbar toolbar;
    /**
     * DrawerLayout
     */
    private DrawerLayout drawerLayout;
    /**
     * Shared Preferences
     */
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AutocompleteAdapter autocompleteAdapter;
    private PlacesClient placesClient;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        //-- Bottom Navigation View --
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //-- Toolbar --
        toolbar = findViewById(R.id.activity_main_toolbar);
        toolbar.setTitle(getString(R.string.main_activity_title));

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        setSupportActionBar(toolbar);
        Places.initialize(getApplicationContext(), BuildConfig.api_key_google);
        placesClient = Places.createClient(this);
        mRecyclerView = findViewById(R.id.autocomplete_recycler_view);

        //-- Configuration --
        configureDrawerLayout();
        configureNavigationView();
        //-- Set default selected tab --
        bottomNavigationView.setSelectedItemId(R.id.action_mapview);
    }


    //-- DRAWER --//

    /**
     * Drawer Navigation View
     *
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            //-- Drawer layout --
            case R.id.action_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
            case R.id.action_lunch: {
                if (sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null) != null) {
                    String restaurant = sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null);
                    Intent intent = new Intent(this, LunchActivity.class);
                    intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, restaurant);
                    startActivity(intent);
                    break;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                    builder.setMessage(getString(R.string.drawer_no_restaurant_selected))
                            .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss())
                            .create().show();
                    break;
                }
            }
            case R.id.action_logout: {
                signOut();
                break;
            }
            //-- Bottom navigation view --
            case R.id.action_mapview: {
                MapviewFragment mapviewFragment = MapviewFragment.newInstance();
                addFragment(mapviewFragment);
                return true;
            }
            case R.id.action_listview: {
                ListViewFragment listViewFragment = ListViewFragment.newInstance();
                addFragment(listViewFragment);
                return true;
            }
            case R.id.action_workmates: {
                toolbar.setTitle(getString(R.string.workmates_fragment_title));
                WorkmatesFragment fragment = WorkmatesFragment.newInstance();
                addFragment(fragment);
                return true;
            }
            case R.id.action_chat: {
                toolbar.setTitle(getString(R.string.chat_fragment_title));
                ChatFragment fragment = ChatFragment.newInstance();
                addFragment(fragment);
                return true;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    //-- AUTOCOMPLETE --//

    /**
     * When click on Search Button of the Toolbar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (bottomNavigationView.getSelectedItemId() == R.id.action_mapview || bottomNavigationView.getSelectedItemId() == R.id.action_listview) {
            if(!newText.equals("")) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                LocationRequest locationRequest = setLocationRequest();
                if (checkPermissions()) {

                    mFusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            LatLng lastLocation = onLocationChanged(locationResult.getLastLocation());
                            LatLng bounds = new LatLng(lastLocation.latitude, lastLocation.longitude);

                            autocompleteAdapter = new AutocompleteAdapter(getApplicationContext(), placesClient, bounds);
                            autocompleteAdapter.getFilter().filter(newText.toLowerCase());
                            mRecyclerView.setAdapter(autocompleteAdapter);
                            autocompleteAdapter.notifyDataSetChanged();

                        }
                    }, null);
                }
            }
            return true;

        }
        return true;
    }

    //-- FRAGMENT --//

    /**
     * Add fragment to Main Activity
     */
    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    //-- CONFIGURATION --//
    private void configureDrawerLayout() {
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        toogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
    }

    private void configureNavigationView() {
        NavigationView navigationView = findViewById(R.id.activity_main_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //-- Show profile's photo, name and email
        View view = navigationView.getHeaderView(0);
        TextView name = view.findViewById(R.id.header_name);
        TextView email = view.findViewById(R.id.header_email);
        CircleImageView photo = view.findViewById(R.id.image_profile);

        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User currentUser = documentSnapshot.toObject(User.class);
            String displayName = currentUser.getUserName();
            name.setText(displayName);

        }).addOnFailureListener(
                onFailureListener()
        );
        email.setText(getCurrentUser().getEmail());
        if (getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this).load(getCurrentUser().getPhotoUrl()).into(photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        //-- Get search view --
        MenuItem searchMenu = menu.findItem(R.id.menu_activity_main_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

        //-- Add listener to search view --
        searchView.setOnQueryTextListener(this);
           return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //-- SIGN OUT --//
    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> startActivity(new Intent(MainActivity.this, AuthActivity.class)));
    }

}



