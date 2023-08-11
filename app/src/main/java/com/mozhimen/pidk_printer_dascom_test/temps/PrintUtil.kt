package com.mozhimen.pidk_printer_dascom_test.temps

import com.dascom.print.PrintCommands.ZPL
import com.dascom.print.Utils.Unit.DPI_203
import com.dascom.print.Utils.Unit.DPI_300
import com.dascom.print.Utils.Unit.DPI_203.CM


/**
 * @ClassName PrintDemo
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/8/10 17:17
 * @Version 1.0
 */
object PrintUtil {
    @JvmStatic
    fun printDemo(smartPrint: ZPL) {
        smartPrint.printMyText("""
^XA
^LH0,50
^XFR:STOREFMT.ZPL^FS
^FN5^FD2022-07-14 16:47:16^FS
^FN6^FD粤A11011B^FS
^FN7^FD[黄绿双拼色]^FS
^FN10^FD金山工业区大道收费站^FS
^FN13^FDxkcwMDAxEjRWeJASNFAgIBEGFCkAAb6pQTEyMzQ1AAAAAAABAAEAAEcwMDAxMSNFZ4BfpPt5nJgdzEkRwHUC7vAqSmwOehEKEAAAACgV^FS
^XZ
        """.trimIndent())
    }
}