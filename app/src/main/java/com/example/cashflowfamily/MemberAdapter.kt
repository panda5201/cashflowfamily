package com.example.cashflowfamily.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.R
import com.example.cashflowfamily.Member

class MemberAdapter(private val onItemClick: (Member) -> Unit) :
    ListAdapter<Member, MemberAdapter.MemberViewHolder>(MemberDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = getItem(position)
        holder.bind(member, onItemClick)
    }

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_member_name)
        private val roleTextView: TextView = itemView.findViewById(R.id.tv_member_role)

        fun bind(member: Member, onItemClick: (Member) -> Unit) {
            nameTextView.text = member.name
            roleTextView.text = member.role
            itemView.setOnClickListener { onItemClick(member) }
        }
    }
}

class MemberDiffCallback : DiffUtil.ItemCallback<Member>() {
    override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
        return oldItem == newItem
    }
}