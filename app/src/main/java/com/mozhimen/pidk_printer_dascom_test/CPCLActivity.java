package com.mozhimen.pidk_printer_dascom_test;

import static com.dascom.print.Utils.Unit.DPI_203.CM;
import static com.dascom.print.Utils.Unit.DPI_203.MM;
import static com.mozhimen.pidk_printer_dascom_test.Static.*;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.dascom.print.PrintCommands.CPCL;
import com.dascom.print.Transmission.Pipe;
public class CPCLActivity extends BaseActivity {
    CPCL smartPrint = null;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new CPCL(pipe);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        tv_tip.setText("CPCL");
        etIP.setText("192.168.0.1");
    }

    @Override
    protected void printBitmap(Bitmap bitmap) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(() -> {

            smartPrint. setLabelStart(bitmap.getHeight(), 1);
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
        Log.d("","");
        new Thread(()->{


            smartPrint.setLabelStart(CM, 1);
            smartPrint.setUnderline(true);
            smartPrint.printText(0, 0, 0, 4, 0, "打印文本：");
            smartPrint.setUnderline(false);
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 0, 0, 4, 0, "font 4,size 0：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(5 * CM, 1);
            smartPrint.printText(0, 0, 0, 4, 0, "  得实集团是一家综合性高");
            smartPrint.printText(0, 6 * MM, 0, 4, 0, "科技集团公司。经过三十");
            smartPrint.printText(0, 12 * MM, 0, 4, 0, "年的努力，得实集团已发");
            smartPrint.printText(0, 18 * MM, 0, 4, 0, "展成为涵盖计算机硬件、");
            smartPrint.printText(0, 24 * MM, 0, 4, 0, "个人健康服务、LED照明等");
            smartPrint.printText(0, 30 * MM, 0, 4, 0, "业务领域的全球性公司，");
            smartPrint.printText(0, 36 * MM, 0, 4, 0, "倾力打造“百年老店”是");
            smartPrint.printText(0, 42 * MM, 0, 4, 0, "得实集团一贯秉持的目标。");
            smartPrint.formFeed();
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 5 * MM, 0, 5, 0, "font 5,size 0：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(5 * CM, 1);
            smartPrint.printText(0, 0, 0, 5, 0, "  得实集团是一家综合性高");
            smartPrint.printText(0, 6 * MM, 0, 5, 0, "科技集团公司。经过三十");
            smartPrint.printText(0, 12 * MM, 0, 5, 0, "年的努力，得实集团已发");
            smartPrint.printText(0, 18 * MM, 0, 5, 0, "展成为涵盖计算机硬件、");
            smartPrint.printText(0, 24 * MM, 0, 5, 0, "个人健康服务、LED照明等");
            smartPrint.printText(0, 30 * MM, 0, 5, 0, "业务领域的全球性公司，");
            smartPrint.printText(0, 36 * MM, 0, 5, 0, "倾力打造“百年老店”是");
            smartPrint.printText(0, 42 * MM, 0, 5, 0, "得实集团一贯秉持的目标。");
            smartPrint.formFeed();
            smartPrint.setLabelEnd();
        }).start();
    }



    public void code128(View view)  {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }

        new Thread(()-> {
            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 0, 0, 4, 0, "打印一维码：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 5 * MM, 0, 5, 0, "不显示注释：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printCode128Setting(false, null, null);
            smartPrint.printCode128(0, 0, 5, 50, "abc123");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 5 * MM, 0, 5, 0, "显示注释：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(2 * CM, 1);
            smartPrint.printCode128Setting(true, 7, 5);
            smartPrint.printCode128(0, 0, 5, 50, "abc123");
            smartPrint.setLabelEnd();
        }).start();
    }

    public void codeQR(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()-> {
            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 0, 0, 4, 0, "打印二维码：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 5 * MM, 0, 5, 0, "大小 3，纠错 L：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printQRCode(0, 0, 3, "L", Dascom);
            smartPrint.formFeed();
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 5 * MM, 0, 5, 0, "大小 6，纠错 M：");
            smartPrint.setLabelEnd();
            Double cm=2.5 * CM;
            smartPrint.setLabelStart(cm.intValue(), 1);
            smartPrint.printQRCode(0, 0, 6, "M", Dascom);
            smartPrint.formFeed();
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 5 * MM, 0, 5, 0, "大小 12，纠错 Q：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(5 * CM, 1);
            smartPrint.printQRCode(0, 0, 12, "Q", Dascom);
            smartPrint.formFeed();
            smartPrint.setLabelEnd();


            smartPrint.setLabelStart(CM, 1);
            smartPrint.printText(0, 5 * MM, 0, 5, 0, "大小 16，纠错 H：");
            smartPrint.setLabelEnd();

            smartPrint.setLabelStart(7 * CM, 1);
            smartPrint.printQRCode(0, 0, 16, "H", Dascom);
            smartPrint.formFeed();
            smartPrint.setLabelEnd();
        }).start();
    }
}
