package com.example.ankit.stackup.database;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.ankit.stackup.commons.StackUpApplication;
import com.example.ankit.stackup.dataModels.Profile;
import com.example.ankit.stackup.dataModels.SOQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper = null;

    private DatabaseHelper() {
        super(StackUpApplication.APP_CONTEXT, StackUpContract.DB_NAME, null, StackUpContract.DB_VERSION);
    }

    public static DatabaseHelper getInstance() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper();
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_PROFILE = "CREATE TABLE " + StackUpContract.Profile.TABLE_NAME + "(" +
                StackUpContract.Profile.COLUMN_USER_ID + " INTEGER PRIMARY KEY," +
                StackUpContract.Profile.COLUMN_USER_TYPE + " TEXT," +
                StackUpContract.Profile.COLUMN_ACCEPT_RATE + " INTEGER," +
                StackUpContract.Profile.COLUMN_PROFILE_IMAGE_URL + " TEXT," +
                StackUpContract.Profile.COLUMN_DISPLAY_NAME + " TEXT," +
                StackUpContract.Profile.COLUMN_PROFILE_LINK + " TEXT," +
                StackUpContract.Profile.COLUMN_REPUTATION + " INTEGER" + ")";

        String CREATE_TABLE_SO_QUESTION = "CREATE TABLE " + StackUpContract.SOQuestion.TABLE_NAME + "(" +
                StackUpContract.SOQuestion.COLUMN_QUESTION_ID + " INTEGER KEY," +
                StackUpContract.SOQuestion.COLUMN_TITLE + " TEXT," +
                StackUpContract.SOQuestion.COLUMN_TAGS + " TEXT," +
                StackUpContract.SOQuestion.COLUMN_OWNER + " INTEGER," +
                StackUpContract.SOQuestion.COLUMN_IS_ANSWERED + " INTEGER," +
                StackUpContract.SOQuestion.COLUMN_VIEW_COUNT + " INTEGER," +
                StackUpContract.SOQuestion.COLUMN_ANSWER_COUNT + " INTEGER," +
                StackUpContract.SOQuestion.COLUMN_SCORE + " INTEGER," +
                StackUpContract.SOQuestion.COLUMN_LAST_ACTIVE_DATE + " INTEGER," +
                StackUpContract.SOQuestion.COLUMN_CREATION_DATE + " INTEGER," +
                StackUpContract.SOQuestion.COLUMN_QUESTION_LINK + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_SO_QUESTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertOrUpdateQuestion(SOQuestion soQuestion) {

        //insert or update owner profile in profile table
        if (isProfilePresent(soQuestion.owner.userId)) {
            updateProfile(soQuestion.owner);
        } else {
            insertProfile(soQuestion.owner);
        }

        //insert or update question in SoQuestionsDB
        if (isQuestionPresent(soQuestion.questionId)) {
            return updateQuestion(soQuestion);
        } else {
            return insertQuestion(soQuestion);
        }
    }

    public boolean insertQuestion(SOQuestion soQuestion) {

        String INSERT_INTO_SO_QUESTION = "INSERT INTO " + StackUpContract.SOQuestion.TABLE_NAME + "(" +
                StackUpContract.SOQuestion.COLUMN_QUESTION_ID + "," +
                StackUpContract.SOQuestion.COLUMN_TITLE + "," +
                StackUpContract.SOQuestion.COLUMN_TAGS + "," +
                StackUpContract.SOQuestion.COLUMN_OWNER + "," +
                StackUpContract.SOQuestion.COLUMN_IS_ANSWERED + "," +
                StackUpContract.SOQuestion.COLUMN_VIEW_COUNT + "," +
                StackUpContract.SOQuestion.COLUMN_ANSWER_COUNT + "," +
                StackUpContract.SOQuestion.COLUMN_SCORE + "," +
                StackUpContract.SOQuestion.COLUMN_LAST_ACTIVE_DATE + "," +
                StackUpContract.SOQuestion.COLUMN_CREATION_DATE + "," +
                StackUpContract.SOQuestion.COLUMN_QUESTION_LINK + ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";

        SQLiteDatabase database = getReadableDatabase();
        SQLiteStatement statement = database.compileStatement(INSERT_INTO_SO_QUESTION);
        statement.bindLong(1, soQuestion.questionId);
        statement.bindString(2, soQuestion.title);
        statement.bindString(3, soQuestion.tags.toString().replaceAll("\\]", "").replaceAll("\\[", ""));
        statement.bindLong(4, soQuestion.owner.userId);
        statement.bindLong(5, soQuestion.isAnswered ? 1 : 0);
        statement.bindLong(6, soQuestion.viewCount);
        statement.bindLong(7, soQuestion.answerCount);
        statement.bindLong(8, soQuestion.score);
        statement.bindLong(9, soQuestion.lastActivityDate);
        statement.bindLong(10, soQuestion.creationDate);
        statement.bindString(11, soQuestion.questionLink);
        long result = statement.executeInsert();
        statement.close();

        return result == 1;
    }

    public void insertProfile(Profile profile) {

        String INSERT_INTO_PROFILE = "INSERT INTO " + StackUpContract.Profile.TABLE_NAME + "(" +
                StackUpContract.Profile.COLUMN_USER_ID + "," +
                StackUpContract.Profile.COLUMN_USER_TYPE + "," +
                StackUpContract.Profile.COLUMN_ACCEPT_RATE + "," +
                StackUpContract.Profile.COLUMN_PROFILE_IMAGE_URL + "," +
                StackUpContract.Profile.COLUMN_DISPLAY_NAME + "," +
                StackUpContract.Profile.COLUMN_PROFILE_LINK + "," +
                StackUpContract.Profile.COLUMN_REPUTATION + ") VALUES(?,?,?,?,?,?,?)";

        SQLiteDatabase database = getReadableDatabase();
        SQLiteStatement statement = database.compileStatement(INSERT_INTO_PROFILE);
        statement.bindLong(1, profile.userId);
        statement.bindString(2, profile.userType);
        statement.bindLong(3, profile.acceptRate);
        statement.bindString(4, profile.profileImageUrl);
        statement.bindString(5, profile.displayName);
        statement.bindString(6, profile.profileLink);
        statement.bindLong(7, profile.reputation);
        statement.executeInsert();
        statement.close();
    }

    public boolean updateQuestion(SOQuestion soQuestion) {
        return true;
    }

    public boolean updateProfile(Profile profile) {
        return true;

    }

    public void deleteQuestion(long questionId) {

    }

    public void deleteProfile(long userId) {

    }

    public boolean isQuestionPresent(long questionId) {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(),
                StackUpContract.SOQuestion.TABLE_NAME,
                StackUpContract.SOQuestion.COLUMN_QUESTION_ID + " = ?",
                new String[]{String.valueOf(questionId)}) > 0;
    }

    public boolean isProfilePresent(long userId) {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(),
                StackUpContract.Profile.TABLE_NAME,
                StackUpContract.Profile.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}) > 0;
    }

    public Map<Long, Profile> getUserIdAndProfileMap() {
        String SELECT_PROFILES = "SELECT * FROM " + StackUpContract.Profile.TABLE_NAME;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(SELECT_PROFILES, null);

        Map<Long, Profile> userIdAndProfileMap = new HashMap<>(cursor.getCount());

        int COLUMN_USER_ID = cursor.getColumnIndex(StackUpContract.Profile.COLUMN_USER_ID);
        int COLUMN_USER_TYPE = cursor.getColumnIndex(StackUpContract.Profile.COLUMN_USER_TYPE);
        int COLUMN_ACCEPT_RATE = cursor.getColumnIndex(StackUpContract.Profile.COLUMN_ACCEPT_RATE);
        int COLUMN_PROFILE_IMAGE_URL = cursor.getColumnIndex(StackUpContract.Profile.COLUMN_PROFILE_IMAGE_URL);
        int COLUMN_DISPLAY_NAME = cursor.getColumnIndex(StackUpContract.Profile.COLUMN_DISPLAY_NAME);
        int COLUMN_PROFILE_LINK = cursor.getColumnIndex(StackUpContract.Profile.COLUMN_PROFILE_LINK);
        int COLUMN_REPUTATION = cursor.getColumnIndex(StackUpContract.Profile.COLUMN_REPUTATION);

        while (cursor.moveToNext()) {
            Profile profile = new Profile();
            profile.userId = cursor.getLong(COLUMN_USER_ID);
            profile.userType = cursor.getString(COLUMN_USER_TYPE);
            profile.acceptRate = cursor.getInt(COLUMN_ACCEPT_RATE);
            profile.profileImageUrl = cursor.getString(COLUMN_PROFILE_IMAGE_URL);
            profile.displayName = cursor.getString(COLUMN_DISPLAY_NAME);
            profile.profileLink = cursor.getString(COLUMN_PROFILE_LINK);
            profile.reputation = cursor.getLong(COLUMN_REPUTATION);

            userIdAndProfileMap.put(profile.userId, profile);
        }
        cursor.close();
        return userIdAndProfileMap;
    }

    public List<SOQuestion> getAllQuestions() {
        String SELECT_QUESTIONS = "SELECT * FROM " + StackUpContract.SOQuestion.TABLE_NAME;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(SELECT_QUESTIONS, null);

        List<SOQuestion> soQuestions = new ArrayList<>(cursor.getCount());

        int COLUMN_QUESTION_ID = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_QUESTION_ID);
        int COLUMN_TITLE = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_TITLE);
        int COLUMN_TAGS = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_TAGS);
        int COLUMN_OWNER = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_OWNER);
        int COLUMN_IS_ANSWERED = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_IS_ANSWERED);
        int COLUMN_VIEW_COUNT = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_VIEW_COUNT);
        int COLUMN_ANSWER_COUNT = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_ANSWER_COUNT);
        int COLUMN_SCORE = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_SCORE);
        int COLUMN_LAST_ACTIVE_DATE = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_LAST_ACTIVE_DATE);
        int COLUMN_CREATION_DATE = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_CREATION_DATE);
        int COLUMN_QUESTION_LINK = cursor.getColumnIndex(StackUpContract.SOQuestion.COLUMN_QUESTION_LINK);

        while (cursor.moveToNext()) {

            Map<Long, Profile> userIdAndProfileMap = getUserIdAndProfileMap();

            SOQuestion soQuestion = new SOQuestion();

            soQuestion.questionId = cursor.getLong(COLUMN_QUESTION_ID);
            soQuestion.title = cursor.getString(COLUMN_TITLE);
            String[] tags = cursor.getString(COLUMN_TAGS).split(",");
            soQuestion.tags = Arrays.asList(tags);
            soQuestion.owner = userIdAndProfileMap.get(cursor.getLong(COLUMN_OWNER));
            soQuestion.isAnswered = cursor.getInt(COLUMN_IS_ANSWERED) == 1;
            soQuestion.viewCount = cursor.getLong(COLUMN_VIEW_COUNT);
            soQuestion.answerCount = cursor.getLong(COLUMN_ANSWER_COUNT);
            soQuestion.score = cursor.getLong(COLUMN_SCORE);
            soQuestion.lastActivityDate = cursor.getLong(COLUMN_LAST_ACTIVE_DATE);
            soQuestion.creationDate = cursor.getLong(COLUMN_CREATION_DATE);
            soQuestion.questionLink = cursor.getString(COLUMN_QUESTION_LINK);

            soQuestions.add(soQuestion);
        }
        cursor.close();

        return soQuestions;
    }
}
