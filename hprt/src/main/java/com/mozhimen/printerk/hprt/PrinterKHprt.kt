package com.mozhimen.printerk.hprt

import androidx.lifecycle.LiveData
import com.mozhimen.kotlin.elemk.commons.I_Listener
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.printerk.hprt.annors.APagerSize
import com.mozhimen.printerk.hprt.annors.APagerType
import com.mozhimen.printerk.hprt.annors.AStatus
import com.mozhimen.printerk.hprt.cons.CParams
import com.mozhimen.printerk.hprt.helpers.FuncProvider
import com.mozhimen.printerk.hprt.helpers.PrintProvider
import com.mozhimen.basick.impls.MutableLiveDataStrict
import com.mozhimen.kotlin.elemk.commons.IA_Listener
import cpcl.PrinterHelper
import cpcl.listener.DisConnectBTListener

/**
 * @ClassName PrinterKHprt
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/20 21:28
 * @Version 1.0
 */
object PrinterKHprt : BaseUtilK() {
    private val _printProvider by lazy { PrintProvider() }
    private val _funcProvider by lazy { FuncProvider() }
    private val _liveSwitch = MutableLiveDataStrict(false)

    fun getPrintProvider(): PrintProvider =
        _printProvider

    fun getFuncProvider(): FuncProvider =
        _funcProvider

    fun getLiveSwitch(): LiveData<Boolean> =
        _liveSwitch

    /////////////////////////////////////////////////////////////////

    fun isOpened(): Boolean =
        getPrintProvider().isOpened().also { _liveSwitch.postValue(it) }

    fun safeOpen(address: String?): Boolean =
        if (!address.isNullOrEmpty()) {
            val result = getPrintProvider().portOpenBT(_context, address)
            UtilKLogWrapper.d(TAG, "safeOpen: $result")
            result
        } else {
            UtilKLogWrapper.w("safeOpen: address is empty")
            false
        }.also { _liveSwitch.postValue(it) }

    @JvmStatic
    fun safeClose() {
        if (getPrintProvider().isOpened()) {
            _liveSwitch.postValue(false)
            getPrintProvider().portClose()
        }
    }

    fun safePrint(onOk: IA_Listener<PrintProvider>) {
        if (getPrintProvider().isOpened()) {
            onOk.invoke(getPrintProvider())
        }
    }

    fun safePrint(onOk: IA_Listener<PrintProvider>, onError: I_Listener) {
        if (getPrintProvider().isOpened()) {
            onOk.invoke(getPrintProvider())
        } else {
            onError.invoke()
        }
    }

    @JvmStatic
    fun startListen(listener: DisConnectBTListener) {
        getPrintProvider().setDisConnectBTListener(listener)
    }

    @JvmStatic
    fun stopListen() {
        getPrintProvider().setIsListenerBT(false)
    }

    /////////////////////////////////////////////////////////////////

    @JvmStatic
    fun setPagerSize(which: Int) {
        getFuncProvider().putSpString(CParams.PAPERSIZE, which.toString())
    }

    @JvmStatic
    fun getPagerSize(): Int =
        getFuncProvider().getSpString(CParams.PAPERSIZE, APagerSize.PAPERSIZE_THREEINCH.toString()).toInt()

    @JvmStatic
    fun getPagerType(): Int =
        getFuncProvider().getSpString(CParams.PAPERTYPE, APagerType.PAPERTYPE_THREEINCH_LABEL.toString()).toInt()

    @JvmStatic
    fun setPagerType_fourInch(which: Int) {
        when (which) {
            APagerType.PAPERTYPE_FOURINCH_RECEIPT -> getPrintProvider().setPaperFourInch(PrinterHelper.Paper_FourInch_Receipt)
            APagerType.PAPERTYPE_FOURINCH_LABEL -> getPrintProvider().setPaperFourInch(PrinterHelper.Paper_FourInch_Label)
            APagerType.PAPERTYPE_FOURINCH_TWO_BM -> getPrintProvider().setPaperFourInch(PrinterHelper.Paper_FourInch_TWO_BM)
            APagerType.PAPERTYPE_FOURINCH_THREE_BM -> getPrintProvider().setPaperFourInch(PrinterHelper.Paper_FourInch_THREE_BM)
            APagerType.PAPERTYPE_FOURINCH_FOUR_BM -> getPrintProvider().setPaperFourInch(PrinterHelper.Paper_FourInch_FOUR_BM)
            else -> {}
        }
        getFuncProvider().putSpString(CParams.PAPERTYPE, which.toString())
    }

    @JvmStatic
    fun setPagerType_threeInch(which: Int) {
        when (which) {
            APagerType.PAPERTYPE_THREEINCH_RECEIPT -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RECEIPT)
            APagerType.PAPERTYPE_THREEINCH_LABEL -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LABEL)
            APagerType.PAPERTYPE_THREEINCH_LEFT_TOP_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_TOP_BM)
            APagerType.PAPERTYPE_THREEINCH_LEFT_BEL_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_BEL_BM)
            APagerType.PAPERTYPE_THREEINCH_RIGHT_TOP_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_TOP_BM)
            APagerType.PAPERTYPE_THREEINCH_RIGHT_BEL_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_BEL_BM)
            APagerType.PAPERTYPE_THREEINCH_CENTRAL_TOP_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_TOP_BM)
            APagerType.PAPERTYPE_THREEINCH_CENTRAL_BEL_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_BEL_BM)
            APagerType.PAPERTYPE_THREEINCH_2INCH_LEFT_TOP_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_TOP_BM)
            APagerType.PAPERTYPE_THREEINCH_2INCH_LEFT_BEL_BM -> getPrintProvider().papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_BEL_BM)
            else -> {}
        }
        getFuncProvider().putSpString(CParams.PAPERTYPE, which.toString())
    }

    @JvmStatic
    fun getStatus(): Int {
        val status = getPrintProvider().getPrinterStatus()
        if (status == AStatus.STATUS_READY) {
            return AStatus.STATUS_READY
        } else if ((status and AStatus.STATUS_NOPAPER) == AStatus.STATUS_NOPAPER) {
            return AStatus.STATUS_NOPAPER
        } else if ((status and AStatus.STATUS_OPEN) == AStatus.STATUS_OPEN) {
            return AStatus.STATUS_OPEN
        }
        return AStatus.STATUS_ERROR
    }
}