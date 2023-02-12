package com.podosoft.zenela;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.podosoft.zenela.Fragments.ProfilePostsFragment;
import com.podosoft.zenela.Interfaces.ProgressHorizonListener;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.RandomVideosResponseListener;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.Responses.RandomVideosResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewProfileActivity extends ThemeSettingsActivity implements ProgressHorizonListener {

    ImageView iv_cover, iv_profile, iv_layout_prof_images, iv_layout_prof_videos;
    TextView tv_name, tv_friends, tv_request, tv_invited;
    Button btn_invite_friend, btn_message_friend;
    LinearLayout  layout_prof_videos, layout_prof_images;
    ProgressBar progressBarHorizon;

    ArrayList<Post> myPost = new ArrayList<>();
    ArrayList<Post> myVideos = new ArrayList<>();

    SharedPreferences sharedPreferences = null;

    RequestManagerProfile managerProfile;

    long principalId;
    String email;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);


        //
        sharedPreferences = this.getSharedPreferences("login", 0);
        email = sharedPreferences.getString("email", null);
        principalId = sharedPreferences.getLong("principalId", 0);

        // init views

        iv_cover = findViewById(R.id.iv_cover);
        iv_profile = findViewById(R.id.iv_profile);
        tv_name = findViewById(R.id.tv_name);
        tv_friends = findViewById(R.id.tv_friends);
        tv_request = findViewById(R.id.tv_request);
        tv_invited = findViewById(R.id.tv_invited);

        iv_layout_prof_images = findViewById(R.id.iv_layout_prof_images);
        iv_layout_prof_videos = findViewById(R.id.iv_layout_prof_videos);

        layout_prof_images = findViewById(R.id.layout_prof_images);
        layout_prof_videos = findViewById(R.id.layout_prof_videos);

        btn_invite_friend = findViewById(R.id.btn_invite_friend);
        btn_message_friend = findViewById(R.id.btn_message_friend);
        progressBarHorizon = findViewById(R.id.progressBarHorizon);

        // managerProfile
        managerProfile = new RequestManagerProfile(ViewProfileActivity.this);

        // LinearLayout Albums Clicked  Show Hide Albums


        Intent intent = getIntent();
        ProfileResponse response = (ProfileResponse) intent.getSerializableExtra("ProfileResponse");

        populateProfile(response);
        myPost = response.getPosts();
        populateFragment();

        // LinearLayout Albums Clicked  Show Hide Albums
        iv_layout_prof_images.setImageDrawable(getResources().getDrawable(R.drawable.ic_posts_orange));

        //  My Posts images
        layout_prof_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_layout_prof_images.setImageDrawable(getResources().getDrawable(R.drawable.ic_posts_orange));
                iv_layout_prof_videos.setImageResource(R.drawable.ic_videos_black);
                populateFragment();
            }
        });

        //  His Videos
        layout_prof_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_layout_prof_images.setImageDrawable(getResources().getDrawable(R.drawable.ic_posts));
                iv_layout_prof_videos.setImageResource(R.drawable.ic_videos_orange);

                if (myVideos.isEmpty()){
                    showHorProg();
                    managerProfile.findMyVideos(randomVideosResponseListener, response.getPrincipal().getId());
                }else{
                    populateFragmentVideos();
                }
            }
        });

        // Profile Photo Clicked
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, ViewPhotoActivity.class);
                intent.putExtra("photo_link", response.getPrincipal().getProfile());
                startActivity(intent);
            }
        });
    }

    // populate profile
    private void populateProfile(ProfileResponse response){
        tv_name.setText(String.format("@%s %s", response.getPrincipal().getFirstName(), response.getPrincipal().getLastName()));
        tv_friends.setText(String.format("%s", response.getMyFriends().size()));
        tv_request.setText(String.format("%s", response.getFriendRequests().size()));
        tv_invited.setText(String.format("%s", response.getInvitedFriends().size()));
        Picasso.get().load(response.getPrincipal().getCover()).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(iv_cover);
        Picasso.get().load(response.getPrincipal().getProfileThumb() == null || response.getPrincipal().getProfileThumb().isEmpty() ? response.getPrincipal().getProfile() : response.getPrincipal().getProfileThumb()).placeholder(R.drawable.profile3).error(R.drawable.profile3).into(iv_profile);
        // btn Add Friend
        if (!response.getFriends() && !response.getPrincipal().getId().equals(principalId)){
            btn_invite_friend.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = btn_invite_friend.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            btn_invite_friend.setLayoutParams(layoutParams);
            btn_invite_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    managerProfile.sendFriendRequest(inviteResponseListener, response.getPrincipal().getId(), principalId);
                }
            });
        }
        // btn Message
        if (response.getFriends() && !response.getPrincipal().getId().equals(principalId)){
            btn_message_friend.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = btn_message_friend.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            btn_message_friend.setLayoutParams(layoutParams);
        }
    }

    // populateFragment
    private void populateFragment(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("myPosts", myPost);
        bundle.putLong("principalId", principalId);
        bundle.putString("type", "view_profile_posts");
        ProfilePostsFragment fragment = new ProfilePostsFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_profile_posts_container, fragment);
        transaction.commit();
    }

    // populateFragment
    private void populateFragmentVideos(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("myPosts", myVideos);
        bundle.putLong("principalId", principalId);
        bundle.putString("type", "view_profile_videos");
        ProfilePostsFragment fragment = new ProfilePostsFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_profile_posts_container, fragment);
        transaction.commit();
    }






//********   LISTENERS ************

    private LoginResponseListener inviteResponseListener = new LoginResponseListener() {
        @Override
        public void didLogin(LoginResponse body, String message) {
            btn_invite_friend.setVisibility(View.INVISIBLE);
            Toast.makeText(ViewProfileActivity.this, getString(R.string.sent), Toast.LENGTH_LONG).show();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(ViewProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_LONG).show();
        }
    };

    // Random VideoResponseListener
    private final RandomVideosResponseListener randomVideosResponseListener = new RandomVideosResponseListener() {
        @Override
        public void didFetch(RandomVideosResponse response, String message) {
            myVideos = response.getPosts();
            hideHorProg();
            populateFragmentVideos();
        }

        @Override
        public void didError(String message) {
            hideHorProg();
            Toast.makeText(ViewProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_SHORT).show();
        }
    };

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