package com.medreminder.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;

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

    }


    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        super.onStartCommand( intent, flags, startId );
        mTts = new TextToSpeech( this, this );
        if ( intent != null )
            if ( intent.getStringArrayListExtra( "ttsContent" ) != null )
                spokenText = intent.getStringArrayListExtra( "ttsContent" );
        return START_STICKY;
    }


    @Override
    public void onInit( int status ) {
        if ( status == TextToSpeech.SUCCESS ) {
            int result = mTts.setLanguage( Locale.US );
            if ( result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED ) {
                Log.d( "Init", "Success" );

                // speak all notifications which have same reminder time and day
                if( spokenText == null || spokenText.isEmpty() )
                    return;

                if( mTts.isSpeaking() )
                    return;

                for ( String text : spokenText ) {
                    mTts.speak( text, TextToSpeech.QUEUE_ADD, null, null );
                    mTts.playSilentUtterance( 2000, TextToSpeech.QUEUE_ADD, null );
                    mTts.speak( text, TextToSpeech.QUEUE_ADD, null, null );
                }
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

            isIntentServiceRunning = false;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }
}