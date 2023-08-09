package com.mozhimen.pidk_printer_dascom_test;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.dascom.print.PrintCommands.ESCPOS_9Pin;
import com.dascom.print.Transmission.Pipe;
import com.dascom.print.Utils.CanvasUtils;
import com.dascom.print.Utils.encoding.EncodingHandler;
import com.google.zxing.WriterException;

public class ESCPOS_9PinActivity extends BaseActivity {
    ESCPOS_9Pin smartPrint = null;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        tv_tip.setText("ESCPOS_9Pin");
        etIP.setText("192.168.0.1");
    }


    public void text(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.printText("普通模式：");
            smartPrint.lineFeed();

            //打印文本
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            //打印缓冲器内容并换行.
            smartPrint.lineFeed();
            //走纸
            smartPrint.advancePosition(0.2);

            //开启粗体/强调模式.
            smartPrint.printText("粗体/强调模式：");
            smartPrint.lineFeed();
            smartPrint.setFontBold(true);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.setFontBold(false);
            smartPrint.lineFeed();
            smartPrint.advancePosition(0.2);

            smartPrint.printText("斜体模式：");
            smartPrint.lineFeed();
            smartPrint.setFontItalic(true);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.setFontItalic(false);
            smartPrint.lineFeed();
            smartPrint.advancePosition(0.2);

            smartPrint.printText("倍宽倍高：");
            smartPrint.lineFeed();
            smartPrint.setMultipleFont(true, true);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.setMultipleFont(false, false);
            boolean b=smartPrint.lineFeed();

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.printText("打印一维码：");
            smartPrint.lineFeed();
            //打印一维码
            smartPrint.printCode128(0.0, 0.0, 2, 1, false, "abc123");
            smartPrint.lineFeed();

            smartPrint.printText("显示注释：");
            smartPrint.carriageReturn();
            //显示注释
            smartPrint.printCode128(0.0, 0.2, 2, 1, true, "abc123");
            smartPrint.lineFeed();

            smartPrint.printText("增加条码宽度：");
            smartPrint.carriageReturn();
            //变宽
            smartPrint.printCode128(0.0, 0.2, 3, 1, true, "abc123");
            smartPrint.lineFeed();

            smartPrint.printText("增加条码长度");
            smartPrint.carriageReturn();
            //变长
            smartPrint.printCode128(0.0, 0.2, 2, 2, true, "abc123");
            boolean b=smartPrint.lineFeed();

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void codeQR(View view) throws WriterException {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.printText("打印二维码：");
            smartPrint.lineFeed();
            Bitmap qrCodeBitmap = null ;
            try {
                qrCodeBitmap = EncodingHandler.createQRCode(Static.Dascom, 200);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            printBitmap(CanvasUtils.BackgroundWhite(qrCodeBitmap));
        }).start();


    }

    @Override
    protected void printBitmap(Bitmap bitmap) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            boolean b=smartPrint.printBitmap(bitmap,0,0);
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new ESCPOS_9Pin(pipe);
    }
}
