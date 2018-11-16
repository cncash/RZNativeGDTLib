package com.cn.runzhong.joke;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by CN.
 */

public class JokeApp {
    public static void init(Application application){
        Fresco.initialize(application);
    }
}
