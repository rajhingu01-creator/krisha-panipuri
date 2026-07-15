package com.dadaschatpos.ui.bill

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.databinding.FragmentBillBinding
import com.dadaschatpos.data.model.OrderWithItems
import com.dadaschatpos.data.model.UserEntity
import com.dadaschatpos.ui.AppViewModelFactory
import com.dadaschatpos.util.BluetoothPrinterManager
import com.dadaschatpos.util.CurrencyFormatter
import com.dadaschatpos.util.DateTimeUtils
import com.dadaschatpos.util.PdfHelper
import com.dadaschatpos.util.ReceiptFormatter
import com.dadaschatpos.util.ShareHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillFragment : Fragment() {
    private var _binding: FragmentBillBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BillViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as DadasPosApplication)
    }

    private val billItemAdapter = BillItemAdapter()
    private var currentReceipt: OrderWithItems? = null
    private var currentUser: UserEntity? = null

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showPrinterDialog() else Toast.makeText(requireContext(), "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.billItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = billItemAdapter
        }
        setupActions()
        observeViewModel()
        viewModel.load(arguments?.getLong("orderId", -1L) ?: -1L)
    }

    private fun setupActions() = with(binding) {
        whatsappShareButton.setOnClickListener {
            val receipt = currentReceipt ?: return@setOnClickListener
            ShareHelper.shareTextOnWhatsApp(requireContext(), ReceiptFormatter.format(currentUser, receipt))
        }
        sharePdfButton.setOnClickListener {
            val receipt = currentReceipt ?: return@setOnClickListener
            val uri = PdfHelper.createReceiptPdf(requireContext(), currentUser, receipt)
            ShareHelper.sharePdf(requireContext(), uri)
        }
        printButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !BluetoothPrinterManager.hasBluetoothConnectPermission(requireContext())) {
                bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                showPrinterDialog()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            currentUser = user
            renderReceipt()
        }
        viewModel.receipt.observe(viewLifecycleOwner) { receipt ->
            currentReceipt = receipt
            renderReceipt()
        }
    }

    private fun renderReceipt() {
        val receipt = currentReceipt
        val user = currentUser
        val hasReceipt = receipt != null
        binding.emptyBillText.isVisible = !hasReceipt
        binding.receiptScrollView.isVisible = hasReceipt
        binding.billActionRow.isVisible = hasReceipt
        if (receipt == null) return

        binding.shopNameText.text = user?.shopName ?: "DADA'S CHAT POS"
        binding.shopAddressText.text = user?.address ?: "Main Road, Your City"
        binding.shopMobileText.text = "Mobile: ${user?.mobile ?: "+91 98765 43210"}"
        binding.billNumberText.text = "Bill Number: #${receipt.order.id}"
        binding.dateTimeText.text = "${DateTimeUtils.formatDate(receipt.order.date)} • ${DateTimeUtils.formatTime(receipt.order.date)}"
        binding.totalAmountText.text = "Total Amount: ${CurrencyFormatter.format(receipt.order.total)}"
        billItemAdapter.submitList(receipt.items)
    }

    private fun showPrinterDialog() {
        val devices = BluetoothPrinterManager.pairedDevices(requireContext())
        if (devices.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("No printer found")
                .setMessage("Pair your 58mm / 80mm Bluetooth thermal printer in Android Bluetooth settings, then try again.")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Printer")
            .setItems(devices.map { it.name }.toTypedArray()) { _, which -> printTo(devices[which]) }
            .show()
    }

    private fun printTo(device: BluetoothPrinterManager.PrinterDevice) {
        val receipt = currentReceipt ?: return
        val context = requireContext().applicationContext
        val text = ReceiptFormatter.format(currentUser, receipt)
        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { BluetoothPrinterManager.print(context, device.address, text) }
            val message = result.fold(
                onSuccess = { "Bill sent to ${device.name}" },
                onFailure = { it.message ?: "Print failed" }
            )
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
