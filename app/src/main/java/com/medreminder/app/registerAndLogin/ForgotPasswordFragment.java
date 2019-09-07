package com.medreminder.app.registerAndLogin;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.medreminder.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordFragment extends Fragment implements
        OnClickListener {
    private View view;

    private EditText emailId;
    private TextView submit, back;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

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
        progressBar = ( ProgressBar ) view.findViewById( R.id.forgotPswdProgressBar );
        progressBar.setVisibility( View.GONE );

        mAuth = FirebaseAuth.getInstance();
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
        String emailString = emailId.getText().toString().trim();

        // Pattern for email id validation
        Pattern p = Pattern.compile( LoginFragment.emailRegEx );

        // Match the pattern
        Matcher m = p.matcher( emailString );

        // First check if email id is not null else show error toast
        if ( emailString.equals( "" ) || emailString.length() == 0 )

            new CustomToast().showToast( getActivity(), view,
                    getString( R.string.enter_email_err ) );

            // Check if email id is valid or not
        else if ( !m.find() )
            new CustomToast().showToast( getActivity(), view,
                    getString( R.string.invalid_email ) );

            // Else submit email id and fetch password
        else
            resetPasswordWithEmail( emailString );
    }

    private void resetPasswordWithEmail( String email ) {
        progressBar.setVisibility( View.VISIBLE );

        mAuth.sendPasswordResetEmail( email )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( @NonNull Task<Void> task ) {
                        progressBar.setVisibility( View.GONE );

                        if( task.isSuccessful() )
                            Toast.makeText( getActivity(), R.string.forgot_pswd_send, Toast.LENGTH_SHORT ).show();
                        else
                            new CustomToast().showToast( getActivity(), view, getString( R.string.reset_email_err ) );
                    }
                } );
    }
}
