package com.example.cashflowfamily

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DashboardPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DailyReportFragment()
            1 -> WeeklyReportFragment()
            2 -> MonthlyReportFragment()
            3 -> YearlyReportFragment()
            else -> Fragment()
        }
    }
}