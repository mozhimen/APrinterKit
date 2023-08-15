package com.mozhimen.pidk_printer_dascom_test;


import static com.mozhimen.pidk_printer_dascom_test.Static.emulation;
import static com.mozhimen.pidk_printer_dascom_test.Static.emulations;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */


public class SelectEmulationActivity extends InitActivity implements CompoundButton.OnCheckedChangeListener {

    private Spinner spinner_emulation;
    private ArrayAdapter<String> adapter;
    private ImageView question;
    private Button confirm;
    private TextView mTextViewVersion;
    private Switch mSwLogOpen;
    private CustomApplication mCusApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_emulation);
        init();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emulations);
        spinner_emulation.setAdapter(adapter);

        spinner_emulation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                emulation = emulations[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        question.setOnClickListener(v -> {
            DialogUtils dialog = new DialogUtils(SelectEmulationActivity.this);
            dialog.show(R.string.select_emulation_title_tips, R.string.select_emulation_message_tips);
        });
        confirm.setOnClickListener(v -> {
            switch (emulation) {
                case "ZPL":
                    startActivity(ZPLActivity.class);
                    break;
                case "TSPL":
                    startActivity(TSPLActivity.class);
                    break;
                case "ESCP":
                    startActivity(ESCPActivity.class);
                    break;
                case "ESCPOS":
                    startActivity(ESCPOSActivity.class);
                    break;
                case "EPL":
                    startActivity(EPLActivity.class);
                    break;
                case "ESCPOS_9Pin":
                    startActivity(ESCPOS_9PinActivity.class);
                    break;
                case "OKI":
                    startActivity(OKIActivity.class);
                    break;
                case "CPCL":
                    startActivity(CPCLActivity.class);
                    break;
            }
        });
    }

    private void init() {
        spinner_emulation = findViewById(R.id.select_dialog_listview);
        question = findViewById(R.id.question);
        confirm = findView(R.id.select_emulation_confirm);
        mTextViewVersion =  findViewById(R.id.tv_verisonmsg);
        mSwLogOpen =  findViewById(R.id.sw_log_open);
        //现实版本信息
        mTextViewVersion.setText(String.format("版本:%s", BuildConfig.VERSION_NAME));
        //日志开关
        mSwLogOpen.setOnCheckedChangeListener(this);
        //获取application
        mCusApp = (CustomApplication) getApplication(); // 获取应用程序
    }

    long firstTime = 0;

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast("再按一次退出程序");
            firstTime = secondTime;
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        DSLogManager.d("日志是否开启:" + isChecked);
        mCusApp.setSetLog(isChecked);
//        Toast("日志是否开启(app- value):" + mCusApp.getSetLog());
    }
}
