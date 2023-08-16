package com.mozhimen.pidk_printer_dascom

import androidx.appcompat.app.AppCompatActivity
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.manifestk.permission.ManifestKPermission
import com.mozhimen.basick.utilk.android.app.UtilKPermission
import com.mozhimen.basick.utilk.android.util.wt
import com.mozhimen.basick.utilk.bases.BaseUtilK
import com.mozhimen.bluetoothk.commons.BluetoothConnectWithDataManageCallback
import com.mozhimen.bluetoothk.MedBluetooth

/**
 * @ClassName PidKPrinterDascom
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/8/15 15:20
 * @Version 1.0
 */
object PidKPrinterDascom : BaseUtilK() {
    @JvmStatic
    fun selectBluetoothPrinter(activity: AppCompatActivity, callback: BluetoothConnectWithDataManageCallback) {
        if (UtilKPermission.checkPermissions(arrayOf(CPermission.ACCESS_COARSE_LOCATION, CPermission.ACCESS_FINE_LOCATION))) {
            MedBluetooth.connectBluetooth(_context, callback)
        } else {
            ManifestKPermission.requestPermissions(activity, arrayOf(CPermission.ACCESS_COARSE_LOCATION, CPermission.ACCESS_FINE_LOCATION)) {
                if (it) selectBluetoothPrinter(activity, callback)
                else "selectBluetoothPrinter dont have permission".wt(TAG)
            }
        }
    }
}