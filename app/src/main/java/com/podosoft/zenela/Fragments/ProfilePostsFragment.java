package com.podosoft.zenela.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.podosoft.zenela.Adapters.MyProfilePostsAdapter;
import com.podosoft.zenela.Adapters.MyProfileVideosAdapter;
import com.podosoft.zenela.Adapters.RandomPostsAdapter;
import com.podosoft.zenela.Adapters.RandomVideosAdapter;
import com.podosoft.zenela.Adapters.SavedPostsAdapter;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;
import com.podosoft.zenela.R;
import com.podosoft.zenela.Requests.RequestManager;
import com.podosoft.zenela.Requests.RequestManagerProfile;

import java.util.ArrayList;
import java.util.List;


public class ProfilePostsFragment extends Fragment {

    private static final String NAME_ARG = "name";
    private String fragName;

    List<Post> myPosts = new ArrayList<>();
    private User principal = new User();

    RecyclerView recyclerView;
    RandomPostsAdapter randomPostsAdapter;
    MyProfilePostsAdapter myProfilePostsAdapter;
    MyProfileVideosAdapter myProfileVideosAdapter;
    RandomVideosAdapter randomVideosAdapter;
    SavedPostsAdapter savedPostsAdapter;

    Long principalId;
    String type;

    public ProfilePostsFragment() {
        // Required empty public constructor
    }


    public static ProfilePostsFragment newInstance(final String name) {
        final ProfilePostsFragment fragment = new ProfilePostsFragment();

        final Bundle args = new Bundle(1);

        args.putString(NAME_ARG, name);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(NAME_ARG)) {
            fragName = arguments.getString(NAME_ARG);
        }

        ////////////////////
        if (arguments != null && arguments.containsKey("myPosts")){
            myPosts = (List<Post>) arguments.getSerializable("myPosts");

        }

        if (arguments != null && arguments.containsKey("principalId")){
            principalId = arguments.getLong("principalId");
        }

        if (arguments != null && arguments.containsKey("type")){
            type = arguments.getString("type");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_posts, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null){
            recyclerView = view.findViewById(R.id.recycler_my_posts);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

            if (type.equals("posts")){
                myProfilePostsAdapter = new MyProfilePostsAdapter(getActivity(), this.myPosts, principal, new RequestManager(getActivity()), principalId, new RequestManagerProfile(getActivity()));
                recyclerView.setAdapter(myProfilePostsAdapter);
            }
            else if (type.equals("saved")){
                savedPostsAdapter = new SavedPostsAdapter(getActivity(), this.myPosts, principal, new RequestManager(getActivity()), principalId, new RequestManagerProfile(getActivity()));
                recyclerView.setAdapter(savedPostsAdapter);
            }
            else if (type.equals("view_profile_posts")){
                randomPostsAdapter = new RandomPostsAdapter(getActivity(), this.myPosts, principal, new RequestManager(getActivity()), principalId, new RequestManagerProfile(getActivity()));
                recyclerView.setAdapter(randomPostsAdapter);
            }
            else if (type.equals("view_profile_videos")){
                randomVideosAdapter = new RandomVideosAdapter(getActivity(), this.myPosts, principal, new RequestManager(getActivity()), principalId, new RequestManagerProfile(getActivity()));
                recyclerView.setAdapter(randomVideosAdapter);
            }
            else if (type.equals("my_videos")){
                myProfileVideosAdapter = new MyProfileVideosAdapter(getActivity(), this.myPosts, principal, new RequestManager(getActivity()), principalId, new RequestManagerProfile(getActivity()));
                recyclerView.setAdapter(myProfileVideosAdapter);
            }
        }
    }
}