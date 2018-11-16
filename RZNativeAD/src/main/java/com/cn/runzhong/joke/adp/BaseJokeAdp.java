package com.cn.runzhong.joke.adp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cn.runzhong.joke.ImageDetailActivity;
import com.cn.runzhong.joke.R;
import com.cn.runzhong.joke.bean.NativeADModel;
import com.cn.runzhong.joke.bean.RandomBean;
import com.cn.runzhong.joke.common.NativeADConst;
import com.cn.runzhong.joke.common.OnNativeADClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CN.
 */

public abstract class BaseJokeAdp extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private OnNativeADClickListener onNativeADClickListener;
    protected Activity activity;

    public BaseJokeAdp(final Activity activity, final List<MultiItemEntity> multiItemEntityList, OnNativeADClickListener onNativeADClickListener, final boolean isPicture) {
        super(multiItemEntityList);
        this.activity = activity;
        addItemTypes();
        this.onNativeADClickListener = onNativeADClickListener;
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (multiItemEntityList.get(position - 2) instanceof NativeADModel) {
                    if (BaseJokeAdp.this.onNativeADClickListener != null) {
                        BaseJokeAdp.this.onNativeADClickListener.onNativeADClick((NativeADModel) multiItemEntityList.get(position- 2), position- 2);
                    }
                } else {
                    if (isPicture) {
                        ViewCompat.setTransitionName(view, NativeADConst.ELEMENT_NAME);
                        ActivityOptionsCompat optionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        activity, view, NativeADConst.ELEMENT_NAME);
                        ActivityCompat.setExitSharedElementCallback(activity,
                                new android.support.v4.app.SharedElementCallback() {
                                    @Override
                                    public void onSharedElementEnd(List<String> sharedElementNames,
                                                                   List<View> sharedElements, List<View>
                                                                           sharedElementSnapshots) {
                                        super.onSharedElementEnd(sharedElementNames, sharedElements,
                                                sharedElementSnapshots);
                                        for (final View view : sharedElements) {
                                            if (view instanceof SimpleDraweeView) {
                                                view.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                });
                        Intent intent = new Intent(activity, ImageDetailActivity.class);
                        intent.putExtra(NativeADConst.URI, (Serializable) getData().get(position - 2));
                        ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
                    }
                }
            }

        });
    }

    protected void loadGDTView(BaseViewHolder helper, MultiItemEntity item) {
//        int position = helper.getAdapterPosition();
        final NativeExpressADView adView = ((NativeADModel) item).getNativeExpressADViewGDT();
        FrameLayout frameLayout = helper.getView(R.id.container);
        if (frameLayout.getChildCount() > 0
                && frameLayout.getChildAt(0) == adView) {
            return;
        }

        if (frameLayout.getChildCount() > 0) {
            frameLayout.removeAllViews();
        }

        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }

        frameLayout.addView(adView);
        adView.render(); // 调用render方法后sdk才会开始展示广告
    }

    @Override
    public int getItemViewType(int position) {
        if (mData != null && mData.size() > 0) {
            MultiItemEntity multiItemEntity = mData.get(position);
            if (multiItemEntity instanceof NativeADModel) {
                NativeADModel nativeADModel = (NativeADModel) multiItemEntity;
                return nativeADModel.getItemType();
            } else if (multiItemEntity instanceof RandomBean.ResultBean) {
                return NativeADConst.TYPE_DATA;
            }
        }
        return super.getItemViewType(position);
    }

    public abstract void addItemTypes();
}
