package com.example.cashflowfamily.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.cashflowfamily.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

object TransactionRepository {

    val transactionsLiveData = MutableLiveData<List<Transaction>>()
    private var appContext: Context? = null

    fun setContext(context: Context) {
        appContext = context
    }

    private fun uriToBase64(uri: Uri): String {
        if (appContext == null) return ""
        return try {
            val inputStream: InputStream? = appContext!!.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun fetchTransactions(filterMemberId: Long? = null) {
        val userId = UserManager.getUserId()
        val userRole = UserManager.getUserRole()
        Log.d("DEBUG_USER_DATA", "Fetching transactions for User ID: $userId, Role: $userRole, Filtered by: $filterMemberId")

        val call = if (userRole == "Admin" && filterMemberId == null) {
            ApiClient.instance.getTransactions()
        } else if (userRole == "Admin" && filterMemberId != null) {
            ApiClient.instance.getTransactions(filterMemberId)
        }
        else {
            ApiClient.instance.getTransactions(userId)
        }

        call.enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    transactionsLiveData.value = response.body()
                    Log.d("DEBUG_REPO", "Data fetched: ${response.body()?.size} items")
                } else {
                    Log.e("DEBUG_REPO", "Fetch failed: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("DEBUG_REPO", "Fetch Error: ${t.message}")
            }
        })
    }


    fun addTransaction(transaction: Transaction, onComplete: (String?) -> Unit) {
        val userId = UserManager.getUserId()
        if (userId == -1L) {
            return
        }

        val imageString = if (!transaction.imageUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(transaction.imageUri)
                uriToBase64(uri)
            } catch (e: Exception) { "" }
        } else { "" }


        ApiClient.instance.addTransaction(
            userId,
            transaction.title,
            transaction.amount,
            transaction.type,
            transaction.date,
            transaction.description ?: "",
            imageString
        ).enqueue(object : Callback<ResponseModel> {

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("DEBUG_REPO", "Add Success")
                    fetchTransactions()


                    val warningMsg = response.body()?.warning

                    onComplete(warningMsg)
                } else {
                    Log.e("DEBUG_REPO", "Add Failed")
                    onComplete(null)
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Log.e("DEBUG_REPO", "Add Error: ${t.message}")
                onComplete(null)
            }
        })
    }

    fun updateTransaction(transaction: Transaction) {
        val userId = UserManager.getUserId()
        if (userId == -1L) {
            Log.e("DEBUG_REPO", "User not logged in, cannot update transaction.")
            return
        }

        val imageString = if (!transaction.imageUri.isNullOrEmpty()) {
            if (transaction.imageUri!!.startsWith("http")) {
                ""
            } else {
                try {
                    val uri = Uri.parse(transaction.imageUri)
                    uriToBase64(uri)
                } catch (e: Exception) { "" }
            }
        } else { "" }

        ApiClient.instance.updateTransaction(
            transaction.id,
            userId,
            transaction.title,
            transaction.amount,
            transaction.type,
            transaction.date,
            transaction.description ?: "",
            imageString
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DEBUG_REPO", "Update Success")
                    fetchTransactions()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DEBUG_REPO", "Update Error: ${t.message}")
            }
        })
    }

    fun deleteTransaction(id: Long) {
        ApiClient.instance.deleteTransaction(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) fetchTransactions()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    fun getTransactionById(id: Long): Transaction? {
        return transactionsLiveData.value?.find { it.id == id }
    }
}