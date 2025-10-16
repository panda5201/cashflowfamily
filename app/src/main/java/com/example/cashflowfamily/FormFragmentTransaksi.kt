package com.example.cashflowfamily

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
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
import com.example.cashflowfamily.data.BudgetRepository
import com.example.cashflowfamily.data.CategoryRepository // <-- PASTIKAN IMPORT INI ADA
import com.example.cashflowfamily.data.Transaction
import com.example.cashflowfamily.data.TransactionRepository
import com.example.cashflowfamily.data.TransactionType
import com.google.android.material.button.MaterialButtonToggleGroup
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FormTransaksiFragment : Fragment() {

    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var transactionType = TransactionType.EXPENSE
    private val calendar = Calendar.getInstance()

    private var imageUri: Uri? = null
    private var existingTransaction: Transaction? = null

    // Launcher untuk izin kamera
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    // Launcher untuk galeri
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                imageUri = it
                view?.findViewById<ImageView>(R.id.iv_preview)?.apply {
                    setImageURI(imageUri)
                    visibility = View.VISIBLE
                }
            }
        }
    }

    // Launcher untuk kamera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            view?.findViewById<ImageView>(R.id.iv_preview)?.apply {
                setImageURI(imageUri)
                visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val transactionId = arguments?.getLong("transactionId", -1L) ?: -1L
        if (transactionId != -1L) {
            (activity as AppCompatActivity).supportActionBar?.title = "Edit Transaksi"
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = "Form Transaksi"
        }
        return inflater.inflate(R.layout.fragment_form_transaksi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionId = arguments?.getLong("transactionId", -1L) ?: -1L

        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggle_button_group)
        val etTanggal = view.findViewById<EditText>(R.id.et_tanggal)
        val spinnerKategori = view.findViewById<Spinner>(R.id.spinner_kategori)
        val btnEditKategori = view.findViewById<ImageButton>(R.id.btn_edit_kategori)
        val btnAmbilFoto = view.findViewById<Button>(R.id.btn_ambil_foto)
        val btnPilihGaleri = view.findViewById<Button>(R.id.btn_pilih_galeri)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
        val btnHapus = view.findViewById<Button>(R.id.btn_hapus)

        val userRole = activity?.intent?.getStringExtra("USER_ROLE") ?: "Anggota Keluarga"
        btnEditKategori.visibility = if (userRole == "Admin") View.VISIBLE else View.GONE

        if (transactionId != -1L) {
            existingTransaction = TransactionRepository.getTransactionById(transactionId)
            btnHapus.visibility = View.VISIBLE
            fillFormWithData()
        } else {
            btnHapus.visibility = View.GONE
            setupDatePicker(etTanggal)
        }

        updateSpinner(spinnerKategori)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                transactionType = if (checkedId == R.id.btn_pengeluaran) TransactionType.EXPENSE else TransactionType.INCOME
                updateSpinner(spinnerKategori)
            }
        }

        btnEditKategori.setOnClickListener { showManageKategoriDialog() }
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

    private fun fillFormWithData() {
        existingTransaction?.let { trx ->
            val toggleGroup = view?.findViewById<MaterialButtonToggleGroup>(R.id.toggle_button_group)
            if (trx.type == TransactionType.INCOME) {
                toggleGroup?.check(R.id.btn_pemasukan)
            } else {
                toggleGroup?.check(R.id.btn_pengeluaran)
            }

            calendar.time = trx.date
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("in-ID"))
            view?.findViewById<EditText>(R.id.et_tanggal)?.setText(dateFormat.format(trx.date))

            // --- PERUBAHAN 3: Ambil kategori dari Repository untuk mencari posisi ---
            val categories = if (trx.type == TransactionType.EXPENSE) {
                CategoryRepository.getExpenseCategories(requireContext())
            } else {
                CategoryRepository.getIncomeCategories(requireContext())
            }
            val categoryPosition = categories.indexOf(trx.title)
            if (categoryPosition >= 0) {
                view?.findViewById<Spinner>(R.id.spinner_kategori)?.setSelection(categoryPosition)
            }
            // ----------------------------------------------------------------------

            view?.findViewById<EditText>(R.id.et_jumlah)?.setText(abs(trx.amount).toString())
            view?.findViewById<EditText>(R.id.et_keterangan)?.setText(trx.description)

            trx.imageUri?.let {
                imageUri = it
                view?.findViewById<ImageView>(R.id.iv_preview)?.apply {
                    setImageURI(it)
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkBudgetStatus(transaction: Transaction) {
        if (transaction.type != TransactionType.EXPENSE) return
        val budget = BudgetRepository.getBudgetForCategory(requireContext(), transaction.title) ?: return
        if (budget.amount <= 0) return
        val allTransactions = TransactionRepository.transactionsLiveData.value ?: emptyList()
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val totalExpenseThisMonth = allTransactions
            .filter {
                val trxCal = Calendar.getInstance().apply { time = it.date }
                it.type == TransactionType.EXPENSE &&
                        it.title.equals(transaction.title, ignoreCase = true) &&
                        trxCal.get(Calendar.MONTH) == currentMonth &&
                        trxCal.get(Calendar.YEAR) == currentYear
            }
            .sumOf { abs(it.amount) }
        val usagePercentage = (totalExpenseThisMonth / budget.amount * 100).toInt()
        if (usagePercentage >= 80) {
            val prefs = requireContext().getSharedPreferences("notif_status", Context.MODE_PRIVATE)
            val lastNotifiedPercent = prefs.getInt("last_notif_${transaction.title}", 0)
            if (usagePercentage > lastNotifiedPercent) {
                NotificationHelper.showBudgetAlertNotification(requireContext(), transaction.title, usagePercentage)
                prefs.edit().putInt("last_notif_${transaction.title}", usagePercentage).apply()
            }
        }
    }

    private fun saveTransaction() {
        val spinnerKategori = view?.findViewById<Spinner>(R.id.spinner_kategori)!!
        val etJumlah = view?.findViewById<EditText>(R.id.et_jumlah)!!
        val etKeterangan = view?.findViewById<EditText>(R.id.et_keterangan)!!

        val kategori = spinnerKategori.selectedItem?.toString()
        val jumlahString = etJumlah.text.toString()
        val keterangan = etKeterangan.text.toString()

        if (kategori == null) {
            Toast.makeText(requireContext(), "Pilih kategori", Toast.LENGTH_SHORT).show()
            return
        }
        if (jumlahString.isEmpty()) {
            Toast.makeText(requireContext(), "Jumlah harus diisi", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = jumlahString.toDouble()

        val transactionToSave = existingTransaction?.copy(
            title = kategori,
            amount = if (transactionType == TransactionType.EXPENSE) -amount else amount,
            type = transactionType,
            date = calendar.time,
            description = keterangan,
            imageUri = imageUri
        ) ?: Transaction(
            id = 0,
            title = kategori,
            amount = if (transactionType == TransactionType.EXPENSE) -amount else amount,
            type = transactionType,
            date = calendar.time,
            description = keterangan,
            imageUri = imageUri
        )

        if (existingTransaction != null) {
            TransactionRepository.updateTransaction(transactionToSave)
            Toast.makeText(requireContext(), "Transaksi diperbarui", Toast.LENGTH_SHORT).show()
        } else {
            TransactionRepository.addTransaction(transactionToSave)
            Toast.makeText(requireContext(), "Transaksi disimpan", Toast.LENGTH_SHORT).show()
        }
        checkBudgetStatus(transactionToSave)
        findNavController().popBackStack()
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Izin Kamera Diperlukan")
                    .setMessage("Aplikasi ini memerlukan izin untuk mengambil gambar bukti transaksi.")
                    .setPositiveButton("OK") { _, _ ->
                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (storageDir == null) {
            Toast.makeText(requireContext(), "Gagal mengakses penyimpanan", Toast.LENGTH_SHORT).show()
            return
        }

        val photoFile: File = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        imageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", photoFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraLauncher.launch(intent)
    }

    // --- PERUBAHAN 2: Fungsi ini sekarang mengambil data dari CategoryRepository ---
    private fun updateSpinner(spinner: Spinner) {
        val currentCategories = if (transactionType == TransactionType.EXPENSE) {
            CategoryRepository.getExpenseCategories(requireContext())
        } else {
            CategoryRepository.getIncomeCategories(requireContext())
        }
        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currentCategories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }
    // ---------------------------------------------------------------------------

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

    private fun showManageKategoriDialog() {
        val currentCategories = if (transactionType == TransactionType.EXPENSE) {
            CategoryRepository.getExpenseCategories(requireContext())
        } else {
            CategoryRepository.getIncomeCategories(requireContext())
        }
        val items = currentCategories.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Kelola Kategori")
            .setItems(items) { _, which -> showEditDeleteDialog(items[which]) }
            .setPositiveButton("Tambah Baru") { dialog, _ ->
                showAddKategoriDialog()
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // --- PERUBAHAN 4: Fungsi ini sekarang menyimpan ke CategoryRepository ---
    private fun showAddKategoriDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Kategori Baru")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val newCategory = editText.text.toString().trim()
                if (newCategory.isNotEmpty()) {
                    if (transactionType == TransactionType.EXPENSE) {
                        val categories = CategoryRepository.getExpenseCategories(requireContext())
                        categories.add(newCategory)
                        CategoryRepository.saveExpenseCategories(requireContext(), categories)
                    } else {
                        val categories = CategoryRepository.getIncomeCategories(requireContext())
                        categories.add(newCategory)
                        CategoryRepository.saveIncomeCategories(requireContext(), categories)
                    }
                    spinnerAdapter.add(newCategory)
                    spinnerAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Kategori '$newCategory' ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showEditDeleteDialog(category: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Opsi untuk '$category'")
            .setItems(arrayOf("Edit", "Hapus")) { _, which ->
                val isExpense = transactionType == TransactionType.EXPENSE
                val list = if (isExpense) CategoryRepository.getExpenseCategories(requireContext()) else CategoryRepository.getIncomeCategories(requireContext())
                when (which) {
                    0 -> { // Edit
                        val editText = EditText(requireContext()).apply { setText(category) }
                        AlertDialog.Builder(requireContext())
                            .setTitle("Edit Kategori")
                            .setView(editText)
                            .setPositiveButton("Simpan") { _, _ ->
                                val updatedCategory = editText.text.toString().trim()
                                if (updatedCategory.isNotEmpty()) {
                                    val index = list.indexOf(category)
                                    if (index != -1) {
                                        list[index] = updatedCategory
                                        if (isExpense) CategoryRepository.saveExpenseCategories(requireContext(), list) else CategoryRepository.saveIncomeCategories(requireContext(), list)
                                        updateSpinner(view!!.findViewById(R.id.spinner_kategori))
                                    }
                                }
                            }
                            .setNegativeButton("Batal", null)
                            .show()
                    }
                    1 -> { // Hapus
                        list.remove(category)
                        if (isExpense) CategoryRepository.saveExpenseCategories(requireContext(), list) else CategoryRepository.saveIncomeCategories(requireContext(), list)
                        updateSpinner(view!!.findViewById(R.id.spinner_kategori))
                        Toast.makeText(requireContext(), "Kategori '$category' dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}