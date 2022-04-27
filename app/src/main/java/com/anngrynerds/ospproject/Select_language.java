package com.anngrynerds.ospproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.home.ProfileFragment;
import com.anngrynerds.ospproject.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class Select_language extends AppCompatActivity  {
    Button eg,hi,mr;
    TextView engg,hii;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        getWindow().setFlags(
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW,
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        eg=findViewById(R.id.Btenglish);
        hi=findViewById(R.id.btHindi);
        mr=findViewById(R.id.Btmarathi);


        changeLang();





        }
        boolean back=false;
@Override
public void onBackPressed(){
        super.onBackPressed();
    startActivity(new Intent(Select_language.this, HomeActivity.class));

}


    private void changeLang(){
        eg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                recreate();
                if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    startActivity(new Intent(Select_language.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(Select_language.this, LoginActivity.class));
                }

            }
        });
        hi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("hi");
                recreate();
                if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    startActivity(new Intent(Select_language.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(Select_language.this, LoginActivity.class));
                }
            }
        });
        mr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("mr");
                recreate();
                if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    startActivity(new Intent(Select_language.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(Select_language.this, LoginActivity.class));
                }
            }
        });

    }

    private void setLocale(String language) {
        Locale local=new Locale(language);
        Locale.setDefault(local);

        Configuration configuration=new Configuration();
        configuration.locale=local;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor=getSharedPreferences(Constantss.sharedPrefID,MODE_PRIVATE).edit();
        editor.putString("app_lang",language);
        editor.apply();
    }
    private void loadLanguage(){
        SharedPreferences preferences=getSharedPreferences(Constantss.sharedPrefID,MODE_PRIVATE);
        String lang=preferences.getString("app_lang","");
        setLocale(lang);

    }


       /* private void changeLanguage() {
        final String languages[]={"English","Hindi","Marathi"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreen.this);
        alertDialog.setTitle("Choose Language");

            alertDialog.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    setLocale("en");
                    recreate();
                } else if(which==1){
                    setLocale("hi");
                    recreate();
                } else if(which==2){
                    setLocale("mr");
                    recreate();
                }
            }
        });
        alertDialog.create();
        alertDialog.show();
    }
    private void setLocale(String language) {
        Locale local=new Locale(language);
        Locale.setDefault(local);

        Configuration configuration=new Configuration();
        configuration.locale=local;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("app_lang",language);
        editor.apply();
    }
    private void loadLanguage(){
        SharedPreferences preferences=getSharedPreferences("Settings",MODE_PRIVATE);
        String lang=preferences.getString("app_lang","");
        setLocale(lang);

    }*/

}