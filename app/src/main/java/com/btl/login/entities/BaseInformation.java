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
    private String email;
    private String phoneNumber;
    private String address;

    protected BaseInformation() {
        firstName = "";
        lastName = "";
    }

    public BaseInformation(@NotNull String firstName, @NotNull String lastName, String dateOfBirth,
                           boolean gender, String email, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public BaseInformation(@NotNull String firstName, @NotNull String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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
