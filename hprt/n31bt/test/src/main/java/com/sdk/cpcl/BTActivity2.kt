package com.sdk.cpcl

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
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
    private var tag = 0
    var myBluetoothAdapter: BluetoothAdapter? = null
    private var list: MutableList<BluetoothDevice>? = null
    private var baseQuickAdapter: BaseQuickAdapter<BluetoothDevice, BaseViewHolder>? = null
    private var bluetooth: Bluetooth? = null
    private var progressDialog: ProgressDialog? = null

    override fun initView(savedInstanceState: Bundle?) {
        tag = intent.getIntExtra("TAG", RESULT_CANCELED)
        ListBluetoothDevice()
    }

    @SuppressLint("MissingPermission")
    fun ListBluetoothDevice() {
        if ((BluetoothAdapter.getDefaultAdapter().also { myBluetoothAdapter = it }) == null) {
            Toast.makeText(this, "没有找到蓝牙适配器", Toast.LENGTH_LONG).show()
            return
        }

        if (!myBluetoothAdapter!!.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 2)
        }
        list = ArrayList<BluetoothDevice>()
        baseQuickAdapter = object : BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(android.R.layout.simple_list_item_2, list) {
            override fun convert(helper: BaseViewHolder, item: BluetoothDevice?) {
                if (item != null) {
                    if (item.name != null) helper.setText(android.R.id.text1, if (item.name.isEmpty()) "Null" else item.name)
                    helper.setText(android.R.id.text2, item.address)
                }
            }
        }
        vdb.recyHistory.setLayoutManager(LinearLayoutManager(this))
        vdb.recyHistory.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        vdb.recyHistory.setAdapter(baseQuickAdapter)
        bluetooth = Bluetooth.getBluetooth(this)
        initBT()
        baseQuickAdapter!!.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            Bluetooth.setOnBondState(list!!.get(position)) {
                if (progressDialog != null && progressDialog!!.isShowing()) progressDialog!!.dismiss()
                val intent = Intent()
                intent.putExtra("SelectedBDAddress", list!!.get(position).getAddress())
                setResult(tag, intent)
                finish()
            }
            if (list!!.get(position).getBondState() == BluetoothDevice.BOND_BONDED) {
                val intent = Intent()
                intent.putExtra("SelectedBDAddress", list!!.get(position).getAddress())
                setResult(tag, intent)
                finish()
            } else {
//                    Method method = null;
//                    try {
//                        method = BluetoothDevice.class.getMethod("createBond");
//                        Log.d("Print", "开始配对");
//                        method.invoke(list.get(position));
//                    } catch (Exception e) {
//                    }
                progressDialog = ProgressDialog(this@BTActivity2)
                progressDialog!!.setMessage(getString(R.string.activity_devicelist_connect))
                progressDialog!!.show()
                Thread { list!!.get(position).createBond() }.start()
            }
        })
        vdb.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        vdb.swipeRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            initBT()
            if (vdb.swipeRefresh.isRefreshing()) vdb.swipeRefresh.setRefreshing(false)
        })
    }

    private fun initBT() {
        Log.d("TAG", "initBT:")
        list?.clear()
        baseQuickAdapter!!.notifyDataSetChanged()
        bluetooth!!.doDiscovery()
        bluetooth!!.getData(Bluetooth.toData { bluetoothDevice ->
            for (printBT in list!!) {
                if (bluetoothDevice.address == printBT.address) {
                    return@toData
                }
            }
            //XiangYinBao_X3,ATOL1
            list?.add(bluetoothDevice)
            baseQuickAdapter!!.notifyDataSetChanged()
        })
    }
}