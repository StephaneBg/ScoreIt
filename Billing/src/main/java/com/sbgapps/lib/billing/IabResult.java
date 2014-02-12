package com.sbgapps.lib.billing;

/**
 * Created by sbaiget on 15/09/13.
 */
public class IabResult {
    int mResponse;
    String mMessage;

    public IabResult(int response, String message) {
        mResponse = response;
        if (message == null || message.trim().length() == 0) {
            mMessage = IabHelper.getResponseDesc(response);
        } else {
            mMessage = message + " (response: " + IabHelper.getResponseDesc(response) + ")";
        }
    }

    public int getResponse() {
        return mResponse;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isSuccess() {
        return mResponse == IabHelper.BILLING_RESPONSE_RESULT_OK;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public String toString() {
        return "IabResult: " + getMessage();
    }
}
