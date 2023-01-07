package com.podosoft.zenela.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.podosoft.zenela.Listeners.ProfileResponseListener;
import com.podosoft.zenela.Models.Comment;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.ViewProfileActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentViewHolder>{
    Context context;
    List<Comment> comments;
    RequestManagerProfile managerProfile;
    Long principalId;

    public PostCommentsAdapter(Context context, List<Comment> comments, Long principalId) {
        this.context = context;
        this.comments = comments;
        this.managerProfile = new RequestManagerProfile(context);
        this.principalId = principalId;
    }


    @NonNull
    @Override
    public PostCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.post_comments_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.commenter_name.setText(comment.getCommenterName());
        holder.comment_text.setText(comment.getCommentText());
        Picasso.get().load(comment.getCommenterProfile()).placeholder(R.drawable.profile2).error(R.drawable.profile2).into(holder.commenter_profile);

        Locale loc = new Locale(context.getString(R.string.date_language), context.getString(R.string.date_country));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
        String date = dateFormat.format(comment.getCreatedAt());

        holder.comment_created_at.setText(date);

        // View Profile of Poster
        holder.layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managerProfile.viewProfile(profileResponseListener, comment.getCommenter(), principalId);
            }
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    ///  Listeners
    private final ProfileResponseListener profileResponseListener = new ProfileResponseListener() {
        @Override
        public void didFetch(ProfileResponse response, String message) {
            Intent intent = new Intent(context, ViewProfileActivity.class);
            intent.putExtra("ProfileResponse", response);
            context.startActivity(intent);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.connection_failed_uc), Toast.LENGTH_LONG).show();
        }
    };

}

class PostCommentViewHolder extends RecyclerView.ViewHolder{
    LinearLayout layout_user;
    CardView post_comments_container;
    TextView commenter_name,  comment_created_at,  comment_text;
    ImageView commenter_profile;


    public PostCommentViewHolder(@NonNull View itemView) {
        super(itemView);

        post_comments_container = itemView.findViewById(R.id.post_comments_container);
        commenter_name = itemView.findViewById(R.id.commenter_name);
        comment_created_at = itemView.findViewById(R.id.comment_created_at);
        comment_text = itemView.findViewById(R.id.comment_text);
        commenter_profile = itemView.findViewById(R.id.commenter_profile);

        layout_user = itemView.findViewById(R.id.layout_user);

    }
}