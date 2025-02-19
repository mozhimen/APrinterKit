package com.mozhimen.pidk_printer_dascom

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.appcompat.app.AppCompatActivity
import com.dascom.print.PrintCommands.ZPL
import com.dascom.print.Transmission.BluetoothPipe
import com.dascom.print.Transmission.Pipe
import com.dascom.print.Utils.BluetoothUtils
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.pidk_printer_dascom.helpers.PrinterDascomUtil
import java.io.IOException

/**
 * @ClassName PidKPrinterDascom
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/8/15 15:20
 * @Version 1.0
 */
class PidKPrinterDascom : BaseUtilK() {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    ///////////////////////////////////////////////////////////////////////////////////

    private var _pipe: Pipe? = null
    private var _name: String = ""
    private var _mac: String = ""
    private var _smartPrint: ZPL? = null
    val smartPrint get() = _smartPrint
    private var _bluetoothKConnectWithDataManageCallback: BluetoothKConnectWithDataManageCallback? = null
    private var _innerBluetoothKConnectWithDataManageCallback = object : BluetoothKConnectWithDataManageCallback() {
        override fun getMac(name: String?, mac: String?) {
            if (!name.isNullOrEmpty() && !mac.isNullOrEmpty()) {
                _name = name
                _mac = mac
                _bluetoothKConnectWithDataManageCallback?.getMac(_name, _mac)
            }
        }

        override fun connected(socket: BluetoothSocket?, device: BluetoothDevice?, e: Exception?) {
        }

        override fun disconnected() {
        }
    }

    fun openBT(activity: Activity) {
        if (!BluetoothUtils.getInstance().isEnable) {
            BluetoothUtils.getInstance().openBluetooth(activity, 0)
        }
    }

    fun init(callback: BluetoothKConnectWithDataManageCallback) {
        _bluetoothKConnectWithDataManageCallback = callback
    }

    fun select(activity: AppCompatActivity) {
        PrinterDascomUtil.select(activity, _innerBluetoothKConnectWithDataManageCallback)
    }

    fun connect() {
        if (_pipe != null) {
            _pipe?.close()
            _pipe = null
        }
        if (_mac.isEmpty()) {
            "_mac is null".wt(TAG)
            return
        }
        Thread {
            try {
                _pipe = BluetoothPipe(_mac) //蓝牙连接
                _smartPrint = ZPL(_pipe!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun printTest() {
        if (_pipe == null || !_pipe!!.isConnected) return
        Thread {
            _smartPrint?.setLabelStart();
            _smartPrint?.printMyText(
                """
^XA
^LH0,50
^XFR:STOREFMT.ZPL^FS
^FN5^FD2022-07-14 16:47:16^FS
^FN6^FD粤A11011B^FS
^FN7^FD[黄绿双拼色]^FS
^FN10^FD金山工业区大道收费站^FS
^FN13^FDxkcwMDAxEjRWeJASNFAgIBEGFCkAAb6pQTEyMzQ1AAAAAAABAAEAAEcwMDAxMSNFZ4BfpPt5nJgdzEkRwHUC7vAqSmwOehEKEAAAACgV^FS
^XZ
        """.trimIndent()
            )
            val b: Boolean = _smartPrint?.setLabelEnd() ?: false //标签结束，然后开始打印
            "printTest b $b".dt(TAG)
        }.start()
    }

    fun disconnect() {
        if (_pipe != null) {
            _pipe!!.close()
            _pipe = null
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

    private object INSTANCE {
        val holder = PidKPrinterDascom()
    }
}