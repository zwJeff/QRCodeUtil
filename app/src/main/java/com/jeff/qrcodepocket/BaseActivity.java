package com.jeff.qrcodepocket;

import android.app.Activity;
import android.os.Bundle;

import com.jeff.utils.UIUtils;

/**
 * Created by 张武 on 2016/8/26.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //设置浸入式状态栏
        UIUtils.openImmerseStatasBarMode(this);
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }
}
