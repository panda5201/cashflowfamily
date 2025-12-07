package com.example.cashflowfamily.data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import com.example.cashflowfamily.Member
interface ApiService {
    @GET("get_transactions.php")
    fun getTransactions(): Call<List<Transaction>>

    @FormUrlEncoded
    @POST("add_transaction.php")
    fun addTransaction(
        @Field("title") title: String,
        @Field("amount") amount: Double,
        @Field("type") type: String,
        @Field("date") date: Long,
        @Field("description") description: String,
        @Field("encoded_image") encodedImage: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("update_transaction.php")
    fun updateTransaction(
        @Field("id") id: Long,
        @Field("title") title: String,
        @Field("amount") amount: Double,
        @Field("type") type: String,
        @Field("date") date: Long,
        @Field("description") description: String,
        @Field("encoded_image") encodedImage: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("delete_transaction.php")
    fun deleteTransaction(
        @Field("id") id: Long
    ): Call<Void>

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): Call<LoginResponse>

    @GET("get_members.php")
    fun getMembers(): Call<List<Member>>

    @FormUrlEncoded
    @POST("add_member.php")
    fun addMember(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("role") role: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("update_member.php")
    fun updateMember(
        @Field("id") id: Long,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("role") role: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("delete_member.php")
    fun deleteMember(
        @Field("id") id: Long
    ): Call<Void>
}