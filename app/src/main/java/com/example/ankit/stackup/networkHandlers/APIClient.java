package com.example.ankit.stackup.networkHandlers;

import com.example.ankit.stackup.dataModels.SOQuestion;
import com.example.ankit.stackup.responseModels.UnansweredQuestionsResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface APIClient {

    @GET("/questions/unanswered")
    void listUnansweredQuestions(@Query("order") String order, @Query("sort") String sort, @Query("tagged") String tagged,
                                 @Query("site") String site, Callback<UnansweredQuestionsResponse> response);

}
