package com.example.cashflowfamily

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cashflowfamily.data.Transaction
import com.example.cashflowfamily.data.TransactionType
import com.example.cashflowfamily.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class FormTransaksiFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var selectedDate = Calendar.getInstance()
    private var imageUri: Uri? = null

    private lateinit var etJudul: EditText
    private lateinit var etNominal: EditText
    private lateinit var etKeterangan: EditText
    private lateinit var rgType: RadioGroup
    private lateinit var tvDate: TextView
    private lateinit var ivProofPreview: ImageView

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            ivProofPreview.setImageURI(it)
            ivProofPreview.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_form_transaksi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etJudul = view.findViewById(R.id.et_judul)
        etNominal = view.findViewById(R.id.et_nominal)
        etKeterangan = view.findViewById(R.id.et_keterangan)
        rgType = view.findViewById(R.id.rg_type)
        tvDate = view.findViewById(R.id.tv_date)
        ivProofPreview = view.findViewById(R.id.iv_proof_preview)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
        val btnUpload = view.findViewById<Button>(R.id.btn_upload_proof)

        updateDateInView()

        tvDate.setOnClickListener { showDatePicker() }
        btnUpload.setOnClickListener { pickImage.launch("image/*") }
        btnSimpan.setOnClickListener { saveTransaction() }
    }

    private fun saveTransaction() {
        val title = etJudul.text.toString()
        val amount = etNominal.text.toString().toDoubleOrNull()

        if (title.isBlank() || amount == null || amount == 0.0) {
            Toast.makeText(requireContext(), "Judul dan Nominal harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedTypeId = rgType.checkedRadioButtonId
        val type = if (selectedTypeId == R.id.rb_income) TransactionType.INCOME else TransactionType.EXPENSE

        val newTransaction = Transaction(
            id = 0, // ID akan dibuat oleh Repository
            title = title,
            amount = amount,
            type = type,
            date = selectedDate.time,
            description = etKeterangan.text.toString().takeIf { it.isNotBlank() },
            imageUri = imageUri
        )

        viewModel.addTransaction(newTransaction)
        Toast.makeText(requireContext(), "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val myFormat = "dd MMMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.forLanguageTag("id-ID"))
        tvDate.text = sdf.format(selectedDate.time)
    }
}