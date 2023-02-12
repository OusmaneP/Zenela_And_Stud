package com.podosoft.zenela.Requests;

import android.content.Context;

import androidx.annotation.NonNull;

import com.podosoft.zenela.Dto.CommentDto;
import com.podosoft.zenela.Dto.UserRegistrationDto;
import com.podosoft.zenela.Listeners.CommentResponseListener;
import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.Dto.UserDto;
import com.podosoft.zenela.Listeners.PostNotificationResponseListener;
import com.podosoft.zenela.Listeners.RandomPostsResponseListener;
import com.podosoft.zenela.Listeners.RandomVideosResponseListener;
import com.podosoft.zenela.Listeners.ViewPostResponseListener;
import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Responses.CommentResponse;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.PostNotificationResponse;
import com.podosoft.zenela.Responses.RandomPostsResponse;
import com.podosoft.zenela.Responses.RandomVideosResponse;
import com.podosoft.zenela.Responses.ViewPostResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public class RequestManager {
    Context context;
    OkHttpClient okHttpClient = new OkHttpClient();
    OkHttpClient clientWith1mTimeout = okHttpClient.newBuilder().readTimeout(5, TimeUnit.MINUTES).build();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.77.118:8080/mobile/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientWith1mTimeout)
            .build();

    // Constructor
    public RequestManager(Context context){
        this.context = context;
    }

    // Login User
    public void login(LoginResponseListener listener, UserDto userDto){
        CallLoginUser callLoginUser = retrofit.create(CallLoginUser.class);
        Call<LoginResponse> call = callLoginUser.loginUser(userDto.getEmail(), userDto.getPassword());

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

    // Register User
    public void register(LoginResponseListener listener, UserRegistrationDto userRegistrationDto){
        CallRegisterUser callRegisterUser = retrofit.create(CallRegisterUser.class);
        Call<LoginResponse> call = callRegisterUser.registerUser(userRegistrationDto.getFirstName(), userRegistrationDto.getLastName(), userRegistrationDto.getEmail(), userRegistrationDto.getPassword());

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

    // get Random Post
    public void getRandomPosts(RandomPostsResponseListener listener, String email){
        CallRandomPosts callRandomPosts = retrofit.create(CallRandomPosts.class);
        Call<RandomPostsResponse> call = callRandomPosts.callRandomPosts(email);

        call.enqueue(new Callback<RandomPostsResponse>() {

            @Override
            public void onResponse(@NonNull Call<RandomPostsResponse> call, @NonNull Response<RandomPostsResponse> response) {
                if (!response.isSuccessful()){
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

    // Like a post
    public void likeAPost(LoginResponseListener listener, Long principalId, Long postId){
        CallLikeAPost callLikeAPost = retrofit.create(CallLikeAPost.class);
        Call<LoginResponse> call = callLikeAPost.callLikeAPost(principalId, postId);

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

    // UnLike a post
    public void unlikeAPost(LoginResponseListener listener, Long principalId, Long postId){
        CallUnlikeAPost callUnlikeAPost = retrofit.create(CallUnlikeAPost.class);
        Call<LoginResponse> call = callUnlikeAPost.callUnlikeAPost(principalId, postId);

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

    // Comment a post
    public void commentAPost(CommentResponseListener listener, CommentDto commentDto, Long principalId){
        CallCommentAPost callCommentAPost = retrofit.create(CallCommentAPost.class);
        Call<CommentResponse> call = callCommentAPost.callCommentAPost(commentDto, principalId);

        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommentResponse> call, @NonNull Response<CommentResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didLogin(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<CommentResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    ////// Save a post
    public void saveAPost(LoginResponseListener listener, Long principalId, Long postId){
        CallSaveAPost callSaveAPost = retrofit.create(CallSaveAPost.class);
        Call<LoginResponse> call = callSaveAPost.callSaveAPost(principalId, postId);

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

    ////// UnSave a post
    public void unSaveAPost(LoginResponseListener listener, Long principalId, Long postId){
        CallUnSaveAPost callUnSaveAPost = retrofit.create(CallUnSaveAPost.class);
        Call<LoginResponse> call = callUnSaveAPost.callUnSaveAPost(principalId, postId);

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


    //***** Report a post   ******
    public void reportPost(LoginResponseListener listener, Long principalId, Long postId, int level,
                           String reason, String comment){

        CallReportAPost callReportAPost = retrofit.create(CallReportAPost.class);
        Call<LoginResponse> call = callReportAPost.callReportPost(principalId, postId, level, reason, comment);

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



    ////////////  Random Videos
    public void getRandomVideos(RandomVideosResponseListener listener, String email){
        CallRandomVideos callRandomVideos = retrofit.create(CallRandomVideos.class);
        Call<RandomVideosResponse> call = callRandomVideos.callRandomVideos(email);

        call.enqueue(new Callback<RandomVideosResponse>() {

            @Override
            public void onResponse(@NonNull Call<RandomVideosResponse> call, @NonNull Response<RandomVideosResponse> response) {
                if (!response.isSuccessful()){
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

    ////////////  Random Videos
    public void getPostNotifications(PostNotificationResponseListener listener, Long principalId){
        CallGetPostNotifications callGetPostNotifications = retrofit.create(CallGetPostNotifications.class);
        Call<PostNotificationResponse> call = callGetPostNotifications.callGetPostNotifications(principalId);

        call.enqueue(new Callback<PostNotificationResponse>() {

            @Override
            public void onResponse(@NonNull Call<PostNotificationResponse> call, @NonNull Response<PostNotificationResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<PostNotificationResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    ////////////  Random Videos
    public void viewPost(ViewPostResponseListener listener, Long principalId, Long postId){
        CallViewPost callViewPost = retrofit.create(CallViewPost.class);
        Call<ViewPostResponse> call = callViewPost.callViewPost(principalId, postId);

        call.enqueue(new Callback<ViewPostResponse>() {

            @Override
            public void onResponse(@NonNull Call<ViewPostResponse> call, @NonNull Response<ViewPostResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<ViewPostResponse> call, @NonNull Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    ////////////  Random Videos
    public void readPostNotifications(LoginResponseListener listener, Long principalId){
        CallReadPostNotifications callReadPostNotifications = retrofit.create(CallReadPostNotifications.class);
        Call<LoginResponse> call = callReadPostNotifications.callReadPostNotifications(principalId);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (!response.isSuccessful()){
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

    //////////////////// **************      Calls          **************** //////////////////////
    // Call Login User
    private interface CallLoginUser{
        @GET("login")
        Call<LoginResponse> loginUser(
                @Query("email") String email,
                @Query("password") String password
        );
    }

    // Call Register User
    private interface CallRegisterUser{
        @POST("register")
        Call<LoginResponse> registerUser(
                @Query("first_name") String first_name,
                @Query("last_name") String last_name,
                @Query("email") String email,
                @Query("password") String password
        );
    }

    //-------------------       POST         -----------------------/
    // get posts
    private interface CallRandomPosts{
        @GET("posts")
        Call<RandomPostsResponse> callRandomPosts(
                @Query("email") String email
        );
    }

    // Like a post
    private interface CallLikeAPost{
        @POST("like")
        Call<LoginResponse> callLikeAPost(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId
        );
    }

    // Unlike a post
    private interface CallUnlikeAPost{
        @POST("unlike")
        Call<LoginResponse> callUnlikeAPost(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId
        );
    }

    // comment a post
    private interface CallCommentAPost{
        @POST("comment_post")
        Call<CommentResponse> callCommentAPost(
                @Body() CommentDto commentDto,
                @Query("principalId") Long principalId
        );
    }



    ////////////////////////////// Videos /////////////////////////////////////
    private  interface CallRandomVideos{
        @GET("videos")
        Call<RandomVideosResponse> callRandomVideos(
                @Query("email") String email
        );
    }

    // Save a post
    private interface CallSaveAPost{
        @POST("save_post")
        Call<LoginResponse> callSaveAPost(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId
        );
    }

    // UnSave a post
    private interface CallUnSaveAPost{
        @POST("un_save_post")
        Call<LoginResponse> callUnSaveAPost(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId
        );
    }

    /////****** UnSave a post   ******
    private interface CallReportAPost{
        @POST("report_post")
        Call<LoginResponse> callReportPost(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId,
                @Query("level") int level,
                @Query("reason") String reason,
                @Query("comment") String comment
        );
    }

    // Get Post Notifications
    private interface CallGetPostNotifications{
        @GET("post_notifications")
        Call<PostNotificationResponse> callGetPostNotifications(
                @Query("principalId") Long principalId
        );
    }

    // UnSave a post
    private interface CallViewPost{
        @GET("view_post")
        Call<ViewPostResponse> callViewPost(
                @Query("principalId") Long principalId,
                @Query("postId") Long postId
        );
    }

    // Read Notifications
    private interface CallReadPostNotifications{
        @POST("read_post_notifications")
        Call<LoginResponse> callReadPostNotifications(
                @Query("principalId") Long principalId
        );
    }

}
