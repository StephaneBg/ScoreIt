package com.sbgapps.lib.billing;

/**
 * Created by sbaiget on 15/09/13.
 */
public class IabException extends Exception {
    IabResult mResult;

    public IabException(IabResult r) {
        this(r, null);
    }

    public IabException(int response, String message) {
        this(new IabResult(response, message));
    }

    public IabException(IabResult r, Exception cause) {
        super(r.getMessage(), cause);
        mResult = r;
    }

    public IabException(int response, String message, Exception cause) {
        this(new IabResult(response, message), cause);
    }

    /**
     * Returns the IAB result (error) that this exception signals.
     */
    public IabResult getResult() {
        return mResult;
    }
}