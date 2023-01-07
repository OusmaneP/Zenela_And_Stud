package com.podosoft.zenela.ui.videos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.podosoft.zenela.Adapters.RandomPostsAdapter;
import com.podosoft.zenela.Adapters.RandomVideosAdapter;
import com.podosoft.zenela.Listeners.RandomPostsResponseListener;
import com.podosoft.zenela.Listeners.RandomVideosResponseListener;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.RandomPostsResponse;
import com.podosoft.zenela.Responses.RandomVideosResponse;
import com.podosoft.zenela.databinding.FragmentVideosBinding;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment {

    private FragmentVideosBinding binding;

    List<Post> posts = new ArrayList<>();
    private User principal = new User();

    ProgressBar progressBar;
    Button btn_see_more;
    RecyclerView randomVideosRecyclerView;
    SwipeRefreshLayout randomVideosSwipeRefresh;
    RequestManager manager;

    String email;
    Long principalId;
    RandomVideosAdapter randomVideosAdapter;

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
        VideosViewModel videosViewModel =
                new ViewModelProvider(this).get(VideosViewModel.class);

        binding = FragmentVideosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // init views
        progressBar = binding.randomVideosProgress;
        btn_see_more = binding.btnSeeMore;
        randomVideosRecyclerView = binding.recyclerRandomVideos;
        randomVideosSwipeRefresh = binding.swipeRefreshRandomVideos;

        if (email != null){
            if (posts.size() < 1){
                showProgress();
                getRandomVideos();
            }
            else{
                hideProgress();
                populateRecycler();
                showBtnSeeMore();
            }
        }

        //  On Refresh
        randomVideosSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRandomVideos();
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
    private void getRandomVideos(){
        if (email != null) {
            manager = new RequestManager(getActivity());
            manager.getRandomVideos(randomVideosResponseListener, email);
        }
    }


    // Listener Posts
    private final RandomVideosResponseListener randomVideosResponseListener = new RandomVideosResponseListener() {

        @Override
        public void didFetch(RandomVideosResponse response, String message) {
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
                randomVideosSwipeRefresh.setRefreshing(true);
                posts = new ArrayList<>();
                populateRecycler();
                hideBtnSeeMore();
                getRandomVideos();
            }
        });
    }

    private void hideBtnSeeMore() {
        btn_see_more.setVisibility(View.INVISIBLE);
    }


    // Populate recyclerView
    private void populateRecycler(){
        randomVideosRecyclerView.setHasFixedSize(true);
        randomVideosRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        randomVideosAdapter = new RandomVideosAdapter(getActivity(), this.posts, principal, new RequestManager(getActivity()), principalId, new RequestManagerProfile(getActivity()));
        randomVideosRecyclerView.setAdapter(randomVideosAdapter);
    }


    // hide ProgressBar
    private void hideProgress(){
        progressBar.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = 0;
        progressBar.setLayoutParams(layoutParams);
        randomVideosSwipeRefresh.setRefreshing(false);

    }

    // show Progress
    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }

}