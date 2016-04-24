package com.example.ankit.stackup.database;

public final class StackUpContract {

    private StackUpContract() {
    }

    public static final String DB_NAME = "StackUp.db";

    public static final int DB_VERSION = 1;

    public static final class SOQuestion {
        public static final String TABLE_NAME = "SOQuestion";

        public static final String COLUMN_QUESTION_ID = "questionId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TAGS = "tags";
        public static final String COLUMN_OWNER = "owner";
        public static final String COLUMN_IS_ANSWERED = "isAnswered";
        public static final String COLUMN_VIEW_COUNT = "viewCount";
        public static final String COLUMN_ANSWER_COUNT = "answerCount";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_LAST_ACTIVE_DATE = "lastActivityDate";
        public static final String COLUMN_CREATION_DATE = "creationDate";
        public static final String COLUMN_QUESTION_LINK = "questionLink";

    }

    public static final class Profile {
        public static final String TABLE_NAME = "Profile";

        public static final String COLUMN_USER_ID = "userId";
        public static final String COLUMN_USER_TYPE = "userType";
        public static final String COLUMN_ACCEPT_RATE = "acceptRate";
        public static final String COLUMN_PROFILE_IMAGE_URL = "profileImageUrl";
        public static final String COLUMN_DISPLAY_NAME = "displayName";
        public static final String COLUMN_PROFILE_LINK = "profileLink";
        public static final String COLUMN_REPUTATION = "reputation";
    }

}
