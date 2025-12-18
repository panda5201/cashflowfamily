package com.example.cashflowfamily

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cashflowfamily.data.*
import com.example.cashflowfamily.utils.UserManager
import com.google.android.material.button.MaterialButtonToggleGroup
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FormTransaksiFragment : Fragment() {
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var transactionTypeString = TransactionType.EXPENSE.name
    private val calendar = Calendar.getInstance()
    private var newImageUri: Uri? = null
    private var existingTransaction: Transaction? = null
    private lateinit var btnTambahKategori: Button

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) launchCamera()
            else Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                newImageUri = it
                showImagePreview(it)
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            newImageUri?.let { showImagePreview(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val transactionId = arguments?.getLong("transactionId", -1L) ?: -1L
        val title = if (transactionId != -1L) "Edit Transaksi" else "Form Transaksi"
        (activity as AppCompatActivity).supportActionBar?.title = title
        return inflater.inflate(R.layout.fragment_form_transaksi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionId = arguments?.getLong("transactionId", -1L) ?: -1L
        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggle_button_group)
        val etTanggal = view.findViewById<EditText>(R.id.et_tanggal)
        val spinnerKategori = view.findViewById<Spinner>(R.id.spinner_kategori)
        val btnAmbilFoto = view.findViewById<Button>(R.id.btn_ambil_foto)
        val btnPilihGaleri = view.findViewById<Button>(R.id.btn_pilih_galeri)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
        val btnHapus = view.findViewById<Button>(R.id.btn_hapus)
        btnTambahKategori = view.findViewById(R.id.btn_tambah_kategori_baru)

        if (UserManager.getUserRole() == "Admin") {
            btnTambahKategori.visibility = View.VISIBLE
            btnTambahKategori.setOnClickListener { showAddCategoryDialog() }
        } else {
            btnTambahKategori.visibility = View.GONE
        }

        if (transactionId != -1L) {
            existingTransaction = TransactionRepository.getTransactionById(transactionId)
            btnHapus.visibility = View.VISIBLE
            fillFormWithData()
        } else {
            btnHapus.visibility = View.GONE
            setupDatePicker(etTanggal)
        }

        CategoryRepository.categoriesLiveData.observe(viewLifecycleOwner) {
            updateSpinner(spinnerKategori)
        }
        CategoryRepository.fetchCategories()
        updateSpinner(spinnerKategori)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                transactionTypeString = if (checkedId == R.id.btn_pengeluaran)
                    TransactionType.EXPENSE.name else TransactionType.INCOME.name
                updateSpinner(spinnerKategori)
            }
        }

        btnPilihGaleri.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        btnAmbilFoto.setOnClickListener { checkCameraPermissionAndLaunch() }

        btnHapus.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Transaksi")
                .setMessage("Apakah Anda yakin?")
                .setPositiveButton("Hapus") { _, _ ->
                    existingTransaction?.id?.let {
                        TransactionRepository.deleteTransaction(it)
                        Toast.makeText(requireContext(), "Transaksi dihapus", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        btnSimpan.setOnClickListener { saveTransaction() }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.et_new_category_name)
        val radioGroupType = dialogView.findViewById<RadioGroup>(R.id.radio_group_category_type)
        val rbIncome = dialogView.findViewById<RadioButton>(R.id.rb_income_type)
        val rbExpense = dialogView.findViewById<RadioButton>(R.id.rb_expense_type)

        if (transactionTypeString == TransactionType.INCOME.name) {
            rbIncome.isChecked = true
        } else {
            rbExpense.isChecked = true
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Kategori Baru")
            .setView(dialogView)
            .setPositiveButton("Tambah") { dialog, _ ->
                val newCategoryName = etCategoryName.text.toString().trim()
                val newCategoryType = if (radioGroupType.checkedRadioButtonId == R.id.rb_income_type) {
                    TransactionType.INCOME.name
                } else {
                    TransactionType.EXPENSE.name
                }

                if (newCategoryName.isEmpty()) {
                    Toast.makeText(requireContext(), "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
                } else {
                    CategoryRepository.addCategory(newCategoryName, newCategoryType) { success, message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun fillFormWithData() {
        existingTransaction?.let { trx ->
            val toggleGroup = view?.findViewById<MaterialButtonToggleGroup>(R.id.toggle_button_group)

            if (trx.type == TransactionType.INCOME.name) {
                toggleGroup?.check(R.id.btn_pemasukan)
            } else {
                toggleGroup?.check(R.id.btn_pengeluaran)
            }
            transactionTypeString = trx.type

            calendar.timeInMillis = trx.date
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("in-ID"))
            view?.findViewById<EditText>(R.id.et_tanggal)?.setText(dateFormat.format(Date(trx.date)))

            val categories = if (trx.type == TransactionType.EXPENSE.name) {
                CategoryRepository.getExpenseCategories()
            } else {
                CategoryRepository.getIncomeCategories()
            }
            val categoryPosition = categories.indexOf(trx.title)
            if (categoryPosition >= 0) {
                view?.findViewById<Spinner>(R.id.spinner_kategori)?.setSelection(categoryPosition)
            }

            view?.findViewById<EditText>(R.id.et_jumlah)?.setText(abs(trx.amount).toString())
            view?.findViewById<EditText>(R.id.et_keterangan)?.setText(trx.description)

            if (!trx.imageUri.isNullOrEmpty()) {
                val ivPreview = view?.findViewById<ImageView>(R.id.iv_preview)
                ivPreview?.visibility = View.VISIBLE
                Glide.with(this)
                    .load(trx.imageUri)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivPreview!!)
            }
        }
    }

    private fun saveTransaction() {
        val spinnerKategori = view?.findViewById<Spinner>(R.id.spinner_kategori)
        val etJumlah = view?.findViewById<EditText>(R.id.et_jumlah)
        val etKeterangan = view?.findViewById<EditText>(R.id.et_keterangan)

        val kategori = spinnerKategori?.selectedItem?.toString()
        val jumlahString = etJumlah?.text.toString()
        val keterangan = etKeterangan?.text.toString()

        if (kategori == null) {
            Toast.makeText(requireContext(), "Pilih kategori", Toast.LENGTH_SHORT).show()
            return
        }
        if (jumlahString.isEmpty()) {
            Toast.makeText(requireContext(), "Jumlah harus diisi", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = jumlahString.toDouble()

        val imageToSend = when {
            newImageUri != null -> newImageUri.toString()
            existingTransaction != null -> existingTransaction!!.imageUri
            else -> null
        }

        val transactionToSave = Transaction(
            id = existingTransaction?.id ?: 0,
            memberId = UserManager.getUserId(),
            title = kategori,
            amount = abs(amount),
            type = transactionTypeString,
            date = calendar.timeInMillis,
            description = keterangan,
            imageUri = imageToSend
        )

        Toast.makeText(requireContext(), "Sedang menyimpan...", Toast.LENGTH_SHORT).show()
        val btnSimpan = view?.findViewById<Button>(R.id.btn_simpan)
        btnSimpan?.isEnabled = false

        if (existingTransaction != null) {
            TransactionRepository.updateTransaction(transactionToSave)
            Toast.makeText(requireContext(), "Transaksi diperbarui", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        } else {
            TransactionRepository.addTransaction(transactionToSave) { warningMessage ->

                btnSimpan?.isEnabled = true

                if (!warningMessage.isNullOrEmpty()) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("PERINGATAN ANGGARAN")
                        .setMessage(warningMessage)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Saya Mengerti") { dialog, _ ->
                            dialog.dismiss()
                            findNavController().popBackStack()
                        }
                        .setCancelable(false)
                        .show()

                } else {
                    Toast.makeText(requireContext(), "Transaksi Disimpan", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun showImagePreview(uri: Uri) {
        val ivPreview = view?.findViewById<ImageView>(R.id.iv_preview)
        ivPreview?.apply {
            setImageURI(uri)
            visibility = View.VISIBLE
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> launchCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Izin Kamera")
                    .setMessage("Aplikasi perlu kamera untuk bukti transaksi.")
                    .setPositiveButton("OK") { _, _ -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                    .setNegativeButton("Batal", null)
                    .show()
            }
            else -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir == null) return

        val photoFile: File = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        newImageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, newImageUri)
        cameraLauncher.launch(intent)
    }

    private fun updateSpinner(spinner: Spinner) {
        val currentCategories = if (transactionTypeString == TransactionType.EXPENSE.name) {
            CategoryRepository.getExpenseCategories()
        } else {
            CategoryRepository.getIncomeCategories()
        }
        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currentCategories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }

    private fun setupDatePicker(etTanggal: EditText) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("in-ID"))
        etTanggal.setText(dateFormat.format(Date()))
        etTanggal.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    etTanggal.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}