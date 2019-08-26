package com.menard.go4lunch.controller.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.menard.go4lunch.R
import com.menard.go4lunch.controller.fragment.ListViewFragment
import com.menard.go4lunch.controller.fragment.MapviewFragment
import com.menard.go4lunch.controller.fragment.WorkmatesFragment

class MainActivity : AppCompatActivity() {

    /**Bottom Navigation View */
    private lateinit var bottomNavigationView: BottomNavigationView
    /**Toolbar */
    private lateinit var toolbar: Toolbar
    /** Navigation View */
    private lateinit var navigationView: NavigationView
    /** DrawerLayout*/
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //-- Bottom Navigation View --
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)
        bottomNavigationView.setOnNavigationItemReselectedListener { item ->openFragment(item.itemId) }
        //-- Toolbar --
        toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)

        //-- Configuration --
        configureDrawerLayout()
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

    /**
     * Open a fragment when click on item of the BottomNavigationView
     */
    private fun openFragment(id: Int){
        when(id){
            R.id.action_mapview -> MapviewFragment.newInstance()
            R.id.action_listview -> ListViewFragment.newInstance()
            R.id.action_workmates -> WorkmatesFragment.newInstance()
        }
    }

    //-- CONFIGURATION --
    private fun configureDrawerLayout(){
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer)
        toogle.drawerArrowDrawable.color = resources.getColor(R.color.colorAccent)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
    }

}


