package global.longbridge.android;

import android.app.Application;
import android.content.Context;

import com.lb.price.one.BuildConfig;
import com.longbridge.core.comm.FApp;

public class LBApplication extends Application {
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        FApp.init(getApplication(), "", BuildConfig.DEBUG);
    }

    private Application getApplication() {
        return this;
    }


}
