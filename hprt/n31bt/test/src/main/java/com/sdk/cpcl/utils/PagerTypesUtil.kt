package com.sdk.cpcl.utils

import android.annotation.SuppressLint
import com.sdk.cpcl.R
import com.sdk.cpcl.cons.CParams
import cpcl.PrinterHelper
import cpcl.PublicFunction

/**
 * @ClassName PagerTypesUtil
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/2/19
 * @Version 1.0
 */
object PagerTypesUtil {
    @JvmStatic
    fun writeFourInchForPaperType(which: Int, publicFunction: PublicFunction) {
        try {
            when (which) {
                CParams.PAPERTYPE_FOURINCH_RECEIPT -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_Receipt)
                CParams.PAPERTYPE_FOURINCH_LABEL -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_Label)
                CParams.PAPERTYPE_FOURINCH_TWO_BM -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_TWO_BM)
                CParams.PAPERTYPE_FOURINCH_THREE_BM -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_THREE_BM)
                CParams.PAPERTYPE_FOURINCH_FOUR_BM -> PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_FOUR_BM)
                else -> {}
            }
            publicFunction.WriteSharedPreferencesData(CParams.PAPERTYPE, which.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    fun writeThreeInchForPaperType(which: Int, publicFunction: PublicFunction) {
        try {
            when (which) {
                CParams.PAPERTYPE_THREEINCH_RECEIPT -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RECEIPT)
                CParams.PAPERTYPE_THREEINCH_LABEL -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LABEL)
                CParams.PAPERTYPE_THREEINCH_LEFT_TOP_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_TOP_BM)
                CParams.PAPERTYPE_THREEINCH_LEFT_BEL_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_BEL_BM)
                CParams.PAPERTYPE_THREEINCH_RIGHT_TOP_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_TOP_BM)
                CParams.PAPERTYPE_THREEINCH_RIGHT_BEL_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_BEL_BM)
                CParams.PAPERTYPE_THREEINCH_CENTRAL_TOP_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_TOP_BM)
                CParams.PAPERTYPE_THREEINCH_CENTRAL_BEL_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_BEL_BM)
                CParams.PAPERTYPE_THREEINCH_2INCH_LEFT_TOP_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_TOP_BM)
                CParams.PAPERTYPE_THREEINCH_2INCH_LEFT_BEL_BM -> PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_BEL_BM)
                else -> {}
            }
            publicFunction.WriteSharedPreferencesData(CParams.PAPERTYPE, which.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}