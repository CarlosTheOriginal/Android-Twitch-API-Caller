package com.thegavners.axile;



//Created By: Carlos Mbendera
// Copyright Carlos Mbenderea

class FeedRow {


    private String id;
    private String username;
    private String userImageURL;
    private String thumbnailURL;
    private String views;


    FeedRow( String rowId,  String rowUsername, String rowuserImageURL,
             String rowThumbnailURL, String rowViews
    ) {


        username = rowUsername;
        userImageURL = rowuserImageURL;
        thumbnailURL = rowThumbnailURL;
        views = rowViews;
    }

    String getId(){return id;}

    public void setId(String id) { this.id = id;   }

    String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    String getUserImageURL() {      return userImageURL;    }

    public void setUSerImageURL(String userImageURL) {      this.userImageURL = userImageURL;   }

    String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }


}