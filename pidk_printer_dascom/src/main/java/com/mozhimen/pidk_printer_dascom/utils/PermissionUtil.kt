/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */

package com.mozhimen.pidk_printer_dascom.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtil {

    companion object {
        @JvmField
        val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

        @JvmField
        val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

        @JvmField
        val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

        @JvmField
        val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION


        /**
         * 权限请求方法
         */
        @JvmStatic
        fun requestPermissions(context: Context, requestCode: Int, vararg permissions: String) {
            val noPermissions = ArrayList<String>()
            for (permission in permissions) {
                if (!hadPermission(context, permission)) {
                    noPermissions.add(permission)
                }
            }
            if (noPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(context as Activity, noPermissions.toArray(arrayOf()), requestCode)
            }
        }

        /**
         * 权限请求方法
         */
        @JvmStatic
        fun requestPermission(context: Context, requestCode: Int, permission: String) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), requestCode)
        }

        /**
         * 检查权限
         *
         * @return false代表没有该权限，true代表该权限
         */
        @JvmStatic
        fun hadPermission(context: Context, permission: String): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED) {
                return true
            }
            return false
        }

        /**
         * 检查权限
         *
         * @return false代表没有该权限，true代表该权限
         */
        @JvmStatic
        fun hadPermission(context: Context, vararg permissions: String): Boolean {
            for (permission in permissions) {
                if (!hadPermission(context, permission)) {
                    return false
                }
            }
            return true
        }

    }
}