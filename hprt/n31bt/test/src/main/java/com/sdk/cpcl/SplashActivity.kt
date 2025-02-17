package com.sdk.cpcl

import android.os.Bundle
import android.view.Window
import com.mozhimen.kotlin.utilk.android.content.startActivityAndFinish
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDB
import com.sdk.cpcl.databinding.ActivityLogoBinding

/**
 * @ClassName SplashActivity
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/2/17
 * @Version 1.0
 */
class SplashActivity : BaseActivityVDB<ActivityLogoBinding>() {
    override fun initFlag() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun initView(savedInstanceState: Bundle?) {
        startActivityAndFinish<Activity_Main>()
    }
}