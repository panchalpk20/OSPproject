package com.anngrynerds.ospproject.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    EditText ETmobileNo;
    Button sendOtp;
    Context context;
    TextInputLayout ipl_mobilenumber;
    String phoneNumber;
    String verificationCode;
    Button btn_Login;
    EditText et_Otp;
    FirebaseUser user;
    SharedPreferences prefs;
    TextView pgMsg;
    Dialog dialog;
    private FirebaseAuth mAuth;
    TextInputLayout ipl_otp;
    ImageView verify;

    CardView login1; //mobile number card
    CardView login2; //enter otp card


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = LoginActivity.this;

        login1 = findViewById(R.id.login_card1);
        login2 = findViewById(R.id.login_card2);

        et_Otp = findViewById(R.id.enter_otp_et_otp);
        btn_Login = findViewById(R.id.enter_otp_login);
        ipl_otp = findViewById(R.id.enter_otp_ipl);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(context);
        prefs = this.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);


        ETmobileNo = findViewById(R.id.login_et_mobile_number);
        sendOtp = findViewById(R.id.login_btn_sent_otp);
        ipl_mobilenumber = findViewById(R.id.login_ipl_mobno);
        verify=findViewById(R.id.verifcationdone);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transparent_loading);
        dialog.setCancelable(false);
        pgMsg = dialog.findViewById(R.id.dialog_pgmsg);


        sendOtp.setOnClickListener(v -> {

            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("keyboard: ", e.getMessage() );
            }

            if (ETmobileNo.getText().toString().isEmpty()) {
                ipl_mobilenumber.setError("Mobile number cannot be blank");
            } else if (ETmobileNo.getText().toString().length() != 10) {
                ipl_mobilenumber.setError("Enter 10 digit Mobile Number");
            } else {

                sendOptToNumber("+91" + ETmobileNo.getText().toString());

            }

        });

        btn_Login.setOnClickListener(v -> {

            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }

            String otp = et_Otp.getText().toString();
            if (otp.isEmpty()) {
                ipl_otp.setError("OTP cannot be blank");
                ipl_otp.requestFocus();
            } else if (otp.length() != 6) {
                ipl_otp.setError("Enter 6 Digits");
                ipl_otp.requestFocus();
            } else {
                verifyOtp(otp);
            }

        });
    }


    private void sendOptToNumber(String phoneNumber) {

        showpg();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Toast.makeText(context, "Verification Done", Toast.LENGTH_SHORT).show();

                                //Mobile number is automatically verified
                                mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        closepg();

                                        if(task.isSuccessful()){
                                            user = Objects.requireNonNull(task.getResult()).getUser();
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("id", user.getPhoneNumber());
                                            editor.apply();

                                            Intent i = new Intent(context, ProfileFill.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }else{
                                            Toast.makeText(context,
                                                    "Error: "+task.getException().getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                Toast.makeText(context, "OTP sent", Toast.LENGTH_SHORT).show();

                                showOTPcard();

                                closepg();


                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                closepg();

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                                    Toast.makeText(context, "Error; " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("LOGIN1: ", e.getMessage());

                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    Toast.makeText(context, "SMS Limit reached", Toast.LENGTH_LONG).show();
                                    Log.e("LOGIN1: ", e.getMessage());

                                }
                            }
                        })// OnVerificationStateChangedCallbacks
                        .build();


        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void showOTPcard() {
        login1.setVisibility(View.GONE);
        login2.setVisibility(View.VISIBLE);
    }


    private void verifyOtp(String otp) {

        showpg();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Log.e("signCredential:success", "");
                            user = Objects.requireNonNull(task.getResult()).getUser();
                            verify.setVisibility(View.VISIBLE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("id", user.getPhoneNumber());
                            editor.apply();

                            closepg();

                            Intent i = new Intent(context, ProfileFill.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        } else {
                            closepg();
//                            Log.e("signCredential:failure", Objects.requireNonNull(task.getException()).getMessage());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                ipl_otp.setError("Incorrect OTP");
                                et_Otp.setText("");
                                et_Otp.requestFocus();

                            }
                        }
                    }
                });

    }


    private void closepg(){
        dialog.dismiss();
    }
    private void showpg(){
        pgMsg.setText(R.string.sendotp);
        dialog.show();
    }

}