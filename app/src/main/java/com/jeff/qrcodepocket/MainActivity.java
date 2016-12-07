package com.jeff.qrcodepocket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.jeff.utils.RootUtil;
import com.jeff.utils.WifiConnect;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.decoding.Intents;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private GridView gridView;

    private WifiConnect mWifiConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiConnect = new WifiConnect((WifiManager) this.getSystemService(Context.WIFI_SERVICE), this);
        gridView = (GridView) findViewById(R.id.grid_view);

        initView();

    }

    private void initView() {

        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<Integer> iconIdList = new ArrayList<>();
        nameList.add("扫 码");
        iconIdList.add(R.mipmap.saoyisao);
        nameList.add("支付宝扫码");
        iconIdList.add(R.mipmap.zhifubao);
        nameList.add("支付码");
        iconIdList.add(R.mipmap.ali_pay_code);
        nameList.add("扫一扫");
        iconIdList.add(R.mipmap.saoyisao);
        GridAdapter gridAdapter = new GridAdapter(MainActivity.this, nameList, iconIdList);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case 1:
                        toAliPayScan();
                        break;
                    case 2:
                        toAliPayCode();
                        break;
                    case 3:
                        if (RootUtil.checkRootPermission())
                            toWeChatScan();
                        else {
                            Toast.makeText(MainActivity.this, "调用微信扫一扫需要获取root权限！", Toast.LENGTH_SHORT).show();
                            RootUtil.applyForRootPermission(getPackageCodePath());
                        }
                        break;
                }
            }
        });


    }

    private void toWeChatScan() {
        // try {
        //利用Intent打开微信
        jumpTo3rdApp(new ComponentName("com.tencent.mm", "com.tencent.mm.plugin.scanner.ui.BaseScanUI"));
//        } catch (Exception e) {
//            //若无法正常跳转，在此进行错误处理
//            Toast.makeText(this, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
//        }
    }

    private void toAliPayScan() {
        try {
//            //利用Intent打开支付宝
//            //支付宝跳过开启动画打开扫码和付款码的url scheme分别是alipayqr://platformapi/startapp?saId=10000007和
//            //alipayqr://platformapi/startapp?saId=20000056
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(this, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
        }
    }

    private void toAliPayCode() {
        try {
//            //利用Intent打开支付宝
//            //支付宝跳过开启动画打开扫码和付款码的url scheme分别是alipayqr://platformapi/startapp?saId=10000007和
//            //alipayqr://platformapi/startapp?saId=20000056
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(this, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == 0) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if (result.startsWith("http://weibo.cn/qr/userinfo?uid=")) {
                        /**
                         * 扫微博用户名片
                         */
                        if (isPkgInstalled("com.sina.weibo")) {
                            String[] temp = result.split("=");
                            if (temp != null) {
                                String uid = temp[temp.length - 1];
                                Uri uri = Uri.parse("sinaweibo://userinfo?uid=" + uid);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                return;
                            }
                        }
                        // WIFI:T:WPA;P:"adgjmptw";S:jeff2;
                    }
                    if (result.startsWith("WIFI")) {
                        String ssid, pwd;
                        WifiConnect.WifiCipherType type;
                        String[] temps = result.split(";");
                        if (temps.length >= 3) {
                            String[] temp = temps[0].split(":");
                            type = getWifiTypeByStr(temp[2]);
                            if (type != null) {
                                temp = temps[1].split(":");
                                temp = temp[1].split("\"");
                                pwd = temp[1];
                                temp = temps[2].split(":");
                                ssid = temp[1];
                                mWifiConnect.Connect(ssid, pwd, type);
                                return;
                            }
                        }
                    }
                    if (result.startsWith("http")) {
                        /**
                         * 网址
                         */
                        Uri uri = Uri.parse(result.toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "解析二维码结果：" + result, Toast.LENGTH_LONG).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void jumpTo3rdApp(ComponentName paramComponentName) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // localIntent.setAction("android.intent.action.MAIN");
        localIntent.setComponent(paramComponentName);

        this.startActivity(localIntent);
        return;

    }

    private boolean isPkgInstalled(String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        }
        return true;
    }

    private WifiConnect.WifiCipherType getWifiTypeByStr(String str) {
        switch (str) {
            case "WPA":
                return WifiConnect.WifiCipherType.WIFICIPHER_WPA;
            case "WEP":
                return WifiConnect.WifiCipherType.WIFICIPHER_WEP;
            case "NOPASS":
                return WifiConnect.WifiCipherType.WIFICIPHER_NOPASS;
            case "INVALID":
                return WifiConnect.WifiCipherType.WIFICIPHER_INVALID;
            default:
                return null;
        }
    }

    @Override
    public void onClick(View view) {

    }
}
