package com.alifmrdb.coronamessangeruni.common;
/**
 * Created by IF on 13.01
 * Instances for Firebase
 *
 * */
public class NodeNames {

    //Firebase Database
    public static final String USERS = "users";
    public static final String NICKNAME = "userName";
    public static final String EMAIL = "email";
    public static final String PHOTO = "userPhoto";
    public static final String AGE = "userAge";
    public static final String USERID = "userId";
    public static final String RADIUS = "radius";
    public static final String SEX = "userSex";
    public static final String ABOUT_ME = "aboutMe";

    public static final String USERONLINE ="isUserOnline";
    public static final String LASTONLINE ="lastUserOnline";
    public static final String LATITUDE = "userLatitude";
    public static final String LONGITUDE = "userLongitude";
    public static final String NEWMESSAGE = "newMessage";


    public static final String SEARCH_PREFERENCES = "searchPreferences" ;
    public static final String AGE_MIN = "MinAge";
    public static final String AGE_MAX ="MaxAge" ;
    public static final String DISTANCE_PREFERENCES = "Distance";
    public static final String SEX_PREFERENCES = "Sex";
    public static final int MAX_SEARCH_DISTANCE = 20000;
    public static final String FIRST_TIME_START_PREFERENCES = "firstTimeStart";

    public final static int DEFAULT_SEARCH_SEX = 3;
    public final static int DEFAULT_SEARCH_AGE_MIN = 18;
    public  static int DEFAULT_SEARCH_AGE_MAX = 50;


}
