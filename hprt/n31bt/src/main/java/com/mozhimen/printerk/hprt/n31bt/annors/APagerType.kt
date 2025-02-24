package com.mozhimen.printerk.hprt.n31bt.annors

import androidx.annotation.IntDef
import com.mozhimen.kotlin.utilk.wrapper.UtilKRes

/**
 * @ClassName APagerType
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/23 2:17
 * @Version 1.0
 */
fun Int.intPaperTypeCode2strPaperType(pagerSize: Int): String =
    if (pagerSize == APagerSize.PAPERSIZE_THREEINCH)
        UtilKRes.gainStringArray(com.mozhimen.printerk.hprt.n31bt.R.array.papertype_3inch)[this]
    else
        UtilKRes.gainStringArray(com.mozhimen.printerk.hprt.n31bt.R.array.papertype_4inch)[this]

@IntDef(
    APagerType.PAPERTYPE_THREEINCH_RECEIPT,
    APagerType.PAPERTYPE_THREEINCH_LABEL,
    APagerType.PAPERTYPE_THREEINCH_LEFT_TOP_BM,
    APagerType.PAPERTYPE_THREEINCH_LEFT_BEL_BM,
    APagerType.PAPERTYPE_THREEINCH_RIGHT_TOP_BM,
    APagerType.PAPERTYPE_THREEINCH_RIGHT_BEL_BM,
    APagerType.PAPERTYPE_THREEINCH_CENTRAL_TOP_BM,
    APagerType.PAPERTYPE_THREEINCH_CENTRAL_BEL_BM,
    APagerType.PAPERTYPE_THREEINCH_2INCH_LEFT_TOP_BM,
    APagerType.PAPERTYPE_THREEINCH_2INCH_LEFT_BEL_BM,
//    APagerType.PAPERTYPE_FOURINCH_RECEIPT,
//    APagerType.PAPERTYPE_FOURINCH_LABEL,
//    APagerType.PAPERTYPE_FOURINCH_TWO_BM,
//    APagerType.PAPERTYPE_FOURINCH_THREE_BM,
//    APagerType.PAPERTYPE_FOURINCH_FOUR_BM,
)
annotation class APagerType{
    companion object{
        const val PAPERTYPE_THREEINCH_RECEIPT: Int = 0
        const val PAPERTYPE_THREEINCH_LABEL: Int = 1
        const val PAPERTYPE_THREEINCH_LEFT_TOP_BM: Int = 2
        const val PAPERTYPE_THREEINCH_LEFT_BEL_BM: Int = 3
        const val PAPERTYPE_THREEINCH_RIGHT_TOP_BM: Int = 4
        const val PAPERTYPE_THREEINCH_RIGHT_BEL_BM: Int = 5
        const val PAPERTYPE_THREEINCH_CENTRAL_TOP_BM: Int = 6
        const val PAPERTYPE_THREEINCH_CENTRAL_BEL_BM: Int = 7
        const val PAPERTYPE_THREEINCH_2INCH_LEFT_TOP_BM: Int = 8
        const val PAPERTYPE_THREEINCH_2INCH_LEFT_BEL_BM: Int = 9

        const val PAPERTYPE_FOURINCH_RECEIPT: Int = 0
        const val PAPERTYPE_FOURINCH_LABEL: Int = 1
        const val PAPERTYPE_FOURINCH_TWO_BM: Int = 2
        const val PAPERTYPE_FOURINCH_THREE_BM: Int = 3
        const val PAPERTYPE_FOURINCH_FOUR_BM: Int = 4
    }
}
