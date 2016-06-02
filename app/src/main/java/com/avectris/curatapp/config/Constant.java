package com.avectris.curatapp.config;

/**
 * Created by thuongle on 1/13/16.
 */
public interface Constant {

    //    String BETA_API_END_POINT = "https://app.curatapp.com/api/v1/";
    String BETA_API_END_POINT = "http://curatapp.master.stickyviral.com/public/api/v3/";
    String API_END_POINT = "https://app.curatapp.com/api/v3/";

    String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    String REGISTRATION_COMPLETE = "registrationComplete";
    String RECEIVE_NOTIFICATION = "receiveNotification";
    int ITEM_PER_PAGE = 10;
    int UPCOMING_CONTENT_MODE = 0;
    int POSTED_CONTENT_MODE = 1;
}
