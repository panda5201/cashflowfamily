package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ChildWeeklyReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_child_weekly_report, container, false)
    }

    companion object {
        fun newInstance(): ChildWeeklyReportFragment {
            return ChildWeeklyReportFragment()
        }
    }
}
