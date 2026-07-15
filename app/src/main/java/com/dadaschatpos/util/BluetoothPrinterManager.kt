package com.dadaschatpos.util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import java.util.UUID

object BluetoothPrinterManager {
    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    data class PrinterDevice(
        val name: String,
        val address: String
    )

    fun hasBluetoothConnectPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun pairedDevices(context: Context): List<PrinterDevice> {
        if (!hasBluetoothConnectPermission(context)) return emptyList()
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        return adapter.bondedDevices.map { device ->
            PrinterDevice(device.name ?: "Thermal Printer", device.address)
        }.sortedBy { it.name }
    }

    @SuppressLint("MissingPermission")
    fun print(context: Context, address: String, receiptText: String): Result<Unit> = runCatching {
        require(hasBluetoothConnectPermission(context)) { "Bluetooth permission required" }
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: error("Bluetooth not available")
        require(adapter.isEnabled) { "Bluetooth is disabled" }
        val device = adapter.getRemoteDevice(address)
        val socket = device.createRfcommSocketToServiceRecord(sppUuid)
        socket.use { bluetoothSocket ->
            bluetoothSocket.connect()
            bluetoothSocket.outputStream.use { output ->
                output.write(receiptText.toByteArray(Charsets.UTF_8))
                output.write(byteArrayOf(0x0A, 0x0A, 0x1D, 0x56, 0x00))
                output.flush()
            }
        }
    }
}
