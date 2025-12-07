package com.example.cashflowfamily.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cashflowfamily.Member
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MemberRepository {

    // List LiveData yang akan dipantau oleh Fragment
    private val _membersLiveData = MutableLiveData<List<Member>>()
    val membersLiveData: LiveData<List<Member>> = _membersLiveData

    // Fungsi Mengambil Data dari Server
    // 1. Ambil Data
    fun fetchMembers() {
        ApiClient.instance.getMembers().enqueue(object : Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                if (response.isSuccessful) {
                    _membersLiveData.value = response.body()
                }
            }
            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                Log.e("REPO", "Error: ${t.message}")
            }
        })
    }

    // 2. Tambah Member (FIX ERROR: Unresolved reference addMember)
    fun addMember(member: Member) {
        ApiClient.instance.addMember(member.name, member.email, member.role).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) fetchMembers() // Refresh list setelah nambah
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    // 3. Update Member (FIX ERROR: Unresolved reference updateMember)
    fun updateMember(member: Member) {
        ApiClient.instance.updateMember(member.id, member.name, member.email, member.role).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) fetchMembers()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    // 4. Hapus Member (FIX ERROR: Unresolved reference deleteMember)
    fun deleteMember(id: Long) {
        ApiClient.instance.deleteMember(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) fetchMembers()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    // 5. Get Member by ID
    fun getMemberById(id: Long): Member? {
        return _membersLiveData.value?.find { it.id == id }
    }
}