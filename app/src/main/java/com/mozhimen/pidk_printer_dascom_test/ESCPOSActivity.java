package com.mozhimen.pidk_printer_dascom_test;

import static com.dascom.print.Utils.Unit.DPI_203.MM;
import static com.mozhimen.pidk_printer_dascom_test.Static.Dascom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dascom.print.PrintCommands.ESCPOS;
import com.dascom.print.Transmission.Pipe;

public class ESCPOSActivity extends BaseActivity {
    ESCPOS smartPrint = null;
    byte byte_0 = '0';
    byte byte_1 = '1';
    byte byte_2 = '2';
    byte byte_3 = '3';

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escpos);
    }

    @Override
    public void getRMtatus(View view) {
        super.getRMtatus(view);
        if (this.usbDevice == null) {
            Toast("请用USB连接打印机.");
        } else {
            byte[] data = smartPrint.getPrinterStatus();
            boolean res1 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_RUN_OUT);
            boolean res2 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_JAM_ERROR);
            boolean res3 = smartPrint.printerStatus(data, ESCPOS.STATE_DEVICE_HOMING_ERROR);
            boolean res4 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_HAVE);
            boolean res5 = smartPrint.printerStatus(data, ESCPOS.STATE_ONLINE);
            boolean res6 = smartPrint.printerStatus(data, ESCPOS.STATE_PAPER_LACK);
            boolean res7 = smartPrint.printerStatus(data, ESCPOS.STATE_CUTTER_ERROR);
            boolean res8 = smartPrint.printerStatus(data, ESCPOS.STATE_DEVICE_BUSY);

            Toast(getResources().getString(R.string.paper_will_runout) + res1 + "/" + getResources().getString(R.string.paper_error) + res2 + "/" + getResources().getString(R.string.home_error) + res3 + "/" + getResources().getString(R.string.have_paper) + res4 + "/" + getResources().getString(R.string.online) + res5 + "/" + getResources().getString(R.string.lack_paper) +
                    res6 + "/" + getResources().getString(R.string.cutter_error) + res7 + "/" + getResources().getString(R.string.devices_busy) + res8);
        }
    }

    public static String printDataLog(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        String[] str = new String[src.length];
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                str[i] = 0 + "";
            }
            str[i] = hv;
        }

        StringBuffer s = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            if (str[i].length() == 1) {
                s.append("0" + str[i] + " ");
            } else {
                s.append(str[i] + " ");
            }

        }


        return s.toString();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        tv_tip.setText("ESCPOS");
        etIP.setText("192.168.0.7");
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new ESCPOS(pipe);
    }


    public void text(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {
            smartPrint.setJustification((byte) 2);
            //            width height 范围[0,7]字符宽的倍数,0表示1倍,1表示2倍,以此类推.
            smartPrint.setCharacterSize(0, 0);
            smartPrint.printText("普通模式：");
            //打印缓冲器内容并换行.
            smartPrint.printLineFeed();
            //打印文本
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.printLineFeed();
            //走纸
            smartPrint.printFeed(5 * MM);

            smartPrint.printText("粗体/强调模式：");
            smartPrint.printLineFeed();
            //开启粗体/强调模式.
            smartPrint.setEmphasizedMode(true);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.printLineFeed();
            smartPrint.setEmphasizedMode(false);
            smartPrint.printFeed(5 * MM);

            smartPrint.printText("字体放大一倍：");
            smartPrint.printLineFeed();
            smartPrint.setCharacterSize(1, 1);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.printLineFeed();
            smartPrint.setCharacterSize(0, 0);
            smartPrint.printFeed(5 * MM);

            smartPrint.printText("下划线模式：");
            smartPrint.printLineFeed();
            smartPrint.setUnderlineMode(byte_1);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.setUnderlineMode(byte_0);
            smartPrint.printLineFeed();
            boolean b = smartPrint.printFeed(5 * MM);
            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();
    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {
            smartPrint.printText("一维码默认设置：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(50, 3, byte_0, byte_0);
            //打印一维码
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();
            smartPrint.printCode39("abc123");
            smartPrint.printLineFeed();

            smartPrint.printBarCodeSetting(50, 4, byte_0, byte_0);
            smartPrint.printText("一维码间隙增大1：");
            smartPrint.printLineFeed();
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();
            smartPrint.printCode39("abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("一维码高度100：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_0, byte_0);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();
            smartPrint.printCode39("abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_1, byte_0);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();
            smartPrint.printCode39("abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_2, byte_1);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();
            smartPrint.printCode39("abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_3, byte_2);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();
            smartPrint.printCode39("abc123");
            boolean b = smartPrint.printLineFeed();
            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();
    }

    public void codeQR(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {
            smartPrint.printText("二维码大小：3，纠错级别：L");
            smartPrint.printLineFeed();
            //二维码默认设置
            smartPrint.printQRCodeSetting((byte) 2, 3, byte_0);
            //打印二维码
            smartPrint.printQRCode(Dascom);
            smartPrint.printLineFeed();

            smartPrint.printText("二维码大小：8，纠错级别：M");
            smartPrint.printLineFeed();
            smartPrint.printQRCodeSetting((byte) 2, 8, byte_1);
            smartPrint.printQRCode(Dascom);
            smartPrint.printLineFeed();

            smartPrint.printText("二维码大小：12，纠错级别：Q");
            smartPrint.printLineFeed();
            smartPrint.printQRCodeSetting((byte) 2, 12, byte_2);
            smartPrint.printQRCode(Dascom);
            smartPrint.printLineFeed();

            smartPrint.printText("二维码大小：16，纠错级别：H");
            smartPrint.printLineFeed();
            smartPrint.printQRCodeSetting((byte) 2, 16, byte_3);
            smartPrint.printQRCode(Dascom);
            boolean b = smartPrint.printLineFeed();
            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();
    }

    @Override
    protected void printBitmap(Bitmap bitmap) {
        String btName = tv_btName.getText().toString();
        new Thread(() -> {
            boolean b;
            if (btName.startsWith("DP-330L")) {
                b = smartPrint.printBitmap_DP330L((bitmap), 0, 0);
            } else {
                b = smartPrint.printBitmap((bitmap), 0, 0);
            }
            b = smartPrint.feedPrintPosition();
            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.mozhimen.pidk_printer_dascom.R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == com.mozhimen.pidk_printer_dascom.R.id.DP_130L_menu) {
            startActivity(new Intent(getApplicationContext(), DP130LActivity_Pos.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
