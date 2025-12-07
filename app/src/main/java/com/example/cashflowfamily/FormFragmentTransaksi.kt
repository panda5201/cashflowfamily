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
import com.bumptech.glide.Glide
import com.example.cashflowfamily.data.*
import com.google.android.material.button.MaterialButtonToggleGroup
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FormTransaksiFragment : Fragment() {

    private lateinit var spinnerAdapter: ArrayAdapter<String>
    // Default tipe (String)
    private var transactionTypeString = TransactionType.EXPENSE.name
    private val calendar = Calendar.getInstance()

    // imageUri ini untuk MENYIMPAN FOTO BARU (lokal dari HP)
    private var newImageUri: Uri? = null

    private var existingTransaction: Transaction? = null

    // Launcher Izin Kamera
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) launchCamera()
            else Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }

    // Launcher Galeri
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                newImageUri = it // Simpan Uri lokal
                showImagePreview(it)
            }
        }
    }

    // Launcher Kamera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // newImageUri sudah diisi saat launchCamera()
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
                // Simpan tipe sebagai String
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

    private fun fillFormWithData() {
        existingTransaction?.let { trx ->
            val toggleGroup = view?.findViewById<MaterialButtonToggleGroup>(R.id.toggle_button_group)

            // 1. Set Tipe (Bandingkan String)
            if (trx.type == TransactionType.INCOME.name) {
                toggleGroup?.check(R.id.btn_pemasukan)
            } else {
                toggleGroup?.check(R.id.btn_pengeluaran)
            }
            transactionTypeString = trx.type

            // 2. Set Tanggal (Long -> Date String)
            calendar.timeInMillis = trx.date
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("in-ID"))
            view?.findViewById<EditText>(R.id.et_tanggal)?.setText(dateFormat.format(Date(trx.date)))

            // 3. Set Kategori
            val categories = if (trx.type == TransactionType.EXPENSE.name) {
                CategoryRepository.getExpenseCategories(requireContext())
            } else {
                CategoryRepository.getIncomeCategories(requireContext())
            }
            val categoryPosition = categories.indexOf(trx.title)
            if (categoryPosition >= 0) {
                view?.findViewById<Spinner>(R.id.spinner_kategori)?.setSelection(categoryPosition)
            }

            view?.findViewById<EditText>(R.id.et_jumlah)?.setText(abs(trx.amount).toString())
            view?.findViewById<EditText>(R.id.et_keterangan)?.setText(trx.description)

            // 4. Set Gambar (Tampilkan pakai GLIDE karena URL Server)
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

        // LOGIKA PENENTUAN GAMBAR YANG DIKIRIM
        val imageToSend = when {
            newImageUri != null -> newImageUri.toString() // User ambil foto baru (lokal)
            existingTransaction != null -> existingTransaction!!.imageUri // User tidak ganti foto, pakai URL lama
            else -> null // Tidak ada foto
        }

        // Buat objek Transaction
        val transactionToSave = Transaction(
            id = existingTransaction?.id ?: 0,
            title = kategori,
            amount = abs(amount),
            type = transactionTypeString, // String ("INCOME"/"EXPENSE")
            date = calendar.timeInMillis, // Long
            description = keterangan,
            imageUri = imageToSend // String (bisa path lokal atau URL http)
        )

        if (existingTransaction != null) {
            TransactionRepository.updateTransaction(transactionToSave)
            Toast.makeText(requireContext(), "Transaksi diperbarui", Toast.LENGTH_SHORT).show()
        } else {
            TransactionRepository.addTransaction(transactionToSave)
            Toast.makeText(requireContext(), "Transaksi disimpan", Toast.LENGTH_SHORT).show()
        }

        // Cek budget alert jika pengeluaran
        checkBudgetStatus(transactionToSave)

        findNavController().popBackStack()
    }

    private fun checkBudgetStatus(transaction: Transaction) {
        if (transaction.type != TransactionType.EXPENSE.name) return
        val budget = BudgetRepository.getBudgetForCategory(requireContext(), transaction.title) ?: return
        if (budget.amount <= 0) return

        val allTransactions = TransactionRepository.transactionsLiveData.value ?: emptyList()
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        val totalExpenseThisMonth = allTransactions
            .filter {
                val trxCal = Calendar.getInstance().apply { timeInMillis = it.date }
                it.type == TransactionType.EXPENSE.name &&
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

    // --- Helper UI & Permissions ---
    private fun showImagePreview(uri: Uri) {
        val ivPreview = view?.findViewById<ImageView>(R.id.iv_preview)
        ivPreview?.apply {
            setImageURI(uri) // Tampilkan gambar lokal langsung
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
            CategoryRepository.getExpenseCategories(requireContext())
        } else {
            CategoryRepository.getIncomeCategories(requireContext())
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