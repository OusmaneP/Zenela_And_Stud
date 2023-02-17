package com.podosoft.zenela.ui.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.podosoft.zenela.Adapters.PostNotificationAdapter;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.PostNotificationResponseListener;
import com.podosoft.zenela.Models.PostNotification;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.PostNotificationResponse;
import com.podosoft.zenela.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    List<PostNotification> postNotifications = new ArrayList<>();
    RecyclerView postNotificationRecyclerView;
    RequestManager manager;
    ProgressBar progressBar;
    SwipeRefreshLayout postNotificationSwipeRefresh;

    String email;
    Long principalId;
    PostNotificationAdapter postNotificationAdapter;
    SharedPreferences sharedPreferences = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("login", 0);
        String email = sharedPreferences.getString("email", null);
        long principalId = sharedPreferences.getLong("principalId", 0);

        if (email != null && this.email == null){
            this.email = email;
            this.principalId = principalId;
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // init views
        progressBar = binding.postNotificationProgress;
        postNotificationRecyclerView = binding.recyclerPostNotifications;
        postNotificationSwipeRefresh = binding.swipeRefreshPostNotifications;

        if (email != null){
            if (postNotifications.size() < 1){
                showProgress();
                getNotifications();
            }else{
                hideProgress();
                populateRecycler();
            }
        }

        postNotificationSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // get Not
    private void getNotifications(){
        if (email != null){
            manager = new RequestManager(getActivity());
            manager.getPostNotifications(postNotificationResponseListener, principalId);
        }
    }

    // Listener
    PostNotificationResponseListener postNotificationResponseListener = new PostNotificationResponseListener() {
        @Override
        public void didFetch(PostNotificationResponse response, String message) {
            postNotifications = response.getPostNotifications();
            hideProgress();

            populateRecycler();

            int canRequest = 0;

            if (postNotifications.size() > 0 ){
                for(PostNotification postNotification: postNotifications){
                    if (!postNotification.getHasSeen()){
                        canRequest = 1;
                        break;
                    }
                }

                if (canRequest == 1){
                    manager.seePostNotifications(seePostNotificationResponseListener, principalId);
                }
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), getString(R.string.connection_failed_uc), Toast.LENGTH_LONG).show();
            hideProgress();
        }
    };

    LoginResponseListener seePostNotificationResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
        }

        @Override
        public void didLogin(LoginResponse body, String message) {
        }
    };

    private void populateRecycler(){
        postNotificationRecyclerView.setHasFixedSize(true);
        postNotificationRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        postNotificationAdapter = new PostNotificationAdapter(getActivity(), this.postNotifications, principalId);
        postNotificationRecyclerView.setAdapter(postNotificationAdapter);
    }

    // hide ProgressBar
    private void hideProgress(){
        progressBar.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = 0;
        progressBar.setLayoutParams(layoutParams);
        postNotificationSwipeRefresh.setRefreshing(false);

    }

    // show Progress
    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        progressBar.setLayoutParams(layoutParams);
    }
}