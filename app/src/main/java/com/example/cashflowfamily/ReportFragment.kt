package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {

    private lateinit var tvPeriodTitle: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPeriodTitle = view.findViewById(R.id.tv_period_title)
        btnPrev = view.findViewById(R.id.btn_prev)
        btnNext = view.findViewById(R.id.btn_next)
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)

        val adapter = ReportPagerAdapter(this)
        viewPager.adapter = adapter

        val titles = listOf("Harian", "Mingguan", "Bulanan", "Tahunan")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        updateTitle()

        btnPrev.setOnClickListener {
            shiftPeriod(-1)
        }
        btnNext.setOnClickListener {
            shiftPeriod(1)
        }
    }

    private fun updateTitle() {
        when (viewPager.currentItem) {
            0, 1 -> {
                val format = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID"))
                tvPeriodTitle.text = format.format(calendar.time)
            }
            2 -> {
                val format = SimpleDateFormat("yyyy", Locale.forLanguageTag("id-ID"))
                tvPeriodTitle.text = format.format(calendar.time)
            }
            3 -> {
                val format = SimpleDateFormat("yyyy", Locale.forLanguageTag("id-ID"))
                tvPeriodTitle.text = format.format(calendar.time)
            }
        }
    }


    private fun shiftPeriod(amount: Int) {
        when (viewPager.currentItem) {
            0, 1 -> calendar.add(Calendar.MONTH, amount)
            2 -> calendar.add(Calendar.YEAR, amount)
            3 -> return
        }
        updateTitle()
    }
}
