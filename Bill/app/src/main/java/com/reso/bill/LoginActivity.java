package com.reso.bill;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import util.Utility;

import static android.view.View.VISIBLE;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mPhoneNumberField, mVerificationField;
    private TextView mCodeNumberField;
    private Button mVerifyButton, mResendButton;
//    private ProgressDialog proDialog;
    private Button mStartButton;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private ConstraintLayout layoutvarification, phoneAuth;

    private static final String TAG = "PhoneAuthActivity";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        mPhoneNumberField = findViewById(R.id.field_phone_number);
        mVerificationField = findViewById(R.id.field_verification_code);
        mCodeNumberField = findViewById(R.id.field_code_number);


        mStartButton = findViewById(R.id.button_start_verification);
        mVerifyButton = findViewById(R.id.button_verify_phone);
        mResendButton = findViewById(R.id.button_resend);
        layoutvarification = findViewById(R.id.layoutvarification);
        phoneAuth = findViewById(R.id.layoutauth);
        //layOutReg=findViewById(R.id.reglayout);

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    phoneAuth.setVisibility(VISIBLE);
                    mStartButton.setVisibility(View.VISIBLE);
                    //layOutReg.setVisibility(View.VISIBLE);
                    layoutvarification.setVisibility(View.GONE);

                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                   /* Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();*/
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
//                proDialog.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                phoneAuth.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.GONE);
                //layOutReg.setVisibility(View.GONE);
                layoutvarification.setVisibility(VISIBLE);

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = task.getResult().getUser();
                    user.getPhoneNumber();
                    Log.d("SSSS######", "Phome " + user.getPhoneNumber());
                    SharedPreferences sp = getSharedPreferences("user", 0);
                    SharedPreferences.Editor edit = sp.edit();

                    edit.putString("userphone", user.getPhoneNumber());
                    edit.commit();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        mVerificationField.setError("Invalid code.");
                    }
                }
            }
        });
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        //proDialog.dismiss();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, VendorRegistration.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                System.out.println("Login button clicked");
//                proDialog = new ProgressDialog(LoginActivity.this);
//                proDialog.setMessage("Please wait. We're logging you in....");
//                proDialog.setCancelable(false);
//                proDialog.show();
                if (Utility.isInternetOn(LoginActivity.this)) {
                    Toast.makeText(this, "Please wait for SMS code", Toast.LENGTH_SHORT).show();
                    phoneAuth.setVisibility(View.INVISIBLE);
                    mStartButton.setVisibility(View.GONE);
                    layoutvarification.setVisibility(view.VISIBLE);

                    if (!validatePhoneNumber()) {
                        phoneAuth.setVisibility(view.VISIBLE);
                        mStartButton.setVisibility(View.VISIBLE);
                        //layOutReg.setVisibility(View.GONE);
                        layoutvarification.setVisibility(view.GONE);
//                        proDialog.dismiss();
                        return;
                    }
                    startPhoneNumberVerification(mCodeNumberField.getText().toString() + mPhoneNumberField.getText().toString());
                } else {
//                    proDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please Check Your Internet Connection.", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;

            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
        }

    }

}

