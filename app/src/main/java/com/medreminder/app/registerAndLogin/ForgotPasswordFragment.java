package com.medreminder.app.registerAndLogin;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.medreminder.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordFragment extends Fragment implements
        OnClickListener {
    private static View view;

    private static EditText emailId;
    private static TextView submit, back;

    public ForgotPasswordFragment() {

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        view = inflater.inflate( R.layout.forgotpassword_layout, container,
                false );
        initViews();
        setListeners();
        return view;
    }

    // Initialize the views
    private void initViews() {
        emailId = ( EditText ) view.findViewById( R.id.registered_emailid );
        submit = ( TextView ) view.findViewById( R.id.forgot_button );
        back = ( TextView ) view.findViewById( R.id.backToLoginBtn );

//        // Setting text selector over textviews
//        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
//        try {
//            ColorStateList csl = ColorStateList.createFromXml(getResources(),
//                    xrp);
//
//            back.setTextColor(csl);
//            submit.setTextColor(csl);
//
//        } catch (Exception e) {
//        }

    }

    // Set Listeners over buttons
    private void setListeners() {
        back.setOnClickListener( this );
        submit.setOnClickListener( this );
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.backToLoginBtn:

                // Replace Login Fragment on Back Presses
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.left_enter_animation, R.anim.right_exit_animation )
                        .replace( R.id.frameContainer, new LoginFragment() )
                        .commit();
                break;

            case R.id.forgot_button:

                // Call Submit button task
                submitButtonTask();
                break;

        }

    }

    private void submitButtonTask() {
        String emailString = emailId.getText().toString();

        // Pattern for email id validation
        Pattern p = Pattern.compile( LoginFragment.emailRegEx );

        // Match the pattern
        Matcher m = p.matcher( emailString );

        // First check if email id is not null else show error toast
        if ( emailString.equals( "" ) || emailString.length() == 0 )

            new CustomToast().Show_Toast( getActivity(), view,
                    "Please enter your email." );

            // Check if email id is valid or not
        else if ( !m.find() )
            new CustomToast().Show_Toast( getActivity(), view,
                    "Your email is invalid." );

            // Else submit email id and fetch password or do your stuff
        else
            Toast.makeText( getActivity(), "Get Forgot Password.",
                    Toast.LENGTH_SHORT ).show();
    }
}
