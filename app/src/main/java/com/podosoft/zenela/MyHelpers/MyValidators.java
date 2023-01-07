package com.podosoft.zenela.MyHelpers;

import android.content.Context;
import android.widget.EditText;

import com.podosoft.zenela.R;

public class MyValidators {

    Context context;

    public MyValidators() {
    }

    public MyValidators(Context context) {
        this.context = context;
    }

    // Validations
    public boolean validateFirstName(EditText etFirstName, String firstName){

        if(firstName == null || firstName.isEmpty()){
            etFirstName.setError(context.getString(R.string.first_name_cannot_be_empty));
            return false;
        }
        return true;
    }

    public boolean validateEmail(EditText etEmail, String email){
        if(email == null || email.isEmpty()){
            etEmail.setError(context.getString(R.string.email_cannot_be_empty));
            return false;
        }
        return true;
    }

    public boolean validatePassword(EditText etPassword, String password){

        if(password == null || password.isEmpty()){
            etPassword.setError(context.getString(R.string.password_cannot_be_empty));
            return false;
        }
        return true;
    }

    public boolean validateConfirmP(EditText etConfirmPassword, String password, String confirm){
        if(password == null || confirm == null || confirm.isEmpty()){
            etConfirmPassword.setError(context.getString(R.string.password_confirm_cannot_be_empty));
            return false;
        }else if(!confirm.equals(password)){
            etConfirmPassword.setError(context.getString(R.string.confirm_should_match_password));
            return false;
        }
        return true;
    }

}
