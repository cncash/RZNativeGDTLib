package com.cn.runzhong.joke.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cn.runzhong.joke.common.NativeADConst;

import java.io.Serializable;
import java.util.List;

public class RandomBean implements Serializable {

    public String reason;
    public int error_code;

    public List<ResultBean> result;


    public class ResultBean implements Serializable,MultiItemEntity {
        public String content;
        public String hashId;
        public String unixtime;
        public String url;

        @Override
        public int getItemType() {
            return NativeADConst.TYPE_DATA;
        }
    }
}
