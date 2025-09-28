package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NavigationViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final int eventId;

    public NavigationViewModelFactory(Application application, int eventId) {
        this.application = application;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NavigationViewModel.class)) {
            return (T) new NavigationViewModel(application, eventId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
