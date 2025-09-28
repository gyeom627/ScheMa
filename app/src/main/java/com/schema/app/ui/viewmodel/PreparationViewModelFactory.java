package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * PreparationViewModel에 파라미터(eventId)를 전달하기 위한 팩토리 클래스입니다.
 */
public class PreparationViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final int eventId;

    public PreparationViewModelFactory(Application application, int eventId) {
        this.application = application;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PreparationViewModel.class)) {
            return (T) new PreparationViewModel(application, eventId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
