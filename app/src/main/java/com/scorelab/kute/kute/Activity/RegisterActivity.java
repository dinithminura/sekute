package com.scorelab.kute.kute.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.R;

import java.io.IOException;

/**
 * Created by nrv on 2/4/17.
 */

public class RegisterActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /* TextView that is used to display information about the logged in user */
    private TextView mLoggedInStatusTextView;


    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;

    /* A reference to the Firebase */
    private DatabaseReference mFirebaseRef;

    /* Data from the authenticated user */
    private FirebaseAuth mAuthData;

    /* Listener for Firebase session changes */
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve all issues preventing sign-in
     * without waiting. */
    private boolean mGoogleLoginClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can resolve them when the user clicks
     * sign-in. */
    private ConnectionResult mGoogleConnectionResult;

    /* The login button for Google */
    private SignInButton mGoogleLoginButton;


    private static final String TAG = RegisterActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.testreg);



        /* *************************************
         *               GOOGLE                *
         ***************************************/
        /* Load the Google login button */
            mGoogleLoginButton = (SignInButton) findViewById(R.id.login_with_google);
            mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoogleLoginClicked = true;
                    if (!mGoogleApiClient.isConnecting()) {
                        if (mGoogleConnectionResult != null) {
                            resolveSignInError();
                        } else if (mGoogleApiClient.isConnected()) {
                            getGoogleOAuthTokenAndLogin();
                        } else {
                    /* connect API now */
                            Log.d(TAG, "Trying to connect to Google API");
                            mGoogleApiClient.connect();
                        }
                    }
                }
            });
        /* Setup the Google API object to allow Google+ logins */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();

         /* *************************************
         *               GENERAL               *
         ***************************************/
            mLoggedInStatusTextView = (TextView) findViewById(R.id.login_status);

        /* Create the Firebase ref that is used for all authentication with Firebase */
            mFirebaseRef = FirebaseDatabase.getInstance().getReference();

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
            mAuthProgressDialog = new ProgressDialog(this);
            mAuthProgressDialog.setTitle("Loading");
            mAuthProgressDialog.setMessage("Authenticating with Firebase...");
            mAuthProgressDialog.setCancelable(false);
            mAuthProgressDialog.show();

            mAuthStateListener=new FirebaseAuth.AuthStateListener(){

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    mAuthProgressDialog.hide();
                    setAuthenticatedUser(firebaseAuth);

                }
            };

        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
            mFirebaseRef.addAuthStateListener(mAuthStateListener);
        }


        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"-- "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getGoogleOAuthTokenAndLogin();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!mGoogleIntentInProgress) {
            /* Store the ConnectionResult so that we can use it later when the user clicks on the Google+ login button */
            mGoogleConnectionResult = result;

            if (mGoogleLoginClicked) {
                /* The user has already clicked login so we attempt to resolve all errors until the user is signed in,
                 * or they cancel. */
                resolveSignInError();
            } else {
                Log.e(TAG, result.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_LOGIN) {
            /* This was a request by the Google API */
            if (resultCode != RESULT_OK) {
                mGoogleLoginClicked = false;
            }
            mGoogleIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }

        }
    }

    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();

            Toast.makeText(getApplicationContext()," "+firebaseError.getDetails(),Toast.LENGTH_LONG).show();
            showErrorDialog(firebaseError.toString());
        }
    }


    /* ************************************
     *              GOOGLE                *
     **************************************
     */
    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mGoogleConnectionResult.hasResolution()) {
            try {
                mGoogleIntentInProgress = true;
                mGoogleConnectionResult.startResolutionForResult(this, RC_GOOGLE_LOGIN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mGoogleIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getGoogleOAuthTokenAndLogin() {
        mAuthProgressDialog.show();
        /* Get OAuth token in Background */
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                    if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return "";
                    }
                    token = GoogleAuthUtil.getToken(RegisterActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
                } catch (IOException transientEx) {
                    /* Network or server error */
                    Log.e(TAG, "Error authenticating with Google: " + transientEx);
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                Toast.makeText(getApplicationContext(),"Token:- "+token,Toast.LENGTH_LONG).show();
                mGoogleLoginClicked = false;
                if (token != null) {
                    /* Successfully got OAuth token, now login with Google */
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                    mAuth.signInWithCredential(credential);

                } else if (errorMessage != null) {
                    mAuthProgressDialog.hide();
                    showErrorDialog(errorMessage);
                }
            }
        };
        task.execute();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void setAuthenticatedUser(FirebaseAuth authData) {
        if (authData != null) {
            /* Hide all the login buttons */

            mGoogleLoginButton.setVisibility(View.GONE);
            FirebaseUser user = authData.getCurrentUser();

            /* show a provider specific status text */
            String name = null;

            if (user.isAnonymous()) {
                name = (String) user.getDisplayName();
            } else {
                Log.e(TAG, "Invalid provider: " + "authData.getProvider()");
            }
            if (name != null) {

            }
        } else {
            /* No authenticated user show all the login buttons */

            mGoogleLoginButton.setVisibility(View.VISIBLE);

        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
        supportInvalidateOptionsMenu();
    }

}
