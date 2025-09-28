package com.schema.app;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Kakao SDK 초기화
        // Kakao SDK 초기화 (KAKAO_NATIVE_APP_KEY가 주석 처리되어 임시 비활성화)
        // if (BuildConfig.KAKAO_NATIVE_APP_KEY != null && !BuildConfig.KAKAO_NATIVE_APP_KEY.isEmpty()) {
        //     KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY);
        // }
    }
}
