package com.podosoft.zenela;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.podosoft.zenela.Adapters.FriendAdapter;
import com.podosoft.zenela.Fragments.ProfilePostsFragment;
import com.podosoft.zenela.Interfaces.ProgressHorizonListener;
import com.podosoft.zenela.Listeners.ProfileResponseListener;
import com.podosoft.zenela.Listeners.RandomPostsResponseListener;
import com.podosoft.zenela.Listeners.RandomVideosResponseListener;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.MyHelpers.GeneralHelper;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.Responses.RandomPostsResponse;
import com.podosoft.zenela.Responses.RandomVideosResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends ThemeSettingsActivity implements ProgressHorizonListener {

    RequestManagerProfile managerProfile;

    ImageView iv_cover, iv_profile, iv_chg_cover, iv_chg_profile, iv_layout_prof_images, iv_layout_prof_saved, iv_layout_prof_videos;
    TextView tv_name, tv_friends, tv_request, tv_invited, tv_bio;
    Button btn_create_post, btn_edit_profile;
    ProgressBar progressBarHorizon;

    LinearLayout layout_prof_videos, layout_prof_images, layout_myFriends, layout_FriendRequest,
            layout_InvitedFriends, layout_saved_post;

    SharedPreferences sharedPreferences = null;

    ArrayList<Post> myPost = new ArrayList<>();
    ArrayList<Post> savedPosts = new ArrayList<>();
    ArrayList<Post> myVideos = new ArrayList<>();
    ProfileResponse profileResponse;
    long principalId;
    User principal;

    RecyclerView friendsRecyclerView;
    FriendAdapter friendAdapter;

    int friendRequestExtra;

    GeneralHelper generalHelper;
    // onCreate
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        /////////   Toolbar   //////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        

        // init views
        generalHelper = new GeneralHelper();

        iv_cover = findViewById(R.id.iv_cover);
        iv_profile = findViewById(R.id.iv_profile);
        tv_name = findViewById(R.id.tv_name);
        tv_friends = findViewById(R.id.tv_friends);
        tv_request = findViewById(R.id.tv_request);
        tv_invited = findViewById(R.id.tv_invited);
        iv_chg_cover = findViewById(R.id.iv_chg_cover);
        iv_chg_profile = findViewById(R.id.iv_chg_profile);
        tv_bio = findViewById(R.id.tv_bio);

        layout_prof_images = findViewById(R.id.layout_prof_images);
        layout_prof_videos = findViewById(R.id.layout_prof_videos);
        layout_myFriends = findViewById(R.id.layout_myFriends);
        layout_FriendRequest = findViewById(R.id.layout_FriendRequest);
        layout_InvitedFriends = findViewById(R.id.layout_InvitedFriends);
        btn_create_post = findViewById(R.id.btn_create_post);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        layout_saved_post = findViewById(R.id.layout_saved_post);
        iv_layout_prof_saved = findViewById(R.id.iv_layout_prof_saved);
        iv_layout_prof_images = findViewById(R.id.iv_layout_prof_images);
        iv_layout_prof_videos = findViewById(R.id.iv_layout_prof_videos);
        progressBarHorizon = findViewById(R.id.progressBarHorizon);

        friendRequestExtra = getIntent().getIntExtra("FriendRequestExtra", 0);

        // LinearLayout Albums Clicked  Show Hide Albums
        iv_layout_prof_images.setImageDrawable(getResources().getDrawable(R.drawable.ic_posts_orange));

        //
        sharedPreferences = MyProfileActivity.this.getSharedPreferences("login", 0);
        String email = sharedPreferences.getString("email", null);
        principalId = sharedPreferences.getLong("principalId", 0);


        //   manager go to profile
        if (email != null){
            showHorProg();
            managerProfile = new RequestManagerProfile(MyProfileActivity.this);
            managerProfile.goToProfile(profileResponseListener, email);
        }

        // // show My Friends
        layout_myFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFriendsBottomSheet(view, profileResponse.getMyFriends(), getString(R.string.friends), "All your friends will be displayed here, just invite them");
            }
        });

        // // show Friend Requests
        layout_FriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFriendsBottomSheet(view, profileResponse.getFriendRequests(), getString(R.string.f_requests), "All your friend-requests will be displayed here, whenever they invite you");
            }
        });

        // // Invited Friends
        layout_InvitedFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFriendsBottomSheet(view, profileResponse.getInvitedFriends(), getString(R.string.f_invited), "All the people you invite will be displayed here, just invite them");
            }
        });

        // create post clicked
        btn_create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, PostImageActivity.class);
                intent.putExtra("type", getString(R.string.post_image));
                startActivity(intent);
            }
        });

        // change cover
        iv_chg_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, PostImageActivity.class);
                intent.putExtra("type", getString(R.string.cover));
                startActivity(intent);
            }
        });

        // change profile
        iv_chg_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, PostImageActivity.class);
                intent.putExtra("type", getString(R.string.profile));
                startActivity(intent);
            }
        });

        // Profile Photo Clicked
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, ViewPhotoActivity.class);
                intent.putExtra("photo_link", principal.getProfile());
                startActivity(intent);
            }
        });

        // Cover Photo Clicked
        iv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, ViewPhotoActivity.class);
                intent.putExtra("photo_link", principal.getCover());
                startActivity(intent);
            }
        });

        //  My Posts images
        layout_prof_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_layout_prof_images.setImageDrawable(getResources().getDrawable(R.drawable.ic_posts_orange));
                iv_layout_prof_saved.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_in_black));
                iv_layout_prof_videos.setImageResource(R.drawable.ic_videos_black);
                populateFragment();
            }
        });

        //  Saved Posts
        layout_saved_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_layout_prof_images.setImageDrawable(getResources().getDrawable(R.drawable.ic_posts));
                iv_layout_prof_videos.setImageResource(R.drawable.ic_videos_black);
                iv_layout_prof_saved.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_orange));
                if (savedPosts.isEmpty()) {
                    showHorProg();
                    managerProfile.findSavedPosts(savedPostsResponseListener, principalId);
                }
                else{
                    populateFragmentSavedPosts();
                }
            }
        });

        //  My Videos
        layout_prof_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_layout_prof_images.setImageDrawable(getResources().getDrawable(R.drawable.ic_posts));
                iv_layout_prof_saved.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_in_black));
                iv_layout_prof_videos.setImageResource(R.drawable.ic_videos_orange);

                if (myVideos.isEmpty()){
                    showHorProg();
                    managerProfile.findMyVideos(myVideosResponseListener, principalId);
                }else{
                    populateFragmentVideos();
                }
            }
        });

        //  Edit Profile
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("Principal", principal);
                startActivity(intent);
            }
        });



    }

    private void showFriendsBottomSheet(View view, List<User> userList, String titleText, String textDisplayFriends){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                MyProfileActivity.this, R.style.BottomSheetDialogTheme
        );

        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.bottom_sheet_friends,
                        (LinearLayout) view.findViewById(R.id.bottomSheetFriendsContainer)
                );


        TextView title = bottomSheetView.findViewById(R.id.title);
        title.setText(titleText);

        SearchView searchView = bottomSheetView.findViewById(R.id.searchView);

        if (userList.isEmpty()) {
            TextView tv_display_friends = bottomSheetView.findViewById(R.id.tv_display_friends);
            tv_display_friends.setText(textDisplayFriends);

            tv_display_friends.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams2 = tv_display_friends.getLayoutParams();
            layoutParams2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tv_display_friends.setLayoutParams(layoutParams2);
        }
        else{
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (newText.length() > 0){
                        List<User> userListSearched = new ArrayList<>();
                        for (User user : userList){
                            if (user.getFirstName().contains(newText) || user.getLastName().contains(newText)){
                                userListSearched.add(user);
                            }
                        }
                        friendAdapter = new FriendAdapter(MyProfileActivity.this, userListSearched, principalId, titleText);
                        friendsRecyclerView.setAdapter(friendAdapter);
                    }

                    return false;
                }
            });
        }

        friendsRecyclerView = bottomSheetView.findViewById(R.id.recycler_friends);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new GridLayoutManager(MyProfileActivity.this, 1));
        friendAdapter = new FriendAdapter(MyProfileActivity.this, userList, principalId, titleText);
        friendsRecyclerView.setAdapter(friendAdapter);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    /////////////////////////////    Listener

    private final ProfileResponseListener profileResponseListener = new ProfileResponseListener() {
        @Override
        public void didFetch(ProfileResponse response, String message) {
            populateProfile(response);
            hideHorProg();
            myPost = response.getPosts();
            principal = response.getPrincipal();
            profileResponse = response;
            populateFragment();
            if (friendRequestExtra == 1){
                layout_FriendRequest.callOnClick();
            }
        }

        @Override
        public void didError(String message) {
            hideHorProg();
            Toast.makeText(MyProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_LONG).show();
        }
    };

    // Saved Post Response Listener
    private final RandomPostsResponseListener savedPostsResponseListener = new RandomPostsResponseListener() {
        @Override
        public void didFetch(RandomPostsResponse response, String message) {
            savedPosts = response.getPosts();
            hideHorProg();
            populateFragmentSavedPosts();
        }

        @Override
        public void didError(String message) {
            hideHorProg();
            Toast.makeText(MyProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_LONG).show();
        }
    };

    // Random VideoResponseListener
    private final RandomVideosResponseListener myVideosResponseListener = new RandomVideosResponseListener() {
        @Override
        public void didFetch(RandomVideosResponse response, String message) {
            myVideos = response.getPosts();
            hideHorProg();
            populateFragmentVideos();
        }

        @Override
        public void didError(String message) {
            hideHorProg();
            Toast.makeText(MyProfileActivity.this, getString(R.string.connection_failed_uc), Toast.LENGTH_SHORT).show();
        }
    };

    // populate profile
    private void populateProfile(ProfileResponse response){
        tv_name.setText(String.format("@%s %s", response.getPrincipal().getFirstName(), response.getPrincipal().getLastName()));
        tv_friends.setText(String.format("%s", generalHelper.convertBigNumber(response.getMyFriends().size())));
        tv_request.setText(String.format("%s", generalHelper.convertBigNumber(response.getFriendRequests().size())));
        tv_invited.setText(String.format("%s", generalHelper.convertBigNumber(response.getInvitedFriends().size())));

        // bio
        if (response.getPrincipal().getBio().trim().length() < 1){
            hideViewObject(tv_bio);
        }else{
            tv_bio.setText(String.format("%s", response.getPrincipal().getBio()));
        }


        Picasso.get().load(response.getPrincipal().getCover()).placeholder(R.drawable.placeholder_image).into(iv_cover);
        Picasso.get().load(response.getPrincipal().getProfileThumb() == null || response.getPrincipal().getProfileThumb().isEmpty() ? response.getPrincipal().getProfile() : response.getPrincipal().getProfileThumb()).placeholder(R.drawable.profile3).into(iv_profile);
    }

    // populateFragment
    private void populateFragment(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("myPosts", myPost);
        bundle.putLong("principalId", principalId);
        bundle.putString("type", "posts");
        ProfilePostsFragment fragment = new ProfilePostsFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_profile_posts_container, fragment);
        transaction.commit();
    }

    // populateFragment
    private void populateFragmentSavedPosts(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("myPosts", savedPosts);
        bundle.putLong("principalId", principalId);
        bundle.putString("type", "saved");
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
        bundle.putString("type", "my_videos");
        ProfilePostsFragment fragment = new ProfilePostsFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_profile_posts_container, fragment);
        transaction.commit();
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

    // show View Object
    public void showViewObject(View view){
        view.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(layoutParams);
    }

    // hide View Object
    public void hideViewObject(View view){
        // hide button
        view.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = 0;
        layoutParams.height = 0;
        view.setLayoutParams(layoutParams);
    }

}