package com.sd.bluetooth_arduino

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var arduinoDevice: BluetoothDevice? = null
    private var outputStream: OutputStream? = null
    private lateinit var toggleLedButton: Button

    private var ledOn: Boolean = true
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1
    private var socket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleLedButton = findViewById(R.id.toggleLedButton)

        if (!checkBluetoothPermissions()) {
            requestBluetoothPermissions()
        } else {
            initializeBluetoothAdapter()
            connectToArduino()
        }

        toggleLedButton.setOnClickListener {
            if (checkBluetoothPermissions()) {
                toggleLed()
            } else {
                requestBluetoothPermissions()
            }
        }
    }

    private fun checkBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ), REQUEST_BLUETOOTH_PERMISSIONS
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_BLUETOOTH_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show()
                initializeBluetoothAdapter()
                connectToArduino()
            } else {
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeBluetoothAdapter() {
        bluetoothAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        } else {
            BluetoothAdapter.getDefaultAdapter()
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToArduino() {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Toast.makeText(this, "Ative o Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        if (pairedDevices != null) {
            for (device in pairedDevices) {
                if (device.name == "HC-05" || device.name == "HC-06") {
                    arduinoDevice = device
                    break
                }
            }
        }

        if (arduinoDevice == null) {
            Toast.makeText(this, "Dispositivo Arduino não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            socket = arduinoDevice?.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            outputStream = socket?.outputStream

            Toast.makeText(this, "Conectado ao Arduino", Toast.LENGTH_SHORT).show()

            // Acender o LED automaticamente ao conectar
            sendCommand("AUTORIZADO")
        } catch (e: IOException) {
            Toast.makeText(this, "Erro ao conectar ao Arduino", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun toggleLed() {
        if (ledOn) {
            sendCommand("DESLIGAR")
            ledOn = false
            Toast.makeText(this, "LED desligado", Toast.LENGTH_SHORT).show()
        } else {
            sendCommand("AUTORIZADO")
            ledOn = true
            Toast.makeText(this, "LED ligado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCommand(command: String) {
        try {
            outputStream?.write(command.toByteArray())
        } catch (e: IOException) {
            Toast.makeText(this, "Erro ao enviar comando", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        closeBluetoothConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeBluetoothConnection()
    }

    private fun closeBluetoothConnection() {
        try {
            socket?.close()
            outputStream?.close()
            Toast.makeText(this, "Conexão Bluetooth encerrada", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao encerrar a conexão", Toast.LENGTH_SHORT).show()
        }
    }
}
