package com.example.ankit.stackup.dataModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SOQuestion {

    @SerializedName("question_id")
    public long questionId;

    public String title;

    public List<String> tags;

    public Profile owner;

    @SerializedName("is_answered")
    public boolean isAnswered;

    @SerializedName("view_count")
    public long viewCount;

    @SerializedName("answer_count")
    public long answerCount;

    public long score;

    @SerializedName("last_activity_date")
    public long lastActivityDate;

    @SerializedName("creation_date")
    public long creationDate;

    @SerializedName("link")
    public String questionLink;
}
