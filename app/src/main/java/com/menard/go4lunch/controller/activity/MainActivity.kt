package com.menard.go4lunch.controller.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.menard.go4lunch.R
import com.menard.go4lunch.controller.fragment.ListViewFragment
import com.menard.go4lunch.controller.fragment.MapviewFragment
import com.menard.go4lunch.controller.fragment.WorkmatesFragment
import kotlinx.android.synthetic.main.activity_auth.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    /**Bottom Navigation View */
    private lateinit var bottomNavigationView: BottomNavigationView
    /**Toolbar */
    private lateinit var toolbar: Toolbar
    /** Navigation View */
    private lateinit var navigationView: NavigationView
    /** DrawerLayout*/
    private lateinit var drawerLayout: DrawerLayout

    //-- BOTTOM NAVIGATION VIEW LISTENER --
    private val onBottomNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener{
        override fun onNavigationItemSelected(p0: MenuItem): Boolean {
            when(p0.itemId){
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
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //-- Bottom Navigation View --
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener (onBottomNavigationItemSelectedListener)
        //-- Toolbar --
        toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)

        //-- Configuration --
        configureDrawerLayout()
        configureNavigationView()
    }

    //-- TOOLBAR  AND DRAWER --
    /**
     * Drawer Navigation View
     */
    override fun onNavigationItemSelected(p0: MenuItem):Boolean{
        when(p0.itemId){
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_lunch -> startActivity(Intent(this, LunchActivity::class.java))
            R.id.action_logout -> signOut()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /**
     * When click on Search Button of the Toolbar
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return true
    }

    //-- FRAGMENT --
    /**
     * Add fragment to Main Activity
     */
    private fun addFragment(fragment:Fragment){
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                .commit()
    }



    //-- CONFIGURATION --
    private fun configureDrawerLayout(){
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer)
        toogle.drawerArrowDrawable.color = resources.getColor(R.color.colorAccent)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
    }

    private fun configureNavigationView(){
        navigationView = findViewById(R.id.activity_main_navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }


    //-- CLOSING THE NAVIGATION DRAWER --
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }
    /**
     * Sign Out
     */
    private fun signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener{
                    startActivity(Intent(this, AuthActivity::class.java))
                }
    }

}



