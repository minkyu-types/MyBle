package com.ratel.ble

import android.app.Application
import android.os.ParcelUuid
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanResult
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*
import kotlin.collections.ArrayList

class BleViewModel(
    private val bleRepository: BleRepository, application: Application
): AndroidViewModel(application) {
    private var mScanObservable: Disposable? = null
    private var mNotificationObservable: Disposable? = null
    private var mWriteObservable: Disposable? = null
    private lateinit var mConnectionStateObservable: Disposable

    var deviceTitle = ObservableField("")
    var deviceTx = ObservableInt(0)

    var readTxt = MutableLiveData("")

    var isConnecting = ObservableBoolean(false)
    var isScanning = ObservableBoolean(false)
    var isNotifying = ObservableBoolean(false)

    var isConnected = ObservableBoolean(false)
    var isClearable = ObservableBoolean(false)

    private var scanResults: ArrayList<ScanResult?> = ArrayList()
    private val rxBleClient: RxBleClient = RxBleClient.create(application)

    private val _listUpdate = MutableLiveData<ArrayList<ScanResult?>>()
    val listUpdate: LiveData<ArrayList<ScanResult?>>
        get() = _listUpdate

    fun startScan() {
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUID.fromString("SERVICE STRING ###")))
            .build()

        val settings = ScanSettings.Builder()
            .build()

        scanResults = ArrayList()

        mScanObservable = rxBleClient.scanBleDevices(settings, scanFilter)
            .subscribe({ scanResult ->
                addScanResult(scanResult)
            }, {
                Toast.makeText(getApplication(), "UNKNOWN ERROR", Toast.LENGTH_SHORT).show()
            })

        isScanning.set(true)
    }

    fun stopScan() {
        mScanObservable!!.dispose()
        mScanObservable = null
        isScanning.set(false) // 스캔 상태 false

        scanResults = ArrayList() // List 초기화
    }

    fun addScanResult(result: ScanResult) {
        val device = result.bleDevice
        val deviceMacAddress = device.macAddress

        for (dev in scanResults!!) {
            if (dev!!.bleDevice.macAddress == deviceMacAddress) return
        }

        scanResults.add(result)
        _listUpdate.postValue(scanResults)
    }

    fun connectionStateListener(
        device: RxBleDevice,
        connectionState: RxBleConnection.RxBleConnectionState
    ) {
        when(connectionState) {
            RxBleConnection.RxBleConnectionState.CONNECTED -> {
                isConnected.set(true)
            }
            RxBleConnection.RxBleConnectionState.CONNECTING -> {

            }
            RxBleConnection.RxBleConnectionState.DISCONNECTED -> {

            }
            RxBleConnection.RxBleConnectionState.DISCONNECTING -> {

            }
        }
    }

    fun onClickDisconnect() {
        bleRepository.disconnectDevice()
    }

    fun onClickNotify() {
        if (mNotificationObservable == null || mNotificationObservable!!.isDisposed) {
            mNotificationObservable = bleRepository.notificationBle()
                ?.subscribe({ bytes ->
                    readTxt.postValue(byteArrayToHex(bytes))
                }, { e ->
                    e.printStackTrace()
                    bleRepository.disconnectDevice()
                })
        } else {
            mNotificationObservable?.dispose()
        }
    }

    fun showList() {

    }

    private fun byteArrayToHex(a: ByteArray): String {
        val sb = java.lang.StringBuilder(a.size * 2)
        for (b in a) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}