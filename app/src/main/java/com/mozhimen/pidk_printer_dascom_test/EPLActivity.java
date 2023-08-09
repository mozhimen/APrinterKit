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

import com.dascom.print.PrintCommands.EPL;
import com.dascom.print.Transmission.Pipe;

public class EPLActivity extends BaseActivity{
    private EPL smartPrint = null;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new EPL(pipe);
    }

    @Override
    public void initView() {

    }



    @Override
    public void initData() {
        tv_tip.setText("EPL");
        etIP.setText("192.168.1.11");
    }


    public void text(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            //打印从图像缓冲区页底开始
            smartPrint.setPrintDirection(false);
            //用于在创建新标签数据前清除数据缓冲区
            smartPrint.setLabelStart();
            //设置纸张的可打印宽度区域大小
            smartPrint.setLabelWidth(5 * INCH);
            //打印字体
            smartPrint.printText(0, MM, 1, 1, false,
                    "得实集团是一家综合性高科技集团公司。" +
                            "经过三十年的努力，得实集团已发展");
            //字体大小2，反白打印
            smartPrint.printText(0, 5 * MM, 2, 2, true,
                    "成为涵盖计算机硬件、个人健康服务、");
            //字体大小3
            smartPrint.printText(0, 12 * MM, 3, 3, false, "LED照明等业务领域的全球性公");
            //字体大小4
            smartPrint.printText(0, 2 * CM, 4, 4, false, "司，倾力打造“百年");
            //字体大小5
            smartPrint.printText(0, 33 * MM, 5, 5, false, "年老店”是得");
            //字体大小6，反白打印
            smartPrint.printText(0, 49 * MM, 6, 6, true, "实集团一贯");
            //字体大小8
            smartPrint.printText(0, 68 * MM, 8, 8, false, "秉持的目标。");

            smartPrint.printText(2 * CM, 95 * MM, 8, 8, true, "标。");

            //将图像缓冲区中的内容打印出来
            boolean b=smartPrint.setLabelEnd();
            Toast(b ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed));
        }).start();
    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            smartPrint.setPrintDirection(false);
            smartPrint.setLabelStart();
            //设置纸张的可打印宽度区域大小
            smartPrint.setLabelWidth(4 * INCH);
            //不显示注释，一维码高5毫米
            smartPrint.printCode128(0, 0, 2, 5 * MM, false, "abc123");
            //显示注释
            smartPrint.printCode128(35 * MM, 0, 2, 5 * MM, true, "abc123");
            //条宽5
            smartPrint.printCode128(0, 2 * CM, 5, 5 * MM, true, "abc123");
            boolean b=smartPrint.setLabelEnd();
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void codeQR(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        Double cm_35=2.5 * CM;
        new Thread(()->{
            smartPrint.setPrintDirection(false);
            smartPrint.setLabelStart();
            //设置纸张的可打印宽度区域大小
            smartPrint.setLabelWidth(4 * INCH);
            //打印二维码默认设置
            smartPrint.printText(0, 0, 1, 1, false, "二维码大小1，纠错级别L");
            smartPrint.printQrCode(CM, 5 * MM, 3, 0, Dascom);

            //大小6,低纠错
            smartPrint.printText(4 * CM, 0, 1, 1, false, "二维码大小6，纠错级别M");
            smartPrint.printQrCode(5 * CM, 5 * MM, 6, 1, Dascom);

            smartPrint.printText(0, 3 * CM, 1, 1, false, "二维码大小9，纠错级别Q");
            smartPrint.printQrCode(CM, cm_35.intValue(), 9, 2, Dascom);

            smartPrint.printText(4 * CM, 3 * CM, 1, 1, false, "二维码大小14，纠错级别H");
            smartPrint.printQrCode(5 * CM, cm_35.intValue(), 14, 3, Dascom);

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
            //设置纸张的可打印宽度区域大小
            smartPrint.setLabelWidth(4 * INCH);
//            bitmap.let { bmp ->
//                    setLabelLength(bmp.height, 0)
//                printBitmap(bmp, 0, 0);
//            }

            smartPrint.setLabelLength(bitmap.getHeight(), 0);
            smartPrint.printBitmap(bitmap, 0, 0);
            boolean b=smartPrint.setLabelEnd();
            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }
}
