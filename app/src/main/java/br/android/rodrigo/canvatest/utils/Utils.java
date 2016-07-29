package br.android.rodrigo.canvatest.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import com.afollestad.materialdialogs.MaterialDialog;

import br.android.rodrigo.canvatest.R;

/**
 * Utils.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 24, 2016
 */
public class Utils {

    //--------------------------------------------------
    // Dialog Methods
    //--------------------------------------------------

    public static Boolean hasConnection(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        Boolean hasConnection = connectivityManager.getActiveNetworkInfo() != null;
        return hasConnection;
    }

    public static void callLostConnectionDialog(Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.dialog__lost_connection_title))
                .content(activity.getString(R.string.dialog__lost_connection_message))
                .positiveText(activity.getString(R.string.dialog__ok))
                .show();
    }

    //--------------------------------------------------
    // String Methods
    //--------------------------------------------------

    public static Boolean isEmpty(String text) {
        Boolean result = true;
        Boolean isNull = (text == null);
        if (!isNull) {
            Boolean isZeroLength = (text.length() <= 0);
            Boolean isEmpty = (text.equals(""));
            Boolean contentOfTextIsLiteralNull = (text.equals("null"));
            result = isNull || isZeroLength || isEmpty || contentOfTextIsLiteralNull;
        }
        return result;
    }
}