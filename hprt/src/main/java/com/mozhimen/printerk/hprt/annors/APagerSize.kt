package com.mozhimen.printerk.hprt.annors

import androidx.annotation.IntDef
import com.mozhimen.kotlin.utilk.wrapper.UtilKRes

/**
 * @ClassName APagerSize
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/23 2:16
 * @Version 1.0
 */
fun Int.intPaperSizeCode2strPaperSize(): String =
    UtilKRes.gainStringArray(com.mozhimen.printerk.hprt.n31bt.R.array.papersize)[this]

@IntDef(APagerSize.PAPERSIZE_THREEINCH, APagerSize.PAPERSIZE_FOURINCH)
annotation class APagerSize {
    companion object {
        const val PAPERSIZE_THREEINCH: Int = 0
        const val PAPERSIZE_FOURINCH: Int = 1
    }
}
