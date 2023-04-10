package com.toygoon.safeshare.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String userId;
    private String name;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    String getUserId() {
        return this.userId;
    }

    String getName() {
        return this.name;
    }
}