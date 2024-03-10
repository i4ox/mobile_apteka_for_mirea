package ru.artlkv.aptekaformirea.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import ru.artlkv.aptekaformirea.SettingsManager;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsManager.getInstance(this);
        if (!SettingsManager.getInstance().getBoolean(SettingsManager.Key.HAS_INTRO_SHOWN, false)) {
            Intent intro = new Intent(this, IntroActivity.class);
            startActivity(intro);
            finish();
            return;
        }

        if (FirebaseAuth.getInstance(FirebaseApp.getInstance()).getCurrentUser() == null) {
            startActivity(LoginActivity.getInstance(this));
            finish();
            return;
        } else if (!SettingsManager.getInstance().getBoolean(SettingsManager.Key.IS_USER_DETAILS_PRESENT, false)) {
            startActivity(OrderListActivity.getInstance(this));
            finish();
        }
    }

    public static Intent getInstance(Context ctx) {
        return new Intent(ctx, LauncherActivity.class);
    }
}