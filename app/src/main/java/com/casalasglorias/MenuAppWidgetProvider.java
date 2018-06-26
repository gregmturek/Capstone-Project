package com.casalasglorias;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;

import java.util.Random;

public class MenuAppWidgetProvider extends AppWidgetProvider {
    private int mCurrentIndex;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Get the layout for the App Widget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.menu_appwidget);

            // Create an Intent to launch MainActivity and attach on-click listener to image
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

            // Create an Intent to update and attach on-click listener to button
            Intent updateIntent = new Intent(context, MenuAppWidgetProvider.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context,
                    0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_button, updatePendingIntent);

            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widget_image, views, appWidgetIds);

            GlideApp
                    .with(context.getApplicationContext())
                    .asBitmap()
                    .load(getImageUri(context))
                    .into(appWidgetTarget);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private String[] getStrings(Context context) {
        String[] strings = new String[17];

        strings[0] = context.getString(R.string.menu_content_salads_url);
        strings[1] = context.getString(R.string.menu_content_enchiladas_url);
        strings[2] = context.getString(R.string.menu_content_burritos_and_wraps_url);
        strings[3] = context.getString(R.string.menu_content_fajitas_and_steaks_url);
        strings[4] = context.getString(R.string.menu_content_specialties_url);
        strings[5] = context.getString(R.string.menu_content_mariscos_url);
        strings[6] = context.getString(R.string.menu_content_combinations_url);
        strings[7] = context.getString(R.string.menu_content_chimichangas_url);
        strings[8] = context.getString(R.string.menu_content_quesadillas_url);
        strings[9] = context.getString(R.string.menu_content_pepes_platters_url);
        strings[10] = context.getString(R.string.menu_content_burgers_and_sandwiches_url);
        strings[11] = context.getString(R.string.menu_content_lunch_url);
        strings[12] = context.getString(R.string.menu_content_appetizers_url);
        strings[13] = context.getString(R.string.menu_content_dips_for_your_chips_url);
        strings[14] = context.getString(R.string.menu_content_extras_url);
        strings[15] = context.getString(R.string.menu_content_beverages_url);
        strings[16] = context.getString(R.string.menu_content_desserts_url);

        return strings;
    }

    private Uri getImageUri(Context context) {
        int randomNumber;

        do {
            Random random = new Random();
            randomNumber = random.nextInt(17);
        } while (randomNumber == mCurrentIndex);

        mCurrentIndex = randomNumber;

        return Uri.parse(getStrings(context)[randomNumber]);
    }
}
