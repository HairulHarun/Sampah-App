package com.gorontalo.chair.sampah_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.gorontalo.chair.sampah_app.LoginActivity;
import com.gorontalo.chair.sampah_app.MainActivity;

public class SessionAdapter {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Sesi";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_SHOW = "IsShow";
    public static final String KEY_ID = "id";
    public static final String KEY_SOPIR = "sopir";
    public static final String KEY_KONDEKTUR1 = "kondektur1";
    public static final String KEY_KONDEKTUR2 = "kondektur2";
    public static final String KEY_KENDERAAN = "kenderaan";
    public static final String KEY_NOPOLISI = "nopolisi";
    public static final String KEY_HP = "hp";
    public static final String KEY_PHOTOSOPIR = "photosopir";
    public static final String KEY_PHOTOKONDEKTUR1 = "photokondektur1";
    public static final String KEY_PHOTOKONDEKTUR2 = "photokondektur2";
    public static final String KEY_PHOTOKENDERAAN = "photokenderaan";
    public static final String KEY_TANKI = "tanki";

    public SessionAdapter(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String id,
                                   String sopir,
                                   String kondektur1,
                                   String kondektur2,
                                   String kenderaan,
                                   String nopolisi,
                                   String hp,
                                   String photosopir,
                                   String photokondektur1,
                                   String photokondektur2,
                                   String photokenderaan,
                                   String tanki){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_SOPIR, sopir);
        editor.putString(KEY_KONDEKTUR1, kondektur1);
        editor.putString(KEY_KONDEKTUR2, kondektur2);
        editor.putString(KEY_KENDERAAN, kenderaan);
        editor.putString(KEY_NOPOLISI, nopolisi);
        editor.putString(KEY_HP, hp);
        editor.putString(KEY_PHOTOSOPIR, photosopir);
        editor.putString(KEY_PHOTOKONDEKTUR1, photokondektur1);
        editor.putString(KEY_PHOTOKONDEKTUR2, photokondektur2);
        editor.putString(KEY_PHOTOKENDERAAN, photokenderaan);
        editor.putString(KEY_TANKI, tanki);
        editor.commit();
    }

    public void createDialogSession(){
        editor.putBoolean(IS_SHOW, true);
        editor.commit();
    }

    public void checkLoginMain(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void checkLogin(){
        if(this.isLoggedIn()){
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public String getId(){
        String user = pref.getString(KEY_ID, null);
        return user;
    }

    public String getSopir(){
        String user = pref.getString(KEY_SOPIR, null);
        return user;
    }

    public String getKondektur1(){
        String user = pref.getString(KEY_KONDEKTUR1, null);
        return user;
    }

    public String getKondektur2(){
        String user = pref.getString(KEY_KONDEKTUR2, null);
        return user;
    }

    public String getKenderaan(){
        String user = pref.getString(KEY_KENDERAAN, null);
        return user;
    }

    public String getNoPolisi(){
        String user = pref.getString(KEY_NOPOLISI, null);
        return user;
    }

    public String getPhotoKenderaan(){
        String user = pref.getString(KEY_PHOTOKENDERAAN, null);
        return user;
    }

    public String getPhotoSopir(){
        String user = pref.getString(KEY_PHOTOSOPIR, null);
        return user;
    }

    public String getPhotoKondektur1(){
        String user = pref.getString(KEY_PHOTOKONDEKTUR1, null);
        return user;
    }

    public String getPhotoKondektur2(){
        String user = pref.getString(KEY_PHOTOKONDEKTUR2, null);
        return user;
    }

    public String getHp(){
        String user = pref.getString(KEY_HP, null);
        return user;
    }

    public String getTanki(){
        String user = pref.getString(KEY_TANKI, null);
        return user;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isShow(){
        return pref.getBoolean(IS_SHOW, false);
    }
}
