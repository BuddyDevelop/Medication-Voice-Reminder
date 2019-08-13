package com.medreminder.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.medreminder.app.Database.FirebaseDBHelper;
import com.medreminder.app.registerAndLogin.RegisterActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static String token;

    //delete app token to not receive notifications from FCM, used on user logout
    public static void deleteToken( final Context context ){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch ( IOException e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Call your Activity where you want to land after log out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent( context, RegisterActivity.class );
                //clear back stack so on logout we exit app instead showing last visible account
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity( intent );
            }
        }.execute();
    }

    //get app token and put to database so user can receive notifications from FCM
    public static void getToken() {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground( Void... voids ) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener( new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete( @NonNull Task<InstanceIdResult> task ) {
                                if ( !task.isSuccessful() ) {
                                    Log.w( TAG, "getInstanceId failed", task.getException() );
                                    return;
                                }

                                // Get new Instance ID token
                                token = task.getResult().getToken();
                                FirebaseDBHelper.updateUserToken( token );
                                Log.e( "Token::", token );
                            }
                        } );
                return null;
            }
        }.execute(  );
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener( new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete( @NonNull Task<InstanceIdResult> task ) {
//                        if ( !task.isSuccessful() ) {
//                            Log.w( TAG, "getInstanceId failed", task.getException() );
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        token = task.getResult().getToken();
//                        Log.e( "Token::", token );
//                    }
//                } );
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer( String token ) {
        FirebaseDBHelper mFirebaseDBHelper = new FirebaseDBHelper();
        FirebaseUser mAuthCurrentUser = mFirebaseDBHelper.mAuth.getCurrentUser();
        if( mAuthCurrentUser == null ){
            return;
        }

        String userId = mAuthCurrentUser.getUid();
        DatabaseReference userRef = mFirebaseDBHelper.mFirebaseUsersReference.child( userId );
        Map<String, Object> updateToken = new HashMap<>( );
        updateToken.put( "token", token);

        userRef.updateChildren(updateToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
