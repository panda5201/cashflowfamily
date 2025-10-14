package com.example.cashflowfamily

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.cashflowfamily.data.Transaction
import com.example.cashflowfamily.data.TransactionType
import com.example.cashflowfamily.ui.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FormTransaksiFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val args: FormTransaksiFragmentArgs by navArgs()

    private var currentTransaction: Transaction? = null
    private var imageUri: Uri? = null
    private var selectedDate = Calendar.getInstance()

    private lateinit var etJudul: EditText
    private lateinit var etNominal: EditText
    private lateinit var etKeterangan: EditText
    private lateinit var rgType: RadioGroup
    private lateinit var tvDate: TextView
    private lateinit var ivProofPreview: ImageView
    private lateinit var btnDelete: Button

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            ivProofPreview.setImageURI(it)
            ivProofPreview.visibility = View.VISIBLE
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            ivProofPreview.setImageURI(imageUri)
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
        val btnTakePicture = view.findViewById<Button>(R.id.btn_take_picture)
        val btnPickGallery = view.findViewById<Button>(R.id.btn_pick_gallery)
        btnDelete = view.findViewById(R.id.btn_delete)

        if (args.transactionId != -1L) {
            activity?.title = "Edit Transaksi"
            loadTransactionData()
            btnDelete.visibility = View.VISIBLE
        } else {
            activity?.title = "Tambah Transaksi"
            updateDateInView()
        }

        tvDate.setOnClickListener { showDatePicker() }
        btnSimpan.setOnClickListener { saveTransaction() }
        btnPickGallery.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnTakePicture.setOnClickListener {
            imageUri = createImageUri()
            takePictureLauncher.launch(imageUri)
        }
        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }


    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Transaksi")
            .setMessage("Apakah Anda yakin ingin menghapus transaksi ini? Aksi ini tidak dapat dibatalkan.")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                viewModel.deleteTransaction(args.transactionId)
                Toast.makeText(requireContext(), "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun loadTransactionData() {
        currentTransaction = viewModel.getTransactionById(args.transactionId)
        currentTransaction?.let { transaction ->
            etJudul.setText(transaction.title)
            etNominal.setText(transaction.amount.toBigDecimal().toPlainString())
            etKeterangan.setText(transaction.description)

            if (transaction.type == TransactionType.INCOME) {
                rgType.check(R.id.rb_income)
            } else {
                rgType.check(R.id.rb_expense)
            }

            selectedDate.time = transaction.date
            updateDateInView()

            transaction.imageUri?.let { uri ->
                imageUri = uri
                ivProofPreview.visibility = View.VISIBLE
                Glide.with(this).load(uri).into(ivProofPreview)
            }
        }
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

        if (currentTransaction != null) {
            // Mode UPDATE
            val updatedTransaction = currentTransaction!!.copy(
                title = title,
                amount = amount,
                type = type,
                date = selectedDate.time,
                description = etKeterangan.text.toString().takeIf { it.isNotBlank() },
                imageUri = imageUri
            )
            viewModel.updateTransaction(updatedTransaction)
            Toast.makeText(requireContext(), "Transaksi berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        } else {
            val newTransaction = Transaction(
                id = 0,
                title = title,
                amount = amount,
                type = type,
                date = selectedDate.time,
                description = etKeterangan.text.toString().takeIf { it.isNotBlank() },
                imageUri = imageUri
            )
            viewModel.addTransaction(newTransaction)
            Toast.makeText(requireContext(), "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    private fun createImageUri(): Uri {
        val image = File(requireContext().getExternalFilesDir("Pictures"), "cashflow_proof_${System.currentTimeMillis()}.png")
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            image
        )
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