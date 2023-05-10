package com.ratel.ble

import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleDevice
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

class BleRepository {
    var rxBleConnection: RxBleConnection? = null
    private var connectDisposable: Disposable? = null

    fun connectDevice(device: RxBleDevice) {
        connectDisposable = device.establishConnection(false)
            .flatMapSingle { _rxBleConnection ->
                rxBleConnection = _rxBleConnection
                _rxBleConnection.discoverServices()
            }.subscribe({

            }, {
                it.stackTrace
            })
    }

    fun disconnectDevice() {
        connectDisposable?.dispose()
        connectDisposable = null
    }

    fun notificationBle() =
        rxBleConnection?.setupNotification(UUID.fromString("UUID"))
            ?.doOnNext { _ ->

            }
            ?.flatMap { notificationObservable -> notificationObservable }

    fun readBle() {
        rxBleConnection?.readCharacteristic(UUID.fromString("UUID ㅑㅑ"))
    }

    fun writeBle(byteData: ByteArray) =
        rxBleConnection?.writeCharacteristic(
        UUID.fromString("UUID ㅕㅕ"),
        byteData
    )
}