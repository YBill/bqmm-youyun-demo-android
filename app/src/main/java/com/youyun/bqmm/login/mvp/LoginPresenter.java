package com.youyun.bqmm.login.mvp;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.data.AuthResultData;
import com.ioyouyun.wchat.message.WChatException;
import com.youyun.bqmm.utils.YouyunUtils;

/**
 * Created by 卫彪 on 2016/9/8.
 */
public class LoginPresenter {

    private Activity activity;
    private LoginView loginView;
    private Handler handler;

    public LoginPresenter(LoginView loginView, Activity activity) {
        this.loginView = loginView;
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 登录
     */
    public void login() {
        loginView.showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AuthResultData authResultData = WeimiInstance.getInstance().registerApp(
                            activity, YouyunUtils.generateOpenUDID(activity), YouyunUtils.CLIENT_ID, YouyunUtils.SECRET, 120);
                    if (authResultData != null && authResultData.success) {
                        loginResult(true);
                    } else {
                        loginResult(false);
                    }
                } catch (WChatException e) {
                    e.printStackTrace();
                    loginResult(false);
                }
            }
        }).start();

    }

    private void loginResult(final boolean result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                loginView.hideProgress();
                if (result)
                    loginView.loginSuccess();
                else
                    loginView.loginFail();
            }
        });
    }

}
