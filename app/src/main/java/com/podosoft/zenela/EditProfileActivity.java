package com.podosoft.zenela;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.MyHelpers.MyValidators;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.LoginResponse;

public class EditProfileActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etBio, editText_old_password, editText_new_password, editText_confirm_password;
    Button btnSaveChanges, btn_save_password;
    MyValidators myValidators;
    RequestManagerProfile managerProfile;
    LinearLayout layout_edit_password_content, layout_edit_password_title;
    ProgressBar progressBarHorizon;

    SharedPreferences sharedPreferences = null;
    long principalId;
    String email;
    User principal;
    String newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etFirstName = findViewById(R.id.editText_firstName);
        etLastName = findViewById(R.id.editText_lastName);
        etEmail = findViewById(R.id.editText_email);
        etBio = findViewById(R.id.editText_bio);

        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btn_save_password = findViewById(R.id.btn_save_password);

        editText_old_password = findViewById(R.id.editText_old_password);
        editText_new_password = findViewById(R.id.editText_new_password);
        editText_confirm_password = findViewById(R.id.editText_confirm_password);

        layout_edit_password_title = findViewById(R.id.layout_edit_password_title);
        layout_edit_password_content = findViewById(R.id.layout_edit_password_content);
        progressBarHorizon = findViewById(R.id.progressBarHorizon);

        myValidators = new MyValidators(EditProfileActivity.this);

        sharedPreferences = EditProfileActivity.this.getSharedPreferences("login", 0);
        principalId = sharedPreferences.getLong("principalId", 0);
        email = sharedPreferences.getString("email", null);

        principal = (User) getIntent().getSerializableExtra("Principal");

        etFirstName.setText(principal.getFirstName());
        etLastName.setText(principal.getLastName());
        etEmail.setText(principal.getEmail());
        etBio.setText(principal.getBio());

        etChangedListener(etFirstName); etChangedListener(etLastName); etChangedListener(etEmail); etChangedListener(etBio);
        etPasswordChangedListener(editText_old_password); etPasswordChangedListener(editText_new_password); etPasswordChangedListener(editText_confirm_password);

        // save Profile Info
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                String bio = etBio.getText().toString();

                if (processFormFields(firstName, email)){
                    btnSaveChanges.setEnabled(false);
                    showHorProg();
                    managerProfile = new RequestManagerProfile(EditProfileActivity.this);
                    managerProfile.editProfileInfo(editProfileResponseListener, principalId, firstName, lastName, email, bio);
                }
            }
        });

        // layout Password
        layout_edit_password_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layout_edit_password_content.getVisibility() == View.VISIBLE){
                    layout_edit_password_content.setVisibility(View.INVISIBLE);
                    ViewGroup.LayoutParams layoutParams = layout_edit_password_content.getLayoutParams();
                    layoutParams.height = 0;
                    layout_edit_password_content.setLayoutParams(layoutParams);
                }else{
                    layout_edit_password_content.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layoutParams = layout_edit_password_content.getLayoutParams();
                    layoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT;
                    layout_edit_password_content.setLayoutParams(layoutParams);
                }
            }
        });

        // btn save Password
        btn_save_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = editText_old_password.getText().toString();
                newPassword = editText_new_password.getText().toString();
                String confirmPassword = editText_confirm_password.getText().toString();

                if (processFormPassword(oldPassword, newPassword, confirmPassword)){
                    btn_save_password.setEnabled(false);
                    showHorProg();
                    managerProfile = new RequestManagerProfile(EditProfileActivity.this);
                    managerProfile.editPassword(editPasswordResponseListener, principalId, oldPassword, newPassword);
                }
            }
        });
    }


    // process fields

    boolean processFormFields(String firstName, String email){
        return myValidators.validateFirstName(etFirstName, firstName) && myValidators.validateEmail(etEmail, email);
    }

    private boolean processFormPassword(String oldPassword, String newPassword, String confirmPassword) {
        return myValidators.validatePassword(editText_old_password, oldPassword) && myValidators.validatePassword(editText_new_password, newPassword) && myValidators.validateConfirmP(editText_confirm_password, newPassword, confirmPassword);
    }

    void etChangedListener(EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                btnSaveChanges.setEnabled(true);
            }
        });
    }

    void etPasswordChangedListener(EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                btn_save_password.setEnabled(true);
            }
        });
    }

    public void showHorProg() {
        progressBarHorizon.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBarHorizon.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        progressBarHorizon.setLayoutParams(layoutParams);
    }

    public void hideHorProg() {
        progressBarHorizon.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBarHorizon.getLayoutParams();
        layoutParams.height = 0;
        progressBarHorizon.setLayoutParams(layoutParams);
    }


    ////         Listeners
    LoginResponseListener editProfileResponseListener = new LoginResponseListener() {
        @Override
        public void didLogin(LoginResponse body, String message) {

            hideHorProg();
            if (body.getStatus().equals("Request Ok")){
                Toast.makeText(EditProfileActivity.this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
                if (!email.equals(body.getPrincipal().getEmail())){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", body.getPrincipal().getEmail());
                    editor.apply();
                }
            }else {
                Toast.makeText(EditProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void didError(String message) {
            hideHorProg();
            Toast.makeText(EditProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_SHORT).show();
        }
    };

    LoginResponseListener editPasswordResponseListener = new LoginResponseListener() {
        @Override
        public void didLogin(LoginResponse body, String message) {
            hideHorProg();
            if (body.getStatus().equals("Request Ok")) {
                Toast.makeText(EditProfileActivity.this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("password", newPassword);
                editor.apply();
            } else {
                Toast.makeText(EditProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void didError(String message) {
            hideHorProg();
            Toast.makeText(EditProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_SHORT).show();
        }
    };
}