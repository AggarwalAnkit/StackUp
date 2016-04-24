package com.example.ankit.stackup.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ankit.stackup.R;
import com.example.ankit.stackup.activities.HomeActivity;
import com.example.ankit.stackup.commons.StackUpApplication;
import com.example.ankit.stackup.commons.Utils;
import com.example.ankit.stackup.dataModels.SOQuestion;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class QuestionsListAdapter extends RecyclerView.Adapter<QuestionsListAdapter.QuestionsViewHolder> {

    private List<SOQuestion> mSOQuestions;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private static String clock;
    private static String tag;
    private static String likeEmpty;
    private static String likeFilled;
    private static String link;
    private static String upVote;
    private static String views;

    public QuestionsListAdapter(Context context, List<SOQuestion> soQuestions) {
        this.mSOQuestions = soQuestions;

        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        clock = context.getString(R.string.fa_clock);
        tag = context.getString(R.string.fa_tag);
        likeEmpty = context.getString(R.string.fa_thumbs_up_empty);
        likeFilled = context.getString(R.string.fa_thumbs_up_filled);
        link = context.getString(R.string.fa_link);
        upVote = context.getString(R.string.fa_caret_up);
        views = context.getString(R.string.fa_eye);
    }

    @Override
    public int getItemCount() {
        return mSOQuestions.size();
    }

    @Override
    public QuestionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuestionsViewHolder(mContext, mLayoutInflater.inflate(R.layout.adapter_questions_list, parent, false));
    }

    @Override
    public void onBindViewHolder(QuestionsViewHolder holder, int position) {

        SOQuestion soQuestion = mSOQuestions.get(position);

        Picasso.with(holder.ivProfilePhoto.getContext())
                .load(TextUtils.isEmpty(soQuestion.owner.profileImageUrl) ? null : soQuestion.owner.profileImageUrl)
                .resize(80, 80)
                .centerCrop()
                .into(holder.ivProfilePhoto);

        holder.tvQuestion.setText(soQuestion.title);


        holder.tvTags.setText(String.format(Locale.getDefault(), "%s\t%s",
                tag, soQuestion.tags.toString().replaceAll("\\[", "").replaceAll("\\]", "")));

        holder.tvUsername.setText(soQuestion.owner.displayName);

        holder.tvReputation.setText(String.format(Locale.getDefault(), "Reputation: \t%d", soQuestion.owner.reputation));

        holder.tvTime.setText(String.format(Locale.getDefault(),
                "%s\t\t%s", Utils.formatTimeStamp(soQuestion.creationDate), clock));

        holder.tvLike.setText(likeEmpty);

        holder.tvLink.setText(link);

        holder.tvVotes.setText(String.format(Locale.getDefault(), "%s\t%d", upVote, soQuestion.score));

        holder.tvViews.setText(String.format(Locale.getDefault(), "%s\t%d", views, soQuestion.viewCount));
    }

    public static class QuestionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivProfilePhoto;
        public TextView tvUsername;
        public TextView tvReputation;
        public TextView tvTime;
        public TextView tvQuestion;
        public TextView tvTags;
        public TextView tvLike;
        public TextView tvLink;
        public TextView tvVotes;
        public TextView tvViews;

        private static Typeface fontAwesomeTypeface = Typeface.createFromAsset(StackUpApplication.APP_CONTEXT.getAssets(),
                "fontawesome-webfont.ttf");

        private WeakReference<Context> activityContext;

        public QuestionsViewHolder(Context context, View itemView) {
            super(itemView);

            activityContext = new WeakReference<>(context);

            ivProfilePhoto = (ImageView) itemView.findViewById(R.id.iv_profile_photo);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvReputation = (TextView) itemView.findViewById(R.id.tv_reputation);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvQuestion = (TextView) itemView.findViewById(R.id.tv_question);
            tvTags = (TextView) itemView.findViewById(R.id.tv_tags);
            tvLike = (TextView) itemView.findViewById(R.id.tv_like);
            tvLink = (TextView) itemView.findViewById(R.id.tv_link);
            tvVotes = (TextView) itemView.findViewById(R.id.tv_votes);
            tvViews = (TextView) itemView.findViewById(R.id.tv_views);

            tvTime.setTypeface(fontAwesomeTypeface);
            tvTags.setTypeface(fontAwesomeTypeface);
            tvLike.setTypeface(fontAwesomeTypeface);
            tvLink.setTypeface(fontAwesomeTypeface);
            tvVotes.setTypeface(fontAwesomeTypeface);
            tvViews.setTypeface(fontAwesomeTypeface);

            ivProfilePhoto.setOnClickListener(this);
            tvUsername.setOnClickListener(this);
            tvReputation.setOnClickListener(this);
            tvLink.setOnClickListener(this);
            tvLike.setOnClickListener(this);
            tvVotes.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.iv_profile_photo:
                case R.id.tv_user_name:
                case R.id.tv_reputation:
                    if (activityContext.get() != null) {
                        ((HomeActivity) activityContext.get()).openProfile(getAdapterPosition());
                    }
                    break;

                case R.id.tv_link:
                    if (activityContext.get() != null) {
                        ((HomeActivity) activityContext.get()).openQuestion(getAdapterPosition());
                    }
                    break;

                case R.id.tv_like:
                    if (activityContext.get() != null) {
                        ((HomeActivity) activityContext.get()).likeQuestion(getAdapterPosition());
                    }
                    break;
            }
        }
    }
}
