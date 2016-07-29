package br.android.rodrigo.canvatest.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;

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

    public static void startActivityTransition(Activity activity, Class clazz, int viewId, int transitionNameId) {
        Intent intent = new Intent(activity, clazz);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity, activity.findViewById(viewId), activity.getString(transitionNameId));
        activity.startActivity(intent, options.toBundle());
    }

    public static void startActivity(Activity activity, Class clazz) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
        Navigation.animate(activity, Navigation.Animation.GO);
    }

    public static void startActivityClearBackstack(Activity activity, Class clazz) {
        Intent intent = new Intent(activity, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        Navigation.animate(activity, Navigation.Animation.GO);
    }

    public static void startActivityExtras(Activity activity, Class clazz, String key, Object value) {
        Intent intent = new Intent(activity, clazz);
        Bundle extras = getExtra(new Bundle(), key, value);
        intent.putExtras(extras);

        activity.startActivity(intent);
        Navigation.animate(activity, Navigation.Animation.GO);
    }

    public static void startActivityExtras(Activity activity, Class clazz, String[] keys, Object[] values) {
        Intent intent = new Intent(activity, clazz);

        Bundle extras = new Bundle();
        Integer size = keys.length;
        for (int i = 0; i < size; i++) {
            String key = keys[i];
            Object value = values[i];
            extras = getExtra(extras, key, value);
        }
        intent.putExtras(extras);

        activity.startActivity(intent);
        Navigation.animate(activity, Navigation.Animation.GO);
    }

    public static void startActivityActionView(Activity activity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, Class clazz, Integer intentReturn) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivityForResult(intent, intentReturn);
        Navigation.animate(activity, Navigation.Animation.GO);
    }

    public static void startActivityForResultExtras(Activity activity, Class clazz, String[] keys, Object[] values, Integer intentReturn) {
        Intent intent = new Intent(activity, clazz);

        Bundle extras = new Bundle();
        Integer size = keys.length;
        for (int i = 0; i < size; i++) {
            String key = keys[i];
            Object value = values[i];
            extras = getExtra(extras, key, value);
        }
        intent.putExtras(extras);

        activity.startActivityForResult(intent, intentReturn);
        Navigation.animate(activity, Navigation.Animation.GO);
    }

    public static void startActivityForResultExtras(Activity activity, Class clazz, String key, Object value, Integer intentReturn) {
        Intent intent = new Intent(activity, clazz);
        Bundle extras = getExtra(new Bundle(), key, value);
        intent.putExtras(extras);

        activity.startActivityForResult(intent, intentReturn);
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