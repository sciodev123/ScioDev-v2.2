package com.redbluekey.sciodev.models;

import lombok.Data;

@Data
public class CommentResponse {

    public String orderby_hot;
    public String title;
    public String comment;
    public String createdDate;
    public String linkURL;
    public String id;
    public String username;
    public String countVotes;
    public String tag;
    public String tag_ImageURL_Thumb;
    public String ImageURL_Thumb;
    public String imageURL_Local;
    public String replies;



    public CommentResponse() {

        this.orderby_hot = "";
        this.title = "";
        this.comment = "";
        this.createdDate = "";
        this.linkURL = "";
        this.id = "";
        this.username = "";
        this.countVotes = "";
        this.tag = "";
        this.tag_ImageURL_Thumb = "";
        this.ImageURL_Thumb = "";
        this.imageURL_Local = "";
        this.replies = "";
    }

    public CommentResponse(String orderby_hot, String title, String comment, String createdDate,
                           String linkURL, String id, String username, String countVotes,
                           String tag, String tag_ImageURL_Thumb, String ImageURL_Thumb, String imageURL_Local, String replies) {

        this.orderby_hot = orderby_hot;
        this.title = title;
        this.comment = comment;
        this.createdDate = createdDate;
        this.linkURL = linkURL;
        this.id = id;
        this.username = username;
        this.countVotes = countVotes;
        this.tag = tag;
        this.tag_ImageURL_Thumb = ImageURL_Thumb;
        this.ImageURL_Thumb = ImageURL_Thumb;
        this.imageURL_Local = imageURL_Local;
        this.replies = replies;
    }
}
