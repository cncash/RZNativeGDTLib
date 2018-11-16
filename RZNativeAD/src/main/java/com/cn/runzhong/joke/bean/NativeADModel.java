package com.cn.runzhong.joke.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cn.runzhong.joke.common.NativeADConst;
import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * Created by CN.
 */

public class NativeADModel implements MultiItemEntity {
    private NativeExpressADView nativeExpressADViewGDT;
    private NativeADChannel nativeADChannel;

    public NativeADModel(NativeExpressADView nativeExpressADViewGDT, NativeADChannel nativeADChannel) {
        this.nativeExpressADViewGDT = nativeExpressADViewGDT;
        this.nativeADChannel = nativeADChannel;
    }

    @Override
    public int getItemType() {
        switch (nativeADChannel) {
            case GDT:
                return NativeADConst.TYPE_GDT;
        }
        return NativeADConst.TYPE_NONE;
    }

    public NativeExpressADView getNativeExpressADViewGDT() {
        return nativeExpressADViewGDT;
    }

    public void setNativeExpressADViewGDT(NativeExpressADView nativeExpressADViewGDT) {
        this.nativeExpressADViewGDT = nativeExpressADViewGDT;
    }

    public NativeADChannel getNativeADChannel() {
        return nativeADChannel;
    }

    public void setNativeADChannel(NativeADChannel nativeADChannel) {
        this.nativeADChannel = nativeADChannel;
    }
}
