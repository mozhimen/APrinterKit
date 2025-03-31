package com.mozhimen.printerk.hprt.annors

import androidx.annotation.IntDef
import com.mozhimen.kotlin.utilk.wrapper.UtilKRes

/**
 * @ClassName AStatus
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/23 14:37
 * @Version 1.0
 */

fun Int.intStatusCode2strStatus(): String =
    when (this) {
        AStatus.STATUS_READY -> UtilKRes.gainString(com.mozhimen.printerk.hprt.R.string.status_ready)
        AStatus.STATUS_NOPAPER -> UtilKRes.gainString(com.mozhimen.printerk.hprt.R.string.status_nopage)
        AStatus.STATUS_OPEN -> UtilKRes.gainString(com.mozhimen.printerk.hprt.R.string.status_nopage)
        else -> UtilKRes.gainString(com.mozhimen.printerk.hprt.R.string.status_error)
    }

@IntDef(
    AStatus.STATUS_READY,
    AStatus.STATUS_NOPAPER,
    AStatus.STATUS_OPEN,
    AStatus.STATUS_ERROR
)
annotation class AStatus {
    companion object {
        const val STATUS_READY = 0
        const val STATUS_NOPAPER = 2
        const val STATUS_OPEN = 4
        const val STATUS_ERROR = -1
    }
}