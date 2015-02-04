package fragments;


import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;

import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_NAME;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_TYPE;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_AUTH_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends android.support.v4.app.Fragment {
    private RequestMethods requestMethods;
    Context mContext;

    private final int REQ_SIGNUP = 1;
    private AccountManager mAccountManager;
    private String mAuthTokenType;

    //Interface with Activity
    public AuthListener mCallback;

    // --------------------------------------------------------

    //LISTENERS
    public interface AuthListener {
        public void onUserSignedIn(Bundle userData);
        //public void onUserSignedUp(Bundle userData);
        public void onCancelLogin();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (AuthListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.login_callback_exception_message));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        requestMethods = new RequestMethods(mContext);


        //Get account information
        String accountName = getActivity().getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getActivity().getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((EditText) getView().findViewById(R.id.accountName)).setText(accountName);
        }

        //UI Elements
        final Button loginButton = (Button) getView().findViewById(R.id.loginButton);
        final Button signUpButton = (Button) getView().findViewById(R.id.signUpButton);
        final EditText accountNameField = (EditText)getView().findViewById(R.id.accountName);
        final EditText accountPasswordField = (EditText)getView().findViewById(R.id.accountPassword);

        //Try Login on Click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountName = accountNameField.getText().toString().trim();
                String accountPassword = accountPasswordField.getText().toString().trim();
                final String accountType = getActivity().getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

                if (accountName.isEmpty() || accountPassword.isEmpty()) {
                    requestMethods.showErrorDialog(mContext, getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                } else {
                    //TODO: Login User + save to sharedPreferences
                    ListUser mCurrentUser = new ListUser(mContext);
                    try {
                        mCurrentUser.userSignIn(accountName, accountPassword, mAuthTokenType, accountType, mCallback);
                    } catch (Exception e) {
                        Log.d("LoginFragment", e.getMessage());
                        //data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }
                }
            }
        });

        //I want to create an account --> show user Sign Up Button
        getView().findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: hide loginButton and show signUpButton
                loginButton.setVisibility(View.GONE);
                signUpButton.setVisibility(View.VISIBLE);
            }
        });

        //Try Sign Up on Click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:userSignUp
                String accountEmail = accountNameField.getText().toString().trim();
                String accountPassword = accountPasswordField.getText().toString().trim();

                if (accountEmail.isEmpty() || accountPassword.isEmpty()) {
                    requestMethods.showErrorDialog(mContext, getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                } else {
                    //TODO: Login User + save to sharedPreferences
                    ListUser mCurrentUser = new ListUser();
                    try {
                        mCurrentUser.userSignUp(accountEmail, accountPassword, mAuthTokenType, mCallback);
                    } catch (Exception e) {
                        Log.d("LoginFragment", e.getMessage());
                    }
                }
            }
        });
    }
}
