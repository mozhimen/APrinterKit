package com.mozhimen.printerk.hprt.helpers

import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.kotlin.utilk.kotlin.applyTry
import com.mozhimen.kotlin.utilk.kotlin.ifNullOrEmpty
import cpcl.PublicFunction

/**
 * @ClassName FuncProvider
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/20 22:06
 * @Version 1.0
 */
class FuncProvider : BaseUtilK() {
    private val _publicFunction: PublicFunction by lazy { PublicFunction(_context) }

    fun getPublicFunction(): PublicFunction =
        _publicFunction

    /////////////////////////////////////////////////////////////////

    fun getSpString(key: String, default: String): String =
        { getPublicFunction().ReadSharedPreferencesData(key).ifNullOrEmpty(default) }.applyTry(default)

    fun putSpString(key: String, value: String) {
        { getPublicFunction().WriteSharedPreferencesData(key, value) }.applyTry()
    }
}