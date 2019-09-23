package com.menard.go4lunch.controller.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.controller.fragment.ChatFragment
import com.menard.go4lunch.controller.fragment.ListViewFragment
import com.menard.go4lunch.controller.fragment.MapviewFragment
import com.menard.go4lunch.controller.fragment.WorkmatesFragment
import com.menard.go4lunch.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    /**Bottom Navigation View */
    private lateinit var bottomNavigationView: BottomNavigationView
    /**Toolbar */
    private lateinit var toolbar: Toolbar
    /** Navigation View */
    private lateinit var navigationView: NavigationView
    /** DrawerLayout*/
    private lateinit var drawerLayout: DrawerLayout
    /** FusedLocation */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /** Shared Preferences */
    lateinit var sharedPreferences: SharedPreferences


    //-- BOTTOM NAVIGATION VIEW LISTENER --
    private val onBottomNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_mapview -> {
                    val fragment = MapviewFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.action_listview -> {
                    val fragment = ListViewFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.action_workmates -> {
                    val fragment = WorkmatesFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.action_chat -> {
                    val fragment = ChatFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        //-- Bottom Navigation View --
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(onBottomNavigationItemSelectedListener)
        //-- Toolbar --
        toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)

        Places.initialize(applicationContext, BuildConfig.api_key_google)
//        val placesClient = Places.createClient(applicationContext)

        //-- Configuration --
        configureDrawerLayout()
        configureNavigationView()

        //-- Set default selected tab --
        bottomNavigationView.selectedItemId = R.id.action_mapview
    }

    //-- DRAWER --
    /**
     * Drawer Navigation View
     */
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.action_settings -> {startActivity(Intent(this, SettingsActivity::class.java))}
            R.id.action_lunch -> {

                if(sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null) != null){
                    val restaurant: String? = sharedPreferences.getString(Constants.PREF_RESTAURANT_SELECTED, null)
                    val intent = Intent(this, LunchActivity::class.java)
                    intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, restaurant)
                    startActivity(intent)
                }else{
                    val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                    builder.setMessage("You don't have any restaurant selected for now")
                            .setNegativeButton("Ok"){ dialog, which ->  
                                
                            }
                            .create().show()
                }
            }

            R.id.action_logout -> {signOut()}

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    //-- AUTOCOMPLETE --
    /**
     * When click on Search Button of the Toolbar
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_activity_main_search -> {
                        val fields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.ADDRESS)
                        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).setTypeFilter(TypeFilter.ESTABLISHMENT).build(this)
                        startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETE)
                }
        }
        return true
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
//    }

    //-- FRAGMENT --
    /**
     * Add fragment to Main Activity
     */
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                .commit()
    }



    //-- CONFIGURATION --
    private fun configureDrawerLayout() {
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer)
        toogle.drawerArrowDrawable.color = resources.getColor(R.color.colorAccent)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
    }

    private fun configureNavigationView() {
        navigationView = findViewById(R.id.activity_main_navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        //-- Show profile's photo, name and email
        val view = navigationView.getHeaderView(0)
        val name: TextView = view.findViewById(R.id.header_name)
        val email: TextView = view.findViewById(R.id.header_email)
        val photo: CircleImageView = view.findViewById(R.id.image_profile)

        UserHelper.getUser(getCurrentUser().uid).addOnSuccessListener { documentSnapshot ->
            val currentUser = documentSnapshot.toObject<User>(User::class.java)
            val displayName = currentUser!!.userName
            name.text = displayName
        }
        email.text = getCurrentUser().email
        if (getCurrentUser().photoUrl != null) {
            Glide.with(this).load(getCurrentUser().photoUrl).into(photo)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    //-- CLOSING THE NAVIGATION DRAWER --
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    //-- SIGN OUT --
    private fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    startActivity(Intent(this, AuthActivity::class.java))
                }
    }

}



