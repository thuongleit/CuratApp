package com.avectris.curatapp.config;

/**
 * Created by thuongle on 1/13/16.
 */
public interface Constant {

    String BETA_API_END_POINT = "http://beta.curatapp.com/api/v2/";
    String API_END_POINT = "http://app.curatapp.com/api/v2/";

    String SECRET_KEY_CHANGE_URL = "~change~";

    String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    String REGISTRATION_COMPLETE = "registrationComplete";
    String RECEIVE_NOTIFICATION = "receiveNotification";
    int ITEM_PER_PAGE = 10;
    int UPCOMING_CONTENT_MODE = 0;
    int POSTED_CONTENT_MODE = 1;
}
