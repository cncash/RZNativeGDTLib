package com.cn.runzhong.joke;

import android.annotation.TargetApi;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;

import com.cn.runzhong.joke.bean.RandomBean;
import com.cn.runzhong.joke.common.NativeADConst;
import com.cn.runzhong.joke.view.photodraweeview.OnPhotoTapListener;
import com.cn.runzhong.joke.view.photodraweeview.PhotoDraweeView;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


public class ImageDetailActivity extends AppCompatActivity implements ControllerListener<ImageInfo>,
        PhotoDraweeView.OnPhotoLoadListener, View.OnClickListener {
    private RandomBean.ResultBean resultBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowTransitions();
        setContentView(R.layout.activity_image_detail);
//        Util.keepFullScreen(this);
        resultBean = (RandomBean.ResultBean) getIntent().getSerializableExtra(NativeADConst.URI);
        if (resultBean == null || resultBean.url==null||resultBean.url.trim().equals("")) {
            finish();
            return;
        }
        findViewById(R.id.imgLeft).setOnClickListener(this);
        PhotoDraweeView draweeView = findViewById(R.id.photo_drawee_view);
        ViewCompat.setTransitionName(draweeView, NativeADConst.ELEMENT_NAME);
        draweeView.setPhotoUri(Uri.parse(resultBean.url));
        draweeView.setOnPhotoLoadListener(this);
        draweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                onBackPressed();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initWindowTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            AutoTransition transition = new AutoTransition();
            getWindow().setSharedElementEnterTransition(transition);
            getWindow().setSharedElementExitTransition(transition);
            ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames,
                                               List<View> sharedElements, List<View> sharedElementSnapshots) {
                    for (final View view : sharedElements) {
                        if (view instanceof PhotoDraweeView) {
                            ((PhotoDraweeView) view).setScale(1f, true);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onSubmit(String id, Object callerContext) {

    }

    @Override
    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
    }

    @Override
    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

    }

    @Override
    public void onIntermediateImageFailed(String id, Throwable throwable) {

    }

    @Override
    public void onFailure(String id, Throwable throwable) {

    }

    @Override
    public void onRelease(String id) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgLeft) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            MobclickAgent.onResume(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPause(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
