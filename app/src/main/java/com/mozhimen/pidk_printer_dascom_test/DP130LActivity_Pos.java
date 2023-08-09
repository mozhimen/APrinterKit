package com.mozhimen.pidk_printer_dascom_test;

import android.graphics.Bitmap;
import android.view.View;

import com.dascom.print.PrintCommands.ESCPOS;
import com.dascom.print.Transmission.Pipe;
import com.dascom.print.Utils.Unit.DPI_203;

public class DP130LActivity_Pos extends DP130LActivity_ZPL{
    ESCPOS smartPrint = null;

    @Override
    public void initData() {
        super.initData();
        tv_tip.setText("DP-130L_Pos");
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
        new Thread(()->{
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
            smartPrint.printFeed(5 * DPI_203.MM);

            smartPrint.printText("粗体/强调模式：");
            smartPrint.printLineFeed();
            //开启粗体/强调模式.
            smartPrint.setEmphasizedMode(true);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.printLineFeed();
            smartPrint.setEmphasizedMode(false);
            smartPrint.printFeed(5 * DPI_203.MM);

            smartPrint.printText("字体放大一倍：");
            smartPrint.printLineFeed();
            smartPrint.setCharacterSize(1, 1);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.printLineFeed();
            smartPrint.setCharacterSize(0, 0);
            smartPrint.printFeed(5 * DPI_203.MM);

            smartPrint.printText("下划线模式：");
            smartPrint.printLineFeed();
            smartPrint.setUnderlineMode((byte) '1');
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.setUnderlineMode((byte) '0');
            smartPrint.printLineFeed();
            boolean b=smartPrint.printFeed(5 * DPI_203.MM);
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();

    }
    byte byte_0='0';
    byte byte_1='1';
    byte byte_2='2';
    byte byte_3='3';
    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }

        new Thread(()->{
            smartPrint.printText("打印A集一维码：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(50, 2,byte_0 , byte_0);
            //打印一维码
            smartPrint.printCode128('A', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printBarCodeSetting(50, 2, byte_0, byte_0);
            smartPrint.printText("一维码间隙增大1：");
            smartPrint.printLineFeed();
            smartPrint.printCode128('A', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("一维码高度100：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 3, byte_0, byte_0);
            smartPrint.printCode128('A', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 3, byte_1, byte_0);
            smartPrint.printCode128('A', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_2, byte_0);
            smartPrint.printCode128('A', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_3, byte_0);
            smartPrint.printCode128('A', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("打印B集一维码：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(50, 2, byte_0, byte_1);
            //打印一维码
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printBarCodeSetting(50, 2, byte_0, byte_1);
            smartPrint.printText("一维码间隙增大1：");
            smartPrint.printLineFeed();
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("一维码高度100：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 3, byte_0, byte_1);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上人工识别符：");
            smartPrint.printLineFeed();
            smartPrint. printBarCodeSetting(100, 3, byte_1, byte_1);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_2, byte_1);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_3, byte_1);
            smartPrint.printCode128('B', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("打印C集一维码：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(50, 2, byte_0, byte_2);
            //打印一维码
            smartPrint.printCode128('C', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printBarCodeSetting(50, 2, byte_0, byte_2);
            smartPrint.printText("一维码间隙增大1：");
            smartPrint.printLineFeed();
            smartPrint.printCode128('C', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("一维码高度100：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 3, byte_0, byte_2);
            smartPrint.printCode128('C', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 3, byte_1, byte_2);
            smartPrint.printCode128('C', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_2, byte_2);
            smartPrint.printCode128('C', "abc123");
            smartPrint.printLineFeed();

            smartPrint.printText("添加上下人工识别符：");
            smartPrint.printLineFeed();
            smartPrint.printBarCodeSetting(100, 4, byte_3, byte_2);
            smartPrint.printCode128('C', "abc123");
            boolean b=smartPrint.printLineFeed();
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start(); ;
    }

    public void codeQR(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.printText("二维码大小：3，纠错级别：L");
            smartPrint.printLineFeed();
            //二维码默认设置
            smartPrint.printQRCodeSetting((byte) 2, 3, byte_0);
            //打印二维码
            smartPrint.printQRCode(Static.Dascom);
            smartPrint.printLineFeed();

            smartPrint.printText("二维码大小：8，纠错级别：M");
            smartPrint.printLineFeed();
            smartPrint.printQRCodeSetting((byte) 2, 8, byte_1);
            smartPrint.printQRCode(Static.Dascom);
            smartPrint.printLineFeed();

            smartPrint.printText("二维码大小：12，纠错级别：Q");
            smartPrint.printLineFeed();
            smartPrint.printQRCodeSetting((byte) 2, 12, byte_2);
            smartPrint.printQRCode(Static.Dascom);
            smartPrint.printLineFeed();

            smartPrint.printText("二维码大小：16，纠错级别：H");
            smartPrint.printLineFeed();
            smartPrint.printQRCodeSetting((byte) 2, 16, byte_3);
            smartPrint.printQRCode(Static.Dascom);
            boolean b=smartPrint.printLineFeed();
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }


    public void printBitmap(Bitmap bitmap) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        String btName = tv_btName.getText().toString();
        new Thread(()->{
            if (btName.startsWith("DP-330L")) {
                smartPrint.printBitmap_DP330L((bitmap), 0, 0);
            } else {
                smartPrint.printBitmap(bitmap, 0, 0);
            }
        }).start();
    }
}
