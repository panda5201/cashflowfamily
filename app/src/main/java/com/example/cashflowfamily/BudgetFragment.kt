package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cashflowfamily.data.ApiClient
import com.example.cashflowfamily.data.Member
import com.example.cashflowfamily.data.ResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class BudgetFragment : Fragment() {

    private var memberList: List<Member> = ArrayList()
    private var selectedChildId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerAnak = view.findViewById<Spinner>(R.id.spinner_anak)
        val etNominal = view.findViewById<EditText>(R.id.et_nominal_budget)
        val btnSave = view.findViewById<Button>(R.id.btn_save_budgets)

        loadMembersToSpinner(spinnerAnak)

        spinnerAnak.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (memberList.isNotEmpty()) {
                    selectedChildId = memberList[position].id.toInt()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedChildId = 0
            }
        }

        btnSave.setOnClickListener {
            val nominalStr = etNominal.text.toString()

            if (selectedChildId == 0) {
                Toast.makeText(requireContext(), "Pilih anak terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nominalStr.isEmpty()) {
                Toast.makeText(requireContext(), "Masukkan nominal!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nominal = nominalStr.toDouble()
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentYear = calendar.get(Calendar.YEAR)

            ApiClient.instance.setBudget(selectedChildId, nominal, currentMonth, currentYear)
                .enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                        if (response.body()?.success == true) {
                            Toast.makeText(requireContext(), "Berhasil kirim ke ${memberList.find { it.id.toInt() == selectedChildId }?.name}", Toast.LENGTH_LONG).show()
                            etNominal.text.clear()
                        } else {
                            Toast.makeText(requireContext(), "Gagal: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        Toast.makeText(requireContext(), "Error Koneksi", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun loadMembersToSpinner(spinner: Spinner) {
        ApiClient.instance.getMembers().enqueue(object : Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                if (response.isSuccessful && response.body() != null) {
                    memberList = response.body()!!

                    val namaMember = ArrayList<String>()
                    for (member in memberList) {
                        namaMember.add(member.name + " (${member.role})")
                    }

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, namaMember)
                    spinner.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal memuat data anak", Toast.LENGTH_SHORT).show()
            }
        })
    }
}