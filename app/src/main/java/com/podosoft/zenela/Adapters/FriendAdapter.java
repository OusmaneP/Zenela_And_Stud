package com.podosoft.zenela.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.podosoft.zenela.Interfaces.ProgressHorizonListener;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.ProfileResponseListener;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.ViewProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendsViewHolder>{
    Context context;
    List<User> users;
    RequestManagerProfile managerProfile;
    Long principalId;
    String type_bottom_sheet;

    ProgressHorizonListener progressHorizonListener;

    public FriendAdapter(Context context, List<User> users, Long principalId, String type_bottom_sheet) {
        this.context = context;
        this.users = users;
        this.principalId = principalId;
        this.type_bottom_sheet = type_bottom_sheet;
        progressHorizonListener = (ProgressHorizonListener) context;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsViewHolder(LayoutInflater.from(context).inflate(R.layout.my_friends_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        User user = users.get(position);
        holder.friend_name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        Picasso.get().load(user.getProfileThumb() == null || user.getProfileThumb().isEmpty() ? user.getProfile() : user.getProfileThumb()).placeholder(R.drawable.profile2).into(holder.friend_profile);

        managerProfile = new RequestManagerProfile(context);

        //  View Profile of Poster
        holder.layout_friend_viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressHorizonListener.showHorProg();
                managerProfile.viewProfile(profileResponseListener, user.getId(), principalId);
            }
        });

        // Message the Friend
        if (type_bottom_sheet.equals(context.getString(R.string.friends))){
            holder.btn_message_friend.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = holder.btn_message_friend.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.btn_message_friend.setLayoutParams(layoutParams);
        }
        else if (type_bottom_sheet.equals(context.getString(R.string.f_requests))){
            // BTN ACCEPT Friend Request
            holder.btn_accept_friend_request.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = holder.btn_accept_friend_request.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.btn_accept_friend_request.setLayoutParams(layoutParams);
            holder.btn_accept_friend_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // update data base
                    managerProfile.acceptFriendRequest(actionOnFriendRespListener, user.getId(), principalId);
                    // hide button
                    holder.btn_cancel_friend_request.setVisibility(View.INVISIBLE);
                    ViewGroup.LayoutParams layoutParams2 = holder.btn_cancel_friend_request.getLayoutParams();
                    layoutParams2.width = 0;
                    holder.btn_cancel_friend_request.setLayoutParams(layoutParams2);

                    holder.btn_accept_friend_request.setVisibility(View.INVISIBLE);
                    ViewGroup.LayoutParams layoutParams3 = holder.btn_accept_friend_request.getLayoutParams();
                    layoutParams3.width = 0;
                    holder.btn_accept_friend_request.setLayoutParams(layoutParams3);
                }
            });

            // BTN Cancel Friend Request
            holder.btn_cancel_friend_request.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams2 = holder.btn_cancel_friend_request.getLayoutParams();
            layoutParams2.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.btn_cancel_friend_request.setLayoutParams(layoutParams2);
            holder.btn_cancel_friend_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // update data base
                    managerProfile.cancelFriend(actionOnFriendRespListener, user.getId(), principalId);
                    // hide button
                    holder.btn_accept_friend_request.setVisibility(View.INVISIBLE);
                    ViewGroup.LayoutParams layoutParams = holder.btn_accept_friend_request.getLayoutParams();
                    layoutParams.width = 0;
                    holder.btn_accept_friend_request.setLayoutParams(layoutParams);
                }
            });
        }
        else if (type_bottom_sheet.equals(context.getString(R.string.f_invited))){
            holder.btn_cancel_friend_request.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams2 = holder.btn_cancel_friend_request.getLayoutParams();
            layoutParams2.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.btn_cancel_friend_request.setLayoutParams(layoutParams2);
            holder.btn_cancel_friend_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // update data base
                    managerProfile.cancelFriend(actionOnFriendRespListener, user.getId(), principalId);
                    // hide button
                    holder.btn_cancel_friend_request.setVisibility(View.INVISIBLE);
                    ViewGroup.LayoutParams layoutParams2 = holder.btn_cancel_friend_request.getLayoutParams();
                    layoutParams2.width = 0;
                    holder.btn_cancel_friend_request.setLayoutParams(layoutParams2);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    ///  Listeners
    private final ProfileResponseListener profileResponseListener = new ProfileResponseListener() {
        @Override
        public void didFetch(ProfileResponse response, String message) {
            progressHorizonListener.hideHorProg();
            Intent intent = new Intent(context, ViewProfileActivity.class);
            intent.putExtra("ProfileResponse", response);
            context.startActivity(intent);
        }

        @Override
        public void didError(String message) {
            progressHorizonListener.hideHorProg();
            Toast.makeText(context, context.getString(R.string.connection_failed_uc), Toast.LENGTH_SHORT).show();
        }
    };

    private final LoginResponseListener actionOnFriendRespListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            Toast.makeText(context, context.getString(R.string.action_failed_ic), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didLogin(LoginResponse body, String message) {
            Toast.makeText(context, "✅", Toast.LENGTH_SHORT).show();
        }
    };
}

class FriendsViewHolder extends RecyclerView.ViewHolder{
    CardView friends_card_container;
    TextView friend_name;
    ImageView friend_profile;
    LinearLayout layout_friend_viewProfile;
    Button btn_message_friend, btn_accept_friend_request, btn_cancel_friend_request;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        friends_card_container = itemView.findViewById(R.id.friends_card_container);
        friend_name = itemView.findViewById(R.id.friend_name);
        friend_profile = itemView.findViewById(R.id.friend_profile);
        layout_friend_viewProfile = itemView.findViewById(R.id.layout_friend_viewProfile);
        btn_message_friend = itemView.findViewById(R.id.btn_message_friend);
        btn_accept_friend_request = itemView.findViewById(R.id.btn_accept_friend_request);
        btn_cancel_friend_request = itemView.findViewById(R.id.btn_cancel_friend_request);
    }
}