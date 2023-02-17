package com.podosoft.zenela.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.podosoft.zenela.Dto.CommentDto;
import com.podosoft.zenela.Listeners.CommentResponseListener;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.ProfileResponseListener;
import com.podosoft.zenela.Models.Comment;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.MyHelpers.GeneralHelper;
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

public class MyProfilePostsAdapter extends RecyclerView.Adapter<MyProfilePostsViewHolder>{
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

    GeneralHelper generalHelper;

    public MyProfilePostsAdapter(Context context, List<Post> posts, User principal, RequestManager manager, Long principalId, RequestManagerProfile managerProfile) {
        this.context = context;
        this.posts = posts;
        this.principal = principal;
        this.manager = manager;
        this.principalId = principalId;
        this.managerProfile = managerProfile;
        this.generalHelper = new GeneralHelper();
    }


    @NonNull
    @Override
    public MyProfilePostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyProfilePostsViewHolder(LayoutInflater.from(context).inflate(R.layout.my_profile_posts_cardview, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyProfilePostsViewHolder holder, int position) {
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

        holder.textView_likes.setText(String.format("%s %s", generalHelper.convertBigNumber(post.getLikingPossibility().getTotalLikes()), context.getString(R.string.likes)));
        holder.textView_comments.setText(String.format("%s %s", generalHelper.convertBigNumber(post.getTotalComments()), context.getString(R.string.comments)));

        Picasso.get().load(post.getPosterProfile()).placeholder(R.drawable.profile2).error(R.drawable.profile2).into(holder.poster_profile);
        Picasso.get().load(post.getFileName()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(holder.imageView_post);



        // Liking Possibility
        if (post.getLikingPossibility().getPossible() == 0){
            holder.ic_likes.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_orange));
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

        //  Bottom Sheet Actions
        holder.btn_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bottomSheet
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        context, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(context.getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_my_post_menu,
                                (LinearLayout) view.findViewById(R.id.bottomSheetMyPostMenu)
                        );

                bottomSheetView.findViewById(R.id.btn_delete_post).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.hide();
                        Toast.makeText(context, context.getString(R.string.deleting), Toast.LENGTH_SHORT).show();
                        managerProfile.deletePost(deleteResponseListener, principalId, post.getId());

                        posts.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), posts.size());
                    }
                });
                Button btnEditPost, btnCancel, btnSubmit;
                EditText etComment;
                LinearLayout layout_post_actions, layout_edit_post_comment;

                btnEditPost = bottomSheetView.findViewById(R.id.btn_edit_post);
                btnCancel = bottomSheetView.findViewById(R.id.btn_cancel);
                btnSubmit = bottomSheetView.findViewById(R.id.btn_submit);
                etComment = bottomSheetView.findViewById(R.id.et_comment);
                layout_post_actions = bottomSheetView.findViewById(R.id.layout_post_actions);
                layout_edit_post_comment = bottomSheetView.findViewById(R.id.layout_edit_post_comment);

                etComment.setText(post.getComment());
                // show Edit Comment
                btnEditPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        layout_post_actions.setVisibility(View.INVISIBLE);
                        ViewGroup.LayoutParams layoutParams = layout_post_actions.getLayoutParams();
                        layoutParams.height = 0;
                        layout_post_actions.setLayoutParams(layoutParams);

                        layout_edit_post_comment.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams layoutParams2 = layout_edit_post_comment.getLayoutParams();
                        layoutParams2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        layout_edit_post_comment.setLayoutParams(layoutParams2);
                    }
                });

                // Hide Edit Comment
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        layout_edit_post_comment.setVisibility(View.INVISIBLE);
                        ViewGroup.LayoutParams layoutParams = layout_edit_post_comment.getLayoutParams();
                        layoutParams.height = 0;
                        layout_edit_post_comment.setLayoutParams(layoutParams);

                        layout_post_actions.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams layoutParams2 = layout_post_actions.getLayoutParams();
                        layoutParams2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        layout_post_actions.setLayoutParams(layoutParams2);
                    }
                });

                // Submit Comment
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etComment.getText().toString().length() > 0) {
                            managerProfile.updatePostComment(updatePostResponseListener, principalId, post.getId(), etComment.getText().toString());
                            bottomSheetDialog.hide();
                        }

                    }
                });



                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });



        // Save Post Clicked
        holder.ic_save.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_save_in_black));
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
//        holder.poster_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                managerProfile.viewProfile(profileResponseListener, post.getPosterId(), principalId);
//            }
//        });
//        holder.poster_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                managerProfile.viewProfile(profileResponseListener, post.getPosterId(), principalId);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private final LoginResponseListener likeResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, "❌  " + message, Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "❌  " + message, Toast.LENGTH_LONG).show();
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
            context.startActivity(intent);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(context, "❌ " + message, Toast.LENGTH_LONG).show();
        }
    };

    private final LoginResponseListener savePostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show();
        }
    };

    private final LoginResponseListener unSavePostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(context, context.getString(R.string.unsaved), Toast.LENGTH_SHORT).show();
        }
    };

    private final LoginResponseListener deleteResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            if (response.getStatus().equals("Request Ok")){
                Toast.makeText(context, context.getString(R.string.deleted_ic), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "⚠️ " + response.getBody(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private final LoginResponseListener updatePostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, "❌  " + message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void didLogin(LoginResponse response, String message) {
            Toast.makeText(context, "✅", Toast.LENGTH_SHORT).show();
        }
    };

    /////////////////////        Privates methods
    private void populateCommentsRecycler(List<Comment> comments){
        commentsAdapter = new PostCommentsAdapter(context, comments, principalId);
        postCommentsRecyclerView.setAdapter(commentsAdapter);
    }

}


class MyProfilePostsViewHolder extends RecyclerView.ViewHolder{
    CardView random_posts_container;
    TextView poster_name, poster_comment, post_date, textView_likes, textView_comments, textView_share;
    ImageView poster_profile, imageView_post, ic_likes, ic_comments, ic_save, btn_actions;
    LinearLayout layout_likes, layout_comments, layout_save_post;


    public MyProfilePostsViewHolder(@NonNull View itemView) {
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

        layout_likes = itemView.findViewById(R.id.layout_likes);
        layout_comments = itemView.findViewById(R.id.layout_comments);
        layout_save_post = itemView.findViewById(R.id.layout_save_post);

        btn_actions = itemView.findViewById(R.id.btn_actions);
    }


}