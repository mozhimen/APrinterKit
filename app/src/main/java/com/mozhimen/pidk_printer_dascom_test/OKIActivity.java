package com.mozhimen.pidk_printer_dascom_test;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.dascom.print.PrintCommands.OKI;
import com.dascom.print.Transmission.Pipe;
import com.dascom.print.Utils.CanvasUtils;
import com.dascom.print.Utils.encoding.EncodingHandler;
import com.google.zxing.WriterException;

public class OKIActivity extends BaseActivity {
    OKI smartPrint = null;

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
        tv_tip.setText("OKI");
        etIP.setText("192.168.0.1");
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new OKI(pipe);
    }

    @Override
    protected void printBitmap(Bitmap bitmap) {
        new Thread(()->{
            smartPrint.printBitmap(bitmap,0,0);
        }).start();
    }


    public void text(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            //清空缓存
            smartPrint.clearBuffer();
            //初始打印机
            smartPrint.initPrinter();
            smartPrint.printText("默认模式：");
            smartPrint.lineFeed();
            smartPrint.printText(" 得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            //前走纸
            smartPrint.paperAdvanceLine(2);
            smartPrint.lineFeed();

            smartPrint.printText("高密打印:");
            smartPrint.lineFeed();
            smartPrint.setHighDensityPrint();
            smartPrint.printText(" 得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.paperAdvanceInch(0.5);

            smartPrint.initPrinter();
            smartPrint.printText("高速打印:");
            smartPrint.lineFeed();
            smartPrint.setHighSpeedPrint();
            smartPrint.printText(" 得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.paperAdvanceLine(2);
            smartPrint.initPrinter();

            smartPrint.printText("下划线:");
            smartPrint.lineFeed();
            smartPrint.setUnderLine(true);
            smartPrint.printText(" 得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.paperAdvanceLine(2);
            smartPrint.initPrinter();

            smartPrint.printText("倍宽倍高:");
            smartPrint.lineFeed();
            smartPrint.setDoubleWidth(true);
            smartPrint.setDoubleHigh(true);
            smartPrint.printText(" 得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.lineFeed();
            //换页
            smartPrint.formFeed();
            //初始化打印机
            smartPrint.initPrinter();
        }).start();
    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            Bitmap bitmap = EncodingHandler.createCode128(360, 180, "abc123");
            smartPrint.printBitmap(CanvasUtils.BackgroundWhite(bitmap), .0, .0);
        }).start();
    }

    public void codeQR(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            Bitmap bitmap = null;
            try {
                bitmap = EncodingHandler.createQRCode(Static.Dascom, 360);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            smartPrint.printBitmap(CanvasUtils.BackgroundWhite(bitmap), .0, .0);
        }).start();
    }
}
