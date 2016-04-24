package com.example.ankit.stackup.dataModels;

import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("user_id")
    public long userId;

    @SerializedName("user_type")
    public String userType;

    @SerializedName("accept_rate")
    public int acceptRate;

    @SerializedName("profile_image")
    public String profileImageUrl;

    @SerializedName("display_name")
    public String displayName;

    @SerializedName("link")
    public String profileLink;

    public long reputation;
}
