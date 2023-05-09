package com.toygoon.safeshare.ui.risk;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RiskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RiskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is risk fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}