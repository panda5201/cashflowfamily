package com.example.cashflowfamily

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cashflowfamily.auth.LoginActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userRole = intent.getStringExtra("USER_ROLE")
        val userEmail = intent.getStringExtra("USER_EMAIL")

        if (userRole == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // --- PERUBAHAN DI SINI: Tambahkan semua halaman drawer ke top-level destinations ---
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
                R.id.transaksiFragment,
                R.id.profilFragment,
                R.id.nav_grafik, // <-- DITAMBAHKAN
                R.id.dataAnakFragment,
                R.id.pengaturanFragment, // <-- DITAMBAHKAN
                R.id.tentangAplikasiFragment // <-- DITAMBAHKAN
            ),
            drawerLayout
        )
        // ------------------------------------------------------------------------------------

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNav.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.txtName).text = userRole
        headerView.findViewById<TextView>(R.id.txtEmail).text = userEmail

        navView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.action_logout) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else {
                val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                if (handled) {
                    drawerLayout.closeDrawers()
                }
                handled
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility = when (destination.id) {
                R.id.dashboardFragment, R.id.transaksiFragment, R.id.profilFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}