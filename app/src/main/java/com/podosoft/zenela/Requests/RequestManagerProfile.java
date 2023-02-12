package com.podosoft.zenela.Requests;

import android.content.Context;

import androidx.annotation.NonNull;

import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Listeners.ProfileResponseListener;
import com.podosoft.zenela.Listeners.RandomPostsResponseListener;
import com.podosoft.zenela.Listeners.RandomVideosResponseListener;
import com.podosoft.zenela.Listeners.SearchResponseListener;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.Responses.RandomPostsResponse;
import com.podosoft.zenela.Responses.RandomVideosResponse;
import com.podosoft.zenela.Responses.SearchResponse;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class RequestManagerProfile {

    Context context;
    OkHttpClient okHttpClient = new OkHttpClient();
    OkHttpClient clientWith1mTimeout = okHttpClient.newBuilder().readTimeout(30, TimeUnit.MINUTES).build();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.77.118:8080/mobile/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientWith1mTimeout)
            .build();

    public RequestManagerProfile(Context context) {
        this.context = context;
    }

    // goToProfile
    public void goToProfile(ProfileResponseListener listener, String email){
        CallProfile callProfile = retrofit.create(CallProfile.class);
        Call<ProfileResponse> call = callProfile.callProfile(email);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // viewProfile
    public void viewProfile(ProfileResponseListener listener, Long userId, Long principalId){
        CallViewProfile callViewProfile = retrofit.create(CallViewProfile.class);
        Call<ProfileResponse> call = callViewProfile.callViewProfile(userId, principalId);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // sendFriendRequest
    public void sendFriendRequest(LoginResponseListener listener, Long userId, Long principalId){
        CallSendFriendRequest callSendFriendRequest = retrofit.create(CallSendFriendRequest.class);
        Call<LoginResponse> call = callSendFriendRequest.callSendRequest(userId, principalId);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // sendFriendRequest
    public void acceptFriendRequest(LoginResponseListener listener, Long userId, Long principalId){
        CallAcceptFriendRequest callAcceptFriendRequest = retrofit.create(CallAcceptFriendRequest.class);
        Call<LoginResponse> call = callAcceptFriendRequest.callAcceptRequest(userId, principalId);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // sendFriendRequest
    public void cancelFriend(LoginResponseListener listener, Long userId, Long principalId){
        CallCancelFriend callCancelFriend = retrofit.create(CallCancelFriend.class);
        Call<LoginResponse> call = callCancelFriend.callCancelFriend(userId, principalId);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // search People
    public void searchPeople(SearchResponseListener listener, String query, Long principalId){
        CallSearchPeople callSearchPeople = retrofit.create(CallSearchPeople.class);
        Call<SearchResponse> call = callSearchPeople.callSearchPeople(query, principalId);

        call.enqueue(new Callback<SearchResponse>() {

            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // Create Post
    public void createPost(LoginResponseListener listener, File file, String comment, Long principalId, String type){

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        //RequestBody fileName = RequestBody.create(MediaType.parse("Multipart/form-data"), file.getName());
        RequestBody postComment = RequestBody.create(MediaType.parse("Multipart/form-data"), comment);
        RequestBody posterId = RequestBody.create(MediaType.parse("Multipart/form-data"), String.valueOf(principalId));
        RequestBody postType = RequestBody.create(MediaType.parse("Multipart/form-data"), type);

        CallCreatePost callCreatePost = retrofit.create(CallCreatePost.class);
        Call<LoginResponse> call = callCreatePost.createPost(body, postComment, posterId, postType);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }


    //////   Find All Saved Post
    public void findSavedPosts(RandomPostsResponseListener listener, Long principalId){
        CallFindSavedPosts callFindSavedPosts = retrofit.create(CallFindSavedPosts.class);
        Call<RandomPostsResponse> call = callFindSavedPosts.callFindSavedPosts(principalId);

        call.enqueue(new Callback<RandomPostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<RandomPostsResponse> call, @NonNull Response<RandomPostsResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<RandomPostsResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    //////   Find All Saved Post
    public void editProfileInfo(LoginResponseListener listener, Long principalId,
                                String firstName, String lastName,
                                String email, String bio){
        CallEditProfile callEditProfile = retrofit.create(CallEditProfile.class);
        Call<LoginResponse> call = callEditProfile.callEditProfile(principalId, firstName, lastName, email, bio);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    //////   Find All Saved Post
    public void editPassword(LoginResponseListener listener, Long principalId,
                             String oldPassword, String newPassword){
        CallEditPassword callEditPassword = retrofit.create(CallEditPassword.class);
        Call<LoginResponse> call = callEditPassword.callEditPassword(principalId, oldPassword, newPassword);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // search People
    public void searchRandomPeople(SearchResponseListener listener, Long principalId){
        CallSearchRandomPeople callSearchRandomPeople = retrofit.create(CallSearchRandomPeople.class);
        Call<SearchResponse> call = callSearchRandomPeople.callSearchRandomPeople(principalId);

        call.enqueue(new Callback<SearchResponse>() {

            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // Delete a post
    public void deletePost(LoginResponseListener listener, Long principalId, Long postId){
        CallDeletePost callDeletePost = retrofit.create(CallDeletePost.class);
        Call<LoginResponse> call = callDeletePost.callDeletePost(principalId, postId);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // Delete a post
    public void updatePostComment(LoginResponseListener listener, Long principalId, Long postId, String comment){
        CallUpdatePostComment callUpdatePostComment = retrofit.create(CallUpdatePostComment.class);
        Call<LoginResponse> call = callUpdatePostComment.callUpdatePostComment(principalId, postId, comment);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // Create Post
    public void createVideo(LoginResponseListener listener, File file, File thumb, String comment, Long principalId){

        RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), file);
        RequestBody requestThumb = RequestBody.create(MediaType.parse("image/*"), thumb);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        MultipartBody.Part bodyThumb = MultipartBody.Part.createFormData("thumb", thumb.getName(), requestThumb);

        RequestBody postComment = RequestBody.create(MediaType.parse("Multipart/form-data"), comment);
        RequestBody posterId = RequestBody.create(MediaType.parse("Multipart/form-data"), String.valueOf(principalId));

        CallCreateVideo callCreateVideo = retrofit.create(CallCreateVideo.class);
        Call<LoginResponse> call = callCreateVideo.createVideo(body, bodyThumb, postComment, posterId);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    //////   Find All Saved Post
    public void findMyVideos(RandomVideosResponseListener listener, Long principalId){
        CallMyVideos callMyVideos = retrofit.create(CallMyVideos.class);
        Call<RandomVideosResponse> call = callMyVideos.callMyVideos(principalId);

        call.enqueue(new Callback<RandomVideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<RandomVideosResponse> call, @NonNull Response<RandomVideosResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<RandomVideosResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    ///////////////////////////   CCCCCCCCCAAAAAAAAAALLLLLLLLLLLLLL
    // My Profile
    private interface CallProfile{
        @GET("profile")
        Call<ProfileResponse> callProfile(
                @Query("email") String email
        );
    }

    // View Profile
    private interface CallViewProfile{
        @GET("view_profile")
        Call<ProfileResponse> callViewProfile(
                @Query("userId") Long userId,
                @Query("principalId") Long principalId
        );
    }

    //  Add friend Request
    private interface CallSendFriendRequest{
        @POST("send_friend_request")
        Call<LoginResponse> callSendRequest(
                @Query("userId") Long userId,
                @Query("principalId") Long principalId
        );
    }

    //  Accept friend Request
    private interface CallAcceptFriendRequest{
        @POST("accept_friend_request")
        Call<LoginResponse> callAcceptRequest(
                @Query("userId") Long userId,
                @Query("principalId") Long principalId
        );
    }

    //  Cancel friend Request
    private interface CallCancelFriend{
        @POST("cancel_friend")
        Call<LoginResponse> callCancelFriend(
                @Query("userId") Long userId,
                @Query("principalId") Long principalId
        );
    }

    //  Search people
    private interface CallSearchPeople{
        @GET("search")
        Call<SearchResponse> callSearchPeople(
                @Query("query") String query,
                @Query("principalId") Long principalId
        );
    }

    // Upload a Post
    private interface CallCreatePost{
        @Multipart
        @POST("create_post")
        Call<LoginResponse> createPost(@Part MultipartBody.Part file,
                                       @Part("comment") RequestBody comment,
                                       @Part("principalId") RequestBody principalId,
                                       @Part("type") RequestBody type);
    }


    //////   Find All Saved Post
    private interface CallFindSavedPosts{
        @GET("find_saved_posts")
        Call<RandomPostsResponse> callFindSavedPosts(
                @Query("principalId") Long principalId
        );
    }

    //  Accept friend Request
    private interface CallEditProfile{
        @POST("edit_profile")
        Call<LoginResponse> callEditProfile(
                @Query("principalId") Long principalId,
                @Query("first_name") String firstName,
                @Query("last_name") String lastName,
                @Query("email") String email,
                @Query("bio") String bio
        );
    }

    //  Accept friend Request
    private interface CallEditPassword{
        @POST("edit_password")
        Call<LoginResponse> callEditPassword(
                @Query("principalId") Long principalId,
                @Query("old_password") String oldPassword,
                @Query("new_password") String newPassword
        );
    }

    //  Search people
    private interface CallSearchRandomPeople{
        @GET("search_random")
        Call<SearchResponse> callSearchRandomPeople(
                @Query("principalId") Long principalId
        );
    }

    // Delete a post
    private interface CallDeletePost{
        @POST("delete_post")
        Call<LoginResponse> callDeletePost(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId
        );
    }

    // Update a post comment
    private interface CallUpdatePostComment{
        @POST("update_post_comment")
        Call<LoginResponse> callUpdatePostComment(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId,
                @Query("comment") String comment
        );
    }

    // Upload a Video
    private interface CallCreateVideo{
        @Multipart
        @POST("create_video")
        Call<LoginResponse> createVideo(@Part MultipartBody.Part file,
                                        @Part MultipartBody.Part thumb,
                                       @Part("comment") RequestBody comment,
                                       @Part("principalId") RequestBody principalId
        );
    }

    //////   Find My Videos
    private interface CallMyVideos{
        @GET("get_my_videos")
        Call<RandomVideosResponse> callMyVideos(
                @Query("principalId") Long principalId
        );
    }

}
