package info.shifang.fluttermipush;

import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import io.flutter.view.FlutterMain;
import io.flutter.view.FlutterView;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BasicMessageChannel.MessageHandler;
import io.flutter.plugin.common.BasicMessageChannel.Reply;
import io.flutter.plugin.common.StringCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
// import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends FlutterActivity {
  // private FlutterView flutterView;

  private BasicMessageChannel<String> messageChannel;
  private static final String PUSH_CHANNEL = "info.shifang.flutterpush/push";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BaseApplication.setMainActivity(this);
    messageChannel = new BasicMessageChannel<>(getFlutterView(), PUSH_CHANNEL, StringCodec.INSTANCE);
    messageChannel.
        setMessageHandler(new MessageHandler<String>() {
            @Override
            public void onMessage(String s, Reply<String> reply) {
                // onFlutterIncrement();
                reply.reply("");
            }
    });
    GeneratedPluginRegistrant.registerWith(this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    // 延迟发送消息，等待flutter环境初始化。
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        // 初始化messageChannel
       
        Bundle b = getIntent().getExtras();
        if (b != null) {
          String json = b.getString("");
          messageChannel.send(json);
        }
      }
    }, 1500l);
  }

  public void sendMessage(String b) {
    Log.v("MainV.TAG", "sendMessage is called. " + b);
    messageChannel.send(b);
  }
  //   @Override
  //   protected void onDestroy() {
  //       if (flutterView != null) {
  //           flutterView.destroy();
  //       }
  //       super.onDestroy();
  //   }

  //   @Override
  //   protected void onPause() {
  //       super.onPause();
  //       flutterView.onPause();
  //   }

  //   @Override
  //   protected void onPostResume() {
  //       super.onPostResume();
  //       flutterView.onPostResume();
  // }
}
