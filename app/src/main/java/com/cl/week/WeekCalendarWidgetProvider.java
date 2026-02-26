package com.cl.week;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Calendar;

public class WeekCalendarWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (Intent.ACTION_DATE_CHANGED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WeekCalendarWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_week_calendar);

        Calendar today = Calendar.getInstance();
        Calendar cal = (Calendar) today.clone();
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int diff = dayOfWeek - Calendar.MONDAY;
        if (diff < 0) {
            diff += 7;
        }
        cal.add(Calendar.DAY_OF_MONTH, -diff);

        int[] weekdayIds = {
                R.id.tv_weekday_0,
                R.id.tv_weekday_1,
                R.id.tv_weekday_2,
                R.id.tv_weekday_3,
                R.id.tv_weekday_4,
                R.id.tv_weekday_5,
                R.id.tv_weekday_6
        };

        int[] dateIds = {
                R.id.tv_date_0,
                R.id.tv_date_1,
                R.id.tv_date_2,
                R.id.tv_date_3,
                R.id.tv_date_4,
                R.id.tv_date_5,
                R.id.tv_date_6
        };

        int[] containerIds = {
                R.id.day_container_0,
                R.id.day_container_1,
                R.id.day_container_2,
                R.id.day_container_3,
                R.id.day_container_4,
                R.id.day_container_5,
                R.id.day_container_6
        };

        String[] weekNames = {"一", "二", "三", "四", "五", "六", "日"};

        for (int i = 0; i < 7; i++) {
            int dayNumber = cal.get(Calendar.DAY_OF_MONTH);

            views.setTextViewText(dateIds[i], String.valueOf(dayNumber));
            views.setTextViewText(weekdayIds[i], "周" + weekNames[i]);

            boolean isToday =
                    cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            && cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

            int bgColor;
            int textColor;

            if (isToday) {
                bgColor = 0xFF4285F4;      // 蓝色高亮
                textColor = 0xFFFFFFFF;    // 白色文字
            } else {
                bgColor = 0x00000000;      // 透明背景
                textColor = 0xCCFFFFFF;    // 半透明白色
            }

            views.setInt(containerIds[i], "setBackgroundColor", bgColor);
            views.setTextColor(dateIds[i], textColor);
            views.setTextColor(weekdayIds[i], textColor);

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

