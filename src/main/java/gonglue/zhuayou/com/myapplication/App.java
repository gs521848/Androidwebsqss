package gonglue.zhuayou.com.myapplication;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;

/**
 * Created by Administrator on 2018\10\19 0019.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
    }
}
