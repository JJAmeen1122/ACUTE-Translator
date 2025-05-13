package com.jjapp.acuetranslator;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.translation.Translator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class lantrans extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;
    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdit;
    private TextView translatedView;
    private final String[] fromLanguages = {"From", "English", "Urdu", "Arabic", "Chinese", "Turkish", "Japanese"};
    private final String[] toLanguages = {"To", "English", "Urdu", "Arabic", "Chinese", "Turkish", "Japanese"};
    private int fromLanguageCode = 0;
    private int toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lantrans);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fromSpinner = findViewById(R.id.idFromSpiner);
        toSpinner = findViewById(R.id.idToSpiner);
        sourceEdit = findViewById(R.id.idEditSource);
        translatedView = findViewById(R.id.TranslatedV);
        ImageView mic = findViewById(R.id.Mic);
        MaterialButton translateBtn = findViewById(R.id.TranslateBtn);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fromLanguageCode = 0;
            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this,R.layout.spinner_item, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toLanguageCode = 0;
            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item, toLanguages);        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedView.setText("");
                if (sourceEdit.getText().toString().isEmpty()){
                    Toast.makeText(lantrans.this, "Please enter text", Toast.LENGTH_SHORT).show();
                }else if (fromLanguageCode == 0){
                    Toast.makeText(lantrans.this, "Please select source language", Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode == 0){
                    Toast.makeText(lantrans.this, "Please select target language", Toast.LENGTH_SHORT).show();
                }else {
                    translateText(fromLanguageCode,toLanguageCode,sourceEdit.getText().toString());
                }
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
    }
    private void startVoiceInput() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");
            startActivityForResult(intent, REQUEST_PERMISSION_CODE);
        } catch (Exception e) {
            showToast("Voice input not available: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                sourceEdit.setText(result.get(0));
            }
        }
    }
    private void translateText(int fromLanguageCode,int toLanguageCode, String source) {
        translatedView.setText("Downloading Model...");
        FirebaseTranslatorOptions options= new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator= FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedView.setText("Translating...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedView.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(lantrans.this, "Fail to translate"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(lantrans.this, "Fail to download language model"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public int getLanguageCode(String language){
        int languageCode = 0;
        switch (language){
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Urdu":
                languageCode = FirebaseTranslateLanguage.UR;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Chinese":
                languageCode = FirebaseTranslateLanguage.CS;
                break;
            case "Turkish":
                languageCode = FirebaseTranslateLanguage.TR;
                break;
            case "Japanese":
                languageCode = FirebaseTranslateLanguage.JA;
                break;
            default:
                languageCode=0;
        }
        return languageCode;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}