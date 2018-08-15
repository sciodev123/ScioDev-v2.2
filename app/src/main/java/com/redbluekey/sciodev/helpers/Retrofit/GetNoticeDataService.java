package com.redbluekey.sciodev.helpers.Retrofit;


import com.redbluekey.sciodev.models.CommentResponse;
import com.redbluekey.sciodev.models.PostImageResponse;
import com.redbluekey.sciodev.models.PostResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface GetNoticeDataService {

//    @Multipart
//    @POST("/api/comment/PostComment")
//    Call<PostResponse> uploadFile(@Query("commentSource") String param, @Part MultipartBody.Part file, @Part("user_from") RequestBody from,
//                                  @Part("user_to") RequestBody to, @Part("is_doctor") RequestBody isDoctor,
//                                  @Part("uid") RequestBody uid, @Part("session_start") RequestBody startTime);

//    @POST("/api/comment/PostComment")
//    Call<PostResponse> uploadFile(@Query("commentSource") String param, @Header("Content-Type") String contentType,
//                                  @Header("authenticationToken") String authToken,
//                                  @Part("user_from") RequestBody tag, @Part("user_to") RequestBody to,
//                                  @Part("is_doctor") RequestBody isDoctor, @Part("uid") RequestBody uid,
//                                  @Part("session_start") RequestBody startTime);


    @POST("api/comment")
    Call<PostResponse> PostComment(@Query("commentSource") String param,
                                   @Header("authenticationToken") String authToken,
                                   @Body RequestBody bean);
    @Multipart
    @POST("api/comment/PostCommentImage")
    Call<PostImageResponse> PostCommentImage(@Query("imageSource") String param,
                                             @Header("authenticationToken") String authToken,
                                             @Part MultipartBody.Part file);

    @GET("api/comment/GetCommentsFromTag")
    Call<List<CommentResponse>> GetCommentsFromTag(@Query("Name") String name,
                                                   @Query("positionFrom") int posFrom,
                                                   @Query("positionTo") int posTo);


}