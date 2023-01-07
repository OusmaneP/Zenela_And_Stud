package com.podosoft.zenela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Dto.UserDto;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Responses.LoginResponse;

public class MainActivity extends ThemeSettingsActivity {

    Button btn_sign_in;
    RequestManager manager;

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Layout
        btn_sign_in = findViewById(R.id.btn_sign_in);

        sharedPreferences = getSharedPreferences("login", 0);

        // Try to login via sharedPref
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);
        if (email != null && password != null){
            UserDto userDto = new UserDto(email, password);
            manager = new RequestManager(MainActivity.this);
            manager.login(loginResponseListener, userDto);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToSignIn();
                }
            }, 500);
        }



    }
    // End Of OnCreate Method

    // Sign In Go to
    public void goToSignIn(){
        Intent intent = new Intent(MainActivity.this, RegisterLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private final LoginResponseListener loginResponseListener = new LoginResponseListener() {
        @Override
        public void didLogin(LoginResponse response, String message) {

            if (String.valueOf(response.getStatus()).equals("Request Ok")) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("loginResponse", (LoginResponse) response);
                startActivity(intent);
                finish();
            }

        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, "❌ Connexion failed \n" + message, Toast.LENGTH_LONG).show();
            goToSignIn();
        }
    };
}