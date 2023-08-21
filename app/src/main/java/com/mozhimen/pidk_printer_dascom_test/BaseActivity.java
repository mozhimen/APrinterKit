package com.mozhimen.pidk_printer_dascom_test;

/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */


import static android.os.Build.VERSION.SDK_INT;

import static com.mozhimen.pidk_printer_dascom_test.PermissionUtil.*;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.dascom.print.Transmission.BluetoothPipe;
import com.dascom.print.Transmission.Pipe;
import com.dascom.print.Transmission.UsbPipe;
import com.dascom.print.Transmission.WifiPipe;
import com.dascom.print.Utils.*;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.mozhimen.basick.utilk.java.io.UtilKFile;
import com.mozhimen.basick.utilk.java.util.UtilKDate;
import com.mozhimen.bluetoothk.commons.BluetoothKConnectWithDataManageCallback;
import com.mozhimen.pidk_printer_dascom.PidKPrinterDascom;
import com.mozhimen.pidk_printer_dascom.helpers.PrinterDascomUtil;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public abstract class BaseActivity extends InitActivity {

    protected BluetoothUtils btUtils;
    protected WifiUtils wifiUtils;
    protected UsbUtils usbUtils;
    protected UsbDevice usbDevice;
    protected EditText etIP;
    protected TextView tv_btName;
    protected TextView tv_tip;
    protected ImageView img_tem;
    protected String TAG = "BaseActivity";
    protected LinearLayout contentView;
    ParcelFileDescriptor pdf;
    public CustomApplication mCusApp;
    MuPDFCore mMuPDFCore;
    protected String btMac = "00:15:83:CE:B1:6B";//DP-230L
    //    protected String btMac = "34:81:F4:37:27:63";//DP-330L
    protected Pipe pipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base);
        initDefaultView();
        //init();
        PermissionUtil.requestPermissions(this, 0, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,
                ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION);

        requestPermission();

        //启动蓝牙
        btUtils = BluetoothUtils.getInstance();
        if (!btUtils.isEnable()) {
            btUtils.openBluetooth(this, 0);
        }
        //启动wifi
        wifiUtils = WifiUtils.getInstance(this);
        if (!wifiUtils.isEnabled()) {
            wifiUtils.openWifi();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (checkPermission()) {
            init();
            return;
        }
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                startActivityForResult(intent, 22);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                startActivityForResult(intent, 22);
            }
        } else {
            // Android 11以下
            PermissionUtil.requestPermissions(this, 0, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,
                    ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION);
        }
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result =
                    ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            int result1 =
                    ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * setContentView和BaseSetContentView效果一样
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (R.layout.layout_base == layoutResID) { //先初始化baselayout
            super.setContentView(R.layout.layout_base);
            contentView = (LinearLayout) findViewById(R.id.v_content);
            contentView.removeAllViews();
        } else if (layoutResID != R.layout.layout_base) {//再初始子类layout
            View addView = LayoutInflater.from(this).inflate(layoutResID, null);
            contentView.addView(addView);
            setActivityState(this);
            initView();
            initData();
        }
    }


    //初始化视图和数据
    public abstract void initView();

    public abstract void initData();

    public void scanDevice(View view) {
        usbUtils = UsbUtils.getInstance(this);
        if (usbUtils != null) {

            usbUtils.getConnectUsbDevice((device, grant) -> {
                if (grant) {
                    usbDevice = device;
                    Toast(getResources().getString(R.string.scan_available_usb_devices));
                } else {
                    if (device != null) {
                        Toast(getResources().getString(R.string.no_usb_authorization));
                    } else {
                        Toast(getResources().getString(R.string.usb_device_not_scanned));
                    }
                }
            });
        }
    }

    public void connectDevice(View view) {
        if (pipe != null) {
            pipe.close();
            pipe = null;
        }
        if (usbDevice != null) {
            try {
                pipe = new UsbPipe(this, usbDevice);
                Pipe(pipe);

                Toast(getResources().getString(R.string.usb_device_connected_successfully));
            } catch (Exception e) {
                e.printStackTrace();
                Toast(getResources().getString(R.string.usb_device_connected_failed));
            }
        } else {
            Toast(getResources().getString(R.string.please_scan_usbdevice_permissions));
        }
    }


    /**
     * 选择蓝牙打印机
     */
    public void selectBluetoothPrinter(View view) {
//        if (hadPermission(this, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)) {
//            MedBluetooth.connectBluetooth(this, new BluetoothConnectWithDataManageCallback() {
//                @Override
//                public void connected(BluetoothSocket socket, BluetoothDevice device, Exception e) {
//
//                }
//
//                @Override
//                public void disconnected() {
//
//                }
//
//                @Override
//                public void getMac(String BtName, String mac) {
//                    tv_btName.setText(BtName);
//                    btMac = mac;
//                }
//            });
//        } else {
//            PermissionUtil.requestPermissions(this, 10003, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION);
//        }
        PrinterDascomUtil.select(this, new BluetoothKConnectWithDataManageCallback() {
                    @Override
                    public void connected(BluetoothSocket socket, BluetoothDevice device, Exception e) {

                    }

                    @Override
                    public void disconnected() {

                    }

                    @Override
                    public void getMac(String BtName, String mac) {
                        tv_btName.setText(BtName);
                        btMac = mac;
                    }
                }
        );
    }


    public void connect(View view) {
        Log.d(TAG, "connect: btMac " + btMac);
        if (pipe != null) {
            pipe.close();
            pipe = null;
        }
        if (TextUtils.isEmpty(btMac)) {
            Toast(getResources().getString(R.string.please_select_bluetooth_first));
            return;
        }
        openDialog();
        new Thread(() -> {
            try {
                pipe = new BluetoothPipe(btMac);//蓝牙连接
                Pipe(pipe);
                closeDialog();
                Toast(getResources().getString(R.string.connection_succeeded));
            } catch (IOException e) {
                e.printStackTrace();
                closeDialog();
                Toast(getResources().getString(R.string.connection_failed));
            }
        }).start();

    }

    public void Pipe(Pipe pipe) {

    }


    public void jump(View view) {

        wifiUtils.jumpToWifiSetting();
    }

    public void connectWifi(View view) {
        if (pipe != null) {
            pipe.close();
            pipe = null;
        }
        new Thread(() -> {
            try {
                pipe = new WifiPipe(etIP.getText().toString(), 9100);//连接
                Pipe(pipe);

                Toast(getResources().getString(R.string.connection_succeeded));
            } catch (Exception e) {
                e.printStackTrace();
                Toast(getResources().getString(R.string.connection_failed));
            }
        }).start();
    }

    public void disconnect(View view) {
        if (pipe != null) {
            pipe.close();
            pipe = null;
            Toast(getResources().getString(R.string.disconnect));
        }
    }


    String picPath = ""; //图片路径
    String pdfPath = ""; //PDF路径

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2333) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    return;
                }
                picPath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                img_tem.setImageBitmap(bitmap);
                Log.e(TAG, "onActivityResult: " + picPath);
            }
        } else if (requestCode == 666) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    pdfPath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    if (mMuPDFCore != null) {
                        mMuPDFCore.onDestroy();
                    }
                    mMuPDFCore = new MuPDFCore(pdfPath);
                    File file = new File(pdfPath);
                    int num = getPdfPageCount(file);
                    Log.d(TAG, "printPdf,pdf总页数:" + num);
                    Bitmap bitmap = HalfTone.FloydSteinberg(CanvasUtils.BackgroundWhite(pdfToBitmap(scale, 0)));
                    img_tem.setImageBitmap(bitmap);
                    Log.e(TAG, "onActivityResult: " + pdfPath);
                }
            }
        }
        if (requestCode == 22) {
            init();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void initDefaultView() {
        etIP = findView(R.id.et_ip);
        tv_btName = findView(R.id.tv_btName);
        tv_tip = findView(R.id.tv_tip);
        img_tem = findView(R.id.img_template);


    }

    String logpath;

    public void init() {
        //获取application
        mCusApp = (CustomApplication) getApplication();
        if (mCusApp.getSetLog()) {
            Date date = new Date();
            //按时间创建父目录
            //按时间创建父目录
            SimpleDateFormat formatterParent = new SimpleDateFormat("yyyyMMdd");
            //日志文件用时间做命名
            //日志文件用时间做命名
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = formatter.format(date);
            String parentPath = formatterParent.format(date);
            //检测目录是否存在
            //LogUtils.checkDir(parentPath);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator + "DasLog" + File.separator + parentPath;
                File parth = new File(externalStoragePath);
                if (!parth.exists())
                    parth.mkdirs();
                logpath =
                        externalStoragePath.toString() + File.separator + time + ".txt";
            } else {
                LogUtils.checkDir(parentPath);
                logpath = Environment.getExternalStorageDirectory()
                        .toString() + "/DascomLog/" + parentPath + "/" + time + ".txt";
            }
            LogUtils.setLog(true, true,
                    logpath);
        }
    }

    /**
     * 处理请求权限结果事件
     *
     * @param requestCode  请求码
     * @param permissions  权限组
     * @param grantResults 结果集
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10001:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPictureFilePickActivity();//图片
                } else {
                    Toast(getResources().getString(R.string.permission_acquisition_failed));
                }
                break;
            case 10002:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPDFilePickActivity();//PDF
                } else {
                    Toast(getResources().getString(R.string.permission_acquisition_failed));
                }
                break;
            case 10003:
                if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    selectBluetoothPrinter(null);//选择蓝牙
                } else {
                    Toast(getResources().getString(R.string.permission_acquisition_failed));
                }
                break;

            case 0:
                XXPermissions.with(this)
                        // 不适配 Android 11 可以这样写
                        //.permission(Permission.Group.STORAGE)
                        // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                if (all) {
                                    //toast("获取存储权限成功");
                                    init();
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                if (never) {
                                    //toast("被永久拒绝授权，请手动授予存储权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(getApplication(), permissions);
                                } else {
                                    //toast("获取存储权限失败");
                                }
                            }
                        });
                break;
        }
    }


    /**
     * 选择图片
     */
    protected void startPictureFilePickActivity() {
        MaterialFilePicker picker = new MaterialFilePicker();
        picker.withActivity(this)
//                .withRootPath()
                .withRequestCode(2333)
                .withFilter(Pattern.compile(".*\\.(png|jpg|jpeg)$"))
                .start();
    }

    protected void startPDFilePickActivity() {
        MaterialFilePicker picker = new MaterialFilePicker();
        picker.withActivity(this)
                .withRequestCode(666)
                .withFilter(Pattern.compile(".*\\.(pdf|PDF)$"))
                .start();
    }

    public void selectPicture(View view) {
//        if (hadPermission(this, READ_EXTERNAL_STORAGE)) {
//            startPictureFilePickActivity();
//        } else {
//            PermissionUtil.requestPermission(this, 10001, READ_EXTERNAL_STORAGE);
//        }


        PictureParameterStyle mPictureParameterStyle = PictureParameterStyle.ofDefaultStyle();
        mPictureParameterStyle.isOpenCompletedNumStyle = true;
        PictureWindowAnimationStyle windowAnimationStyle = new PictureWindowAnimationStyle();
        windowAnimationStyle.ofAllAnimation(0, 0);
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)
                .filterMinFileSize(0)
                .setPictureStyle(mPictureParameterStyle)
                .setPictureWindowAnimationStyle(windowAnimationStyle)
                .forResult(new OnResultCallbackListener<LocalMedia>() {

                    @Override
                    public void onResult(List<LocalMedia> result) {
//                        Log.e(TAG, result.get(0).getPath());
//                        mImageSetting.path = result.get(0).getRealPath();
//                        view.setText(mImageSetting.path);

                        picPath = result.get(0).getRealPath();
                        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                        img_tem.setImageBitmap(bitmap);
                        Log.e(TAG, "onActivityResult: " + picPath);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }

    public void picture(View view) {
        if (TextUtils.isEmpty(picPath)) {
            Toast(getResources().getString(R.string.please_select_printer_picture));
            return;
        }
        Bitmap bitmap = HalfTone.FloydSteinberg(CanvasUtils.BackgroundWhite(BitmapFactory.decodeFile(picPath)));
        printBitmap(bitmap);
    }

    public void selectPdf(View view) {
        if (hadPermission(this, READ_EXTERNAL_STORAGE)) {
            startPDFilePickActivity();
        } else {
            PermissionUtil.requestPermission(this, 10002, READ_EXTERNAL_STORAGE);
        }
    }


    int scale = 1;

    public void printPdf(View view) {
        if (!TextUtils.isEmpty(pdfPath)) {
            File file = new File(pdfPath);
            int num = getPdfPageCount(file);
            Log.d(TAG, "printPdf,pdf总页数:" + num);
            Bitmap bitmap = HalfTone.FloydSteinberg(CanvasUtils.BackgroundWhite(pdfToBitmap(scale, 0)));

            //bitmap= ImageUtils.compressBySampleSize(bitmap,2);
            printBitmap(bitmap);
        } else {
            Toast(getResources().getString(R.string.please_select_pdf));
        }
    }

    protected abstract void printBitmap(Bitmap bitmap);


    /**
     * @param file 文件路径
     * @return
     */
    protected int getPdfPageCount(File file) {
        int temp = -1;
        try {
            pdf = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

            if (pdf == null) {
                Log.e(TAG, "pdfToBitmap: " + file.getPath());
                Log.e(TAG, "pdfToBitmap: " + pdf);
                return -1;
            }

            //返回pdf多少页
            temp = mMuPDFCore.countPages();
            Log.e(TAG, "pdfToBitmap: " + temp);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * @param scale   缩放比例
     * @param pageNum 指定页数 0为第一页
     */
    protected Bitmap pdfToBitmap(double scale, int pageNum) {
        Bitmap bitmap;

        int PN = 0;

//        if (PN < pageNum || mMuPDFCore.countPages() < PN) {
//            return null;
//        }

        PointF pointF = mMuPDFCore.getPageSize(pageNum);

        //dpi:440
        //1212:2685:1.0
        //2.2153465346534653465346534653465
        //198.41:439.39:1.0
        //2.2145557179577642255934680711658

        // int dpi = getResources().getDisplayMetrics().densityDpi;
        // Log.i(TAG, pointF.x + ":" + pointF.y + ":" + scale + ":" + "dpi:" + dpi);
        // int width = (int) (pointF.x * dpi / 72 * scale);
        // int height = (int) (pointF.y * dpi / 72 * scale);

        Log.i(TAG, pointF.x + ":" + pointF.y + ":" + scale);
        int width = (int) (pointF.x * scale);
        int height = (int) (pointF.y * scale);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //显示注释和字段，renderAnnot true, 默认不显示。
        mMuPDFCore.drawPage(bitmap, pageNum, width, height, 0, 0, width, height, new Cookie());

        return bitmap;
    }


    @Override
    protected void onDestroy() {
        disconnect(null);
        if (mMuPDFCore != null) {
            mMuPDFCore.onDestroy();
        }
        //关闭日志
        if (mCusApp != null && mCusApp.getSetLog()) {
            LogUtils.setLog(false, false, "");
        }
        super.onDestroy();
    }


    public void openLog(View view) {
//        DSLogManager.startLog();
    }

    public void getRMtatus(View view) {
    }
}
