package com.example.ankit.stackup.responseModels;

import com.example.ankit.stackup.dataModels.SOQuestion;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnansweredQuestionsResponse {

    @SerializedName("items")
    public List<SOQuestion> soQuestions;

    @SerializedName("has_more")
    public boolean hasMore;

    @SerializedName("quota_max")
    public int quotaMax;

    @SerializedName("quota_remaining")
    public int quotaRemaining;

}
