package com.example.zorzin.gallendar;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public class MenuActivity extends AppCompatActivity {

    private GoogleSignInAccount acct;
    public final static String ID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
         acct = result.getSignInAccount();
        //String personName = acct.getDisplayName();
        //String personGivenName = acct.getGivenName();
        //String personFamilyName = acct.getFamilyName();
        //String personEmail = acct.getEmail();
        //String personId = acct.getId();
        //Uri personPhoto = acct.getPhotoUrl();
    }

    public void onShow(View view) {
        Intent intent = new Intent(this,ShowActivity.class);
        intent.putExtra(ID,acct.getId());
        startActivity(intent);
    }

    public void onLogout(View view) {
        Intent intent = new Intent(this,MainPage.class);
        intent.putExtra(ID,acct.getId());
        startActivity(intent);
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this,AddActivity.class);
        intent.putExtra(ID,acct.getId());
        startActivity(intent);
    }
}
