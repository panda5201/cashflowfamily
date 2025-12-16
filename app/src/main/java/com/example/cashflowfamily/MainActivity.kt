package com.example.cashflowfamily

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.navigation.NavigationView
import com.example.cashflowfamily.utils.UserManager
import com.example.cashflowfamily.data.MemberRepository // Import MemberRepository

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        com.example.cashflowfamily.data.TransactionRepository.setContext(applicationContext)
        com.example.cashflowfamily.data.CategoryRepository.setContext(applicationContext) // Inisialisasi CategoryRepository
        com.example.cashflowfamily.data.MemberRepository.setContext(applicationContext) // Inisialisasi MemberRepository (jika diperlukan, tapi untuk sekarang tidak perlu context)

        UserManager.init(applicationContext)

        val userId = intent.getLongExtra("USER_ID", -1L)
        val userRole = intent.getStringExtra("USER_ROLE")
        val userEmail = intent.getStringExtra("USER_EMAIL")
        val userName = intent.getStringExtra("USER_NAME")

        if (userId != -1L && !userRole.isNullOrEmpty() && !userEmail.isNullOrEmpty() && !userName.isNullOrEmpty()) {
            UserManager.saveUser(userId, userName, userEmail, userRole)
            android.util.Log.d("DEBUG_MAIN", "User saved to UserManager: ID=$userId, Role=$userRole")
        }

        if (!UserManager.isLoggedIn()) {
            android.util.Log.d("DEBUG_MAIN", "User not logged in, redirecting to LoginActivity.")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Ambil kategori dari server saat MainActivity dibuat
        com.example.cashflowfamily.data.CategoryRepository.fetchCategories()
        com.example.cashflowfamily.data.MemberRepository.fetchMembers() // Ambil anggota dari server

        val currentUserRole = UserManager.getUserRole()
        val currentUserEmail = UserManager.getUserEmail()
        android.util.Log.d("DEBUG_MAIN", "Current User from UserManager: Role=$currentUserRole, Email=$currentUserEmail")

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
                R.id.profilFragment,
                R.id.nav_grafik,
                R.id.dataAnakFragment,
                R.id.pengaturanFragment,
                R.id.tentangAplikasiFragment,
                R.id.notificationSettingsFragment,
                R.id.budgetFragment,
                R.id.nav_prediction_ml
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.txtName).text = UserManager.getUserName()
        headerView.findViewById<TextView>(R.id.txtEmail).text = currentUserEmail

        if (currentUserRole != "Admin") {
            val menu = navView.menu
            menu.findItem(R.id.budgetFragment)?.isVisible = false
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.action_logout) {
                UserManager.clearUser()
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
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
