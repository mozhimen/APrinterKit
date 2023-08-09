package com.mozhimen.pidk_printer_dascom_test;

import static com.dascom.print.Utils.Unit.DPI_203.CM;
import static com.dascom.print.Utils.Unit.DPI_203.INCH;
import static com.dascom.print.Utils.Unit.DPI_203.MM;
import static com.mozhimen.pidk_printer_dascom_test.Static.Dascom;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.dascom.print.PrintCommands.TSPL;
import com.dascom.print.Transmission.Pipe;
import com.mozhimen.pidk_printer_dascom.utils.Utils;

public class TSPLActivity extends BaseActivity{
    TSPL smartPrint = null;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new TSPL(pipe);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        tv_tip.setText("TSPL");
        etIP.setText("192.168.1.11");
    }


    public void text(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.setLabelStart();// 标签的开始
            smartPrint.setSize(2*CM, 27*CM);//设置打印范围

            smartPrint.printText(0, 0, 0, 2, 2, "aaaa我");
            //打印文本
//            smartPrint.printText(0, 0, 0, 1, 1, "得实集团是一家综合性高科技集团公司。经过三十年的努力，得实集团已经发");
//            //字体大小2
//            smartPrint.printText(0, 4 * MM, 0, 2, 2, "展成为涵盖计算机硬件、个人健康、L");
//            //字体大小3
//            smartPrint.printText(0, 11 * MM, 0, 3, 3, "ED照明等业务领域的全球性公司");
//            //字体大小4
//            smartPrint.printText(0, 20 * MM, 0, 4, 4, "，倾力打造“百年");
//            //字体大小5
//            smartPrint.printText(0, 32 * MM, 0, 5, 5, "老店”是得实");
//            //字体大小6
//            smartPrint.printText(0, 49 * MM, 0, 6, 6, "集团一贯秉");
//            //字体大小7
//            smartPrint.printText(0, 70 * MM, 0, 7, 7, "的目标。");
//
//            //顺时针旋转90度
//            smartPrint.printText(95 * MM, 95 * MM, 90, 2, 2, "得实集团是一家综合性高科技集团公司");
//            //默认
//            smartPrint.printText(50, 97 * MM, 0, 2, 2, "。经过三十年的努力，得实集");
//            //顺时针旋转180度
//            smartPrint.printText(700, 95 * MM + 150, 180, 2, 2, "团已经发展成为涵盖计算机硬");
//            smartPrint.printText(700, 120 * MM, 180, 1, 1, "件、个人健康、LED照明等业务领域的全球性公司，");
//            smartPrint.printText(700, 135 * MM, 180, 1, 1, "倾力打造“百年");
//            //顺时针旋转270度
//            smartPrint.printText(0, 8 * INCH, 270, 2, 2, "老店”是得实集团一贯秉持的目标。");

            boolean b=smartPrint.setLabelEnd();

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.setLabelStart();
            smartPrint.setSize(4 * INCH, 1 * INCH);
            //打印一维码
            smartPrint.printCode128(0, 0, 5 * MM, false, 1, 1, "abc123");
            smartPrint.printCode128(35 * MM, 0, 10 * MM, true, 2, 2, "abc123");
            smartPrint.printCode128(0, 15 * MM, 10 * MM, false, 1, 2, "abc123");
            smartPrint.printCode128(35 * MM, 15 * MM, 10 * MM, true, 2, 1, "abc123");

            boolean b=smartPrint.setLabelEnd();

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void codeQR(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        Double cm25=2.5 * CM;
        new Thread(()->{
            smartPrint.setLabelStart();
            smartPrint.setSize(4 * INCH, 3 * INCH);
            //打印二维码
            smartPrint.printText(0, 0, 0, 1, 1, "二维码大小1，纠错级别：L");
            smartPrint.printQRCode(CM, 5 * MM, "L", 1, Dascom);

            smartPrint.printText(5 * CM, 0, 0, 1, 1, "二维码大小3，纠错级别：M");
            smartPrint.printQRCode(6 * CM, 5 * MM, "M", 3, Dascom);

            smartPrint.printText(0,  2 * CM, 0, 1, 1, "二维码大小5，纠错级别：Q");
            smartPrint.printQRCode(CM,cm25.intValue(), "Q", 5, Dascom);

            smartPrint.printText(5 * CM,  2 * CM, 0, 1, 1, "二维码大小10，纠错级别：H");
            smartPrint.printQRCode(6 * CM, cm25.intValue(), "H", 10, Dascom);
            boolean b=smartPrint.setLabelEnd();

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    @Override
    protected void printBitmap(Bitmap bitmap) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.setLabelStart();
//            bitmap.let { bmp ->
//                    Utils.showBitmapInfo(bmp);
//                setSize(bmp.width, bmp.height);
//                smartPrint.printBitmap(0, 0, bmp);
//            }
            Utils.showBitmapInfo(bitmap);
            smartPrint.setSize(bitmap.getWidth(), bitmap.getHeight());
            smartPrint.printBitmap(0, 0, bitmap);
            smartPrint.setLabelEnd();
        }).start();
    }
}
