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

import com.podosoft.zenela.Interfaces.ProgressHorizonListener;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.ViewPostResponseListener;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.PostNotification;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.ViewPostResponse;
import com.podosoft.zenela.ViewPostActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public class PostNotificationAdapter extends RecyclerView.Adapter<PostNotificationViewHolder>{

    Context context;
    List<PostNotification> postNotifications;
    RequestManager requestManager ;
    Long principalId;
    ProgressHorizonListener progressHorizonListener;

    public PostNotificationAdapter(Context context, List<PostNotification> postNotifications, Long principalId) {
        this.context = context;
        this.postNotifications = postNotifications;
        this.principalId = principalId;
        progressHorizonListener = (ProgressHorizonListener) context;
    }

    @NonNull
    @Override
    public PostNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostNotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.post_notification_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostNotificationViewHolder holder, int position) {
        PostNotification postNotification = postNotifications.get(position);
        holder.notifier_name.setText(postNotification.getNotifierName());
        holder.not_action.setText(R.string.and_others_reacted_to_your_post);

        // Date
        Locale loc = new Locale(context.getString(R.string.date_language), context.getString(R.string.date_country));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
        String date = dateFormat.format(postNotification.getCreatedAt());
        holder.not_date.setText(date);

        // if HasRead
        if(!postNotification.getHasRead()){
            holder.layoutNotification.setBackgroundColor(context.getResources().getColor(R.color.gray_day_night));
        }

        // image Notifier
        Picasso.get().load(postNotification.getNotifierProfile()).placeholder(R.drawable.profile2).into(holder.notifier_profile);

        requestManager = new RequestManager(context);

        // see Post onclick
        holder.post_notification_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressHorizonListener.showHorProg();
                requestManager.viewPost(viewPostResponseListener, principalId, postNotification.getPostId());
                if (!postNotification.getHasRead()){
                    requestManager.readPostNotifications(readPostNotificationResponseListener, principalId, postNotification.getId());
                    postNotification.setHasRead(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return postNotifications.size();
    }

    private final ViewPostResponseListener viewPostResponseListener = new ViewPostResponseListener() {
        @Override
        public void didFetch(ViewPostResponse viewPostResponse, String message) {
            progressHorizonListener.hideHorProg();
            if (viewPostResponse.getPost() != null){
                Intent intent = new Intent(context, ViewPostActivity.class);
                intent.putExtra("ViewPostResponse", viewPostResponse);
                context.startActivity(intent);
            }
            else {
                Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void didError(String message) {
            progressHorizonListener.hideHorProg();
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }
    };

    LoginResponseListener readPostNotificationResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didLogin(LoginResponse body, String message) {
            Toast.makeText(context, context.getString(R.string.sent), Toast.LENGTH_SHORT).show();
        }
    };
}


class PostNotificationViewHolder extends RecyclerView.ViewHolder{
    CardView post_notification_container;
    LinearLayout layoutNotification;
    TextView notifier_name, not_action, not_date;
    ImageView notifier_profile;

    public PostNotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        post_notification_container = itemView.findViewById(R.id.post_notification_container);
        notifier_name = itemView.findViewById(R.id.notifier_name);
        not_action = itemView.findViewById(R.id.not_action);
        not_date = itemView.findViewById(R.id.not_date);
        notifier_profile = itemView.findViewById(R.id.notifier_profile);

        layoutNotification = itemView.findViewById(R.id.layout_notification);
    }
}