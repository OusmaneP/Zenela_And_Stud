package com.podosoft.zenela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.podosoft.zenela.Fragments.SignInFragment;
import com.podosoft.zenela.Fragments.SignUpFragment;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Dto.UserDto;
import com.podosoft.zenela.MyHelpers.MyValidators;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Responses.LoginResponse;

public class RegisterLoginActivity extends ThemeSettingsActivity implements SignInFragment.GoToSignUpListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

    }


    @Override
    public void goToSignUp() {
        replaceFragment(SignUpFragment.newInstance("sign_up_frag"), "sign_up_frag");
    }

    @Override
    public void goToSignIn() {
        replaceFragment(SignInFragment.newInstance("sign_in_frag"), "sign_in_frag");
    }

    public void replaceFragment(Fragment fragment, String tag){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_sign);

        if (currentFragment.getClass() == fragment.getClass()){
            return;
        }

        if(getSupportFragmentManager().findFragmentByTag(tag) != null){
            getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
                .replace(R.id.fragment_sign, fragment, tag)
                .commit();
    }



}