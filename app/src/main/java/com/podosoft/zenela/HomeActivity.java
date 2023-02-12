package com.podosoft.zenela;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.podosoft.zenela.Interfaces.ProgressHorizonListener;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.databinding.ActivityHomeBinding;

import com.podosoft.zenela.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

public class HomeActivity extends ThemeSettingsActivity implements NavigationView.OnNavigationItemSelectedListener, ProgressHorizonListener {

    private ActivityHomeBinding binding;

    ImageView profileBtn, btn_search;
    LinearLayout btn_friend_request;
    TextView tv_friend_request_notification;
    ProgressBar progressBarHorizon;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///////  Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // init vars
        btn_search = binding.btnSearch;
        profileBtn = binding.profileBtn;
        btn_friend_request = binding.btnFriendRequest;
        tv_friend_request_notification = binding.tvFriendRequestNotification;
        progressBarHorizon = binding.progressBarHorizon;


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_videos, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        View view = binding.navView.findViewById(R.id.navigation_notifications);
        BottomNavigationItemView itemViewNotifications = findViewById(R.id.navigation_notifications);

        // Get Login Response
        Intent intent = getIntent();
        LoginResponse loginResponse = (LoginResponse) intent.getSerializableExtra("loginResponse");
        // Put profile picture
        Picasso.get().load(loginResponse.getPrincipal().getProfileThumb() == null || loginResponse.getPrincipal().getProfileThumb().isEmpty() ? loginResponse.getPrincipal().getProfile() : loginResponse.getPrincipal().getProfileThumb()).placeholder(R.drawable.profile2).into(profileBtn);

        // Post Notifications
        if (loginResponse.getPostNotifications() > 0){
            itemViewNotifications.setTitle(String.valueOf(loginResponse.getPostNotifications()));
            itemViewNotifications.setTextColor(ColorStateList.valueOf(Color.parseColor("#ff7782")));
        }

        // Friend Request Notifications
        if (loginResponse.getFriendRequestsNotification() > 0){
            tv_friend_request_notification.setText(String.valueOf(loginResponse.getFriendRequestsNotification()));
            tv_friend_request_notification.setTextColor(ColorStateList.valueOf(Color.parseColor("#ff7782")));
        }


        // Profile Clicked
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerLayout drawer = (DrawerLayout) binding.drawerLayout;
                if (!drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
        // Search btn Clicked
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        // Friend Request Clicked
        btn_friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                intent.putExtra("FriendRequestExtra", 1);
                startActivity(intent);
            }
        });


        String string = "my man .podo/ is out there for the dinner";
        String[] tab = string.split(".podo");

        System.out.println(tab[1]);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) binding.drawerLayout;
        drawer.closeDrawer(GravityCompat.END);

        int id = item.getItemId();

        switch (id){
            case R.id.profile_drawer:
                Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_drawer:
                Intent intent2 = new Intent(HomeActivity.this, ThemeSettingsActivity.class);
                startActivity(intent2);
                break;
            case R.id.logout_drawer:
                SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.remove("email");
//                editor.remove("password");
                editor.clear();
                editor.apply();
                goToSignIn();
                finish();
                break;
        }


        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }else {
            super.onBackPressed();
        }
    }

    // Sign In Go to
    public void goToSignIn(){
        Intent intent = new Intent(this, RegisterLoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showHorProg() {
        progressBarHorizon.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBarHorizon.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        progressBarHorizon.setLayoutParams(layoutParams);
    }

    @Override
    public void hideHorProg() {
        progressBarHorizon.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBarHorizon.getLayoutParams();
        layoutParams.height = 0;
        progressBarHorizon.setLayoutParams(layoutParams);
    }
}