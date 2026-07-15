package com.dadaschatpos.ui.settings

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.R
import com.dadaschatpos.databinding.FragmentSettingsBinding
import com.dadaschatpos.ui.AppViewModelFactory
import com.dadaschatpos.util.BackupFileHelper
import com.dadaschatpos.util.BluetoothPrinterManager
import com.dadaschatpos.util.ShareHelper
import com.dadaschatpos.util.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as DadasPosApplication)
    }

    private var waitingForExport = false

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showPrinterSettingsDialog() else Toast.makeText(requireContext(), "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
    }

    private val importBackupLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@registerForActivityResult
        runCatching { BackupFileHelper.readText(requireContext(), uri) }
            .onSuccess { json ->
                waitingForExport = false
                viewModel.importBackup(json)
            }
            .onFailure { Toast.makeText(requireContext(), it.message ?: "Unable to read file", Toast.LENGTH_SHORT).show() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = SettingsAdapter(options(), ::handleOptionClick)
        binding.settingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.settingsRecyclerView.adapter = adapter
        observeViewModel()
    }

    private fun options(): List<SettingsOption> = listOf(
        SettingsOption(SettingsAction.ITEM_MANAGEMENT, "Add / Edit Items", "Add new item, change price and change photo", R.drawable.ic_order),
        SettingsOption(SettingsAction.CHANGE_PRICES, "Quick Price Change", "Edit item price quickly", R.drawable.ic_price),
        SettingsOption(SettingsAction.DAILY_SALES_REPORT, "Daily Sales Report", "View today, weekly and monthly reports", R.drawable.ic_daily_report),
        SettingsOption(SettingsAction.BACKUP_RESTORE, "Backup & Restore", "Export/import JSON database backup", R.drawable.ic_backup),
        SettingsOption(SettingsAction.PRINTER_SETTINGS, "Printer Settings", "Bluetooth thermal printer 58mm / 80mm", R.drawable.ic_bluetooth),
        SettingsOption(SettingsAction.APP_SHARE, "App Share", "Share DADA'S CHAT POS with others", R.drawable.ic_share),
        SettingsOption(SettingsAction.ABOUT_APP, "About App", "Professional panipuri and chat POS", R.drawable.ic_about),
        SettingsOption(SettingsAction.LOGOUT, "Logout", "Sign out from this device", R.drawable.ic_logout)
    )

    private fun handleOptionClick(option: SettingsOption) {
        when (option.action) {
            SettingsAction.ITEM_MANAGEMENT,
            SettingsAction.CHANGE_PRICES -> findNavController().navigate(R.id.action_settings_to_itemManagement)
            SettingsAction.DAILY_SALES_REPORT -> findNavController().navigate(R.id.reportsFragment)
            SettingsAction.BACKUP_RESTORE -> showBackupDialog()
            SettingsAction.PRINTER_SETTINGS -> openPrinterSettings()
            SettingsAction.APP_SHARE -> shareApp()
            SettingsAction.ABOUT_APP -> showAboutDialog()
            SettingsAction.LOGOUT -> confirmLogout()
        }
    }

    private fun showBackupDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Backup & Restore")
            .setItems(arrayOf("Export Database JSON", "Import Database JSON")) { _, which ->
                if (which == 0) {
                    waitingForExport = true
                    viewModel.exportBackup()
                } else {
                    waitingForExport = false
                    importBackupLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                }
            }
            .show()
    }

    private fun openPrinterSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !BluetoothPrinterManager.hasBluetoothConnectPermission(requireContext())) {
            bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            showPrinterSettingsDialog()
        }
    }

    private fun showPrinterSettingsDialog() {
        val devices = BluetoothPrinterManager.pairedDevices(requireContext())
        val message = if (devices.isEmpty()) {
            "No paired thermal printer found. Pair your 58mm / 80mm Bluetooth printer in Android settings."
        } else {
            devices.joinToString(separator = "\n") { "• ${it.name} (${it.address})" }
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Printer Settings")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Try DADA'S CHAT POS - a modern Panipuri and Chat Shop billing app.")
        }
        startActivity(Intent.createChooser(intent, "Share app"))
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("DADA'S CHAT POS")
            .setMessage("Modern restaurant POS for panipuri, chat, bheḷ, billing, PDF receipts, WhatsApp sharing, reports and thermal printer support.\n\nVersion 1.0.0")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Logout") { _, _ ->
                viewModel.logout()
                findNavController().navigate(R.id.action_settings_to_login)
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.backupState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> Unit
                UiState.Loading -> Snackbar.make(binding.root, "Working...", Snackbar.LENGTH_SHORT).show()
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetBackupState()
                }
                is UiState.Success -> {
                    if (waitingForExport) {
                        val uri = BackupFileHelper.writeJsonToCache(requireContext(), state.data)
                        ShareHelper.shareJsonBackup(requireContext(), uri)
                    } else {
                        Toast.makeText(requireContext(), state.data, Toast.LENGTH_SHORT).show()
                    }
                    waitingForExport = false
                    viewModel.resetBackupState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
