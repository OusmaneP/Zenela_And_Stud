package com.podosoft.zenela;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.podosoft.zenela.Adapters.PostCommentsAdapter;
import com.podosoft.zenela.Dto.CommentDto;
import com.podosoft.zenela.Listeners.CommentResponseListener;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.ProfileResponseListener;
import com.podosoft.zenela.Models.Comment;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.CommentResponse;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.Responses.ViewPostResponse;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewPostActivity extends AppCompatActivity {

    CardView random_posts_container;
    TextView poster_name, poster_comment, post_date, textView_likes, textView_comments, textView_share;
    ImageView poster_profile, imageView_post, ic_likes, ic_comments, ic_save, iv_post_actions;
    LinearLayout layout_likes, layout_comments, layout_save_post;

    TextView likedArea;
    RequestManager manager;
    RequestManagerProfile managerProfile;
    ViewPostResponse viewPostResponse;

    // Recycler view
    RecyclerView postCommentsRecyclerView;
    PostCommentsAdapter commentsAdapter;

    SharedPreferences sharedPreferences = null;
    Long principalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        random_posts_container = findViewById(R.id.random_posts_container);
        poster_name = findViewById(R.id.poster_name);
        poster_profile = findViewById(R.id.poster_profile);
        imageView_post = findViewById(R.id.imageView_post);

        poster_comment = findViewById(R.id.poster_comment);
        post_date = findViewById(R.id.post_date);

        textView_likes = findViewById(R.id.textView_likes);
        textView_comments = findViewById(R.id.textView_comments);
        textView_share = findViewById(R.id.textView_share);

        ic_likes = findViewById(R.id.ic_likes);
        ic_comments = findViewById(R.id.ic_comments);
        ic_save = findViewById(R.id.ic_save);
        iv_post_actions = findViewById(R.id.iv_post_actions);

        layout_likes = findViewById(R.id.layout_likes);
        layout_comments = findViewById(R.id.layout_comments);
        layout_save_post = findViewById(R.id.layout_save_post);

        Intent intent = getIntent();
        viewPostResponse = (ViewPostResponse) intent.getSerializableExtra("ViewPostResponse");

        manager = new RequestManager(this);
        managerProfile = new RequestManagerProfile(this);
        populatePost(viewPostResponse.getPost());

        sharedPreferences = ViewPostActivity.this.getSharedPreferences("login", 0);
        principalId = sharedPreferences.getLong("principalId", 0);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void populatePost(Post post) {
        poster_name.setText(post.getPosterName());
        // Date
        Locale loc = new Locale(getString(R.string.date_language), getString(R.string.date_country));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
        String date = dateFormat.format(post.getCreatedAt());
        post_date.setText(date);


        final boolean[] expand = {false};

        if (post.getComment().length() < 100){
            poster_comment.setText(post.getComment());
        }
        else {
            poster_comment.setText(MessageFormat.format("{0}  ...{1}", post.getComment().subSequence(0, 100), getString(R.string.see_more)));

            poster_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!expand[0]){
                        poster_comment.setText(post.getComment());
                        expand[0] = true;
                    }
                    else{
                        poster_comment.setText(MessageFormat.format("{0}  ...{1}", post.getComment().subSequence(0, 100), getString(R.string.see_more)));
                        expand[0] = false;
                    }

                }
            });
        }

        textView_likes.setText(String.format("%s %s", String.valueOf(post.getLikingPossibility().getTotalLikes()), getString(R.string.likes)));
        textView_comments.setText(String.format("%s %s", String.valueOf(post.getTotalComments()), getString(R.string.comments)));

        Picasso.get().load(post.getPosterProfile()).placeholder(R.drawable.profile2).error(R.drawable.profile2).into(poster_profile);

        if (post.getType().contains("image")) {
            Picasso.get().load(post.getFileName()).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(imageView_post);
        }
        else if(post.getType().contains("video")){
            Picasso.get().load(post.getThumbnail()).placeholder(R.drawable.video_placeholder).error(R.drawable.video_placeholder).into(imageView_post);

            imageView_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (post.getFileName() != null && !post.getFileName().trim().isEmpty()) {
                        Intent intent = new Intent(ViewPostActivity.this, ViewVideoActivity.class);
                        intent.putExtra("videoUrl", post.getFileName());

                        startActivity(intent);
                    }
                }
            });
        }
        // Liking Possibility
        if (post.getLikingPossibility().getPossible() == 0){
            ic_likes.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_orange));
        }else {
            ic_likes.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
        }
        // like button clicked
        layout_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likedArea = textView_likes;
                if (post.getLikingPossibility().getPossible() == 1){
                    ic_likes.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_orange));
                    post.getLikingPossibility().setPossible(0);
                    manager.likeAPost(likeResponseListener, viewPostResponse.getPrincipal().getId(), post.getId());
                }
                else {
                    ic_likes.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    post.getLikingPossibility().setPossible(1);
                    manager.unlikeAPost(likeResponseListener, viewPostResponse.getPrincipal().getId(), post.getId());
                }
            }
        });

        //  Comments Button Clicked
        layout_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //bottomSheet
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        ViewPostActivity.this, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_comments,
                                (LinearLayout) view.findViewById(R.id.bottomSheetContainer)
                        );

                // Populate Comments
                postCommentsRecyclerView = bottomSheetView.findViewById(R.id.recycler_post_comments);
                postCommentsRecyclerView.setHasFixedSize(true);
                postCommentsRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

                populateCommentsRecycler((List<Comment>) post.getCommentsList());

                //  Send Comment
                bottomSheetView.findViewById(R.id.btn_send_comment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText etComment = bottomSheetView.findViewById(R.id.et_comment);
                        String myComment = etComment.getText().toString();
                        if (!myComment.equals("")){

                            CommentDto commentDto = new CommentDto(post.getId(), myComment);
                            manager.commentAPost(commentPostResponseListener, commentDto, viewPostResponse.getPrincipal().getId());
                            Comment comment = new Comment(viewPostResponse.getPrincipal().getId(), post.getId(), myComment, new Date(), viewPostResponse.getPrincipal().getFirstName() + " " + viewPostResponse.getPrincipal().getLastName(), viewPostResponse.getPrincipal().getProfile());
                            post.getCommentsList().add(0, comment);
                            populateCommentsRecycler(post.getCommentsList());
                        }
                    }
                });



                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();


            }
        });


        // Save Post Clicked
        if (post.getSavingPossibility() == 1){
            ic_save.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_in_black));
        }
        else if (post.getSavingPossibility() == 0){
            ic_save.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_orange));
        }

        layout_save_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getSavingPossibility() == 1){
                    ic_save.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_orange));
                    post.setSavingPossibility(0);
                    manager.saveAPost(savePostResponseListener, viewPostResponse.getPrincipal().getId(), post.getId());
                }
                else if (post.getSavingPossibility() == 0){
                    ic_save.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_in_black));
                    post.setSavingPossibility(1);
                    manager.unSaveAPost(unSavePostResponseListener, viewPostResponse.getPrincipal().getId(), post.getId());
                }
            }
        });

        //  View Profile of Poster
        poster_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managerProfile.viewProfile(profileResponseListener, post.getPosterId(), viewPostResponse.getPrincipal().getId());
            }
        });
        poster_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managerProfile.viewProfile(profileResponseListener, post.getPosterId(), viewPostResponse.getPrincipal().getId());
            }
        });

        // Actions Button Clicked
        iv_post_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bottomSheet
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        ViewPostActivity.this, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_post_menu,
                                (LinearLayout) view.findViewById(R.id.bottomSheetContainer)
                        );

                // Report Clicked
                bottomSheetView.findViewById(R.id.btn_report).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //bottomSheet
                        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(
                                ViewPostActivity.this, R.style.BottomSheetDialogTheme
                        );

                        View bottomSheetView2 = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.bottom_sheet_post_report,
                                        (LinearLayout) view.findViewById(R.id.bottomSheetContainer)
                                );

                        bottomSheetView2.findViewById(R.id.btn_send_report).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RadioGroup radioGroup = bottomSheetView2.findViewById(R.id.radio_grp_report_reason);
                                int checkedId = radioGroup.getCheckedRadioButtonId();

                                int level = 0;

                                if (checkedId == -1 ){

                                }else{
                                    switch (checkedId) {
                                        case R.id.cb_sex_nude:
                                            level = 1;
                                            break;

                                        case R.id.cb_violence:
                                            level = 1;
                                            break;

                                        case R.id.cb_terrorism:
                                            level = 1;
                                            break;

                                        case R.id.cb_harassment:
                                            level = 2;
                                            break;

                                        case R.id.cb_fraud:
                                            level = 2;
                                            break;

                                        case R.id.cb_injury:
                                            level = 3;
                                            break;

                                        case R.id.cb_hate_speech:
                                            level = 3;
                                            break;

                                        case R.id.cb_false_info:
                                            level = 4;
                                            break;

                                        case R.id.cb_some_else:
                                            level = 0;
                                            break;


                                    }

                                    RadioButton radioButton = radioGroup.findViewById(checkedId);

                                    String reason = radioButton.getText().toString();

                                    EditText etReportComment = bottomSheetView2.findViewById(R.id.et_report_comment);

                                    String reportComment = etReportComment.getText().toString();

                                    manager.reportPost(reportPostResponseListener, viewPostResponse.getPrincipal().getId(), post.getId(), level, reason, reportComment);

                                    bottomSheetView2.findViewById(R.id.btn_send_report).setEnabled(false);

                                } // end of else
                            }
                        });

                        bottomSheetDialog2.setContentView(bottomSheetView2);
                        bottomSheetDialog2.show();
                    }
                });


                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });


    }




    private final LoginResponseListener likeResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(ViewPostActivity.this, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            if (likedArea != null)
                likedArea.setText(String.format("%s %s", response.getBody(), getString(R.string.likes)));
        }
    };

    private final CommentResponseListener commentPostResponseListener = new CommentResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(ViewPostActivity.this, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(CommentResponse response, String message) {
            Toast.makeText(ViewPostActivity.this, getString(R.string.sent), Toast.LENGTH_LONG).show();
        }
    };

    private final ProfileResponseListener profileResponseListener = new ProfileResponseListener() {
        @Override
        public void didFetch(ProfileResponse response, String message) {
            Intent intent = new Intent(ViewPostActivity.this, ViewProfileActivity.class);
            intent.putExtra("ProfileResponse", response);
            startActivity(intent);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(ViewPostActivity.this, "❌ " + message, Toast.LENGTH_LONG).show();
        }
    };

    private final LoginResponseListener savePostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(ViewPostActivity.this, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(ViewPostActivity.this, getString(R.string.saved), Toast.LENGTH_LONG).show();
        }
    };

    private final LoginResponseListener unSavePostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(ViewPostActivity.this, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(ViewPostActivity.this, getString(R.string.unsaved), Toast.LENGTH_LONG).show();
        }
    };

    private final LoginResponseListener reportPostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(ViewPostActivity.this, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(ViewPostActivity.this, getString(R.string.report_sent), Toast.LENGTH_LONG).show();
        }
    };


    private void populateCommentsRecycler(List<Comment> comments){
        commentsAdapter = new PostCommentsAdapter(ViewPostActivity.this, comments, principalId);
        postCommentsRecyclerView.setAdapter(commentsAdapter);
    }
}