package com.example.cashflowfamily.data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.example.cashflowfamily.data.Member
import com.example.cashflowfamily.data.Category // Import Category data class

interface ApiService {
    @GET("get_transactions.php")
    fun getTransactions(@Query("member_id") memberId: Long? = null): Call<List<Transaction>>

    @FormUrlEncoded
    @POST("add_transaction.php")
    fun addTransaction(
        @Field("member_id") memberId: Long,
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
        @Field("member_id") memberId: Long,
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

    // API untuk Kategori
    @GET("get_categories.php")
    fun getCategories(): Call<List<Category>>

    @FormUrlEncoded
    @POST("add_category.php")
    fun addCategory(
        @Field("name") name: String,
        @Field("type") type: String
    ): Call<CategoryAddResponse>
}

data class CategoryAddResponse(
    val status: String,
    val message: String,
    val id: Long? // ID kategori yang baru ditambahkan
)