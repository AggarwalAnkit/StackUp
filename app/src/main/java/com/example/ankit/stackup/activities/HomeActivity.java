package com.example.ankit.stackup.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ankit.stackup.R;
import com.example.ankit.stackup.adapters.QuestionsListAdapter;
import com.example.ankit.stackup.commons.Constants;
import com.example.ankit.stackup.commons.Utils;
import com.example.ankit.stackup.dataModels.SOQuestion;
import com.example.ankit.stackup.database.DatabaseHelper;
import com.example.ankit.stackup.networkHandlers.APIClient;
import com.example.ankit.stackup.responseModels.UnansweredQuestionsResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private List<SOQuestion> mSOQuestions;
    private QuestionsListAdapter mQuestionsListAdapter;

    private ProgressDialog mPDHome;

    private TextView mTVSearchResult;
    private SwipeRefreshLayout mSRLMemories;

    private String searchTag = "";
    public int quotaMax = 100;
    public int quotaRemaining = 1;

    private boolean isLikedQuestionsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mPDHome = new ProgressDialog(this);
        mPDHome.setMessage("Please wait...");
        mPDHome.setCancelable(false);

        //setup views
        RecyclerView rvQuestions = (RecyclerView) findViewById(R.id.rv_questions);
        mTVSearchResult = (TextView) findViewById(R.id.tv_search_result);
        mSRLMemories = (SwipeRefreshLayout) findViewById(R.id.srl_questions);

        mTVSearchResult.setText(R.string.label_unanswered_questions);

        if (rvQuestions != null) {
            rvQuestions.setLayoutManager(new LinearLayoutManager(this));

            mSOQuestions = new ArrayList<>();
            mQuestionsListAdapter = new QuestionsListAdapter(this, mSOQuestions);
            rvQuestions.setAdapter(mQuestionsListAdapter);
        }

        mSRLMemories.setColorSchemeResources(android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        //set listeners
        mSRLMemories.setOnRefreshListener(this);

        mPDHome.show();
        fetchUnAnsweredQuestions();
    }

    @Override
    public void onRefresh() {
        mSRLMemories.setRefreshing(true);
        if (isLikedQuestionsVisible) {
            new FetchLikedQuestionsTask(this).execute();
        } else {
            fetchUnAnsweredQuestions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        final MenuItem actionSortByCreation = menu.findItem(R.id.action_sort_by_creation);
        final MenuItem actionSortByVotes = menu.findItem(R.id.action_sort_by_votes);
        final MenuItem actionViewFav = menu.findItem(R.id.action_view_fav);

        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchText = query.trim();

                if (!TextUtils.isEmpty(searchText)) {
                    searchTag = searchText;
                    mPDHome.show();
                    fetchUnAnsweredQuestions();
                }

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                actionSortByCreation.setVisible(true);
                actionSortByVotes.setVisible(true);
                actionViewFav.setVisible(true);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSortByCreation.setVisible(false);
                actionSortByVotes.setVisible(false);
                actionViewFav.setVisible(false);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_by_votes:
                Collections.sort(mSOQuestions, new Comparator<SOQuestion>() {
                    @Override
                    public int compare(SOQuestion lhs, SOQuestion rhs) {
                        //sorting in descending order
                        return (int) (rhs.score - lhs.score);
                    }
                });
                mQuestionsListAdapter.notifyDataSetChanged();
                break;

            case R.id.action_sort_by_creation:
                Collections.sort(mSOQuestions, new Comparator<SOQuestion>() {
                    @Override
                    public int compare(SOQuestion lhs, SOQuestion rhs) {
                        //sorting in descending order
                        return (int) (rhs.creationDate - lhs.creationDate);
                    }
                });
                mQuestionsListAdapter.notifyDataSetChanged();
                break;

            case R.id.action_view_fav:
                if (item.getTitle().equals(getString(R.string.label_view_all))) {
                    isLikedQuestionsVisible = false;
                    searchTag = "";
                    item.setTitle(R.string.label_view_fav);
                } else {
                    isLikedQuestionsVisible = true;
                    item.setTitle(R.string.label_view_all);
                }
                onRefresh();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openProfile(int position) {
        Utils.openLink(this, mSOQuestions.get(position).owner.profileLink);
    }

    public void openQuestion(int position) {
        Utils.openLink(this, mSOQuestions.get(position).questionLink);
    }

    public void likeQuestion(int position) {
        new InsertLikedQuestionsTask(this, mSOQuestions.get(position)).execute();
    }

    private void onLoadFinish() {
        if (mPDHome != null) {
            mPDHome.dismiss();
        }
        mSRLMemories.setRefreshing(false);
    }

    private void reLoadData(List<SOQuestion> soQuestions) {
        mSOQuestions.clear();
        mSOQuestions.addAll(soQuestions);
        mQuestionsListAdapter.notifyDataSetChanged();

        onLoadFinish();
    }

    private void fetchUnAnsweredQuestions() {

        RestAdapter retrofitAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();

        final APIClient apiClient = retrofitAdapter.create(APIClient.class);

        apiClient.listUnansweredQuestions(Constants.ORDER_DESC, Constants.SORT_ACTIVITY, searchTag, Constants.SITE,
                new Callback<UnansweredQuestionsResponse>() {
                    @Override
                    public void success(UnansweredQuestionsResponse apiResponse, Response response) {
                        quotaMax = apiResponse.quotaMax;
                        quotaRemaining = apiResponse.quotaRemaining;
                        reLoadData(apiResponse.soQuestions);

                        if (TextUtils.isEmpty(searchTag)) {
                            mTVSearchResult.setText(String.format(Locale.getDefault(),
                                    "%s \n API Quota: %d / %d", getString(R.string.label_unanswered_questions), quotaRemaining, quotaMax));
                        } else {
                            mTVSearchResult.setText(String.format(Locale.getDefault(),
                                    "%s for : %s \n API Quota: %d / %d",
                                    getString(R.string.label_unanswered_questions), searchTag, quotaRemaining, quotaMax));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        onLoadFinish();
                    }
                });
    }

    public static class FetchLikedQuestionsTask extends AsyncTask<Void, Void, List<SOQuestion>> {

        private WeakReference<Context> contextWeakReference;

        public FetchLikedQuestionsTask(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected List<SOQuestion> doInBackground(Void... params) {
            return DatabaseHelper.getInstance().getAllQuestions();
        }

        @Override
        protected void onPostExecute(List<SOQuestion> soQuestions) {
            if (contextWeakReference.get() != null) {
                ((HomeActivity) contextWeakReference.get()).reLoadData(soQuestions);
                ((HomeActivity) contextWeakReference.get()).mTVSearchResult.setText(R.string.label_favorite_questions);
            }
        }
    }

    public static class InsertLikedQuestionsTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<Context> contextWeakReference;
        private SOQuestion soQuestion;

        public InsertLikedQuestionsTask(Context context, SOQuestion soQuestion) {
            this.contextWeakReference = new WeakReference<>(context);
            this.soQuestion = soQuestion;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return DatabaseHelper.getInstance().insertOrUpdateQuestion(soQuestion);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (contextWeakReference.get() != null) {
                Toast.makeText(contextWeakReference.get(), "Insert Successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
