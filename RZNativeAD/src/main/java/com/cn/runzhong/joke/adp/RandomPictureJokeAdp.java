package com.cn.runzhong.joke.adp;

import android.app.Activity;
import android.net.Uri;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cn.runzhong.joke.R;
import com.cn.runzhong.joke.bean.RandomBean;
import com.cn.runzhong.joke.common.NativeADConst;
import com.cn.runzhong.joke.common.OnNativeADClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class RandomPictureJokeAdp extends BaseJokeAdp {
    public RandomPictureJokeAdp(Activity activity, List<MultiItemEntity> multiItemEntityList, OnNativeADClickListener onNativeADClickListener) {
        super(activity, multiItemEntityList, onNativeADClickListener, true);
    }

    @Override
    public void addItemTypes() {
        addItemType(NativeADConst.TYPE_DATA, R.layout.adp_picture_joke);
        addItemType(NativeADConst.TYPE_GDT, R.layout.adp_ad_gdt_picture);
        addItemType(NativeADConst.TYPE_NONE, R.layout.adp_picture_joke);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case NativeADConst.TYPE_DATA:
                loadDataView(helper, item);
                break;
            case NativeADConst.TYPE_GDT:
                loadGDTView(helper, item);
                break;
            case NativeADConst.TYPE_NONE:
                loadDataView(helper, item);
                break;
        }
    }

    private void loadDataView(BaseViewHolder helper, MultiItemEntity item) {
        RandomBean.ResultBean dataBean = (RandomBean.ResultBean) item;
        SimpleDraweeView mSdvImageView = helper.getView(R.id.sdv_image_view);
        helper.setText(R.id.content, dataBean.content);
        Uri uri = Uri.parse(dataBean.url);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        mSdvImageView.setAspectRatio(1.8f);
//        mSdvImageView.setAspectRatio(1.33f);
        mSdvImageView.setController(controller);
    }

}
