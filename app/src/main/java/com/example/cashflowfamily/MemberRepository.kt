package com.example.cashflowfamily.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cashflowfamily.Member
import java.util.concurrent.atomic.AtomicLong

object MemberRepository {

    private val idCounter = AtomicLong(0)

    private val members = mutableListOf(
        Member(idCounter.incrementAndGet(), "Ayah", "admin@gmail.com", "Admin"),
        Member(idCounter.incrementAndGet(), "Ibu", "ibu@gmail.com", "Admin"),
        Member(idCounter.incrementAndGet(), "Budi", "anggota@gmail.com", "Anggota Keluarga")
    )

    private val _membersLiveData = MutableLiveData<List<Member>>(members)
    val membersLiveData: LiveData<List<Member>> = _membersLiveData


    fun getMemberById(id: Long): Member? {
        return members.find { it.id == id }
    }

    fun addMember(member: Member) {
        val newMember = member.copy(id = idCounter.incrementAndGet())
        members.add(newMember)
        _membersLiveData.value = members.toList()
    }

    fun updateMember(updatedMember: Member) {
        val index = members.indexOfFirst { it.id == updatedMember.id }
        if (index != -1) {
            members[index] = updatedMember
            _membersLiveData.value = members.toList()
        }
    }

    fun deleteMember(id: Long) {
        members.removeIf { it.id == id }
        _membersLiveData.value = members.toList()
    }
}