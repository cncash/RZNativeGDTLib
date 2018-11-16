package com.cn.runzhong.joke.common;

import android.os.Handler;

import com.google.gson.Gson;
import com.runzhong.technology.retrofit.ADRetrofitManager;
import com.runzhong.technology.util.RZUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by CN on 2016/9/23.
 */

public class HttpRequest {
    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_EXCEPTION = 1000;
    public static final int ERROR_NET_FAILED = 1001;
    public static final String RESPONSE_DATA_KEY = "result";
    private static Handler handler = new Handler();
    public static void get(String url, Map<String, String> params, String tag, final HttpResponseListener
            listener){
        get(url,params,tag,listener,true);
    }
    public static void get(String url, Map<String, String> params, String tag, final HttpResponseListener
            listener,boolean isNeedAnalyze){
        request(url,params,tag,listener,false,isNeedAnalyze);
    }
    public static void request(String url, Map<String, String> params, String tag, final HttpResponseListener
            listener){
        request(url,params,tag,listener,true);
    }
    public static void requestNoNeedAnalyze(String url, Map<String, String> params, String tag, final
    HttpResponseListener
            listener){
        request(url,params,tag,listener,true,false);
    }
    public static void request(final String url, Map<String, String> params, String tag, final HttpResponseListener
            listener, final boolean isShowErrorToast) {
        request(url,params,tag,listener,true,true);
    }

    /**
     *
     * @param url 地址
     * @param params 参数
     * @param tag 标签
     * @param listener 监听事件
     * @param isPostMethod 是否用post方法提交，否则用get
     * @param isNeedAnalyze 是否需要解析，否则直接将返回值返回，不再进行解析处理
     */
    private static void request(final String url, Map<String, String> params, String tag, final HttpResponseListener
            listener,final boolean isPostMethod,final boolean isNeedAnalyze) {
        try {
            if (url == null) {
                return;
            }
            FormBody.Builder builder = new FormBody.Builder();
            if(isPostMethod) {
                if (params != null) {
                    Iterator<String> iterator = params.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = params.get(key);
                        if (value != null) {
                            builder.add(key, value);
                        }
                    }
                }
            }
            RequestBody formBody = builder.build();
            Request.Builder requestBuilder = new Request.Builder();
            Request request = null;
            if(isPostMethod){
                request = requestBuilder.url(url).post(formBody).tag(tag)
                        .build();
            }else{
                request = requestBuilder.url(url+createGetParameters(params)).get().tag(tag).build();
            }
            RZUtil.log("params:"+params);
            RZUtil.log("url:"+url+createGetParameters(params));
            RZUtil.log("requestTag:" + tag);
            Call call =
                    ADRetrofitManager.getInstance().getOkInstance().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    RZUtil.log("请求失败:" + e.getMessage());
                    if (listener != null && !call.isCanceled()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onNetError(url, ERROR_NET_FAILED);
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response.isSuccessful()) {
                            final String result = response.body().string();
                            RZUtil.log("请求成功:" + result);
                            if (listener != null && !call.isCanceled()) {
                                if(!isNeedAnalyze){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(url,result);
                                        }
                                    });
                                    return;
                                }
                                final HttpResponse httpResponse = new Gson().fromJson(result, HttpResponse.class);
                                final JSONObject jsonObjectResult = new JSONObject(result);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (httpResponse.getError_code() == SUCCESS_CODE) {
                                                try{
                                                    jsonObjectResult.get(RESPONSE_DATA_KEY);
                                                }catch (Exception e){
                                                    listener.onSuccess(url, result);
                                                    return;
                                                }
                                                if (jsonObjectResult.get(RESPONSE_DATA_KEY)!=null) {
                                                    Object result = jsonObjectResult.get(RESPONSE_DATA_KEY);
                                                    if(result instanceof JSONArray){
                                                        listener.onSuccess(url, jsonObjectResult.getJSONArray
                                                                (RESPONSE_DATA_KEY).toString());
                                                    }else{
                                                        listener.onSuccess(url, jsonObjectResult.getString(RESPONSE_DATA_KEY));
                                                    }
                                                } else {
                                                    listener.onSuccess(url, result);
                                                }
                                            } else {
                                                listener.onWebServiceError(url, httpResponse.getError_code(), httpResponse
                                                        .getReason(),jsonObjectResult.getString(RESPONSE_DATA_KEY));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            if (listener != null)
                                                listener.onNetError(url, ERROR_EXCEPTION);
                                        }
                                    }
                                });
                            }
                        } else {
                            RZUtil.log("请求失败:" + response);
                            if (listener != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onNetError(url, ERROR_EXCEPTION);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (listener != null && !call.isCanceled()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onNetError(url, ERROR_EXCEPTION);
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onNetError(url, ERROR_EXCEPTION);
                    }
                });
            }
        }
    }
    private static String createGetParameters(Map<String, String> parameter) {
        if(parameter == null){
            parameter = new HashMap<>();
        }
        if (parameter != null) {
            StringBuffer linkParameters = new StringBuffer("?");
            for (Map.Entry<String, String> entry : parameter.entrySet()) {
                try {
                    linkParameters.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return linkParameters.deleteCharAt(linkParameters.length() - 1).toString().trim();
        } else {
            return "";
        }
    }
    public interface HttpResponseListener {
        void onNetError(String url, int errorCode);

        void onSuccess(String url, String responseData);

        void onWebServiceError(String url, int errorCode, String errorMsg, String responseData);
    }
    public abstract static class HttpResponseSimpleListener implements HttpResponseListener{
        @Override
        public void onWebServiceError(String url, int errorCode, String errorMsg, String responseData) {
            onNetError(url,errorCode);
        }
    }
}
