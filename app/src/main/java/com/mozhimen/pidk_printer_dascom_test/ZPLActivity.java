package com.mozhimen.pidk_printer_dascom_test;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.dascom.print.PrintCommands.ESCPOS;
import com.dascom.print.PrintCommands.ZPL;
import com.dascom.print.PrintCommands.status.PrinterStatus;
import com.dascom.print.PrinterInformation.DP230;
import com.dascom.print.Transmission.BluetoothPipe;
import com.dascom.print.Transmission.Pipe;
import com.dascom.print.Transmission.WifiPipe;
import com.dascom.print.Utils.LogUtils;
import com.dascom.print.Utils.Unit.DPI_300;

import static com.dascom.print.Utils.Unit.DPI_203.CM;
import static com.dascom.print.Utils.Unit.DPI_203.MM;
import static com.mozhimen.pidk_printer_dascom_test.Static.Dascom;

/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */


public class ZPLActivity extends BaseActivity {
    ZPL smartPrint = null;
    String TAG = "ZPLActivity";
    protected TextView tv_bt_status;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zpl);
        tv_bt_status = findView(R.id.tv_bt_status);

    }


    @Override
    public void initData() {
        etIP.setText("192.168.0.7");
        tv_tip.setText("ZPL");
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new ZPL(pipe);
    }

    @Override
    public void getRMtatus(View view) {
        super.getRMtatus(view);
        if (this.usbDevice == null) {
            Toast("请用USB连接打印机.");

        } else {
            byte[] data = smartPrint.getPrinterStatus();
            if (data != null) {
                boolean res1 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_RUN_OUT);
                boolean res2 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_JAM_ERROR);
                boolean res3 = smartPrint.printerStatus(data, ESCPOS.STATE_DEVICE_HOMING_ERROR);
                boolean res4 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_HAVE);
                boolean res5 = smartPrint.printerStatus(data, ESCPOS.STATE_ONLINE);
                boolean res6 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_LACK);
                boolean res7 = smartPrint.printerStatus(data, ESCPOS.STATE_CUTTER_ERROR);
                boolean res8 = smartPrint.printerStatus(data, ESCPOS.STATE_DEVICE_BUSY);
                String formatStr = String.format(getResources().getString(R.string.paper_will_runout) + "%b/" + getResources().getString(R.string.paper_error) + ":%b/" + getResources().getString(R.string.home_error) + "%b/" + getResources().getString(R.string.have_paper)
                        + "%b/" + getResources().getString(R.string.online) + "%b/" + getResources().getString(R.string.lack_paper) + "%b/" +
                        getResources().getString(R.string.cutter_error) + "%b/" + getResources().getString(R.string.devices_busy) + "%b", res1, res2, res3, res4, res5, res6, res7, res8);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_bt_status.setText(formatStr);
                    }
                });
            }
        }

    }

    @Override
    public void initView() {
    }

    public void text(View view) {
//        DSLogManager.d("ZPL text");
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(40 * DPI_300.MM);
            smartPrint.setLabelLength(CM);
            smartPrint.printText(0, 0, 2, 2, "打印字体：");
            smartPrint.setLabelEnd();


            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(40 * MM);
            smartPrint.setLabelLength(CM);
            smartPrint.printText(10 * DPI_300.MM, 10 * DPI_300.MM, 2, 2, "打印字体：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength(CM);
            smartPrint.printText(0, 0, 1, 1, "FNT字体：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();//标签开始
            smartPrint.setLabelWidth(75 * MM);//标签宽度
            smartPrint.setLabelLength((int) (6.5 * CM));//标签长度
            //打印文本
            smartPrint.printText(5 * MM, 0, 1, 1, "得实集团1988年成立于香港，是一家以香港为总部的kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            //文本高宽放大两倍
            smartPrint.printText(0, 4 * MM, 2, 2, "高科技企业集团。经过三十");
            //文本高宽放大三倍
            smartPrint.printText(0, 11 * MM, 3, 3, "年的努力，得实集");
            //文本高宽放大四倍
            smartPrint.printText(0, 21 * MM, 4, 4, "团已发展成为");
            smartPrint.printText(0, 34 * MM, 3, 3, "涵盖计算机硬件、");
            smartPrint.printText(0, 44 * MM, 2, 2, "个人健康服务、LED照明等");
            smartPrint.printText(0, 51 * MM, 1, 1, "业务领域的全球性公司，倾力打造“百年老店”是得实");
            smartPrint.printText(0, 55 * MM, 1, 1, "集团一贯秉持的目标。");
            boolean b = smartPrint.setLabelEnd();//标签结束，然后开始打印

            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();

    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength(CM);
            smartPrint.printText(0, 0, 2, 2, "打印一维码：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            //smartPrint.printText(0, 0, 1, 1, "高度10毫米:");
            //打印一维码，高度10毫米
            smartPrint.printCode128(0, 0, 80, 1, 1, false, false, "abc123");
            smartPrint.setLabelEnd();


            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            smartPrint.printText(0, 0, 1, 1, "显示下注释:");
            //打印一维码，高度10毫米
            smartPrint.printCode128(0, 5 * MM, 10 * MM, 1, 1, true, false, "abc123");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            smartPrint.printText(0, 0, 1, 1, "显示上注释:");
            smartPrint.printCode128(0, 10 * MM, 10 * MM, 2, 2, true, true, "abc123");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            smartPrint.printText(0, 0, 1, 1, "居中显示上注释:");
            smartPrint.printCode128(0, 8 * MM, 10 * MM, 0, 0, true, true, "abc123");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2.5 * CM));
            smartPrint.printText(0, 0, 1, 1, "居中显示下注释:");
            smartPrint.printCode128(0, 5 * MM, 10 * MM, 0, 0, true, false, "abc123");
            boolean b = smartPrint.setLabelEnd();

            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));

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
            smartPrint.printText(0, 0, 2, 2, "打印二维码：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (2 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printText(0, 0, 1, 1, "大小：3，纠错级别:L");
            smartPrint.printQRCode(0, 5 * MM, 3, 'L', Dascom);
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (3 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printText(0, 0, 1, 1, "大小：5，纠错级别:Q");
            smartPrint.printQRCode(1, 5 * MM, 5, 'Q', Dascom);
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (4 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printText(0, 0, 1, 1, "大小：7，纠错级别:M");
            smartPrint.printQRCode(2, 5 * MM, 7, 'M', Dascom);
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setLabelWidth(75 * MM);
            smartPrint.setLabelLength((int) (5.5 * CM));
            //打印二维码,大小3，纠错级别 L
            smartPrint.printText(0, 0, 1, 1, "大小：10，纠错级别:H");
            smartPrint.printQRCode(0, 5 * MM, 10, 'H', Dascom);
            boolean b = smartPrint.setLabelEnd();

            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();
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
            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));

        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.mozhimen.pidk_printer_dascom.R.menu.activity_menu, menu);
        menu.add(1, 100, 1, "DP-230获取状态");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == com.mozhimen.pidk_printer_dascom.R.id.DP_130L_menu) {
            startActivity(DP130LActivity_ZPL.class);
        } else if (id == 100) {
            new Thread(() -> {
                DP230 dp230 = null;
                if (pipe instanceof BluetoothPipe) {
                    dp230 = new DP230((BluetoothPipe) pipe);
                } else if (pipe instanceof WifiPipe) {
                    dp230 = new DP230((WifiPipe) pipe);
                }
                int status = dp230 != null ? dp230.getPrinterStatus() : -1;
                switch (status) {
                    case 1:
                        Toast("打印机状态: 空闲");
                        break;
                    case 2:
                        Toast("打印机状态: 缺纸");
                        break;
                    case 3:
                        Toast("打印机状态: 开盖");
                        break;
                    case 4:
                        Toast("打印机状态: 正在打印");
                        break;
                    case -1:
                        Toast("获取打印机状态失败");
                        break;
                }
            }).start();
        }
        return super.onOptionsItemSelected(item);
    }


    public void printOWord(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {

            smartPrint.setLabelStart();
            smartPrint.setDirection(1);
            smartPrint.printText(0, 0, 2, 2, "得实计算机Dascom");
            smartPrint.printText(0, 0, 2, 2, "得实计算机Dascom");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setDirection(2);
            smartPrint.printText(0, 0, 2, 2, "得实计算机Dascom");
            smartPrint.printText(0, 0, 2, 2, "得实计算机Dascom");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart();
            smartPrint.setDirection(3);
            smartPrint.printText(0, 0, 2, 2, "得实计算机Dascom");
            smartPrint.printText(0, 0, 2, 2, "得实计算机Dascom");
            smartPrint.setDirection(0);
            boolean b = smartPrint.setLabelEnd();
            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();

    }

    public void getBTStatus(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //先获取状态返回的字节数组
                byte[] devBtStatus = smartPrint.getDevBtStatus();
                if (devBtStatus != null) {
                    //*********状态第一个字节
                    //卡纸出错
                    Boolean res1 = smartPrint.praseStatus(devBtStatus[0], PrinterStatus.STATE_ONE_PAPER_JAM_ERROR);
                    //缺纸
                    Boolean res2 = smartPrint.praseStatus(devBtStatus[0], PrinterStatus.STATE_ONE_PAPER_LACK);
                    //设备忙
                    Boolean res3 = smartPrint.praseStatus(devBtStatus[0], PrinterStatus.STATE_ONE_DEVICE_BUSY);
                    //************状态第二个字节
                    //缓冲非空
                    Boolean res4 = smartPrint.praseStatus(devBtStatus[1], PrinterStatus.STATE_TWO_CACHE_NO_EMPTY);
                    //**************状态第三个字节
                    //打印头抬起
                    Boolean res5 = smartPrint.praseStatus(devBtStatus[2], PrinterStatus.STATE_THERE_PRINTER_UP);
                    //纸张类型  true->标签纸 false->连续纸
                    Boolean res6 = smartPrint.praseStatus(devBtStatus[2], PrinterStatus.STATE_THERE_PAPER_TYPE);
                    String res6Str = "";
                    if (res6 == true) {
                        res6Str = getResources().getString(R.string.tag_paper);
                    } else {
                        res6Str = getResources().getString(R.string.continuous_paper);
                    }

                    //电源复位
                    Boolean res8 = smartPrint.praseStatus(devBtStatus[2], PrinterStatus.STATE_THERE_POWER_RESET);
                    //打印头过热
                    Boolean res9 = smartPrint.praseStatus(devBtStatus[2], PrinterStatus.STATE_THERE_PRINTER_HOT);
                    String allStr = getResources().getString(R.string.paper_error) + res1 + "/" + getResources().getString(R.string.lack_paper) + res2 + "/" + getResources().getString(R.string.devices_busy) + res3 +
                            "/" + getResources().getString(R.string.buffer_not_empty) + res4 + "/" + getResources().getString(R.string.print_head_lifting) + res5 + " /" + getResources().getString(R.string.paper_type) + res6Str +
                            "/" + getResources().getString(R.string.power_reset) + res8 + "/" + getResources().getString(R.string.printer_head_overheating) + res9;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_bt_status.setText(allStr);
                        }
                    });
                }
            }
        }).start();


    }


    public void fieldBlock(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {


            smartPrint.setLabelStart();//标签开始
            smartPrint.setLabelWidth(75 * MM);//标签宽度
            smartPrint.setLabelLength((int) (6.5 * CM));//标签长度

            smartPrint.setFieldBlock(7 * CM, 5, 4 * MM, 0);
            //打印文本
            smartPrint.printText(5 * MM, 0, 1, 1, "得实集团1988年成立于香港，是一家以香港为总部的测试");
//            //文本高宽放大两倍
//            smartPrint.printText(0, 4 * MM, 2, 2, "高科技企业集团。经过三十");
//            //文本高宽放大三倍
//            smartPrint.printText(0, 11 * MM, 3, 3, "年的努力，得实集");
//            //文本高宽放大四倍
//            smartPrint.printText(0, 21 * MM, 4, 4, "团已发展成为");
//            smartPrint.printText(0, 34 * MM, 3, 3, "涵盖计算机硬件、");
//            smartPrint.printText(0, 44 * MM, 2, 2, "个人健康服务、LED照明等");
//            smartPrint.printText(0, 51 * MM, 1, 1, "业务领域的全球性公司，倾力打造“百年老店”是得实");
//            smartPrint.printText(0, 55 * MM, 1, 1, "集团一贯秉持的目标。");
            boolean b = smartPrint.setLabelEnd();//标签结束，然后开始打印

            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();
    }
}
