package com.example.stttest1;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 1;

    String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };

    Spinner dynamicSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = findViewById(R.id.editText);

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        LanguageDetailsChecker LDC = new LanguageDetailsChecker();

        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        sendOrderedBroadcast(
                detailsIntent, null, LDC, null,
                Activity.RESULT_OK, null, null);

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                mSpeechRecognizer.stopListening();
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    editText.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                checkPermissions();
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                editText.setText("");
                editText.setHint("Listening...");
            }
        });

        dynamicSpinner = findViewById(R.id.dynamic_spinner);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        parent.getItemIdAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkPermissions(){
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION);
        }
    }

    public class LanguageDetailsChecker extends BroadcastReceiver{
        String languagePreference;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle results = getResultExtras(true);
            if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
            {
                languagePreference =
                        results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
            }
            if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES))
            {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        com.example.stttest1.MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        results.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dynamicSpinner.setAdapter(adapter);
            }

        }
    }

}
