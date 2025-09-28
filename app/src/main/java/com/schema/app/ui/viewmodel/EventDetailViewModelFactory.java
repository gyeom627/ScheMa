package com.schema.app.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * EventDetailViewModel에 파라미터(eventId)를 전달하기 위한 팩토리 클래스입니다.
 */
public class EventDetailViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final int eventId;

    public EventDetailViewModelFactory(Application application, int eventId) {
        this.application = application;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // 요청된 ViewModel 클래스가 EventDetailViewModel 클래스이거나 그 하위 클래스인지 확인합니다.
        if (modelClass.isAssignableFrom(EventDetailViewModel.class)) {
            try {
                // EventDetailViewModel의 생성자를 호출하여 인스턴스를 생성하고 반환합니다.
                return (T) new EventDetailViewModel(application, eventId);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        // 모르는 ViewModel 클래스가 요청되면 예외를 발생시킵니다.
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}