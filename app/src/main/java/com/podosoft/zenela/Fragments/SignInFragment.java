package com.podosoft.zenela.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.podosoft.zenela.Dto.UserDto;
import com.podosoft.zenela.HomeActivity;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.MyHelpers.MyValidators;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Responses.LoginResponse;


public class SignInFragment extends Fragment {

    private static final String NAME_ARG = "name";
    private String fragName;

    EditText etEmail, etPassword;
    Button btnSignIn;
    MyValidators myValidators;
    RequestManager manager;

    TextView loginAlert, textView_go_to_sign_up;

    SharedPreferences sharedPreferences = null;

    View view;

    private GoToSignUpListener goToSignUpListener;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance(final String name) {

        final SignInFragment fragment = new SignInFragment();

        final Bundle args = new Bundle(1);

        args.putString(NAME_ARG, name);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(NAME_ARG)) {
            fragName = arguments.getString(NAME_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.goToSignUpListener = (GoToSignUpListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        view = getView();
        if(view != null){

            etEmail = view.findViewById(R.id.editText_email);
            etPassword = view.findViewById(R.id.editText_password);

            btnSignIn = view.findViewById(R.id.btn_sign_in);

            loginAlert = view.findViewById(R.id.loginAlert);

            textView_go_to_sign_up = view.findViewById(R.id.textView_go_to_sign_up);

            sharedPreferences = requireActivity().getSharedPreferences("login", 0);

            myValidators = new MyValidators(getActivity());

            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();

                    if (processFormFields(email, password)){
                        UserDto userDto = new UserDto(email, password);
                        manager = new RequestManager(getActivity());
                        manager.login(loginResponseListener, userDto);
                    }
                }
            });

            textView_go_to_sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (goToSignUpListener != null){
                        goToSignUpListener.goToSignUp();
                    }
                }
            });

        }
    }


    // process fields
    boolean processFormFields(String email, String password){

        return myValidators.validateEmail(etEmail, email) && myValidators.validatePassword(etPassword, password);
    }

    // Listener
    public final LoginResponseListener loginResponseListener = new LoginResponseListener() {
        @Override
        public void didLogin(LoginResponse response, String message) {

            loginAlert = view.findViewById(R.id.loginAlert);

            if (String.valueOf(response.getStatus()).equals("Bad Request")){
                loginAlert.setText(R.string.email_or_password_wrong);
            }
            if (String.valueOf(response.getStatus()).equals("Request Ok")){

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putLong("principalId", response.getPrincipal().getId());
                editor.apply();


                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("loginResponse", (LoginResponse) response);

                startActivity(intent);
                requireActivity().finish();
            }


        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), "❌ Connexion failed \n" + message, Toast.LENGTH_LONG).show();
        }
    };



    public interface GoToSignUpListener{
        void goToSignUp();
        void goToSignIn();
    }

}