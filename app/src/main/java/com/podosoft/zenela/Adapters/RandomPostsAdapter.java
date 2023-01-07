package com.podosoft.zenela.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.podosoft.zenela.Dto.CommentDto;
import com.podosoft.zenela.Interfaces.ProgressHorizonListener;
import com.podosoft.zenela.Listeners.CommentResponseListener;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.ProfileResponseListener;
import com.podosoft.zenela.Models.Comment;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.CommentResponse;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.ViewProfileActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RandomPostsAdapter extends RecyclerView.Adapter<RandomPostViewHolder>{
    Context context;
    List<Post> posts;
    RequestManager manager;
    RequestManagerProfile managerProfile;
    Long principalId;
    User principal;
    TextView likedArea;


    // Recycler view
    RecyclerView postCommentsRecyclerView;
    PostCommentsAdapter commentsAdapter;

    ProgressHorizonListener progressHorizonListener;

    public RandomPostsAdapter(Context context, List<Post> posts, User principal, RequestManager manager, Long principalId, RequestManagerProfile managerProfile) {
        this.context = context;
        this.posts = posts;
        this.principal = principal;
        this.manager = manager;
        this.principalId = principalId;
        this.managerProfile = managerProfile;
        progressHorizonListener = (ProgressHorizonListener) context;
    }


    @NonNull
    @Override
    public RandomPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomPostViewHolder(LayoutInflater.from(context).inflate(R.layout.random_posts_cardview, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RandomPostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.poster_name.setText(post.getPosterName());

        // Date
        Locale loc = new Locale(context.getString(R.string.date_language), context.getString(R.string.date_country));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
        String date = dateFormat.format(post.getCreatedAt());
        holder.post_date.setText(date);

        final boolean[] expand = {false};

        // show poster comment based on string length
        if (post.getComment().length() < 100){
            holder.poster_comment.setText(post.getComment());
        }
        else {
            holder.poster_comment.setText(MessageFormat.format("{0}  ...{1}", post.getComment().subSequence(0, 100), context.getString(R.string.see_more)));

            holder.poster_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!expand[0]){
                        holder.poster_comment.setText(post.getComment());
                        expand[0] = true;
                    }
                    else{
                        holder.poster_comment.setText(MessageFormat.format("{0}  ...{1}", post.getComment().subSequence(0, 100), context.getString(R.string.see_more)));
                        expand[0] = false;
                    }

                }
            });
        }

        holder.textView_likes.setText(String.format("%s %s", String.valueOf(post.getLikingPossibility().getTotalLikes()), context.getString(R.string.likes)));
        holder.textView_comments.setText(String.format("%s %s", String.valueOf(post.getTotalComments()), context.getString(R.string.comments)));

        Picasso.get().load(post.getPosterProfile()).placeholder(R.drawable.profile2).error(R.drawable.profile2).into(holder.poster_profile);
        Picasso.get().load(post.getFileName()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(holder.imageView_post);

        // Liking Possibility
        if (post.getLikingPossibility().getPossible() == 0){
            holder.ic_likes.setImageResource(R.drawable.ic_like_orange);
        }else {
            holder.ic_likes.setImageResource(R.drawable.ic_like);
        }
        // like button clicked
        holder.layout_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likedArea = holder.textView_likes;
                if (post.getLikingPossibility().getPossible() == 1){
                    holder.ic_likes.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_orange));
                    post.getLikingPossibility().setPossible(0);
                    manager.likeAPost(likeResponseListener, principalId, post.getId());
                }
                else {
                    holder.ic_likes.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                    post.getLikingPossibility().setPossible(1);
                    manager.unlikeAPost(likeResponseListener, principalId, post.getId());
                }
            }
        });


        //  Comments Button Clicked
        holder.layout_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //bottomSheet
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        context, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(context.getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_comments,
                                (LinearLayout) view.findViewById(R.id.bottomSheetContainer)
                        );

                // Populate Comments
                postCommentsRecyclerView = bottomSheetView.findViewById(R.id.recycler_post_comments);
                postCommentsRecyclerView.setHasFixedSize(true);
                postCommentsRecyclerView.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), 1));

                populateCommentsRecycler((List<Comment>) post.getCommentsList());

                //  Send Comment
                bottomSheetView.findViewById(R.id.btn_send_comment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText etComment = bottomSheetView.findViewById(R.id.et_comment);
                        String myComment = etComment.getText().toString();
                        if (!myComment.equals("")){

                            CommentDto commentDto = new CommentDto(post.getId(), myComment);
                            manager.commentAPost(commentPostResponseListener, commentDto, principalId);
                            Comment comment = new Comment(principal.getId(), post.getId(), myComment, new Date(), principal.getFirstName() + " " + principal.getLastName(), principal.getProfile());
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
            holder.ic_save.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_save_in_black));
        }
        else if (post.getSavingPossibility() == 0){
            holder.ic_save.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_save_orange));
        }

        holder.layout_save_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getSavingPossibility() == 1){
                    holder.ic_save.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_save_orange));
                    post.setSavingPossibility(0);
                    manager.saveAPost(savePostResponseListener, principalId, post.getId());
                }
                else if (post.getSavingPossibility() == 0){
                    holder.ic_save.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_save_in_black));
                    post.setSavingPossibility(1);
                    manager.unSaveAPost(unSavePostResponseListener, principalId, post.getId());
                }
            }
        });

        //  View Profile of Poster
        holder.poster_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressHorizonListener.showHorProg();
                managerProfile.viewProfile(profileResponseListener, post.getPosterId(), principalId);
            }
        });
        holder.poster_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressHorizonListener.showHorProg();
                managerProfile.viewProfile(profileResponseListener, post.getPosterId(), principalId);
            }
        });

        // Actions Button Clicked
        holder.iv_post_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bottomSheet
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        context, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(context.getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_post_menu,
                                (LinearLayout) view.findViewById(R.id.bottomSheetPostMenu)
                        );

                // Report Clicked
                bottomSheetView.findViewById(R.id.btn_report).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //bottomSheet
                        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(
                                context, R.style.BottomSheetDialogTheme
                        );

                        View bottomSheetView2 = LayoutInflater.from(context.getApplicationContext())
                                .inflate(
                                        R.layout.bottom_sheet_post_report,
                                        (LinearLayout) view.findViewById(R.id.bottomSheetPostReport)
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

                                    manager.reportPost(reportPostResponseListener, principalId, post.getId(), level, reason, reportComment);

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

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private final LoginResponseListener likeResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            if (likedArea != null)
                likedArea.setText(String.format("%s %s", response.getBody(), context.getString(R.string.likes)));
        }
    };

    private final CommentResponseListener commentPostResponseListener = new CommentResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didLogin(CommentResponse response, String message) {
            Toast.makeText(context, context.getString(R.string.sent), Toast.LENGTH_SHORT).show();
        }
    };

    private final ProfileResponseListener profileResponseListener = new ProfileResponseListener() {
        @Override
        public void didFetch(ProfileResponse response, String message) {
            Intent intent = new Intent(context, ViewProfileActivity.class);
            intent.putExtra("ProfileResponse", response);
            progressHorizonListener.hideHorProg();
            context.startActivity(intent);
        }

        @Override
        public void didError(String message) {
            progressHorizonListener.hideHorProg();
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }
    };

    private final LoginResponseListener savePostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show();
        }
    };

    private final LoginResponseListener unSavePostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(context, context.getString(R.string.unsaved), Toast.LENGTH_SHORT).show();
        }
    };

    private final LoginResponseListener reportPostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(context, context.getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
        }
    };

    private void populateCommentsRecycler(List<Comment> comments){
        commentsAdapter = new PostCommentsAdapter(context, comments, principalId);
        postCommentsRecyclerView.setAdapter(commentsAdapter);
    }
}





class RandomPostViewHolder extends RecyclerView.ViewHolder{
    CardView random_posts_container;
    TextView poster_name, poster_comment, post_date, textView_likes, textView_comments, textView_share;
    ImageView poster_profile, imageView_post, ic_likes, ic_comments, ic_save, iv_post_actions;
    LinearLayout layout_likes, layout_comments, layout_save_post;

    public RandomPostViewHolder(@NonNull View itemView) {
        super(itemView);

        random_posts_container = itemView.findViewById(R.id.random_posts_container);
        poster_name = itemView.findViewById(R.id.poster_name);
        poster_profile = itemView.findViewById(R.id.poster_profile);
        imageView_post = itemView.findViewById(R.id.imageView_post);

        poster_comment = itemView.findViewById(R.id.poster_comment);
        post_date = itemView.findViewById(R.id.post_date);

        textView_likes = itemView.findViewById(R.id.textView_likes);
        textView_comments = itemView.findViewById(R.id.textView_comments);
        textView_share = itemView.findViewById(R.id.textView_share);

        ic_likes = itemView.findViewById(R.id.ic_likes);
        ic_comments = itemView.findViewById(R.id.ic_comments);
        ic_save = itemView.findViewById(R.id.ic_save);
        iv_post_actions = itemView.findViewById(R.id.iv_post_actions);

        layout_likes = itemView.findViewById(R.id.layout_likes);
        layout_comments = itemView.findViewById(R.id.layout_comments);
        layout_save_post = itemView.findViewById(R.id.layout_save_post);
    }


}