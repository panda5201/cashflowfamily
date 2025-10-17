package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var tvCurrentMonthYear: TextView
    private lateinit var monthYearNavigation: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvCurrentDate = view.findViewById<TextView>(R.id.tv_current_date)
        val localeID = Locale.forLanguageTag("id-ID")
        val fullDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", localeID)
        tvCurrentDate.text = fullDateFormat.format(Calendar.getInstance().time)

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)
        tvCurrentMonthYear = view.findViewById(R.id.tv_current_month_year)
        val btnPrevMonth = view.findViewById<ImageButton>(R.id.btn_prev_month)
        val btnNextMonth = view.findViewById<ImageButton>(R.id.btn_next_month)
        monthYearNavigation = view.findViewById(R.id.month_year_navigation)

        viewPager.adapter = DashboardPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_harian)
                1 -> getString(R.string.tab_mingguan)
                2 -> getString(R.string.tab_bulanan)
                3 -> getString(R.string.tab_tahunan)
                else -> null
            }
        }.attach()

        updateDateDisplay()

        btnPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateDateDisplay()
        }

        btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateDateDisplay()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                monthYearNavigation.visibility = if (tab?.position == 1) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val fabAddTransaction = view.findViewById<FloatingActionButton>(R.id.fab_add_transaction)
        fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_formTransaksiFragment)
        }
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID"))
        tvCurrentMonthYear.text = sdf.format(calendar.time)
    }
}
