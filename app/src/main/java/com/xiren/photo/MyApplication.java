package com.xiren.photo;

import android.app.Application;
import android.content.Context;

/**
 * description:
 * Date: 2016/9/8 18:04
 * User: shaobing
 */
public class MyApplication extends Application {

    private static Context mContext;

    /**
     * 获取系统Context
     * @return 返回值
     */
    public static Context getAppContext(){



        return mContext;


    }

    public static boolean isAcceptLesson() {
        return isAcceptLesson;
    }

    public static void setIsAcceptLesson(boolean isAcceptLesson) {
        MyApplication.isAcceptLesson = isAcceptLesson;
    }

    public static boolean isAcceptLesson = true;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        // 注册crashHandler
//        crashHandler.init(getApplicationContext());
//		// 发送以前没发送的报告(可选)
//		crashHandler.sendPreviousReportsToServer();
    }
}
