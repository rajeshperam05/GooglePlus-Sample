package com.rajesh.gplussampleapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "MainActivity";
    public static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    private Button singOutButton;
    private SignInButton signInButton;

    private TextView tvName;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization for default sign
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN), new Scope(Scopes.PLUS_ME))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        singOutButton = (Button) findViewById(R.id.sing_out_button);
        singOutButton.setOnClickListener(this);

        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.sign_in_button:

                // Signin to google account and get user details
                signIn();

                break;

            case R.id.sing_out_button:

                // Signout from google account
                signOut();

                break;
        }
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            // If login success get user information.
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.e(TAG, "handleSignInResult " + result.isSuccess());

        if (result.isSuccess()){ // Login success

            // Hide signin button and show signout button
            singOutButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);

            // Get user details
            GoogleSignInAccount account = result.getSignInAccount();

            tvName.setText(account.getDisplayName());
            tvEmail.setText(account.getEmail());

        }else {  // Login fails

            Log.e(TAG, "Sign out");

        }

    }

    private void signOut(){

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        Log.e(TAG, "Sign out " + status.getStatusMessage());

                        // If Logout success show signIn button and hide signout button
                        signInButton.setVisibility(View.VISIBLE);
                        singOutButton.setVisibility(View.GONE);

                        tvName.setText("");
                        tvEmail.setText("");


                    }
                }
        );

    }
}
