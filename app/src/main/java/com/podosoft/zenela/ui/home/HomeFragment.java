package com.podosoft.zenela.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.podosoft.zenela.Adapters.RandomPostsAdapter;
import com.podosoft.zenela.Interfaces.ProgressHorizonListener;
import com.podosoft.zenela.Listeners.RandomPostsResponseListener;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.RandomPostsResponse;
import com.podosoft.zenela.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private List<Post> posts = new ArrayList<>();
    private User principal = new User();

    ProgressBar progressBar;
    Button btn_see_more;
    RecyclerView randomPostsRecyclerView;
    SwipeRefreshLayout randomPostsSwipeRefresh;
    RequestManager manager;


    String email;
    Long principalId;
    RandomPostsAdapter randomPostsAdapter;

    SharedPreferences sharedPreferences = null;

    ProgressHorizonListener progressHorizonListener;

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

    // OnAttach
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProgressHorizonListener){
            progressHorizonListener = (ProgressHorizonListener) context;
        }
    }

    // OnDetach
    @Override
    public void onDetach() {
        super.onDetach();
        progressHorizonListener = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // init views
        progressBar = binding.randomPostsProgress;
        btn_see_more = binding.btnSeeMore;
        randomPostsRecyclerView = binding.recyclerRandomPosts;
        randomPostsSwipeRefresh = binding.swipeRefreshRandomPosts;

        if (email != null){
            if (posts.isEmpty()){
                showProgress();
                getRandomPosts();
            }
            else{
                hideProgress();
                populateRecycler();
                showBtnSeeMore();
            }
        }


        // On Refresh
        randomPostsSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRandomPosts();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // get Posts
    private void getRandomPosts(){
        if (email != null) {
            manager = new RequestManager(getActivity());
            manager.getRandomPosts(randomPostsResponseListener, email);
        }
    }

    // Listener Posts
    private final RandomPostsResponseListener randomPostsResponseListener = new RandomPostsResponseListener() {
        @Override
        public void didFetch(RandomPostsResponse response, String message) {
            posts = response.getPosts();
            principal = response.getPrincipal();

            hideProgress();

            populateRecycler();

            showBtnSeeMore();

        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            hideProgress();
        }
    };

    private void showBtnSeeMore() {
        btn_see_more.setVisibility(View.VISIBLE);
        btn_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomPostsSwipeRefresh.setRefreshing(true);
                posts = new ArrayList<>();
                populateRecycler();
                hideBtnSeeMore();
                getRandomPosts();
            }
        });
    }

    private void hideBtnSeeMore() {
        btn_see_more.setVisibility(View.INVISIBLE);
    }

    // Populate recyclerView
    private void populateRecycler(){
        randomPostsRecyclerView.setHasFixedSize(true);
        randomPostsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        randomPostsAdapter = new RandomPostsAdapter(getActivity(), this.posts, principal, new RequestManager(getActivity()), principalId, new RequestManagerProfile(getActivity()));
        randomPostsRecyclerView.setAdapter(randomPostsAdapter);
    }

    // hide ProgressBar
    private void hideProgress(){
        progressBar.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = 0;
        progressBar.setLayoutParams(layoutParams);
        randomPostsSwipeRefresh.setRefreshing(false);

    }

    // show Progress
    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }
}