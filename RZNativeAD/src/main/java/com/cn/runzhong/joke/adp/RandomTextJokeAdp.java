package com.cn.runzhong.joke.adp;

import android.app.Activity;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cn.runzhong.joke.R;
import com.cn.runzhong.joke.bean.RandomBean;
import com.cn.runzhong.joke.common.NativeADConst;
import com.cn.runzhong.joke.common.OnNativeADClickListener;

import java.util.List;


public class RandomTextJokeAdp extends BaseJokeAdp {

    public RandomTextJokeAdp(final Activity activity, final List<MultiItemEntity> multiItemEntityList, OnNativeADClickListener onNativeADClickListener) {
        super(activity, multiItemEntityList, onNativeADClickListener,false);
    }


    @Override
    public void addItemTypes() {
        addItemType(NativeADConst.TYPE_DATA, R.layout.adp_txt_joke);
        addItemType(NativeADConst.TYPE_GDT, R.layout.adp_ad_gdt_text);
        addItemType(NativeADConst.TYPE_NONE, R.layout.adp_txt_joke);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case NativeADConst.TYPE_DATA:
                loadDataView(helper, item);
                break;
            case NativeADConst.TYPE_GDT:
                loadGDTView(helper,item);
                break;
            case NativeADConst.TYPE_NONE:
                loadDataView(helper,item);
                break;
        }
    }
    private void loadDataView(BaseViewHolder helper, MultiItemEntity item) {
        helper.setText(R.id.tv_content,((RandomBean.ResultBean)item).content);
    }
}
