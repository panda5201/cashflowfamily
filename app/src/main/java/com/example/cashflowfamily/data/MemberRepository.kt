package com.example.cashflowfamily.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData // Import LiveData
import androidx.lifecycle.MutableLiveData // Import MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MemberRepository {

    // Gunakan MutableLiveData secara internal untuk memposting nilai
    private val _membersLiveData = MutableLiveData<List<Member>>()
    // Ekspos sebagai LiveData agar hanya bisa diobservasi dari luar, tidak diubah langsung
    val membersLiveData: LiveData<List<Member>> = _membersLiveData

    private var appContext: Context? = null

    fun setContext(context: Context) {
        appContext = context
    }

    fun fetchMembers() {
        ApiClient.instance.getMembers().enqueue(object : Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                if (response.isSuccessful) {
                    // Gunakan _membersLiveData untuk memposting nilai
                    _membersLiveData.postValue(response.body() ?: emptyList())
                    Log.d("MemberRepo", "Members fetched: ${response.body()?.size} items")
                } else {
                    Log.e("MemberRepo", "Failed to fetch members: ${response.code()}")
                    _membersLiveData.postValue(emptyList()) // Pastikan LiveData memiliki nilai bahkan saat gagal
                }
            }

            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                Log.e("MemberRepo", "Error fetching members: ${t.message}")
                _membersLiveData.postValue(emptyList()) // Pastikan LiveData memiliki nilai bahkan saat gagal
            }
        })
    }

    fun getMemberById(memberId: Long): Member? {
        return membersLiveData.value?.find { it.id == memberId }
    }

    fun addMember(member: Member, callback: (Boolean) -> Unit) {
        ApiClient.instance.addMember(member.name, member.email, member.role)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("MemberRepo", "Member added successfully")
                        fetchMembers() // Refresh list
                        callback(true)
                    } else {
                        Log.e("MemberRepo", "Failed to add member: ${response.code()}")
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MemberRepo", "Error adding member: ${t.message}")
                    callback(false)
                }
            })
    }

    fun updateMember(member: Member, callback: (Boolean) -> Unit) {
        ApiClient.instance.updateMember(member.id, member.name, member.email, member.role)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("MemberRepo", "Member updated successfully")
                        fetchMembers() // Refresh list
                        callback(true)
                    } else {
                        Log.e("MemberRepo", "Failed to update member: ${response.code()}")
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MemberRepo", "Error updating member: ${t.message}")
                    callback(false)
                }
            })
    }

    fun deleteMember(memberId: Long, callback: (Boolean) -> Unit) {
        ApiClient.instance.deleteMember(memberId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("MemberRepo", "Member deleted successfully")
                        fetchMembers() // Refresh list
                        callback(true)
                    } else {
                        Log.e("MemberRepo", "Failed to delete member: ${response.code()}")
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MemberRepo", "Error deleting member: ${t.message}")
                    callback(false)
                }
            })
    }
}