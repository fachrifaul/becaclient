package id.web.gocak.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import id.web.gocak.util.Const;

public class UserSessionManager {

    // Sharedpref file name
    private static final String PREFER_NAME = "GocakClient";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "isUserLoggedIn";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    private String idUser, username, telp, email;
    private boolean status;

    // Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    //Create login session
    public void createUserIdSession(boolean status, String idUser, String username, String telp, String email) {

        //public void createUserLoginSession(String StoreUserID, String email) {
        Log.v("kumaha ", idUser + "=" + username);

        // Storing status in pref
        editor.putString(Const.id, idUser);
        editor.putString(Const.username, username);
        editor.putString(Const.TELP, telp);
        editor.putString(Const.EMAIL, email);
        editor.putBoolean(IS_USER_LOGIN, status);

        this.idUser = idUser;
        this.username = username;
        this.status = status;
        this.telp = telp;
        this.email = email;

        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     */
    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            return true;
        } else {
            return false;
        }
    }

    public void userLogoutUser() {
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, status);
    }

    public String getIdUser() {
        return pref.getString(Const.id, idUser);
    }

    public String getUsername() {
        return pref.getString(Const.username, username);
    }

    public String getTelp() {
        return pref.getString(Const.TELP, telp);
    }

    public String getEmail() {
        return pref.getString(Const.EMAIL, email);
    }

}