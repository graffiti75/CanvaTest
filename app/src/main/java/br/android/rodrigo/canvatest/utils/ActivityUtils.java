package br.android.rodrigo.canvatest.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import br.android.rodrigo.canvatest.view.animation.Navigation;

/**
 * ActivityUtils.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 28, 2016
 */
public class ActivityUtils {

    //--------------------------------------------------
    // Activity Methods
    //--------------------------------------------------

    public static void startActivityExtras(Activity activity, Class clazz, String key, Object value) {
        Intent intent = new Intent(activity, clazz);
        Bundle extras = getExtra(new Bundle(), key, value);
        intent.putExtras(extras);

        activity.startActivity(intent);
        Navigation.animate(activity, Navigation.Animation.GO);
    }

    private static Bundle getExtra(Bundle extras, String key, Object value) {
        if (value instanceof String) {
            extras.putString(key, (String)value);
        } else if (value instanceof Integer) {
            extras.putInt(key, (Integer)value);
        } else if (value instanceof Long) {
            extras.putLong(key, (Long)value);
        } else if (value instanceof Boolean) {
            extras.putBoolean(key, (Boolean) value);
        }
        return extras;
    }
}