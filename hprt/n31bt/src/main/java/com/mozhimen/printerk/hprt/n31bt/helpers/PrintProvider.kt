package com.mozhimen.printerk.hprt.n31bt.helpers

import android.content.Context
import androidx.annotation.IntRange
import com.mozhimen.kotlin.utilk.kotlin.applyTry
import com.mozhimen.printerk.hprt.n31bt.annors.AStatus
import cpcl.PrinterHelper
import cpcl.listener.DisConnectBTListener

/**
 * @ClassName PrintProvider
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/20 21:46
 * @Version 1.0
 */
class PrintProvider {

    /**
     * #### 3.42 **获取打印机SN**
     */
    fun getPrintSN(): String =
        { PrinterHelper.getPrintSN() }.applyTry("")

    fun getPrintName(): String =
        { PrinterHelper.getPrintName() }.applyTry("")

    /**
     * 电量
     *
     * - 返回
     *   | 值     | 描述       |
     *   | ------ | ---------- |
     *   | 大于 0 | 电量百分比 |
     *   | -1     | 发送失败   |
     */
    fun getElectricity(): Int =
        { PrinterHelper.getElectricity() }.applyTry(-1)

    /**
     * #### 3.55 **获取电压**
     *
     * - 返回
     *   | 值       | 描述                |
     *   | -------- | ------------------- |
     *   | 不为空   | 电压（格式：x.xxV） |
     *   | 空字符串 | 发送失败            |
     */
    fun getVoltage(): String =
        { PrinterHelper.getVoltage() }.applyTry("")

    //版本号
    fun getPrinterVersion(): String =
        { PrinterHelper.getPrinterVersion() }.applyTry("")

    /**
     * #### 3.32 **获取打印机状态**
     *
     * - 返回
     *
     *   | 值                | 描述       |
     *   | ----------------- | ---------- |
     *   | status == 0       | 打印机正常 |
     *   | status == -1      | 发送失败   |
     *   | (status & 2) == 2 | 缺纸       |
     *   | (status & 4) == 4 | 开盖       |
     */
    fun getPrinterStatus(): Int =
        { PrinterHelper.getPrinterStatus() }.applyTry(AStatus.STATUS_ERROR)

    /**
     * #### 3.45 **读取QRcode版本**
     *
     * - 返回
     *   | 值    | 描述                     |
     *   | ----- | ------------------------ |
     *   | 大于0 | QRcode版本（失败返回空） |
     */
    fun getQRcodeVersion(): String =
        { PrinterHelper.getQRcodeVersion() }.applyTry("")

    /////////////////////////////////////////////////////////////////////

    /**
     * 设置双色打印
     * - 参数
     *   | 参数  | 描述                                |
     *   | :---- | :---------------------------------- |
     *   | layer | 需要打印的颜色， 0：红色，1：黑色。 |
     *
     * - 返回
     *   | 值   | 描述     |
     *   | ---- | -------- |
     *   | 0    | 发送成功 |
     *   | -1   | 发送失败 |
     */
    fun setLayer(layer: Int): Int =
        { PrinterHelper.setLayer(layer) }.applyTry(-1)

    fun setDisConnectBTListener(listener: DisConnectBTListener) {
        { PrinterHelper.setDisConnectBTListener(listener) }.applyTry()
    }

    fun setIsListenerBT(boolean: Boolean) {
        PrinterHelper.setIsListenerBT(boolean)
    }

    fun setPaperFourInch(paperType: Int): Boolean =
        { PrinterHelper.setPaperFourInch(paperType) > 0 }.applyTry(false)

    fun papertype_CPCL_TWO(paperType: Int) {
        { PrinterHelper.papertype_CPCL_TWO(paperType) }.applyTry()
    }

    /**
     * #### 3.51 **设置蓝牙名字**
     *
     * - 描述
     *   修改蓝牙名字，主要需要配合保存接口一起使用
     *
     * - 参数
     *   | 参数 | 描述                                               |
     *   | :--- | :------------------------------------------------- |
     *   | name | 需要修改的蓝牙名称（不能是中文，且长度不能超过32） |
     *
     * - 返回
     *   | 值     | 描述     |
     *   | ------ | -------- |
     *   | 大于 0 | 发送成功 |
     *   | -1     | 发送失败 |
     *   | -2     | 参数错误 |
     * @param name String
     */
    fun setBluetoothName(name: String): Int =
        { PrinterHelper.setBluetoothName(name) }.applyTry(-1)

    /**
     * #### 3.52 **设置保存**
     *
     * - 返回
     *   | 值     | 描述     |
     *   | ------ | -------- |
     *   | 大于 0 | 发送成功 |
     *   | -1     | 发送失败 |
     */
    fun saveParameter(): Int =
        PrinterHelper.saveParameter()

    /////////////////////////////////////////////////////////////////////

    fun isOpened(): Boolean =
        PrinterHelper.IsOpened()

    /**
     * #### 	2.1 **蓝牙连接接口**
     *
     * - 参数
     *   | 参数        | 描述             |
     *   | ----------- | ---------------- |
     *   | context     | 上下文对象       |
     *   | portSetting | 蓝牙地址（大写） |
     *
     * - 返回
     *   | 值   | 描述                            |
     *   | :--- | :------------------------------ |
     *   | 0    | 连接成功                        |
     *   | -1   | 连接超时                        |
     *   | -2   | 蓝牙地址格式错误                |
     *   | -3   | 打印机与SDK不匹配（握手不通过） |
     */
    fun portOpenBT(context: Context, address: String): Boolean =
        { PrinterHelper.portOpenBT(context, address) == 0 }.applyTry(false)

    /**
     * #### 	2.4 **断开连接接口**
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | true  | 断开成功 |
     *   | false | 连接超时 |
     */
    fun portClose() {
        { PrinterHelper.portClose() }.applyTry(false)
    }

    /////////////////////////////////////////////////////////////////////

    /**
     * #### 	3.1 **设置标签高度**
     *
     * - 参数
     *   | 参数       | 描述                                                         |
     *   | :--------- | :----------------------------------------------------------- |
     *   | offset     | 上下文对象                                                   |
     *   | horizontal | 打印机水平方向dpi（根据实际打印机dpi设置）                   |
     *   | vertical   | 打印机垂直方向dpi（根据实际打印机dpi设置）                   |
     *   | height     | 标签高度 （单位：dot）200dpi  8 dot = 1mm，300dpi 12 dot = 1mm |
     *   | qty        | 打印次数                                                     |
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun printAreaSize(offset: String, horizontal: String, vertical: String, height: String, qty: String): Int =
        PrinterHelper.printAreaSize(offset, horizontal, vertical, height, qty)

    fun printAreaSize(offset: String, horizontal: String, vertical: String, height: String): Int =
        PrinterHelper.printAreaSize(offset, horizontal, vertical, height, "1")

    /**
     * #### 3.26 **打印宽度**
     * - 参数
     *   | 参数 | 描述                        |
     *   | :--- | :-------------------------- |
     *   | pw   | 指定页面宽度。（单位：dot）（单位：dot）200dpi  8 dot = 1mm，300dpi 12 dot = 1mm |
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     *
     */
    fun pageWidth(pw: String): Int =
        PrinterHelper.PageWidth(pw)

    /**
     * #### 	3.10 **对齐方式**
     *
     * - 参数
     *   | 参数  | 描述                                                         |
     *   | :---- | :----------------------------------------------------------- |
     *   | align | PrinterHelper.CENTER：居中。<br/>PrinterHelper.LEFT：左对齐。<br/>PrinterHelper.RIGHT：右对齐。 |
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun align(align: String) {
        PrinterHelper.Align(align)
    }

    fun alignStart() {
        align(PrinterHelper.LEFT)
    }

    fun alignCenter() {
        align(PrinterHelper.CENTER)
    }

    fun alignEnd() {
        align(PrinterHelper.RIGHT)
    }

    /**
     * #### 	3.7 **文本打印**
     *
     * - 描述
     *   **PrintTextCPCL**用于中文固件。
     *   **PrintCodepageTextCPCL**用于英文固件。
     *   **Text**两种固件都能用。
     *   **printTextPro**选择字库打印文本。
     *
     * - 参数
     *   | 参数    | 描述                                                         |
     *   | :------ | :----------------------------------------------------------- |
     *   | command | 文字的方向 <br />PrinterHelper.TEXT：水平。<br/>PrinterHelper.TEXT90：逆时针旋转90度。<br/>PrinterHelper.TEXT180：逆时针旋转180度。<br/>PrinterHelper.TEXT270：逆时针旋转270度。 |
     *   | font    | 字体点阵大小：（单位：dot）<br />注意：英文固件只支持（0和1）。<br/>0：12x24。<br/>1：12x24（中文模式下打印繁体），英文模式下字体变成（9x17）大小<br/>2：8x16。<br/>3：20x20。<br/>4：32x32或者16x32，由ID3字体宽高各放大两倍。<br/>7：24x24或者12x24，视中英文而定。<br/>8：24x24或者12x24，视中英文而定。<br/>20：16x16或者8x16，视中英文而定。<br/>24：24x24或者12x24，视中英文而定。<br/>55：16x16或者8x16，视中英文而定。<br/>其它默认24x24或者12x24，视中英文而定。 |
     *   | size    | 字体大小。（该功能被屏蔽统一参数传0）                        |
     *   | x       | 横坐标（单位 dot）                                           |
     *   | y       | 纵坐标（单位 dot）                                           |
     *   | data    | 文本数据                                                     |
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun text(command: String, font: String, size: String, x: String, y: String, data: String): Int =
        PrinterHelper.Text(command, font, size, x, y, data)

    fun text(font: String, x: String, y: String, data: String): Int =
        PrinterHelper.Text(PrinterHelper.TEXT, font, "0", x, y, data)

    /**
     * #### 3.31 **字体加粗**
     *
     * - 参数
     *   | 参数 | 描述                  |
     *   | :--- | :-------------------- |
     *   | bold | 加粗系数（范围：1-5）0 不加粗 |
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun setBold(@IntRange(0, 5) bold: Int): Int =
        PrinterHelper.SetBold(bold.toString())

    fun setBoldFalse(): Int =
        setBold(0)

    /**
     * #### 	3.9 **设置字符宽高放大倍数**
     *
     * - 参数
     *   | 参数   | 描述               |
     *   | :----- | :----------------- |
     *   | width  | 字体宽度的放大倍数 |
     *   | height | 字体高度的放大倍数 |
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun setMag(widthRatio: String, heightRatio: String): Int =
        PrinterHelper.SetMag(widthRatio, heightRatio)

    fun setMag(ratio: String): Int =
        setMag(ratio, ratio)

    fun setMagFalse(): Int =
        setMag("1")

    /**
     * #### 	3.4 **标签定位**
     *
     * - 描述
     *   在Print()之前调用，只在标签模式下起作用
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun form(): Int =
        PrinterHelper.Form()

    /**
     * #### 	3.2 **打印**
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun print(): Int =
        PrinterHelper.Print()

    //打印自检页
    fun printSelf(): Int =
        PrinterHelper.printSelf()

    fun printBinaryFile(strFilePathName: String): Boolean =
        PrinterHelper.PrintBinaryFile(strFilePathName)

    /**
     * 回退
     * - 参数
     *   | 参数 | 描述                                  |
     *   | :--- | :------------------------------------ |
     *   | feed | 回退距离。（单位：行，范围：1-255）。 |
     *
     * - 返回
     *   | 值    | 描述     |
     *   | ----- | -------- |
     *   | 大于0 | 发送成功 |
     *   | -1    | 发送失败 |
     */
    fun reverseFeed(length: Int): Int =
        PrinterHelper.ReverseFeed(length)
}