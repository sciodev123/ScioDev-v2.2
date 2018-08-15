package com.redbluekey.sciodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ProgressBar;

import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.adapters.PostAdapter;
import com.redbluekey.sciodev.helpers.Retrofit.GetNoticeDataService;
import com.redbluekey.sciodev.helpers.Retrofit.RetrofitInstance;
import com.redbluekey.sciodev.models.CommentResponse;
import com.redbluekey.sciodev.models.PostResponse;
import com.redbluekey.sciodev.models.Section;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String ITEM_NAME = "itemName";

    private ListView m_lvPosts;
    private TextView m_tv_text_noComments;
    private ProgressBar m_pb_comments;
    public String itemName = "";
    private int commentCount = 0;
    public int getCommentCount(){return commentCount;}


    PostAdapter m_postAdapter;
    private ArrayList<CommentResponse> m_commentsList = new ArrayList<CommentResponse>();

    public PostFragment() {
        // Required empty public constructor
    }

    public PostAdapter getAdapter() {
        return m_postAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            return;

        Bundle args = getArguments();
        itemName = args.getString(ITEM_NAME);

        GetNoticeDataService service = RetrofitInstance.getRetrofitInstance().create(GetNoticeDataService.class);
        Call<List<CommentResponse>> callComments = service.GetCommentsFromTag(itemName, 0, 30);

        callComments.enqueue(new Callback<List<CommentResponse>>() {
            @Override
            public void onResponse(Call<List<CommentResponse>> call, Response<List<CommentResponse>> response) {

                if (response.isSuccessful()) {

                    List<CommentResponse> commentArray = response.body();
                    commentCount = commentArray.size();
                    for (int i = 0; i < commentCount; i++) {

                        m_commentsList.add(commentArray.get(i));
                    }
                    m_postAdapter.notifyDataSetChanged();

                    //if there are no comments display the text to inform the user
                    m_pb_comments.setVisibility(View.GONE);
                    if(commentCount==0){m_tv_text_noComments.setVisibility(View.VISIBLE);}else{m_tv_text_noComments.setVisibility(View.GONE);}
                }
            }

            @Override
            public void onFailure(Call<List<CommentResponse>> call, Throwable t) {

                Log.e("Pinky", "onFailure");
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_post, container, false);

        m_lvPosts = contentView.findViewById(R.id.lv_post);
        m_postAdapter = new PostAdapter(getActivity(), m_commentsList);
        m_lvPosts.setAdapter(m_postAdapter);
        m_lvPosts = contentView.findViewById(R.id.lv_post);

        //if there are no comments display the text to inform the user
        m_tv_text_noComments = contentView.findViewById(R.id.tv_text_noComments);
        m_tv_text_noComments.setVisibility(View.GONE);
        m_pb_comments = contentView.findViewById(R.id.pb_comments);
        m_pb_comments.setVisibility(View.VISIBLE);

        return contentView;
    }
}
