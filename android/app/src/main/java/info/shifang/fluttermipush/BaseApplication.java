package info.shifang.fluttermipush;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
// import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.security.*;
import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import android.content.pm.Signature;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

// import io.flutter.app;
// import io.flutter.view.FlutterMain;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * 1、为了打开客户端的日志，便于在开发过程中调试，需要自定义一个 Application。
 * 并将自定义的 application 注册在 AndroidManifest.xml 文件中。<br/>
 * 2、为了提高 push 的注册率，您可以在 Application 的 onCreate 中初始化 push。你也可以根据需要，在其他地方初始化 push。
 *
 * @author wangkuiwei
 */
public class BaseApplication extends io.flutter.app.FlutterApplication {

    // user your appid the key.
    private static final String APP_ID = "2882303761517842664";
    // user your appid the key.
    private static final String APP_KEY = "5361784279664";
    private static final String APP_SECRET = "tGkeWKtKZcQbQfL7ok0RMw==";

    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    
    public static final String TAG = "info.shifang.fluttermipush";
    private static DemoHandler sHandler = null;
    private static MainActivity sMainActivity = null;

    public static DemoHandler getHandler() {
        return sHandler;
    }
    public static void setMainActivity(MainActivity activity) {
        sMainActivity = activity;
    }

    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            if (sMainActivity != null) {
                sMainActivity.sendMessage(msg.toString());
                // sMainActivity.refreshLogInfo();
            }
            if (!TextUtils.isEmpty(s)) {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onCreate() {
        // FlutterMain.startInitialization(applicationContext);
        // FlutterMain.startInitialization(this);
        super.onCreate();

        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
        if (sHandler == null) {
            sHandler = new DemoHandler(getApplicationContext());
        }
        Log.v("TAG:", "SignInfo: " + getSignInfo(getApplicationContext()));
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    protected String getSignInfo(Context mContext) {
        String signcode = "";
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
                getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
 
            signcode = parseSignature(sign.toByteArray());
            signcode = signcode.toLowerCase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return signcode;
    }
 
    protected String parseSignature(byte[] signature) {
        String sign = "";
        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            Log.e(TAG, "pubKey" + pubKey);
            String ss = subString(pubKey);
            ss = ss.replace(",", "");
            ss = ss.toLowerCase();
            int aa = ss.indexOf("modulus");
            int bb = ss.indexOf("publicexponent");
            sign = ss.substring(aa + 8, bb);
        } catch (CertificateException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return sign;
    }
 
    public String subString(String sub) {
        Pattern pp = Pattern.compile("\\s*|\t|\r|\n");
        Matcher mm = pp.matcher(sub);
        return mm.replaceAll("");
    }
}
