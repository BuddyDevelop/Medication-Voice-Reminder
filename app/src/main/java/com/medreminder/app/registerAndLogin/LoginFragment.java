package com.medreminder.app.registerAndLogin;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.medreminder.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment implements OnClickListener {
    private static View view;

    private EditText email, password;
    private Button loginButton;
    private TextView forgotPassword, signUp;
    private CheckBox show_hide_password;
    private LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    public static final String emailRegEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    public LoginFragment() {

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        view = inflater.inflate( R.layout.login_layout, container, false );
        initViews();
        setListeners();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        email = ( EditText ) view.findViewById( R.id.login_emailid );
        password = ( EditText ) view.findViewById( R.id.login_password );
        loginButton = ( Button ) view.findViewById( R.id.loginBtn );
        forgotPassword = ( TextView ) view.findViewById( R.id.forgot_password );
        signUp = ( TextView ) view.findViewById( R.id.createAccount );
        show_hide_password = ( CheckBox ) view
                .findViewById( R.id.show_hide_password );
        loginLayout = ( LinearLayout ) view.findViewById( R.id.login_layout );

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation( getActivity(),
                R.anim.shake_animation );

        // Setting text selector over textviews
//        XmlResourceParser xrp = getResources().getXml( R.drawable.text_selector );
//        try {
//            ColorStateList csl = ColorStateList.createFromXml( getResources(),
//                    xrp );
//
//            forgotPassword.setTextColor( csl );
//            show_hide_password.setTextColor( csl );
//            signUp.setTextColor( csl );
//        } catch ( Exception e ) {
//        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener( this );
        forgotPassword.setOnClickListener( this );
        signUp.setOnClickListener( this );

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener( new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged( CompoundButton button,
                                                  boolean isChecked ) {

                        // If it is checked then show password else hide
                        // password
                        if ( isChecked ) {

                            show_hide_password.setText( R.string.hide_pswd );// change

                            password.setInputType( InputType.TYPE_CLASS_TEXT );
                            password.setTransformationMethod( HideReturnsTransformationMethod
                                    .getInstance() );// show password
                        } else {
                            show_hide_password.setText( R.string.show_pswd );// change

                            password.setInputType( InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                            password.setTransformationMethod( PasswordTransformationMethod
                                    .getInstance() );// hide password

                        }

                    }
                } );
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.loginBtn:
                checkValidation();
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations( R.anim.right_enter_animation, R.anim.left_exit_animation )
                        .replace( R.id.frameContainer, new ForgotPasswordFragment() )
                        .commit();
                break;
            case R.id.createAccount:

                // Replace signup fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations( R.anim.right_enter_animation, R.anim.left_exit_animation )
                        .replace( R.id.frameContainer, new SignUpFragment() )
                        .commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email and password
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        // Check patter for email
        Pattern p = Pattern.compile( emailRegEx );

        Matcher m = p.matcher( emailString );

        // Check for both field is empty or not
        if ( emailString.equals( "" ) || emailString.length() == 0
                || passwordString.equals( "" ) || passwordString.length() == 0 ) {
            loginLayout.startAnimation( shakeAnimation );
            new CustomToast().Show_Toast( getActivity(), view,
                    "Enter both credentials." );

        }
        // Check if email is valid or not
        else if ( !m.find() )
            new CustomToast().Show_Toast( getActivity(), view,
                    "Your email is invalid." );
            // Else do login and do your stuff
        else
            Toast.makeText( getActivity(), "Do Login.", Toast.LENGTH_SHORT )
                    .show();

    }
}
