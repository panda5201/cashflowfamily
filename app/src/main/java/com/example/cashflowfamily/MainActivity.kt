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

        // Ambil data peran dari intent, PENTING untuk simulasi
        val userRole = intent.getStringExtra("USER_ROLE")
        val userEmail = intent.getStringExtra("USER_EMAIL")

        // Jika tidak ada peran (misalnya aplikasi dibuka ulang), kembali ke Login
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

        // Pastikan ID fragment Anggota ada di sini
        val topLevelDestinations = setOf(
            R.id.dashboardFragment,
            R.id.transaksiFragment,
            R.id.profilFragment,
            R.id.dataAnakFragment // Pastikan ID ini sesuai dengan ID di nav_graph.xml
        )

        appBarConfiguration = AppBarConfiguration(topLevelDestinations, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)



        // Update header di navigation drawer (tetap ada)
        val headerView = navView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.txtName)
        val headerEmail = headerView.findViewById<TextView>(R.id.txtEmail)
        headerName.text = userRole
        headerEmail.text = userEmail

        // Logika untuk menangani klik di side bar (termasuk logout)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) {
                        drawerLayout.closeDrawers()
                    }
                    handled
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment,
                R.id.transaksiFragment,
                R.id.profilFragment -> {
                    bottomNav.visibility = View.VISIBLE
                }
                else -> {
                    bottomNav.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}