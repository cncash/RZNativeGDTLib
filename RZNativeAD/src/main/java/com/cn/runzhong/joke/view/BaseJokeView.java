package com.cn.runzhong.joke.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cn.runzhong.joke.R;
import com.cn.runzhong.joke.bean.NativeADChannel;
import com.cn.runzhong.joke.bean.NativeADModel;
import com.cn.runzhong.joke.bean.RandomBean;
import com.cn.runzhong.joke.common.HttpRequest;
import com.cn.runzhong.joke.common.NativeADConst;
import com.cn.runzhong.joke.util.ADIRecyclerViewUtil;
import com.google.gson.Gson;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.runzhong.technology.RZManager;
import com.runzhong.technology.util.RZADPolicy;
import com.runzhong.technology.util.RZConst;
import com.runzhong.technology.util.RZUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by CN.
 */

public abstract class BaseJokeView extends RelativeLayout implements OnRefreshListener, OnLoadMoreListener,
        RZADPolicy.OnADLoadListener {
    protected IRecyclerView recyclerView;
    protected Activity activity;

    private String posIdGDT;
    private String appIdGDT;
    private int adPosition;
    private List<MultiItemEntity> dataList;
    //    protected Map<NativeExpressADView, Integer> mAdViewPositionMap;
    private List<NativeExpressADView> currentAdListGDT;
    private String adType;
    private RZADPolicy rzadPolicy;
    private OnLoadListener onLoadListener;
    private boolean isLoadMore;
	protected String keyJoke;
//    public BaseJokeView(Context context) {
//        super(context);
//        initView();
//    }

    public BaseJokeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public BaseJokeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public void loadData(Activity activity) {
        this.activity = activity;
        onRefresh();
    }

    private void initView(AttributeSet attrs) {
        initParams(attrs);
        rzadPolicy = new RZADPolicy(getContext(), this);
//        mAdViewPositionMap = new HashMap<>();
        dataList = new ArrayList<>();
        inflate(getContext(), R.layout.layout_joke, this);
        recyclerView = findViewById(R.id.recyclerView);
        ADIRecyclerViewUtil.setVerticalLinearLayoutManager(getContext(), recyclerView);
        recyclerView.setOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
    }

    private void initParams(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.JokeView);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.JokeView_appIdGDT) {
                appIdGDT = typedArray.getString(R.styleable.JokeView_appIdGDT);
            } else if (attr == R.styleable.JokeView_posIdGDT) {
                posIdGDT = typedArray.getString(R.styleable.JokeView_posIdGDT);
            } else if (attr == R.styleable.JokeView_adPosition) {
                adPosition = typedArray.getInt(R.styleable.JokeView_adPosition, 2);
            } else if (attr == R.styleable.JokeView_key) {
                keyJoke = typedArray.getString(R.styleable.JokeView_key);
            }

        }
        typedArray.recycle();
    }

    @Override
    public void onRefresh() {
        isLoadMore = false;
        if (dataList == null) {
            dataList = new ArrayList<>();
        } else {
            dataList.clear();
        }
        setAdapterData(dataList);
        rzadPolicy.loadADPolicy();
    }

    @Override
    public void onLoadMore() {
        isLoadMore = true;
        rzadPolicy.loadADPolicy();
    }

    @Override
    public void onLoadGDTAD() {
        RZUtil.log("加载广点通原生广告！");
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
        new NativeExpressAD(getContext(), adSize, appIdGDT, posIdGDT, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(AdError adError) {
                RZUtil.log("onNoAD");
                RZManager.getInstance().onUMEvent(RZConst.AD_THIRD_PLATFORM_ERROR_EVENT_ID,
                        RZManager.getInstance().getErrorEventMap(RZConst.AD_PLATFORM_GDT,
                                String.valueOf(adError.getErrorCode()), adError.getErrorMsg()));
                rzadPolicy.fetchNextPlatformAd();
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                if (list == null || list.size() == 0) {
                    rzadPolicy.fetchNextPlatformAd();
                } else {
                    adType = RZConst.AD_PLATFORM_GDT;
                    currentAdListGDT = list;
                    if (rzadPolicy.getOnADLoadListener() != null) {
                        rzadPolicy.getOnADLoadListener().onLoadData();
                    }
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView adView) {
            }

            @Override
            public void onRenderSuccess(NativeExpressADView adView) {
                RZUtil.log("onRenderSuccess");
            }

            @Override
            public void onADExposure(NativeExpressADView adView) {
                RZUtil.log("onADExposure");
            }

            @Override
            public void onADClicked(NativeExpressADView adView) {
                RZUtil.log("onADClicked");
            }

            @Override
            public void onADClosed(NativeExpressADView adView) {
                RZUtil.log("onADClosed");
                // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，释放资源，不可以再用来展示。
            }

            @Override
            public void onADLeftApplication(NativeExpressADView adView) {
                RZUtil.log("onADLeftApplication");
            }

            @Override
            public void onADOpenOverlay(NativeExpressADView adView) {
                RZUtil.log("onADOpenOverlay");
            }

            @Override
            public void onADCloseOverlay(NativeExpressADView adView) {
                RZUtil.log("onADCloseOverlay");
            }
        }).loadAD(1);
    }

    @Override
    public void onLoadData() {
        HttpRequest.requestNoNeedAnalyze(NativeADConst.BASE_URL_JOKE_RANDOM, getParams(), getRequestTag(), new HttpRequest.HttpResponseSimpleListener() {

            @Override
            public void onNetError(String url, int errorCode) {
                Toast.makeText(getContext(),getContext().getString(R.string.error_joke_load),Toast.LENGTH_SHORT).show();
                if (onLoadListener != null) {
                    onLoadListener.onError(isLoadMore);
                }
                ADIRecyclerViewUtil.judgePullRefreshStatus(recyclerView, Integer.MAX_VALUE);
            }

            @Override
            public void onSuccess(String url, String responseData) {
                RandomBean mRandomBean = new Gson().fromJson(responseData, RandomBean.class);
                if (mRandomBean != null && mRandomBean.result != null && mRandomBean.result.size() > 0) {
                    List<RandomBean.ResultBean> resultBeanList = mRandomBean.result;
                    if (resultBeanList.size() >= adPosition) {
                        for (int i = 0; i < resultBeanList.size(); i++) {
                            if (i == adPosition) {
                                loadAD();
                            }
                            dataList.add(resultBeanList.get(i));
                        }
                    } else if (resultBeanList.size() == 1) {
                        dataList.addAll(resultBeanList);
                        loadAD();
                    }
                } else {
                    loadAD();
                }
                if (onLoadListener != null) {
                    onLoadListener.onSuccess();
                }
                setAdapterData(dataList);
                ADIRecyclerViewUtil.judgePullRefreshStatus(recyclerView, Integer.MAX_VALUE);
            }
        });
    }

    private void loadAD() {
        if (adType != null) {
            switch (adType) {
                case RZConst.AD_PLATFORM_GDT:
                    loadGDTAD();
                    break;
            }
        }
    }

    private void loadGDTAD() {
        if (currentAdListGDT != null && currentAdListGDT.size() > 0) {
            NativeExpressADView nativeExpressADView = currentAdListGDT.get(0);
            NativeADModel nativeADModel = new NativeADModel(nativeExpressADView, NativeADChannel.GDT);
            dataList.add(nativeADModel);
        }
    }

    public abstract Map<String, String> getParams();

    public abstract String getRequestTag();

    public abstract void setAdapterData(List<MultiItemEntity> multiItemEntityList);

    public OnLoadListener getOnLoadListener() {
        return onLoadListener;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public interface OnLoadListener {
        void onSuccess();

        void onError(boolean isLoadMore);
    }
}
