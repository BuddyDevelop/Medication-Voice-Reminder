package com.example.grzeiek.pracainzynierska;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.ArrayList;
import java.util.Locale;

/**
 * Manage text to speech
 */

public class TTS extends Service implements TextToSpeech.OnInitListener {
    private TextToSpeech mTts;
    private ArrayList<String> spokenText;
    public static boolean isIntentServiceRunning = false;

    @Override
    public void onCreate() {
        mTts = new TextToSpeech( this, this );
    }


//    @Override
//    public int onStartCommand( Intent intent, int flags, int startId ) {
//        super.onStartCommand( intent, flags, startId );
//        if ( intent != null )
//            if ( intent.getStringArrayListExtra( "ttsContent" ) != null )
//                spokenText = intent.getStringArrayListExtra( "ttsContent" );
//        return START_STICKY;
//    }

    @Override
    public void onStart( Intent intent, int startId ) {
        super.onStart( intent, startId );
        spokenText = intent.getStringArrayListExtra( "ttsContent" );
    }

    @Override
    public void onInit( int status ) {
        if ( status == TextToSpeech.SUCCESS ) {
            int result = mTts.setLanguage( Locale.US );
            if ( result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED ) {
                Log.d( "Init", "Success" );

                // wait a little for the initialization to complete and to speak if there are multiple alarms on same time
//                Handler handler = new Handler();
//                handler.postDelayed( new Runnable() {
//                    @Override
//                    public void run() {
                // speak all notifications which have same reminder time and day
                for ( String text : spokenText ) {
                    mTts.speak( text, TextToSpeech.QUEUE_ADD, null );
                    mTts.playSilentUtterance( 2000, TextToSpeech.QUEUE_ADD, null );
                    mTts.speak( text, TextToSpeech.QUEUE_ADD, null );
                }
//                    }
//                }, 2000 );
            }
        } else {
            Log.d( getClass().getSimpleName(), " Could not initialize TextToSpeech" );
        }
    }

    @Override
    public synchronized void onDestroy() {
        if ( mTts != null ) {
            mTts.stop();
            mTts.shutdown();

//            synchronized ( this ) {
            isIntentServiceRunning = false;
//            }
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}