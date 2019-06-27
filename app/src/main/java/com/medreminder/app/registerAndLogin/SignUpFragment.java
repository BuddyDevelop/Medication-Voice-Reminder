package com.medreminder.app.registerAndLogin;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.medreminder.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, mobileNumber, location,
            password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;

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
        emailId = ( EditText ) view.findViewById( R.id.userEmailId );
        mobileNumber = ( EditText ) view.findViewById( R.id.mobileNumber );
        password = ( EditText ) view.findViewById( R.id.password );
        confirmPassword = ( EditText ) view.findViewById( R.id.confirmPassword );
        signUpButton = ( Button ) view.findViewById( R.id.signUpBtn );
        login = ( TextView ) view.findViewById( R.id.already_user );
        terms_conditions = ( CheckBox ) view.findViewById( R.id.terms_conditions );

        // Setting text selector over textviews
//        XmlResourceParser xrp = getResources().getXml( R.drawable.text_selector );
//        try {
//            ColorStateList csl = ColorStateList.createFromXml( getResources(),
//                    xrp );
//
//            login.setTextColor( csl );
//            terms_conditions.setTextColor( csl );
//        } catch ( Exception e ) {
//        }
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

        // Get all edittext texts
        String fullNameString = fullName.getText().toString();
        String emailString = emailId.getText().toString();
        String mobileNumberString = mobileNumber.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile( LoginFragment.emailRegEx );
        Matcher m = p.matcher( emailString );

        // Check if all strings are null or not
        if ( fullNameString.equals( "" ) || fullNameString.length() == 0
                || emailString.equals( "" ) || emailString.length() == 0
                || mobileNumberString.equals( "" ) || mobileNumberString.length() == 0
                || passwordString.equals( "" ) || passwordString.length() == 0
                || confirmPasswordString.equals( "" )
                || confirmPasswordString.length() == 0 )

            new CustomToast().Show_Toast( getActivity(), view,
                    "All fields are required." );

            // Check if email id valid or not
        else if ( !m.find() )
            new CustomToast().Show_Toast( getActivity(), view,
                    "Your email is Invalid." );

            // Check if both password should be equal
        else if ( !confirmPasswordString.equals( passwordString ) )
            new CustomToast().Show_Toast( getActivity(), view,
                    "Both password doesn't match." );

            // Make sure user should check Terms and Conditions checkbox
        else if ( !terms_conditions.isChecked() )
            new CustomToast().Show_Toast( getActivity(), view,
                    "Please select Terms and Conditions." );

            // Else do signup or do your stuff
        else
            Toast.makeText( getActivity(), "Do SignUp.", Toast.LENGTH_SHORT )
                    .show();

    }
}
