package com.btl.login.userViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private final MutableLiveData<String> userRole = new MutableLiveData<>(null);

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn.setValue(loggedIn);
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public LiveData<String> getUserRole() {
        return userRole;
    }

    public void setUserRole(String role) {
        userRole.setValue(role);
    }
}
