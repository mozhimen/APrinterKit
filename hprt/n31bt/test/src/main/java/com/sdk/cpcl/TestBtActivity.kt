package com.sdk.cpcl

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.mozhimen.kotlin.elemk.android.bluetooth.cons.CBluetoothDevice
import com.mozhimen.kotlin.utilk.android.widget.showToast
import com.mozhimen.kotlin.utilk.java.io.inputStream2bytes_use_stream
import com.mozhimen.kotlin.utilk.kotlin.bytes2str
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDB
import com.sdk.cpcl.databinding.ActivityTestBtBinding
import com.tbruyelle.rxpermissions.RxPermissions
import cpcl.PrinterHelper
import cpcl.PublicFunction
import cpcl.listener.DisConnectBTListener
import rx.functions.Action1

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/2/17
 * @Version 1.0
 */
class TestBtActivity : BaseActivityVDB<ActivityTestBtBinding>() {
    private var _publicFunction: PublicFunction? = null
    private var _bluetoothAdapter: BluetoothAdapter? = null
    private var _connectType = ""
    private var _paperType: String = "0"

    companion object {
        private const val PAPERTYPE: String = "papertype"

        private const val REQUEST_ENABLE_BT: Int = 2
        private const val threeInch: Int = 0
        private const val fourInch: Int = 1
        private const val fourInch_Receipt: Int = 0
        private const val fourInch_Label: Int = 1
        private const val fourInch_Two_BM: Int = 2
        private const val fourInch_Three_BM: Int = 3
        private const val fourInch_Four_BM: Int = 4
    }

    private val mBtReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                try {
                    PrinterHelper.portClose()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                vdb.txtTips.setText(R.string.activity_main_tips)
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d(TAG, "STATE_OFF 手机蓝牙关闭")
                        if (PrinterHelper.IsOpened()) {
                            Log.d(TAG, "BluetoothBroadcastReceiver:Bluetooth close")
                            try {
                                PrinterHelper.portClose()
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            vdb.txtTips.setText(R.string.activity_main_tips)
                            Utility.show(this@TestBtActivity, getString(R.string.activity_main_close))
                        }
                    }

                    BluetoothAdapter.STATE_TURNING_OFF -> Log.d(TAG, "STATE_TURNING_OFF 手机蓝牙正在关闭")
                    BluetoothAdapter.STATE_ON -> Log.d(TAG, "STATE_ON 手机蓝牙开启")
                    BluetoothAdapter.STATE_TURNING_ON -> Log.d(TAG, "STATE_TURNING_ON 手机蓝牙正在开启")
                }
            }
        }
    }

    private val disConnectBTListener = DisConnectBTListener {
        runOnUiThread {
            vdb.txtTips.setText("BT Disconnect")
            ("BT Disconnect").showToast()
        }
    }

    @SuppressLint("HandlerLeak")
    override fun initView(savedInstanceState: Bundle?) {
        setTitle(BuildConfig.VERSION_NAME)
        try {
            //bt
            val intentFilterBt = IntentFilter()
            intentFilterBt.addAction(CBluetoothDevice.ACTION_ACL_DISCONNECTED)
            intentFilterBt.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            registerReceiver(mBtReceiver, intentFilterBt)

            _publicFunction = PublicFunction(this)
            val paperType: String = _publicFunction!!.ReadSharedPreferencesData(PAPERTYPE)
            if ("" != paperType) {
                _paperType = paperType
            }
            //Enable Bluetooth
            enableBluetooth()
            PrinterHelper.setDisConnectBTListener(disConnectBTListener)
        } catch (e: Exception) {
            Log.e("HPRTSDKSample", (StringBuilder(" --> onCreate ")).append(e.message).toString())
        }
    }

    override fun onResume() {
        super.onResume()
        val paperType = _publicFunction!!.ReadSharedPreferencesData(PAPERTYPE)
        if (!TextUtils.isEmpty(paperType)) {
            _paperType = paperType
        }
        val arrpaper = resources.getStringArray(R.array.activity_main_papertype)
        if (Activity_Main.LABEL == _paperType) {
            vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + arrpaper[1])
        } else {
            vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + arrpaper[0])
        }
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        try {
            PrinterHelper.setIsListenerBT(false)
            PrinterHelper.portClose()
            if (mBtReceiver != null) {
                unregisterReceiver(mBtReceiver)
            }
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data == null) return
            when (resultCode) {
                Activity.RESULT_CANCELED -> connectBT(data.getStringExtra("SelectedBDAddress"))

                PrinterHelper.ACTIVITY_PRNFILE -> {
                    val strPRNFile = data.extras!!.getString("FilePath")
                    PrinterHelper.PrintBinaryFile(strPRNFile)
                    return
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, (java.lang.StringBuilder("Activity_Main --> onActivityResult ")).append(e.message).toString())
        }
    }

    /////////////////////////////////////////////////

    private fun connectBT(selectedBDAddress: String?) {
        if (TextUtils.isEmpty(selectedBDAddress)) return
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.activity_devicelist_connect))
        progressDialog.show()
        object : Thread() {
            override fun run() {
                super.run()
                try {
                    val result = PrinterHelper.portOpenBT(applicationContext, selectedBDAddress)
                    runOnUiThread {
                        if (result == 0) vdb.txtTips.setText(getString(R.string.activity_main_connected))
                        else vdb.txtTips.setText(getString(R.string.activity_main_connecterr))
                    }
                    progressDialog.dismiss()
                } catch (e: java.lang.Exception) {
                    progressDialog.dismiss()
                }
            }
        }.start()
    }

    //EnableBluetooth
    private fun enableBluetooth(): Boolean {
        var bRet = false
        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (_bluetoothAdapter != null) {
            if (_bluetoothAdapter!!.isEnabled()) return true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val rxPermissions = RxPermissions(this)
                rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT).subscribe(Action1<Boolean> { aBoolean ->
                    if (aBoolean) {
                        if (ActivityCompat.checkSelfPermission(this@TestBtActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                            return@Action1
                        }
                    }
                })
            } else {
                _bluetoothAdapter!!.enable()
            }
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (!_bluetoothAdapter!!.isEnabled()) {
                bRet = true
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK")
            }
        } else {
            Log.d("HPRTSDKSample", (java.lang.StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString())
        }
        return bRet
    }

    private fun doublePrint() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "500", "1")
            PrinterHelper.setLayer(0)
            PrinterHelper.Align(PrinterHelper.LEFT)
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "66", "CODE128")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, "128", "2", "1", "50", "0", "100", true, "7", "0", "5", "123456789")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "180", "UPCA")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.UPCA, "2", "1", "50", "0", "210", true, "7", "0", "5", "123456789012")
            PrinterHelper.setLayer(1)
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "310", "UPCE")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code128, "2", "1", "50", "0", "340", true, "7", "0", "5", "0234565687")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "440", "EAN8")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.EAN8, "2", "1", "50", "0", "470", true, "7", "0", "5", "12345678")
            if ("1" == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            Log.e("HPRTSDKSample", (e.message.toString()))
        }
    }

    private fun setBluetoothName() {
        AlertDialogUtil.edTextDialog(this, getString(R.string.activity_bluetooth_name), getString(R.string.activity_input_name), "") { data ->
            val result = PrinterHelper.setBluetoothName(data)
            if (result > 0) {
                PrinterHelper.saveParameter()
                (getString(R.string.activity_global_cmd_send)).showToast()
            } else {
                (getString(R.string.send_fail) + result).showToast()
            }
        }
    }

    /**
     * 选择字库打印文本
     */
    private fun fontPrint() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "500", "1")
            PrinterHelper.printTextPro(PrinterHelper.TEXT, "SIMSUN.TTF", 24, 24, 0, 0, "SIMSUN.TTF 24 Test")
            PrinterHelper.printTextPro(PrinterHelper.TEXT, "TT0003M_.TTF", 48, 48, 0, 50, "TT0003M_.TTF 48 Test")
            if ("1" == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun selectPaperSize() {
        val papertype = resources.getStringArray(R.array.activity_main_paper_size)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.activity_esc_function_print_type))
            .setItems(papertype) { dialogInterface, i ->
                when (i) {
                    0 -> paperAlertDialog(threeInch)
                    1 -> paperAlertDialog(fourInch)
                    else -> {}
                }
            }.show()
    }

    private fun setExpress() {
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.logo2)
        val cities = resources.getStringArray(R.array.activity_main_express)
        builder.setItems(cities) { dialog, which ->
            when (which) {
                0 -> STexpress()
                1 -> ZTexpress()
                2 -> TTexpress()
                else -> {}
            }
        }
        builder.show()
    }

    private fun setTestPage() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "1400", "1")
            PrinterHelper.Box("50", "5", "450", "400", "1")
            PrinterHelper.Align(PrinterHelper.CENTER)
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "50", "5", resources.getString(R.string.activity_test_page))
            PrinterHelper.Align(PrinterHelper.LEFT)
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "66", "CODE128")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, "128", "2", "1", "50", "0", "100", true, "7", "0", "5", "123456789")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "180", "UPCA")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.UPCA, "2", "1", "50", "0", "210", true, "7", "0", "5", "123456789012")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "310", "UPCE")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code128, "2", "1", "50", "0", "340", true, "7", "0", "5", "0234565687")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "440", "EAN8")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.EAN8, "2", "1", "50", "0", "470", true, "7", "0", "5", "12345678")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "570", "CODE93")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code93, "2", "1", "50", "0", "600", true, "7", "0", "5", "123456789")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "700", "CODE39")
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code39, "2", "1", "50", "0", "730", true, "7", "0", "5", "123456789")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "830", resources.getString(R.string.activity_esc_function_btnqrcode))
            PrinterHelper.PrintQR(PrinterHelper.BARCODE, "0", "870", "4", "6", "ABC123")
            PrinterHelper.PrintQR(PrinterHelper.BARCODE, "150", "870", "4", "6", "ABC123")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "1000", resources.getString(R.string.activity_test_line))
            PrinterHelper.Line("0", "1030", "400", "1030", "1")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "1050", resources.getString(R.string.activity_test_box))
            PrinterHelper.Box("10", "1080", "400", "1300", "1")
            if ("1" == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            Log.e("HPRTSDKSample", (e.message.toString()))
        }
    }

    private fun PrintSN() {
        try {
            val printSN = PrinterHelper.getPrintSN()
            if (TextUtils.isEmpty(printSN)) {
                (getString(R.string.activity_main_data_error)).showToast()
                return
            }
            (printSN).showToast()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun ReverseFeed() {
        try {
            PrinterHelper.ReverseFeed(50)
        } catch (e: java.lang.Exception) {
        }
    }

    private fun TTexpress() {
        try {
            val pum = java.util.HashMap<String, String>()
            pum["[Referred]"] = "蒙 锡林郭勒盟"
            pum["[City]"] = "锡林郭勒盟 包"
            pum["[Number]"] = "108"
            pum["[Receiver]"] = "渝州"
            pum["[Receiver_Phone]"] = "15182429075"
            pum["[Receiver_address1]"] = "内蒙古自治区 锡林郭勒盟 正黄旗 解放东路与" //收件人地址第一行
            pum["[Receiver_address2]"] = "外滩路交叉口62号静安中学静安小区10栋2单元" //收件人第二行（若是没有，赋值""）
            pum["[Receiver_address3]"] = "1706室" //收件人第三行（若是没有，赋值""）
            pum["[Sender]"] = "洲瑜"
            pum["[Sender_Phone]"] = "13682429075"
            pum["[Sender_address1]"] = "浙江省 杭州市 滨江区 滨盛路1505号1706室信息部,滨盛路1505号滨盛" //寄件人地址第一行
            pum["[Sender_address2]"] = "滨盛路1505号1706室信息部" //寄件人第二行（若是没有，赋值""）
            pum["[Barcode]"] = "998016450402"
            pum["[Waybill]"] = "运单号：998016450402"
            pum["[Product_types]"] = "数码产品"
            pum["[Quantity]"] = "数量：22"
            pum["[Weight]"] = "重量：22.66KG"
            val keySet: Set<String> = pum.keys
            val iterator = keySet.iterator()
            val inputStream = this.resources.assets.open("TTKD.txt") //打印模版放在assets文件夹里
            var path = inputStream.inputStream2bytes_use_stream().bytes2str(charset("utf-8"))  //打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                val string = iterator.next()
                path = path.replace(string, pum[string]!!)
            }
            PrinterHelper.printText(path)
            if ("1" == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
            Log.e("HPRTSDKSample", (java.lang.StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.message).toString())
        }
    }

    private fun ZTexpress() {
        try {
            val pum = java.util.HashMap<String, String>()
            pum["[payment]"] = "18"
            pum["[remark]"] = "上海"
            pum["[Barcode]"] = "376714121"
            pum["[orderCodeNumber]"] = "100"
            pum["[date]"] = "200"
            pum["[siteName]"] = "上海 上海市 长宁区"
            pum["[Receiver]"] = "申大通"
            pum["[Receiver_Phone]"] = "13826514987"
            pum["[Receiver_address]"] = "上海市宝山区共和新路47"
            pum["[Sender]"] = "快小宝"
            pum["[Sender_Phone]"] = "13826514987"
            pum["[Sender_address]"] = "上海市长宁区北曜路1178号（鑫达商务楼）"
            pum["[goodName1]"] = "鞋子"
            pum["[goodName2]"] = "衬衫"
            pum["[wight]"] = "10kg"
            pum["[price]"] = "200"
            pum["[payment]"] = "18"
            pum["[orderCode]"] = "12345"
            pum["[goodName]"] = "帽子"
            pum["[nowDate]"] = "2017.3.13"
            val keySet: Set<String> = pum.keys
            val iterator = keySet.iterator()
            val inputStream = this.resources.assets.open("ZhongTong.txt") //打印模版放在assets文件夹里
            var path = inputStream.inputStream2bytes_use_stream().bytes2str(charset("utf-8")) //打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                val string = iterator.next()
                path = path.replace(string, pum[string]!!)
            }
            PrinterHelper.printText(path)
            if ("1" == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
            Log.e("HPRTSDKSample", (java.lang.StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.message).toString())
        }
    }

    private fun STexpress() {
        try {
//			PrinterHelper.openEndStatic(true)
            val pum = java.util.HashMap<String, String>()
            pum["[barcode]"] = "363604310467"
            pum["[distributing]"] = "上海 上海市 长宁区"
            pum["[receiver_name]"] = "申大通"
            pum["[receiver_phone]"] = "13826514987"
            pum["[receiver_address1]"] = "上海市宝山区共和新路4719弄共"
            pum["[receiver_address2]"] = "和小区12号306室" //收件人地址第一行
            pum["[sender_name]"] = "快小宝" //收件人第二行（若是没有，赋值""）
            pum["[sender_phone]"] = "13826514987" //收件人第三行（若是没有，赋值""）
            pum["[sender_address1]"] = "上海市长宁区北曜路1178号（鑫达商务楼）"
            pum["[sender_address2]"] = "1号楼305室"
            val keySet: Set<String> = pum.keys
            val iterator = keySet.iterator()
            val inputStream = this.resources.assets.open("STO_CPCL.txt") //打印模版放在assets文件夹里
            var path = inputStream.inputStream2bytes_use_stream().bytes2str(charset("utf-8")) //打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                val string = iterator.next()
                path = path.replace(string, pum[string]!!)
            }
            PrinterHelper.printText(path)
            val inbmp = this.resources.assets.open("logo_sto_print1.png")
            val bitmap = BitmapFactory.decodeStream(inbmp)
            val inbmp2 = this.resources.assets.open("logo_sto_print2.png")
            val bitmap2 = BitmapFactory.decodeStream(inbmp2)
            PrinterHelper.Expanded("10", "20", bitmap, 0, 0) //向打印机发送LOGO
            PrinterHelper.Expanded("10", "712", bitmap2, 0, 0) //向打印机发送LOGO
            PrinterHelper.Expanded("10", "1016", bitmap2, 0, 0) //向打印机发送LOGO
            if ("1" == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
            //			PrinterHelper.getEndStatus(16)
        } catch (e: java.lang.Exception) {
            Log.e("HPRTSDKSample", (java.lang.StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.message).toString())
        }
    }

    private fun paperAlertDialog(paperSize: Int) {
        val papertype = resources.getStringArray(if (paperSize == threeInch) R.array.activity_main_papertype else R.array.activity_main_papertype_4inch)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.activity_esc_function_btnopencashdrawer))
            .setItems(papertype) { dialog, which ->
                if (paperSize == threeInch) selectThreePaper(which, papertype)
                else selectFourPaper(which, papertype)
            }
        builder.create().show()
    }

    private fun selectFourPaper(which: Int, papertype: Array<String>) {
        try {
            when (which) {
                fourInch_Receipt -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_Receipt)
                fourInch_Label -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_Label)
                fourInch_Two_BM -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_TWO_BM)
                fourInch_Three_BM -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_THREE_BM)
                fourInch_Four_BM -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_FOUR_BM)
                else -> {}
            }
            _publicFunction!!.WriteSharedPreferencesData("papertype", "" + which)
            vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
        } catch (e: java.lang.Exception) {
        }
    }

    private fun selectThreePaper(which: Int, papertype: Array<String>) {
        when (which) {
            1 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LABEL)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "1")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            0 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RECEIPT)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "0")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            2 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_TOP_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "2")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            3 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_BEL_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "3")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            4 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_TOP_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "4")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            5 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_BEL_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "5")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            6 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_TOP_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "6")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            7 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_BEL_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "7")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            8 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_TOP_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "8")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            9 -> try {
                PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_BEL_BM)
                _publicFunction!!.WriteSharedPreferencesData("papertype", "9")
                vdb.containerEscFunction.btnOpenCashDrawer.setText(resources.getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which])
            } catch (e: java.lang.Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            else -> {}
        }
    }

    private fun PrintSampleReceipt() {
        try {
            val ReceiptLines = resources.getStringArray(R.array.activity_main_sample_2inch_receipt)
            PrinterHelper.LanguageEncode = "GBK"
            PrinterHelper.RowSetX("200") //设置X坐标
            PrinterHelper.Setlp("5", "2", "32") //5:字体这个是默认值。2：字体大小。32：设置的整行的行高。
            PrinterHelper.RowSetBold("2") //字体加粗2倍
            PrinterHelper.PrintData(ReceiptLines[0] + "\r\n") //小票内容
            PrinterHelper.RowSetBold("1") //关闭加粗
            PrinterHelper.RowSetX("100")
            PrinterHelper.Setlp("5", "2", "32")
            PrinterHelper.RowSetBold("2")
            PrinterHelper.PrintData(ReceiptLines[1] + "\r\n")
            PrinterHelper.RowSetBold("1") //关闭加粗
            PrinterHelper.RowSetX("100")
            for (i in 2 until ReceiptLines.size) {
                PrinterHelper.Setlp("5", "0", "32")
                PrinterHelper.PrintData(ReceiptLines[i] + "\r\n")
            }
            PrinterHelper.RowSetX("0")
        } catch (e: java.lang.Exception) {
            Log.e(TAG, (java.lang.StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.message).toString())
        }
    }

    ////////////////////////////////////////////////

    @SuppressLint("NewApi")
    fun onClickConnect(view: View) {
//    	if (!checkClick.isClickEvent()) return

        try {
            PrinterHelper.portClose()
            if (view.id == R.id.btnBT) {
                //获取蓝牙动态权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val rxPermissions = RxPermissions(this)
                    rxPermissions.request(
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ).subscribe { aBoolean ->
                        if (aBoolean) {
                            _connectType = "Bluetooth"
                            val intent = Intent(this, BTActivity::class.java)
                            intent.putExtra("TAG", 0)
                            startActivityForResult(intent, 0)
                        }
                    }
                } else {
                    val rxPermissions = RxPermissions(this)
                    rxPermissions.request(
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ).subscribe { aBoolean ->
                        if (aBoolean) {
                            _connectType = "Bluetooth"
                            val intent = Intent(this, BTActivity::class.java)
                            intent.putExtra("TAG", 0)
                            startActivityForResult(intent, 0)
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("HPRTSDKSample", (java.lang.StringBuilder("Activity_Main --> onClickConnect $_connectType")).append(e.message).toString())
        }
    }

    fun onClickClose(view: View?) {
        if (!checkClick.isClickEvent()) return

        try {
            PrinterHelper.portClose()
            vdb.txtTips.setText(R.string.activity_main_tips)
            return
        } catch (e: java.lang.Exception) {
            Log.e(TAG, (java.lang.StringBuilder("Activity_Main --> onClickClose ")).append(e.message).toString())
        }
    }

    fun onClickbtnSetting(view: View?) {
        if (!checkClick.isClickEvent()) return
        try {
            val myIntent = Intent(this, Activity_Setting::class.java)
            startActivityForResult(myIntent, PrinterHelper.ACTIVITY_IMAGE_FILE)
            startActivityFromChild(this, myIntent, 0)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, (java.lang.StringBuilder("Activity_Main --> onClickClose ")).append(e.message).toString())
        }
    }

    fun onClickDo(view: View) {
        if (!checkClick.isClickEvent()) return

        if (!PrinterHelper.IsOpened()) {
            (getText(R.string.activity_main_tips)).showToast()
            return
        }
        val paper = _publicFunction!!.ReadSharedPreferencesData(PAPERTYPE)
        if ("" != paper) {
            _paperType = paper
        }
        if (view.id == R.id.btnOpenCashDrawer) {
            selectPaperSize()
        }
        if (view.id == R.id.btnGetStatus) {
            val myIntent = Intent(this, Activity_Status::class.java)
            startActivityFromChild(this, myIntent, 0)
        } else if (view.id == R.id.btnSampleReceipt) {
            PrintSampleReceipt()
        } else if (view.id == R.id.btn1DBarcodes) {
            val myIntent = Intent(this, Activity_1DBarcodes::class.java)
            startActivityFromChild(this, myIntent, 0)
        } else if (view.id == R.id.btnTextFormat) {
            val myIntent = Intent(this, Activity_TextFormat::class.java)
            startActivityFromChild(this, myIntent, 0)
        } else if (view.id == R.id.btnQRCode) {
            val myIntent = Intent(this, Activity_QRCode::class.java)
            startActivityFromChild(this, myIntent, 0)
        } else if (view.id == R.id.btnPrintTestPage) {
            setTestPage()
        } else if (view.id == R.id.btnExpress) {
            setExpress()
        } else if (view.id == R.id.btnReverseFeed) {
            ReverseFeed()
        } else if (view.id == R.id.btn_background) {
            startActivity(Intent(this, Activity_TextBackground::class.java))
        } else if (view.id == R.id.btn_printSN) {
            PrintSN()
        } else if (view.id == R.id.btn_font_text) {
            fontPrint()
        } else if (view.id == R.id.btn_get_electricity) {
            ("" + PrinterHelper.getElectricity()).showToast()
        } else if (view.id == R.id.btn_set_bluetooth_name) {
            setBluetoothName()
        } else if (view.id == R.id.btnPrintSelf) {
            PrinterHelper.printSelf()
        } else if (view.id == R.id.btn_double_color_print) {
            doublePrint()
        } else if (view.id == R.id.btn_voltage) {
            ("voltage:" + PrinterHelper.getVoltage()).showToast()
        } else if (view.id == R.id.btn_printer_version) {
            ("version:" + PrinterHelper.getPrinterVersion()).showToast()
        }
    }
}