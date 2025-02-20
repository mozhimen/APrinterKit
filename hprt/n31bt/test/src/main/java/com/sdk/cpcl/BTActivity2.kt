package com.sdk.cpcl

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mozhimen.bluetoothk.BluetoothK
import com.mozhimen.bluetoothk.BluetoothKScanProxy
import com.mozhimen.bluetoothk.commons.IBluetoothKScanListener
import com.mozhimen.kotlin.elemk.android.app.cons.CActivity
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.bluetooth.isBondState_BOND_BONDED
import com.mozhimen.kotlin.utilk.android.widget.showToast
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDB
import com.sdk.cpcl.databinding.ActivityBtBinding

/**
 * @ClassName BTActivity2
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/19 0:19
 * @Version 1.0
 */
class BTActivity2 : BaseActivityVDB<ActivityBtBinding>() {
    private var bluetoothDevices: MutableList<BluetoothDevice> = ArrayList()
    private var baseQuickAdapter: BaseQuickAdapter<BluetoothDevice, BaseViewHolder> = object : BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(android.R.layout.simple_list_item_2, bluetoothDevices) {
        @SuppressLint("MissingPermission")
        override fun convert(helper: BaseViewHolder, item: BluetoothDevice?) {
            if (item != null) {
                if (item.name != null) helper.setText(android.R.id.text1, if (item.name.isEmpty()) "Null" else item.name)
                helper.setText(android.R.id.text2, item.address)
            }
        }
    }
//    private var bluetooth: Bluetooth = Bluetooth.getBluetooth(this)
    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    private val bluetoothKScanProxy by lazy { BluetoothKScanProxy() }
    private var progressDialog: ProgressDialog? = null

    override fun initData(savedInstanceState: Bundle?) {
        BluetoothK.instance.requestBluetoothPermission(this) {
            super.initData(savedInstanceState)
        }
    }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    @SuppressLint("MissingPermission")
    override fun initView(savedInstanceState: Bundle?) {
        if (BluetoothK.instance.getBluetoothAdapter() == null) {
            "没有找到蓝牙适配器".showToast()
            return
        }

        vdb.recyHistory.setLayoutManager(LinearLayoutManager(this))
        vdb.recyHistory.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        vdb.recyHistory.setAdapter(baseQuickAdapter)
        initBT()
        baseQuickAdapter.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            Bluetooth.setOnBondState(bluetoothDevices.get(position)) {
                if (progressDialog != null && progressDialog!!.isShowing()) progressDialog!!.dismiss()
                val intent = Intent()
                intent.putExtra("SelectedBDAddress", bluetoothDevices[position].address)
                setResult(CActivity.RESULT_OK, intent)
                finish()
            }
            if (bluetoothDevices.get(position).isBondState_BOND_BONDED()) {
                val intent = Intent()
                intent.putExtra("SelectedBDAddress", bluetoothDevices[position].address)
                setResult(CActivity.RESULT_OK, intent)
                finish()
            } else {
                progressDialog = ProgressDialog(this@BTActivity2)
                progressDialog!!.setMessage(getString(R.string.activity_devicelist_connect))
                progressDialog!!.show()
                Thread { bluetoothKScanProxy.startBound(bluetoothDevices[position]) }.start()
            }
        })
        vdb.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        vdb.swipeRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            initBT()
            if (vdb.swipeRefresh.isRefreshing) vdb.swipeRefresh.isRefreshing = false
        })
    }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    override fun initObserver() {
        bluetoothKScanProxy.apply {
            setBluetoothKScanListener(object :IBluetoothKScanListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onFound(bluetoothDevice: BluetoothDevice) {
                    for (printBT in bluetoothDevices) {
                        if (bluetoothDevice.address == printBT.address) {
                            return
                        }
                    }

                    //XiangYinBao_X3,ATOL1
                    bluetoothDevices.add(bluetoothDevice)
                    baseQuickAdapter.notifyDataSetChanged()
                }
            })
            bindLifecycle(this@BTActivity2)
        }
    }

    @OptIn(OApiInit_ByLazy::class, OApiCall_BindLifecycle::class, OApiCall_BindViewLifecycle::class)
    @SuppressLint("NotifyDataSetChanged")
    private fun initBT() {
        bluetoothDevices.clear()
        baseQuickAdapter.notifyDataSetChanged()
        bluetoothKScanProxy.startScan(this)
    }
}