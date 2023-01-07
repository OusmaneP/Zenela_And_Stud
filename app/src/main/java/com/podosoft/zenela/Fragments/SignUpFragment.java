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

import com.podosoft.zenela.Dto.UserRegistrationDto;
import com.podosoft.zenela.HomeActivity;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.MainActivity;
import com.podosoft.zenela.MyHelpers.MyValidators;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Responses.LoginResponse;


public class SignUpFragment extends Fragment {

    private static final String NAME_ARG = "name";
    private String fragName;

    EditText etFirstName, etLastName, etEmail, etPassword, etConfirmP;
    Button btnSignUp;
    MyValidators myValidators;
    RequestManager manager;

    TextView registerAlert, textView_go_to_sign_in;

    SharedPreferences sharedPreferences = null;

    View view;

    private SignInFragment.GoToSignUpListener goToSignInListener;

    public SignUpFragment() {
        // Required empty public constructor
    }


    public static SignUpFragment newInstance(final String name) {
        final SignUpFragment fragment = new SignUpFragment();

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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.goToSignInListener = (SignInFragment.GoToSignUpListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        view = getView();
        if (view != null){

            etFirstName = view.findViewById(R.id.editText_firstName);
            etLastName = view.findViewById(R.id.editText_lastName);
            etEmail = view.findViewById(R.id.editText_email);
            etPassword = view.findViewById(R.id.editText_password);
            etConfirmP = view.findViewById(R.id.editText_confirm_password);

            btnSignUp = view.findViewById(R.id.btn_sign_up);

            registerAlert = view.findViewById(R.id.registerAlert);

            textView_go_to_sign_in = view.findViewById(R.id.textView_go_to_sign_in);

            sharedPreferences = requireActivity().getSharedPreferences("login", 0);

            myValidators = new MyValidators(getActivity());

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String firstName = etFirstName.getText().toString();
                    String lastName = etLastName.getText().toString();
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    String confirmP = etConfirmP.getText().toString();

                    if(processFormFields(firstName, email, password, confirmP)){
                        UserRegistrationDto userRegistrationDto = new UserRegistrationDto(firstName, lastName, email, password);
                        manager = new RequestManager(getActivity());
                        manager.register(loginResponseListener, userRegistrationDto);
                    }
                }
            });
            textView_go_to_sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (goToSignInListener != null){
                        goToSignInListener.goToSignIn();
                    }
                }
            });
        }
    }

    // process fields
    boolean processFormFields(String firstName, String email, String password, String confirmP){

        return myValidators.validateFirstName(etFirstName, firstName) && myValidators.validateEmail(etEmail, email) && myValidators.validatePassword(etPassword, password) && myValidators.validateConfirmP(etConfirmP, password, confirmP);
    }

    // Listener
    public final LoginResponseListener loginResponseListener = new LoginResponseListener() {
        @Override
        public void didLogin(LoginResponse response, String message) {
            registerAlert = view.findViewById(R.id.registerAlert);

            if (String.valueOf(response.getStatus()).equals("Bad Request")){
                registerAlert.setText(R.string.email_or_password_wrong);
            }
            if (String.valueOf(response.getStatus()).equals("Request Ok")){
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), "❌ Connexion failed \n" + message, Toast.LENGTH_LONG).show();
        }
    };


}