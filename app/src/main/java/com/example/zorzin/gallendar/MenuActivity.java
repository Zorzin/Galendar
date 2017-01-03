package com.example.zorzin.gallendar;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MenuActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount acct;
    public final static String ID = "";
    public final static String NAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Intent intent = getIntent();
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);

        if (result!=null)
        {
            acct = result.getSignInAccount();
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        //String personName = acct.getDisplayName();
        //String personGivenName = acct.getGivenName();
        //String personFamilyName = acct.getFamilyName();
        //String personEmail = acct.getEmail();
        //String personId = acct.getId();
        //Uri personPhoto = acct.getPhotoUrl();
    }

    public void onShow(View view) {
        Intent intent = new Intent(this,EventListActivity.class);
        intent.putExtra(NAME,acct.getEmail());
        startActivity(intent);
    }

    public void onLogout(View view) {
        Intent intent = new Intent(this,MainPage.class);
        intent.putExtra(ID,acct.getId());
        signOut();
        startActivity(intent);
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this,AddActivity.class);
        intent.putExtra(NAME,acct.getEmail());
        startActivity(intent);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }
}
