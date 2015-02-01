package com.kylemsguy.studybuddy.backend;

import com.google.android.gms.auth.GoogleAuthUtil;

/**
 * Created by kyle on 31/01/15.
 */
public class CalendarManager {

    String token = GoogleAuthUtil.getToken(mActivity, mEmail, mScopes);

    public CalendarManager() {

    }

}
