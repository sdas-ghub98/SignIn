package com.example.sourishdas.signin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener
{
    private SignInButton SignIn;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    DataBaseHelper myDb;
    private FrameLayout prof_section;
    //private Button signout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DataBaseHelper(this);
        SignIn = (SignInButton)findViewById(R.id.bn_login);
        SignIn.setSize(SignInButton.SIZE_STANDARD);
        SignIn.setOnClickListener(this);
        prof_section = (FrameLayout)findViewById(R.id.content_frame);
        //signout = (Button) findViewById(R.id.sout);
        //signout.setOnClickListener(this);
        prof_section.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId()==R.id.bn_login)
            signIn();
        else
            signOut();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    private void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,RC_SIGN_IN);
    }
    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>()
        {
            @Override
            public void onResult(@NonNull Status status)
            {
                updateUI(false);
            }
        });
    }
    private void handleResult (GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
            SharedPreferences signin = getSharedPreferences("1", Context.MODE_PRIVATE);
            String name = account.getDisplayName();
            String email = account.getEmail();
            Boolean result1 = myDb.insertData(name,email);
            if(result1 == true)
                Toast.makeText(this,"Sign In successful",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,"Sign In successful but data not recorded",Toast.LENGTH_SHORT).show();
            updateUI(true);
        }
        else
        {
            Toast.makeText(this,"Sign In failed!!!",Toast.LENGTH_SHORT).show();
        }

    }
    private void updateUI(boolean isLogin)
    {
        if(isLogin)
        {
            SignIn.setVisibility(View.GONE);
            prof_section.setVisibility(View.VISIBLE);
        }
        else
        {
            SignIn.setVisibility(View.VISIBLE);
            prof_section.setVisibility(View.INVISIBLE);
        }
    }

}
