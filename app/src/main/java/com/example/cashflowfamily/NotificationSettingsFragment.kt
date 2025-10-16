package com.example.cashflowfamily

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

class NotificationSettingsFragment : Fragment() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Izin notifikasi diberikan.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askNotificationPermission()

        val switchReminder = view.findViewById<SwitchMaterial>(R.id.switch_reminder)
        val timePicker = view.findViewById<TimePicker>(R.id.time_picker)
        val btnSave = view.findViewById<Button>(R.id.btn_save_reminder)

        val sharedPref = activity?.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
        val isReminderEnabled = sharedPref?.getBoolean("is_enabled", false) ?: false
        val hour = sharedPref?.getInt("hour", 20) ?: 20
        val minute = sharedPref?.getInt("minute", 0) ?: 0

        switchReminder.isChecked = isReminderEnabled
        timePicker.hour = hour
        timePicker.minute = minute

        val viewsToShow = if (isReminderEnabled) View.VISIBLE else View.GONE
        timePicker.visibility = viewsToShow
        btnSave.visibility = viewsToShow

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            timePicker.visibility = visibility
            btnSave.visibility = visibility

            if (!isChecked) {
                NotificationScheduler.cancelReminder(requireContext())
                with(sharedPref?.edit()) {
                    this?.putBoolean("is_enabled", false)
                    this?.apply()
                }
                Toast.makeText(context, "Pengingat dinonaktifkan", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            // --- PERUBAHAN DI SINI: Cek izin sebelum menjadwalkan ---
            if (canScheduleExactAlarms()) {
                val selectedHour = timePicker.hour
                val selectedMinute = timePicker.minute

                NotificationScheduler.setReminder(requireContext(), selectedHour, selectedMinute)

                with(sharedPref?.edit()) {
                    this?.putBoolean("is_enabled", true)
                    this?.putInt("hour", selectedHour)
                    this?.putInt("minute", selectedMinute)
                    this?.apply()
                }
                Toast.makeText(context, "Pengingat diatur untuk jam $selectedHour:$selectedMinute setiap hari", Toast.LENGTH_LONG).show()
            } else {
                // Jika izin tidak ada, minta pengguna untuk memberikannya
                requestExactAlarmPermission()
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // --- FUNGSI-FUNGSI BARU DI BAWAH INI ---

    private fun canScheduleExactAlarms(): Boolean {
        // Izin ini hanya diperlukan untuk Android 12 (API 31) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            return alarmManager?.canScheduleExactAlarms() ?: false
        }
        // Versi Android di bawahnya tidak memerlukan izin khusus ini
        return true
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlertDialog.Builder(requireContext())
                .setTitle("Izin Diperlukan")
                .setMessage("Aplikasi ini memerlukan izin untuk mengatur pengingat yang akurat. Harap aktifkan izin 'Alarms & reminders' di pengaturan aplikasi.")
                .setPositiveButton("Buka Pengaturan") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
}