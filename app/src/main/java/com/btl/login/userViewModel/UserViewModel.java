package com.btl.login.userViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn.setValue(loggedIn);
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }
}
