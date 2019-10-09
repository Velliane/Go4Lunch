package com.menard.go4lunch.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.menard.go4lunch.BuildConfig;
import com.menard.go4lunch.R;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.controller.fragment.ChatFragment;
import com.menard.go4lunch.controller.fragment.ListViewFragment;
import com.menard.go4lunch.controller.fragment.MapviewFragment;
import com.menard.go4lunch.controller.fragment.WorkmatesFragment;
import com.menard.go4lunch.model.User;
import com.menard.go4lunch.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    /**
     * Toolbar
     */
    private Toolbar toolbar;
    /**
     * DrawerLayout
     */
    private DrawerLayout drawerLayout;
    /**
     * FusedLocation
     */
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Shared Preferences
     */
    private SharedPreferences sharedPreferences;

    private RelativeLayout autocomplete;
    private AutoCompleteTextView editAutoComplete;
    private ImageButton btnCloseAutocomplete;


    //-------------------------------------//
    //-- BOTTOM NAVIGATION VIEW LISTENER --//
    //-------------------------------------//
    private BottomNavigationView.OnNavigationItemSelectedListener onBottomNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_mapview: {
                    MapviewFragment fragment = MapviewFragment.newInstance();
                    addFragment(fragment);
                    return true;
                }
                case R.id.action_listview: {
                    ListViewFragment fragment = ListViewFragment.newInstance();
                    addFragment(fragment);
                    return true;
                }
                case R.id.action_workmates: {
                    WorkmatesFragment fragment = WorkmatesFragment.newInstance();
                    addFragment(fragment);
                    return true;
                }
                case R.id.action_chat: {
                    ChatFragment fragment = ChatFragment.newInstance();
                    addFragment(fragment);
                    return true;
                }
            }
            return false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        autocomplete = findViewById(R.id.autocomplete_layout);

        //-- Bottom Navigation View --
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onBottomNavigationItemSelectedListener);
        //-- Toolbar --
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        //-- Autocomplete --
        editAutoComplete = findViewById(R.id.autocomplete_restaurant_search);
        btnCloseAutocomplete = findViewById(R.id.autocomplete_restaurant_btn_back);
        btnCloseAutocomplete.setOnClickListener(this);

        Places.initialize(getApplicationContext(), BuildConfig.api_key_google);

        //-- Configuration --
        configureDrawerLayout();
        configureNavigationView();
        //-- Set default selected tab --
        bottomNavigationView.setSelectedItemId(R.id.action_mapview);
    }

    //------------//
    //-- DRAWER --//
    //------------//

    /**
     * Drawer Navigation View
     *
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            case R.id.action_lunch: {
                if (sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null) != null) {
                    String restaurant = sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null);
                    Intent intent = new Intent(this, LunchActivity.class);
                    intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, restaurant);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                    builder.setMessage(getString(R.string.drawer_no_restaurant_selected))
                            .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss())
                            .create().show();
                }
            }

            case R.id.action_logout: {
                signOut();
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //------------------//
    //-- AUTOCOMPLETE --//
    //------------------//

    /**
     * When click on Search Button of the Toolbar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_activity_main_search) {
            toolbar.setVisibility(View.GONE);
            autocomplete.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v == btnCloseAutocomplete) {
            toolbar.setVisibility(View.VISIBLE);
            autocomplete.setVisibility(View.GONE);
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {
//            if (resultCode == Activity.RESULT_OK) {
//                TODO("val place = Autocomplete.getPlaceFromIntent(data!!)")
//
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                TODO()
//            }
//        }
//        val fields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.ADDRESS)
//                        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).setTypeFilter(TypeFilter.ESTABLISHMENT).build(this)
//                        startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETE)
//    }

    //--------------//
    //-- FRAGMENT --//
    //--------------//

    /**
     * Add fragment to Main Activity
     */
    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    //-------------------//
    //-- CONFIGURATION --//
    //-------------------//
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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    //--------------//
    //-- SIGN OUT --//
    //--------------//
    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> startActivity(new Intent(MainActivity.this, AuthActivity.class)));
        }


}



