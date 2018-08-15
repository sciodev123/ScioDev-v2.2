package com.redbluekey.sciodev.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.activities.PostViewActivity;
import com.redbluekey.sciodev.models.CommentResponse;
import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<CommentResponse> m_commentsList;

    private static final String IMAGES_URL = "http://redbluekey.com/images";

    private TextView m_tvTag;
    private TextView m_tvUser;
    private TextView m_tvCommentTitle;
    private TextView m_tvCommentBody;
    private ImageView m_ivCommentImage;
    private ImageView m_ivCommentTagImage;
    private TextView m_tvCommentCount;
    private TextView m_tvVoteCount;
    private ImageView m_ivVoteUP;
    private ImageView m_ivVoteDown;

    public PostAdapter(Context context, ArrayList<CommentResponse> commentsList) {

        this.mContext = context;
        this.m_commentsList = commentsList;
    }

    @Override
    public int getCount() {
        return m_commentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_commentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.post_item, parent, false);
        }

        m_tvTag = convertView.findViewById(R.id.tv_tag);
        m_tvUser = convertView.findViewById(R.id.tv_user);
        m_tvCommentTitle = convertView.findViewById(R.id.tv_comment_title);
        m_tvCommentBody = convertView.findViewById(R.id.tv_comment_body);
        m_ivCommentImage = convertView.findViewById(R.id.iv_comment_image);
        m_ivCommentTagImage = convertView.findViewById(R.id.tv_tag_image);
        m_tvCommentCount = convertView.findViewById(R.id.tv_comment_count);
        m_tvVoteCount = convertView.findViewById(R.id.tv_vote_count);
        m_ivVoteUP = convertView.findViewById(R.id.iv_vote_up);
        m_ivVoteDown = convertView.findViewById(R.id.iv_vote_down);


        CommentResponse comment = m_commentsList.get(position);
        m_tvTag.setText(comment.tag);
        m_tvUser.setText(comment.username);
        m_tvCommentTitle.setText(comment.title);
        if (comment.comment.isEmpty()) {
            m_tvCommentBody.setVisibility(View.GONE);
        }else {
            m_tvCommentBody.setText(comment.comment);
            m_tvCommentBody.setVisibility(View.VISIBLE);
        }

        //post image
        if (comment.ImageURL_Thumb.isEmpty()) {
            m_ivCommentImage.setVisibility(View.GONE);
        }
        else {
            m_ivCommentImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .asBitmap()
                    .load(IMAGES_URL + comment.ImageURL_Thumb)
                    .into(m_ivCommentImage);
        }

        //tag image (small next to tag name)
        Glide.with(mContext)
                .asBitmap()
                .load(IMAGES_URL + comment.tag_ImageURL_Thumb)
                .into(m_ivCommentTagImage);

        m_tvVoteCount.setText(comment.countVotes);
        if (comment.replies.isEmpty()) {
            m_tvCommentCount.setText("0 Comments");
        }else {
            m_tvCommentCount.setText(comment.replies + " Comments");
        }

        //set links
        m_tvCommentTitle.setOnClickListener(getOnClickListener(comment.id));
        m_tvCommentBody.setOnClickListener(getOnClickListener(comment.id));
        m_tvCommentCount.setOnClickListener(getOnClickListener(comment.id));
        m_tvUser.setOnClickListener(getOnClickListener(comment.id));

        //TODO implement activity to view tag
        m_tvTag.setOnClickListener(getOnClickListener(comment.id));

        //TODO implement voting
        m_ivVoteUP.setOnClickListener(getOnClickListener(comment.id));
        m_ivVoteDown.setOnClickListener(getOnClickListener(comment.id));

        return convertView;
    }

    private View.OnClickListener getOnClickListener(final String commentID) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Runnable() {
                    @Override
                    public void run() {
                        int a = 1;
                        //_mainActivity.fetchInitialContent(itemName, false);

                        //open comment view activity
                        Intent postView = new Intent(mContext, PostViewActivity.class);
                        postView.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        postView.putExtra(PostViewActivity.QUERY, commentID);
                        mContext.startActivity(postView);
                    }
                }.run();
            }
        };
    }
}
