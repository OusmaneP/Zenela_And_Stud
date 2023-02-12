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
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.ViewProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleViewHolder>{

    Context context;
    List<User> users;
    RequestManagerProfile managerProfile;
    Long principalId;

    public PeopleAdapter(Context context, List<User> users, Long principalId) {
        this.context = context;
        this.users = users;
        this.principalId = principalId;
    }


    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PeopleViewHolder(LayoutInflater.from(context).inflate(R.layout.people_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder holder, int position) {
        User user = users.get(position);

        holder.user_name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        Picasso.get().load(user.getProfileThumb() == null || user.getProfileThumb().isEmpty() ? user.getProfile() : user.getProfileThumb()).placeholder(R.drawable.profile2).into(holder.user_profile);

        managerProfile = new RequestManagerProfile(context);
        // View Profile of Poster
        holder.layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managerProfile.viewProfile(profileResponseListener, user.getId(), principalId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
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

class PeopleViewHolder extends RecyclerView.ViewHolder{
    CardView people_card_container;
    TextView user_name;
    ImageView user_profile;
    LinearLayout layout_user;


    public PeopleViewHolder(@NonNull View itemView) {
        super(itemView);

        people_card_container = itemView.findViewById(R.id.people_card_container);
        user_name = itemView.findViewById(R.id.user_name);
        user_profile = itemView.findViewById(R.id.user_profile);
        layout_user = itemView.findViewById(R.id.layout_user);

    }
}