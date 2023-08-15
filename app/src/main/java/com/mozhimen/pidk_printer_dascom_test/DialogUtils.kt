package com.mozhimen.pidk_printer_dascom_test

import android.content.Context
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog

/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */


class DialogUtils constructor(val context: Context){

    fun show(message:String){
        MaterialDialog(context).show {
            message(text=message)
            cornerRadius(16f)
            cancelOnTouchOutside(false)
            positiveButton(text = "确定"){
            }
        }
    }

    fun show(title:String,message:String){
        MaterialDialog(context).show {
            title(text=title)
            message(text=message)
            cornerRadius(16f)
            cancelOnTouchOutside(false)
            positiveButton(text = "确定"){
            }
        }
    }

    fun show(@StringRes res:Int, @StringRes message: Int){
        MaterialDialog(context).show {
            title(res)
            message(message){
                html {  }
            }
            cornerRadius(16f)
            cancelOnTouchOutside(false)
            positiveButton(text = "确定"){
            }
        }
    }


}