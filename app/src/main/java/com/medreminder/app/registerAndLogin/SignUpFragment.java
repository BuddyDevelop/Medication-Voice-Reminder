package com.medreminder.app.registerAndLogin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.medreminder.app.Activities.MainActivity;
import com.medreminder.app.Models.User;
import com.medreminder.app.MyFirebaseMessagingService;
import com.medreminder.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment implements OnClickListener {
    private View view;
    private EditText fullName, email, peselNumber,
            password, confirmPassword;
    private TextView login;
    private Button signUpButton;
    private CheckBox terms_conditions;
    private String peselRegEx = "\\b^[0-9]{11}$\\b";
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        view = inflater.inflate( R.layout.signup_layout, container, false );
        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        fullName = ( EditText ) view.findViewById( R.id.fullName );
        email = ( EditText ) view.findViewById( R.id.userEmailId );
        peselNumber = ( EditText ) view.findViewById( R.id.peselNumber );
        password = ( EditText ) view.findViewById( R.id.password );
        confirmPassword = ( EditText ) view.findViewById( R.id.confirmPassword );
        signUpButton = ( Button ) view.findViewById( R.id.signUpBtn );
        login = ( TextView ) view.findViewById( R.id.already_user );
        terms_conditions = ( CheckBox ) view.findViewById( R.id.terms_conditions );
        progressBar = ( ProgressBar ) view.findViewById( R.id.progressBar );
        progressBar.setVisibility( View.GONE );

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        if ( mAuth.getCurrentUser() != null ) {
            userAlreadyLogged( getActivity() );
        }
    }

    public static void userAlreadyLogged( Context context ) {
        Intent intent = new Intent( context, MainActivity.class );
        context.startActivity( intent );
        ( ( Activity ) context ).finish();
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener( this );
        login.setOnClickListener( this );
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.signUpBtn:

                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.left_enter_animation, R.anim.right_exit_animation )
                        .replace( R.id.frameContainer, new LoginFragment() )
                        .commit();

                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {

        // Get all editText texts
        String fullNameString = fullName.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String peselNumberString = peselNumber.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        // Check patter for pesel
        Pattern peselPattern = Pattern.compile( peselRegEx );
        Matcher peselMatcher = peselPattern.matcher( peselNumberString );

        // Pattern match for email
        Pattern emailPattern = Pattern.compile( LoginFragment.emailRegEx );
        Matcher emailMatcher = emailPattern.matcher( emailString );

        // Check if all strings are null or not
        if ( fullNameString.equals( "" ) || fullNameString.length() == 0
                || emailString.equals( "" ) || emailString.length() == 0
                || peselNumberString.equals( "" ) || peselNumberString.length() == 0
                || passwordString.equals( "" ) || passwordString.length() == 0
                || confirmPasswordString.equals( "" )
                || confirmPasswordString.length() == 0 )

            new CustomToast().showToast( getActivity(), view,
                    "All fields are required." );

        else if ( fullNameString.length() < 6 )
            new CustomToast().showToast( getActivity(), view,
                    "Enter your full name" );

            // Check if email id valid or not
        else if ( !emailMatcher.find() )
            new CustomToast().showToast( getActivity(), view,
                    "Your email is invalid." );

        else if ( !peselMatcher.find() )
            new CustomToast().showToast( getActivity(), view,
                    "Your pesel is invalid." );

        else if ( passwordString.length() < 6 )
            new CustomToast().showToast( getActivity(), view,
                    "Your password should have at least 6 characters." );

            // Check if both password should be equal
        else if ( !confirmPasswordString.equals( passwordString ) )
            new CustomToast().showToast( getActivity(), view,
                    "Both password doesn't match." );

            // Make sure user should check Terms and Conditions checkbox
        else if ( !terms_conditions.isChecked() )
            new CustomToast().showToast( getActivity(), view,
                    "Please select Terms and Conditions." );

            // Else do signup or do your stuff
        else {
            progressBar.setVisibility( View.VISIBLE );
            RegisterUser( emailString, passwordString, fullNameString, peselNumberString );
        }


    }

    private void RegisterUser( final String email, String password, final String fullName, final String pesel ) {

        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( @NonNull Task<AuthResult> task ) {
                        if ( task.isSuccessful() ) {

                            User user = new User(
                                    fullName,
                                    email,
                                    pesel
                            );

                            mFirebaseDatabase.getReference()
                                    .child( "Users" )
                                    .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                                    .setValue( user )
                                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete( @NonNull Task<Void> task ) {
                                            progressBar.setVisibility(View.GONE );
                                            if ( task.isSuccessful() ) {
                                                Toast.makeText( getActivity(), R.string.registration_successful, Toast.LENGTH_SHORT ).show();
                                                userAlreadyLogged( getContext() );
                                                //get app token and put to database so user can receive notifications from FCM
                                                MyFirebaseMessagingService.getToken();
                                            } else
                                                new CustomToast().showToast( getActivity(), view,
                                                        "Something went wrong with registration, check your connection" );
                                        }
                                    } );

                        } else
                            new CustomToast().showToast( getActivity(), view,
                                    task.getException().getMessage() );
                        progressBar.setVisibility(View.GONE );
                    }
                } );
    }
}
