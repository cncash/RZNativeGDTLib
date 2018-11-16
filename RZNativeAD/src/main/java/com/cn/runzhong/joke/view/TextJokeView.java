package com.cn.runzhong.joke.view;

import android.content.Context;
import android.util.AttributeSet;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cn.runzhong.joke.adp.RandomTextJokeAdp;
import com.cn.runzhong.joke.bean.NativeADModel;
import com.cn.runzhong.joke.common.NativeADConst;
import com.cn.runzhong.joke.common.OnNativeADClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CN.
 */

public class TextJokeView extends BaseJokeView implements OnNativeADClickListener {
    private RandomTextJokeAdp textJokeAdp;
    public TextJokeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextJokeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("key", NativeADConst.APPKEY_JOKE);
        return map;
    }

    @Override
    public String getRequestTag() {
        return "TextJokeView";
    }

    @Override
    public void setAdapterData(List<MultiItemEntity> multiItemEntityList) {
        if(textJokeAdp == null){
            textJokeAdp = new RandomTextJokeAdp(activity,multiItemEntityList,this);
            recyclerView.setIAdapter(textJokeAdp);
        }else{
            textJokeAdp.notifyDataSetChanged();
        }
    }

    @Override
    public void onNativeADClick(NativeADModel nativeADModel, int position) {

    }
}
