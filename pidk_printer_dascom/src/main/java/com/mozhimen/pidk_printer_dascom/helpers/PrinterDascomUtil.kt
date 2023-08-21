package com.mozhimen.pidk_printer_dascom.helpers

import androidx.appcompat.app.AppCompatActivity
import com.mozhimen.basick.manifestk.cons.CPermission
import com.mozhimen.basick.manifestk.permission.ManifestKPermission
import com.mozhimen.basick.utilk.android.app.UtilKPermission
import com.mozhimen.basick.utilk.android.util.wt
import com.mozhimen.basick.utilk.bases.IUtilK
import com.mozhimen.bluetoothk.BluetoothK
import com.mozhimen.bluetoothk.commons.BluetoothKConnectWithDataManageCallback


/**
 * @ClassName PrinterDascomUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/8/21 12:01
 * @Version 1.0
 */
object PrinterDascomUtil : IUtilK {
    @JvmStatic
    fun select(activity: AppCompatActivity, callback: BluetoothKConnectWithDataManageCallback) {
        if (UtilKPermission.checkPermissions(arrayOf(CPermission.ACCESS_COARSE_LOCATION, CPermission.ACCESS_FINE_LOCATION))) {
            BluetoothK.instance.connectBluetooth(activity, callback)
        } else {
            ManifestKPermission.requestPermissions(activity, arrayOf(CPermission.ACCESS_COARSE_LOCATION, CPermission.ACCESS_FINE_LOCATION)) {
                if (it) select(activity, callback)
                else "selectBluetoothPrinter dont have permission".wt(TAG)
            }
        }
    }
}