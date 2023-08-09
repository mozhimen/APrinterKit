package com.mozhimen.pidk_printer_dascom_test;

import static com.dascom.print.Utils.CanvasUtils.BackgroundWhite;
import static com.dascom.print.Utils.Unit.DPI_203.CM;
import static com.dascom.print.Utils.Unit.DPI_203.INCH;
import static com.dascom.print.Utils.Unit.DPI_203.MM;
import static com.mozhimen.pidk_printer_dascom_test.Static.Dascom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.dascom.print.BroadcastReceiver.UsbStatus;
import com.dascom.print.PrintCommands.ESCP;
import com.dascom.print.Transmission.Pipe;
import com.dascom.print.Utils.CanvasUtils;
import com.dascom.print.Utils.HalfTone;
import com.dascom.print.Utils.encoding.EncodingHandler;
import com.google.zxing.WriterException;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class ESCPActivity extends BaseActivity {
    private ESCP smartPrint = null;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escp);

    }

    @Override
    public void initView() {

    }




    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }


    public static String printDataLog(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        String [] str = new String[src.length];
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                str[i] = 0+"";
            }
            str[i] = hv;
        }

        StringBuffer s=new StringBuffer();
        for(int i=0;i<str.length;i++) {
            if(str[i].length()==1) {
                s.append("0"+str[i]+" ");
            }else {
                s.append(str[i]+" ");
            }

        }


        return s.toString();
    }

    @Override
    public void initData() {
        tv_tip.setText("ESCP");
        etIP.setText("192.168.0.1");
    }

    @Override
    public void Pipe(Pipe pipe) {
        super.Pipe(pipe);
        smartPrint = new ESCP(pipe);
    }

    public void text(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            //打印文本
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            //换行
            smartPrint.lineFeed();
            //顺向走纸10毫米
            smartPrint.advancePosition(10 * MM);

            //加粗
            smartPrint.setFontBold(true);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.setFontBold(false);
            smartPrint.lineFeed();
            smartPrint.advancePosition(10 * MM);

            //斜体
            smartPrint.setFontItalic(true);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.setFontItalic(false);
            smartPrint.lineFeed();
            smartPrint.advancePosition(10 * MM);

            //纵横倍级放大
            smartPrint.scaleCharacters(16, 16);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.lineFeed();
            smartPrint.advancePosition(10 * MM);

            smartPrint.scaleCharacters(50, 50);
            smartPrint.printText("  得实集团是一家综合性高科技集团公司。" +
                    "经过三十年的努力，得实集团已发展成为涵盖计算机硬件、个人健康服务、" +
                    "LED照明等业务领域的全球性公司，倾力打造“百年老店”是得实集团一贯秉持的目标。");
            smartPrint.scaleCharacters(0, 0);
            smartPrint.lineFeed();
            boolean b=smartPrint.advancePosition(10 * MM);

            //退纸
//            it.formFeed()

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void code128(View view) {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        new Thread(()->{
            //打印一维码
            smartPrint.printCode128(0, 0, 2, 1, false, "abc123");
            //显示注释
            smartPrint.printCode128(5 * CM, 0, 2, 1, true, "abc123");

            smartPrint.printCode128(10 * CM, 0, 3, 1, true, "abc123");

            boolean b=smartPrint.printCode128(0, 0, 2, 2, true, "abc123");

            Toast(b ? getResources().getString(R.string.print_success) :getResources().getString(R.string.print_failed));
        }).start();
    }

    public void codeQR(View view) throws WriterException {
        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        Bitmap qrCodeBitmap = EncodingHandler.createQRCode(Dascom, INCH);
        printBitmap(BackgroundWhite(qrCodeBitmap));
    }



    @Override
    protected void printBitmap(Bitmap bitmap) {

        if (pipe == null || !pipe.isConnected()) {
            Toast(getResources().getString(R.string.please_first_connect_printer));
            return;
        }
        smartPrint.setGray(245);
        new Thread(()->{
            smartPrint.printBitmap(bitmap,0,0);
        }).start();
    }


    public void getEscPStatus(View view){
        if(this.usbDevice == null){
            Toast("请用USB连接打印机.");
        }else {
            byte[] status_data=smartPrint.getESCPStatusData();

            //纸将尽, 不是所有机器都支持
//            val res1 = smartPrint?.getESCPStatus(ESCP.STATE_PAPER_RUN_OUT,ESCP.STATE_BYTE_ONE);
            boolean res2 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_PAPER_JAM_ERROR,ESCP.STATE_BYTE_ONE);
            boolean res3 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_DEVICE_HOMING_ERROR,ESCP.STATE_BYTE_ONE);
            boolean res4 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_PAPER_HAVE,ESCP.STATE_BYTE_ONE);
            boolean res5 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_ONLINE,ESCP.STATE_BYTE_ONE);
            boolean res6 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_PAPER_LACK,ESCP.STATE_BYTE_ONE);
            boolean res7 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_CUTTER_ERROR,ESCP.STATE_BYTE_ONE);
            boolean res8 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_DEVICE_BUSY,ESCP.STATE_BYTE_ONE);
            //缓冲非空属于第二个状态位
            boolean res9 = smartPrint.handlerESCPStatus(status_data,ESCP.STATE_CACHE_EMPTY,ESCP.STATE_BYTE_TWO);
            Toast(getResources().getString(R.string.paper_error)+res2+"/"+getResources().getString(R.string.home_error)+res3+"/"+getResources().getString(R.string.have_paper)
                    +res4+"/"+getResources().getString(R.string.online)+res5+"/"
                    +getResources().getString(R.string.lack_paper)+res6+"/"+getResources().getString(R.string.cutter_error)
                    +res7+"/"+getResources().getString(R.string.devices_busy)+res8+"/"+getResources().getString(R.string.buffer_not_empty)+""+res9);
        }
    }
}
