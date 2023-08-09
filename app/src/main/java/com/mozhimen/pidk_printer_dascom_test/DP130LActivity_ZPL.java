/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */

package com.mozhimen.pidk_printer_dascom_test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.dascom.print.BroadcastReceiver.NfcStatus;
import com.dascom.print.PrintCommands.ZPL;
import com.dascom.print.Transmission.BluetoothPipe;
import com.dascom.print.Transmission.NfcPipe;
import com.dascom.print.Transmission.Pipe;
import com.dascom.print.Transmission.WifiPipe;
import com.dascom.print.Utils.HalfTone;
import com.dascom.print.Utils.NfcUtils;
import com.mozhimen.pidk_printer_dascom.utils.DialogUtils;

import java.io.IOException;
import java.util.Map;

import static com.dascom.print.Utils.Unit.DPI_203.CM;
import static com.dascom.print.Utils.Unit.DPI_203.MM;
import static com.mozhimen.pidk_printer_dascom_test.Static.Dascom;

public class DP130LActivity_ZPL extends BaseActivity {

    private ImageView iv_NFC_Status;
    private Switch sw_NFC_fast_connect, sw_NFC;
    private NfcUtils nfcUtils;
    private ZPL smartPrint;
    private NfcStatus nfcStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zpl_activity_dp130l);
        initDa();
    }


    private void initDa() {
        nfcUtils = NfcUtils.getInstance(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (nfcUtils != null) {
            Tag tag = nfcUtils.getConnectNfcTag(intent);
            if (pipe instanceof NfcPipe && tag != null) {
                ((NfcPipe) pipe).init(tag);
                Toast("nfc就绪");
                //一碰即连开关
                if (!sw_NFC_fast_connect.isChecked()) {
                    openDialog("正在上传，请勿移动！");
                    new Thread(() -> {
                        if (((NfcPipe) pipe).upload()) {
                            closeDialog();
                            Toast("上传成功");
                        } else {
                            closeDialog();
                            Toast("上传失败，请靠近nfc继续上传", Toast.LENGTH_LONG);
                        }
                    }).start();
                } else {
                    Map<String, Map<String, String>> map = NfcUtils.parseNfcRead((NfcPipe) pipe);
                    if (map != null) {
                        Map<String, String> bt = map.get("BT");
                        Map<String, String> wifi = map.get("WIFI");

                        if (bt != null && wifi != null) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setTitle("一碰即连：")
                                    .setMessage("设备可以抬离打印机，请选择连接要连接的方式")
                                    .setCancelable(false);
                            dialog.setPositiveButton("蓝牙", (dia, which) -> {
                                connectBT(bt);
                            });
                            dialog.setNegativeButton("Wifi", (dia, which) -> {
                                connectWifi(wifi);
                            });
                            dialog.show();
                        } else if (bt != null) {
                            connectBT(bt);
                        } else if (wifi != null) {
                            connectWifi(wifi);
                        }
                    }
                }
            }
        }
    }


    private void connectWifi(Map<String, String> wifi) {
        String SSID = wifi.get("SSID");
        String password = wifi.get("PASSWORD");
        String BSSID = wifi.get("BSSID");//"78:44:fd:80:9d:e3"
        String IP = wifi.get("IP");
        String port = wifi.get("PORT");
        String c = wifi.get("CAPABILITIES");
        WifiInfo info = wifiUtils.getCurrentWifi();
        if (wifiUtils.isConnected()) {
            if (info.getSSID().equals("\"" + SSID + "\"") && info.getBSSID().equalsIgnoreCase(BSSID)) {//要连的wifi 和 已连的wifi
                //匹配就保留连接
                connectWifiPrinter(IP, port);
                return;
            } else {
                //不匹配就断开
                if (!wifiUtils.disconnectWifi()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("出错了！")
                            .setMessage("尝试连接：" + SSID + "失败，请手动断开当前wifi后,再重新尝试！")
                            .setPositiveButton("确定", (dia, which) -> {
                                wifiUtils.jumpToWifiSetting();
                            })
                            .setNegativeButton("取消", (dia, which) -> {

                            })
                            .setCancelable(false)
                            .show();
                    return;
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (wifiUtils.connectWifi(SSID, password, BSSID, c)) {
                Toast("正在尝试连接：" + SSID);
                wifiUtils.networkRequest(wifiInfo -> {
                    connectWifiPrinter(IP, port);
                    wifiUtils.cancelNetworkCallback();
                });
            } else {
                Toast("尝试连接wifi失败");
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            wifiUtils.connectWifi(SSID, password, BSSID);
//            wifiUtils.networkRequest(wifiInfo -> {
//                connectWifiPrinter(IP, port);
//                    wifiUtils.cancelNetworkCallback();
//            });
            Toast("暂不支持安卓10");
        } else {
            Toast("设备安卓版本低于5.0，无法使用该功能");
        }

    }

    private void connectWifiPrinter(String IP, String port) {
        new Thread(() -> {

            runOnUiThread(() -> etIP.setText(IP));
            try {
                if (pipe != null) {
                    pipe.close();
                }
                pipe = new WifiPipe(IP, Integer.parseInt(port));
                Toast("连接成功");
                Pipe(pipe);
            } catch (IOException e) {
                Toast("连接失败");
                e.printStackTrace();
            }

        }).start();
    }

    private void connectBT(Map<String, String> bt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            btMac = bt.get("MAC");
            String pin = bt.get("PIN");
            String name = bt.get("NAME");
            tv_btName.setText(name);
            Toast("正在尝试连接：" + name);
            new Thread(() -> {
                try {
                    if (pipe != null) {
                        pipe.close();
                    }
                    pipe = new BluetoothPipe(this, btMac, pin);
                    Pipe(pipe);
                    Toast("连接成功");
                } catch (IOException e) {
                    Toast("连接失败");
                    e.printStackTrace();
                }
            }).start();
        } else {
            Toast("设备安卓版本低于4.4，无法使用该功能");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置处理优于其他NFC的处理
        if (nfcUtils != null)
            nfcUtils.enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消当前的前台活动优先于所有其他活动
        if (nfcUtils != null)
            nfcUtils.disableForegroundDispatch();
    }

    @Override
    public void Pipe(Pipe pipe) {
        smartPrint = new ZPL(pipe);
    }

    @Override
    public void initView() {
        iv_NFC_Status = findViewById(R.id.iv_nfc_status);
        sw_NFC_fast_connect = findViewById(R.id.sw_nfc_fast_connect);
        sw_NFC = findViewById(R.id.sw_nfc);
    }

    @Override
    public void initData() {
        etIP.setText("192.168.0.7");
        tv_tip.setText("DP-130L_ZPL");
        sw_NFC_fast_connect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (sw_NFC.isChecked()) {
                    buttonView.setChecked(true);
                } else {
                    buttonView.setChecked(false);
                    Toast("nfc未启用");
                }
            } else {
                buttonView.setChecked(false);
            }
        });
        sw_NFC.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (nfcUtils == null) {
                iv_NFC_Status.setImageResource(R.drawable.ic_error_red_24dp);
                buttonView.setChecked(false);
                Toast("该设备不支持NFC功能");
                buttonView.setChecked(false);
            } else {
                if (isChecked) {
                    if (!nfcUtils.isEnabled()) {
                        Toast("请打开NFC功能");
                        buttonView.setChecked(false);
                        return;
                    } else if (nfcUtils.isEnabled()) {
                        iv_NFC_Status.setImageResource(R.drawable.ic_check_circle_green_24dp);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        nfcStatus = new NfcStatus(this, status -> {
                            switch (status) {
                                case 3:
                                    iv_NFC_Status.setImageResource(R.drawable.ic_check_circle_green_24dp);
                                    break;
                                case 1:
                                    iv_NFC_Status.setImageResource(R.drawable.ic_error_red_24dp);
                                    break;
                            }
                        });
                    }
                    pipe = new NfcPipe();
                    Pipe(pipe);
                    new DialogUtils(DP130LActivity_ZPL.this).show(R.string.nfc_operation_title_tips, R.string.nfc_operation_message_tips);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (nfcStatus != null) {
                            nfcStatus.unregisterReceiver();
                        }
                    }
                    iv_NFC_Status.setImageResource(R.drawable.ic_error_red_24dp);
                    pipe = null;
                }
            }
        });
    }

    @Override
    protected void printBitmap(Bitmap bitmap) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {
            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(bitmap.getWidth());
            smartPrint.setLabelLength(bitmap.getHeight());
            //打印图片
            smartPrint.printBitmap(0, 0, bitmap);
            boolean b = smartPrint.setLabelEnd();
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void text(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {
            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (1.5 * CM));
            smartPrint.printTextTTF(0, 0, 72, 72, "打印字体：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength(5 * MM);
            smartPrint.printTextTTF(0, 0, 24, 24, "TTF字体：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();//标签开始
            smartPrint.setLabelWidth(75 * MM);//标签宽度
            smartPrint.setLabelLength((int) (6.5 * CM));//标签长度
            smartPrint.printTextTTF(0, 0, 24, 24, "得实集团1988年成立于香港，是一家以香港为总部的");
            smartPrint.printTextTTF(0, 4 * MM, 48, 48, "高科技企业集团。经过三十");
            smartPrint.printTextTTF(0, 11 * MM, 72, 72, "年的努力，得实集");
            smartPrint.printTextTTF(0, 21 * MM, 96, 96, "团已发展成为");
            smartPrint.printTextTTF(0, 34 * MM, 72, 72, "涵盖计算机硬件、");
            smartPrint.printTextTTF(0, 44 * MM, 48, 48, "个人健康服务、LED照明等");
            smartPrint.printTextTTF(0, 51 * MM, 24, 24, "业务领域的全球性公司，倾力打造“百年老店”是得实");
            smartPrint.printTextTTF(0, 55 * MM, 10, 10, "集团一贯秉持的目标。");
            boolean b = smartPrint.setLabelEnd();//标签结束，然后开始打印
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {
//

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (1.5 * CM));
            smartPrint.printTextTTF(0, 0, 72, 72, "打印一维码：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            smartPrint.printTextTTF(0, 0, 36, 36, "高度10毫米:");
            smartPrint.printCode128(0, 5 * MM, CM, false, false, "abc123");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            smartPrint.printTextTTF(0, 0, 36, 36, "显示下注释:");
            smartPrint.printCode128(0, 5 * MM, CM, true, false, "abc123");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            smartPrint.printTextTTF(0, 0, 36, 36, "显示上注释:");
            smartPrint.printCode128(0, 10 * MM, CM, true, true, "abc123");
            smartPrint.setLabelEnd();


            boolean b = smartPrint.setLabelEnd();

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));

        }).start();
    }


    public void codeQR(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength(CM);
            smartPrint.printTextTTF(0, 0, 72, 72, "打印二维码：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printTextTTF(0, 0, 36, 36, "大小：3，纠错级别:L");
            smartPrint.printQRCode(0, 5 * MM, 3, 'L', Dascom);
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (3 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printTextTTF(0, 0, 36, 36, "大小：5，纠错级别:Q");
            smartPrint.printQRCode(0, 5 * MM, 5, 'Q', Dascom);
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (4 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printTextTTF(0, 0, 36, 36, "大小：7，纠错级别:M");
            smartPrint.printQRCode(0, 5 * MM, 7, 'M', Dascom);
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (5.5 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printTextTTF(0, 0, 36, 36, "大小：10，纠错级别:H");
            smartPrint.printQRCode(0, 5 * MM, 10, 'H', Dascom);
            boolean b = smartPrint.setLabelEnd();

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));

        }).start();
    }


    public void openNFC(View view) {
        if (nfcUtils != null) {
            nfcUtils.openNFC();
        } else {
            Toast("该设备不支持NFC功能");
        }
    }

    public void nfcUpload(View view) {
        if (pipe instanceof NfcPipe) {
            Toast(((NfcPipe) pipe).upload() ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }
    }

    public void disconnect(View view) {
        if (pipe != null) {
            pipe.close();
            pipe = null;
            sw_NFC.setChecked(false);
            Toast("已断开连接");
        }
    }


}
