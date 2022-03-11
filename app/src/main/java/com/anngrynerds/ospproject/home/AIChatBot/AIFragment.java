package com.anngrynerds.ospproject.home.AIChatBot;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AIFragment extends Fragment {

    private static final String TAG = "MyActivity";
    private final String BOT_KEY = "bot";
    RecyclerView recyclerView;
    EditText editText;
    ImageButton imageView;
    ArrayList<Chatsmodal> chatsmodalArrayList;
    ChatAdapter chatAdapter;
    Translator translator;
    private Context context;

    public AIFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AIFragment newInstance() {
        /* Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return new AIFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_i, container, false);
        context = view.getContext();

        recyclerView = view.findViewById(R.id.rv_messagaesview);
        editText = view.findViewById(R.id.et_usermsg);
        imageView = view.findViewById(R.id.ib_send);

        chatsmodalArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatsmodalArrayList, context);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatAdapter);

        imageView.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(context, "Please enter your message", Toast.LENGTH_SHORT).show();
                return;
            }
            storeResponse(editText.getText().toString());
            // converter(editText.getText().toString());
            editText.setText("");
        });

        return view;
    }


    private void storeResponse(String message) {
        String USER_KEY = "user";
//        chatsmodalArrayList.add(new Chatsmodal(message, USER_KEY));
//        chatAdapter.notifyDataSetChanged();
        chatAdapter.addItem(new Chatsmodal(message, USER_KEY), 0);
        recyclerView.scrollToPosition(chatsmodalArrayList.size() - 1);

        identifyText(message);
    }

    private void identifyText(String msg) {
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(msg)
                .addOnSuccessListener(
                        languageCode -> {
                            if (languageCode != null) {
                                if (languageCode.equals("und")) {
                                    Log.i(TAG, "Can't identify language.");
                                } else {
                                    Log.i(TAG, "Language: " + languageCode);
                                    //textView.setText(languageCode);
                                    //Toast.makeText(MainActivity.this,languageCode,Toast.LENGTH_SHORT).show();

                                    // inputText.setText(languageCode);
                                    prepareModel(msg, languageCode);


                                }
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Model couldn’t be loaded or other internal error.
                            // ...
                        });
    }
    private void prepareModel(String code, String language) {
        //String l= textView.getText().toString();
        if(TranslateLanguage.fromLanguageTag(language)!=null){

            TranslatorOptions options = new TranslatorOptions.Builder().
                    setSourceLanguage(Objects.requireNonNull(TranslateLanguage.fromLanguageTag(language)))
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
            translator = Translation.getClient(options);

            translator.downloadModelIfNeeded().addOnSuccessListener(unused -> translator.translate(code).addOnSuccessListener(s -> {
                //outputText.setText(s);
                //Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();

                converter(s, language);
                //languagecodeTransfer(s);

            })).addOnFailureListener(e -> {

            });
        }

    }
    private void converter(String message, String lang) {

        // "http://api.brainshop.ai/get?bid=164570&key=w09SsiX1qDYKy4XH&uid=[uid]&msg="+
        //http://api.brainshop.ai/get?bid=164378&key=bwRXUzhOZV3N9mPG&id=[uid]&msg="
        String url = "http://api.brainshop.ai/get?bid=164378&key=bwRXUzhOZV3N9mPG&uid=[uid]&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai/";
        //http://api.brainshop.ai/get?bid=160167&key=8h8vRUhkZo5zyBrO&uid=[uid]&msg
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetroFitApi retroFitApi = retrofit.create(RetroFitApi.class);
        Call<MsgModal> call = retroFitApi.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(@NonNull Call<MsgModal> call, @NonNull Response<MsgModal> response) {
                if (response.isSuccessful()) {
                    MsgModal msgModal = response.body();
                    String msgbody = null;
                    if (msgModal != null) {
                        msgbody = msgModal.getCnt();
                    }
                    Log.e(TAG, "Feed" + msgbody);

                    languagecodeTransfer(msgbody, lang);
                    // getter(s);
                    recyclerView.scrollToPosition(chatsmodalArrayList.size() - 1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MsgModal> call, @NonNull Throwable t) {
//                chatsmodalArrayList.add(new Chatsmodal("no response", BOT_KEY));
//                chatAdapter.notifyDataSetChanged();

                chatAdapter.addItem(new Chatsmodal("no response", BOT_KEY), 0);
                recyclerView.scrollToPosition(chatsmodalArrayList.size() - 1);

            }
        });
    }
    private void languagecodeTransfer(String msgbody, String langcode) {
        //language1="mr";
        TranslatorOptions options = new TranslatorOptions.Builder().
                setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(Objects.requireNonNull(TranslateLanguage.fromLanguageTag(langcode)))
                .build();
        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded().addOnSuccessListener(unused -> // Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                translator.translate(msgbody).addOnSuccessListener(this::getter)).addOnFailureListener(e -> {
        });

    }

    private void getter(String ss) {
//        chatsmodalArrayList.add(new Chatsmodal(ss, BOT_KEY));
//        chatAdapter.notifyDataSetChanged();

        chatAdapter.addItem(new Chatsmodal(ss, BOT_KEY), 0);
        recyclerView.scrollToPosition(chatsmodalArrayList.size() - 1);

    }


}