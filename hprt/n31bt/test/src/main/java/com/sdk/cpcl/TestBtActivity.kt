package com.sdk.cpcl

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.mozhimen.bluetoothk.BluetoothK
import com.mozhimen.bluetoothk.BluetoothKStateProxy
import com.mozhimen.bluetoothk.commons.IBluetoothKStateListener
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.content.startActivityForResult
import com.mozhimen.kotlin.utilk.android.content.startContext
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.android.widget.showToast
import com.mozhimen.kotlin.utilk.kotlin.ifNotEmpty
import com.mozhimen.kotlin.utilk.wrapper.gainStringArray
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDB
import com.sdk.cpcl.cons.CParams
import com.sdk.cpcl.databinding.ActivityTestBtBinding
import com.sdk.cpcl.utils.PagerTypesUtil
import cpcl.PrinterHelper
import cpcl.PublicFunction
import cpcl.listener.DisConnectBTListener

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/2/17
 * @Version 1.0
 */
class TestBtActivity : BaseActivityVDB<ActivityTestBtBinding>() {
    private var _paperType: String = "0"

    companion object {
        private const val LABEL: String = "1"
    }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    private val _bluetoothKStateProxy by lazy { BluetoothKStateProxy() }
    private val _publicFunction: PublicFunction by lazy { PublicFunction(this) }

    @SuppressLint("SetTextI18n")
    private val _disConnectBTListener = object : DisConnectBTListener {
        override fun disConnect() {
            runOnUiThread {
                vdb.txtTips.setText("BT Disconnect")
                ("BT Disconnect").showToast()
            }
        }
    }

    //////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    @SuppressLint("HandlerLeak", "SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        setTitle(BuildConfig.VERSION_NAME)
        try {
            _bluetoothKStateProxy.apply {
                setBluetoothKEventListener(object : IBluetoothKStateListener {
                    override fun onDisconnect() {
                        try {
                            PrinterHelper.portClose()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        vdb.txtTips.setText(R.string.activity_main_tips)
                    }

                    override fun onOff() {
                        if (PrinterHelper.IsOpened()) {
                            try {
                                PrinterHelper.portClose()
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            vdb.txtTips.setText(R.string.activity_main_tips)
                            getString(R.string.activity_main_close).showToast()
                        }
                    }
                })
                bindLifecycle(this@TestBtActivity)
            }
            PrinterHelper.setDisConnectBTListener(_disConnectBTListener)

            _publicFunction.ReadSharedPreferencesData(CParams.PAPERTYPE).ifNotEmpty {
                _paperType = it
            }
            val paperTypes = gainStringArray(R.array.activity_main_papertype_3inch)
            if (LABEL == _paperType) {
                vdb.containerEscFunction.btnOpenCashDrawer.setText(getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + paperTypes[1])
            } else {
                vdb.containerEscFunction.btnOpenCashDrawer.setText(getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + paperTypes[0])
            }
        } catch (e: Exception) {
            UtilKLogWrapper.e(TAG, " --> onCreate ${e.message}")
        }
    }

    override fun onDestroy() {
        try {
            PrinterHelper.setIsListenerBT(false)
            PrinterHelper.portClose()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data == null) return
            when (resultCode) {
                Activity.RESULT_CANCELED -> {
                    connectBT(data.getStringExtra("SelectedBDAddress"))
                }

                PrinterHelper.ACTIVITY_PRNFILE -> {
                    val strPRNFile = data.extras!!.getString("FilePath")
                    PrinterHelper.PrintBinaryFile(strPRNFile)
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //////////////////////////////////////////////////////////////////////

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




    @SuppressLint("SetTextI18n")
    private fun showSelectPaperTypesDialog(paperSize: Int) {
        val paperTypes = gainStringArray(if (paperSize == CParams.PAPERSIZE_THREEINCH) R.array.activity_main_papertype_3inch else R.array.activity_main_papertype_4inch)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.activity_esc_function_btnopencashdrawer))
            .setItems(paperTypes) { _, which ->
                if (paperSize == CParams.PAPERSIZE_THREEINCH) {
                    PagerTypesUtil.writeThreeInchForPaperType(which, _publicFunction)
                    vdb.containerEscFunction.btnOpenCashDrawer.setText(getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + paperTypes[which])
                } else {
                    PagerTypesUtil.writeFourInchForPaperType(which, _publicFunction)
                    vdb.containerEscFunction.btnOpenCashDrawer.setText(getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + paperTypes[which])
                }
            }
        builder.create().show()
    }


    ////////////////////////////////////////////////

    @SuppressLint("NewApi")
    fun onClickConnect(view: View) {
        if (!checkClick.isClickEvent())
            return
        try {
            if (view.id == R.id.btnBT) {
                PrinterHelper.portClose()
                //获取蓝牙动态权限
                BluetoothK.requestBluetoothPermission(this) {
                    startActivityForResult<BTActivity2>(0) {
                        putExtra("TAG", 0)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            UtilKLogWrapper.e(TAG, "--> onClickConnect ${e.message}")
        }
    }

    fun onClickClose(view: View) {
        if (!checkClick.isClickEvent())
            return
        try {
            PrinterHelper.portClose()
            vdb.txtTips.setText(R.string.activity_main_tips)
            return
        } catch (e: java.lang.Exception) {
            UtilKLogWrapper.e(TAG, "--> onClickClose ${e.message}")
        }
    }

    fun onClickSetting(view: View) {
        if (!checkClick.isClickEvent())
            return
        try {
            val myIntent = Intent(this, Activity_Setting::class.java)
            startActivityForResult(myIntent, PrinterHelper.ACTIVITY_IMAGE_FILE)
            startActivityFromChild(this, myIntent, 0)
        } catch (e: java.lang.Exception) {
            UtilKLogWrapper.e(TAG, "--> onClickSetting ${e.message}")
        }
    }

    fun onClickDo(view: View) {
        if (!checkClick.isClickEvent())
            return
        if (!PrinterHelper.IsOpened()) {
            (getText(R.string.activity_main_tips)).showToast()
            return
        }
        _publicFunction.ReadSharedPreferencesData(CParams.PAPERTYPE).ifNotEmpty {
            _paperType = it
        }
        if (view.id == R.id.btnOpenCashDrawer) {
            selectPaperSize()
        } else if (view.id == R.id.btn_font_text) {
            selectFont()
        } else if (view.id == R.id.btnGetStatus) {
            startContext<Activity_Status>()
        } else if (view.id == R.id.btnSampleReceipt) {
            printPageSampleReceipt()
        } else if (view.id == R.id.btn1DBarcodes) {
            startContext<Activity_1DBarcodes>()
        } else if (view.id == R.id.btnTextFormat) {
            startContext<Activity_TextFormat>()
        } else if (view.id == R.id.btnQRCode) {
            startContext<Activity_QRCode>()
        } else if (view.id == R.id.btnPrintTestPage) {
            printPageTest()
        } else if (view.id == R.id.btnReverseFeed) {
            printReverseFeed()
        } else if (view.id == R.id.btn_background) {
            startContext<Activity_TextBackground>()
        } else if (view.id == R.id.btn_printSN) {
            showPrintSN()
        } else if (view.id == R.id.btn_set_bluetooth_name) {
            setBluetoothName()
        } else if (view.id == R.id.btnPrintSelf) {
            PrinterHelper.printSelf()
        } else if (view.id == R.id.btn_double_color_print) {
            printColorDouble()
        } else if (view.id == R.id.btn_get_electricity) {
            ("electricity:" + PrinterHelper.getElectricity()).showToast()
        } else if (view.id == R.id.btn_voltage) {
            ("voltage:" + PrinterHelper.getVoltage()).showToast()
        } else if (view.id == R.id.btn_printer_version) {
            ("version:" + PrinterHelper.getPrinterVersion()).showToast()
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private fun selectPaperSize() {
        val papertype = gainStringArray(R.array.activity_main_paper_size)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.activity_esc_function_print_type))
            .setItems(papertype) { dialogInterface, i ->
                when (i) {
                    CParams.PAPERSIZE_THREEINCH -> showSelectPaperTypesDialog(CParams.PAPERSIZE_THREEINCH)
                    CParams.PAPERSIZE_FOURINCH -> showSelectPaperTypesDialog(CParams.PAPERSIZE_FOURINCH)
                    else -> {}
                }
            }.show()
    }


    private fun printPageSampleReceipt() {
        try {
            val ReceiptLines = gainStringArray(R.array.activity_main_sample_2inch_receipt)
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
            UtilKLogWrapper.e(TAG, "--> PrintSampleReceipt ${e.message}")
        }
    }

    private fun printPageTest() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "1400", "1")
            PrinterHelper.Box("50", "5", "450", "400", "1")
            PrinterHelper.Align(PrinterHelper.CENTER)
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "50", "5", getString(R.string.activity_test_page))
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
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "830", getString(R.string.activity_esc_function_btnqrcode))
            PrinterHelper.PrintQR(PrinterHelper.BARCODE, "0", "870", "4", "6", "ABC123")
            PrinterHelper.PrintQR(PrinterHelper.BARCODE, "150", "870", "4", "6", "ABC123")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "1000", getString(R.string.activity_test_line))
            PrinterHelper.Line("0", "1030", "400", "1030", "1")
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "1050", getString(R.string.activity_test_box))
            PrinterHelper.Box("10", "1080", "400", "1300", "1")
            if (LABEL == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
            UtilKLogWrapper.e(TAG, (e.message.toString()))
        }
    }

    private fun printReverseFeed() {
        try {
            PrinterHelper.ReverseFeed(50)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 选择字库打印文本
     */
    private fun selectFont() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "500", "1")
            PrinterHelper.printTextPro(PrinterHelper.TEXT, "SIMSUN.TTF", 24, 24, 0, 0, "SIMSUN.TTF 24 Test")
            PrinterHelper.printTextPro(PrinterHelper.TEXT, "TT0003M_.TTF", 48, 48, 0, 50, "TT0003M_.TTF 48 Test")
            if (LABEL == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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

    private fun printColorDouble() {
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
            if (LABEL == _paperType) {
                PrinterHelper.Form()
            }
            PrinterHelper.Print()
        } catch (e: java.lang.Exception) {
            UtilKLogWrapper.e(TAG, (e.message.toString()))
        }
    }

    private fun showPrintSN() {
        try {
            val printSN = PrinterHelper.getPrintSN()
            if (TextUtils.isEmpty(printSN)) {
                (getString(R.string.activity_main_data_error)).showToast()
                return
            }
            (printSN).showToast()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}