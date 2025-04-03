package com.btl.login.entities;

import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

public abstract class BaseInformation extends BaseProperties {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    private String dateOfBirth;

    @ColumnInfo(defaultValue = "true")
    private boolean gender;

    @NotNull
    private String email;
    private String phoneNumber;
    private String address;

    protected BaseInformation() {
        firstName = "";
        lastName = "";
    }

    public BaseInformation(@NotNull String firstName, @NotNull String lastName, String dateOfBirth,
                           boolean gender, @NotNull String email, String phoneNumber, String address) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public BaseInformation(@NotNull String firstName, @NotNull String lastName, @NotNull String email ) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public @NotNull String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull String firstName) {
        this.firstName = firstName;
    }

    public @NotNull String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
