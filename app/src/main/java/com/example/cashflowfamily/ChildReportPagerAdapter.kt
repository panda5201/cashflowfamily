package com.example.cashflowfamily

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChildReportPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ChildDailyReportFragment()
            1 -> ChildWeeklyReportFragment()
            2 -> ChildMonthlyReportFragment()
            else -> ChildYearlyReportFragment()
        }
    }
}
