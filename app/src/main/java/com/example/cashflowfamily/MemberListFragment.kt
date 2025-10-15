package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.adapter.MemberAdapter
import com.example.cashflowfamily.MemberRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MemberListFragment : Fragment() {

    private lateinit var memberAdapter: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fabAddMember = view.findViewById<FloatingActionButton>(R.id.fab_add_member)
        val rvMembers = view.findViewById<RecyclerView>(R.id.rv_members)

        val userRole = activity?.intent?.getStringExtra("USER_ROLE")

        // Tampilkan tombol Tambah hanya untuk Admin
        if (userRole == "Admin") {
            fabAddMember.visibility = View.VISIBLE
            fabAddMember.setOnClickListener {
                // Navigasi ke form tambah (tanpa mengirim ID)
                findNavController().navigate(R.id.action_memberListFragment_to_addEditMemberFragment)
            }
        } else {
            fabAddMember.visibility = View.GONE
        }

        memberAdapter = MemberAdapter { member ->
            // Jika Admin, navigasi ke form edit dengan mengirim ID
            if (userRole == "Admin") {
                val bundle = Bundle().apply {
                    putLong("memberId", member.id)
                }
                findNavController().navigate(R.id.action_memberListFragment_to_addEditMemberFragment, bundle)
            }
            // Jika bukan admin, tidak terjadi apa-apa saat item diklik
        }

        rvMembers.layoutManager = LinearLayoutManager(requireContext())
        rvMembers.adapter = memberAdapter

        // Observe data dari repository
        MemberRepository.membersLiveData.observe(viewLifecycleOwner) { members ->
            memberAdapter.submitList(members)
        }
    }
}