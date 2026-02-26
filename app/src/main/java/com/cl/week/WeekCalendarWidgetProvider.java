package com.cl.week;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;

public class WeekCalendarWidgetProvider extends AppWidgetProvider {

    private static final String PREFS_NAME = "WeekWidgetPrefs";
    private static final String KEY_PKG_PREFIX = "pkg_";
    private static final String KEY_CLS_PREFIX = "cls_";
    private static final String KEY_STYLE_INDEX = "style_index"; // 0-4 内置样式, 5=自定义
    private static final String KEY_FONT_INDEX = "font_index";   // 0-4 字体

    // 自定义样式颜色键
    private static final String KEY_BG_COLOR = "custom_bg";
    private static final String KEY_TODAY_DATE_COLOR = "custom_today_date";
    private static final String KEY_TODAY_WEEK_COLOR = "custom_today_week";
    private static final String KEY_PAST_DATE_COLOR = "custom_past_date";
    private static final String KEY_PAST_WEEK_COLOR = "custom_past_week";
    private static final String KEY_FUTURE_DATE_COLOR = "custom_future_date";
    private static final String KEY_FUTURE_WEEK_COLOR = "custom_future_week";
    private static final String KEY_TODAY_BG_COLOR = "custom_today_bg";
    private static final String KEY_OTHER_BG_COLOR = "custom_other_bg";

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
        int fontIndex = getFontIndex(context);
        int layoutId = getLayoutForFont(fontIndex);
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        StyleConfig style = loadStyleConfig(context);
        views.setInt(R.id.content_container, "setBackgroundColor", style.backgroundColor);

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

            boolean isPast = !isToday && isBeforeDay(cal, today);
            boolean isFuture = !isToday && !isPast;

            int bgColor;
            int dateColor;
            int weekColor;

            if (isToday) {
                bgColor = style.todayBgColor;
                dateColor = style.todayDateColor;
                weekColor = style.todayWeekColor;
            } else if (isPast) {
                bgColor = style.otherBgColor;
                dateColor = style.pastDateColor;
                weekColor = style.pastWeekColor;
            } else {
                bgColor = style.otherBgColor;
                dateColor = style.futureDateColor;
                weekColor = style.futureWeekColor;
            }

            views.setInt(containerIds[i], "setBackgroundColor", bgColor);
            views.setTextColor(dateIds[i], dateColor);
            views.setTextColor(weekdayIds[i], weekColor);

            Intent launchIntent = getLaunchIntentForDay(context, i);
            if (launchIntent == null) {
                launchIntent = new Intent(context, AppConfigActivity.class);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    i,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(containerIds[i], pendingIntent);

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private boolean isBeforeDay(Calendar a, Calendar b) {
        if (a.get(Calendar.YEAR) != b.get(Calendar.YEAR)) {
            return a.get(Calendar.YEAR) < b.get(Calendar.YEAR);
        }
        return a.get(Calendar.DAY_OF_YEAR) < b.get(Calendar.DAY_OF_YEAR);
    }

    private int getFontIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_FONT_INDEX, 0);
    }

    private int getLayoutForFont(int fontIndex) {
        switch (fontIndex) {
            case 1:
                return R.layout.widget_week_calendar_style2;
            case 2:
                return R.layout.widget_week_calendar_style3;
            case 3:
                return R.layout.widget_week_calendar_style4;
            case 4:
                return R.layout.widget_week_calendar_style5;
            case 0:
            default:
                return R.layout.widget_week_calendar;
        }
    }

    private StyleConfig loadStyleConfig(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int styleIndex = prefs.getInt(KEY_STYLE_INDEX, 0);

        StyleConfig cfg = new StyleConfig();

        if (styleIndex == 5) {
            cfg.backgroundColor = prefs.getInt(KEY_BG_COLOR, 0xE6222222);
            cfg.todayBgColor = prefs.getInt(KEY_TODAY_BG_COLOR, 0xFF4285F4);
            cfg.otherBgColor = prefs.getInt(KEY_OTHER_BG_COLOR, 0x00000000);

            cfg.todayDateColor = prefs.getInt(KEY_TODAY_DATE_COLOR, 0xFFFFFFFF);
            cfg.todayWeekColor = prefs.getInt(KEY_TODAY_WEEK_COLOR, 0xFFFFFFFF);

            cfg.pastDateColor = prefs.getInt(KEY_PAST_DATE_COLOR, 0x99FFFFFF);
            cfg.pastWeekColor = prefs.getInt(KEY_PAST_WEEK_COLOR, 0x66FFFFFF);

            cfg.futureDateColor = prefs.getInt(KEY_FUTURE_DATE_COLOR, 0xCCFFFFFF);
            cfg.futureWeekColor = prefs.getInt(KEY_FUTURE_WEEK_COLOR, 0x99FFFFFF);

            return cfg;
        }

        switch (styleIndex) {
            case 1:
                cfg.backgroundColor = 0xE61E293B;
                cfg.todayBgColor = 0xFF3F51B5;
                cfg.otherBgColor = 0x00000000;

                cfg.todayDateColor = 0xFFFFFFFF;
                cfg.todayWeekColor = 0xFFFFFFFF;

                cfg.pastDateColor = 0x88FFFFFF;
                cfg.pastWeekColor = 0x55FFFFFF;

                cfg.futureDateColor = 0xCCFFFFFF;
                cfg.futureWeekColor = 0x99FFFFFF;
                break;
            case 2:
                cfg.backgroundColor = 0xE6000000;
                cfg.todayBgColor = 0xFFE91E63;
                cfg.otherBgColor = 0x00000000;

                cfg.todayDateColor = 0xFFFFFFFF;
                cfg.todayWeekColor = 0xFFFFFFFF;

                cfg.pastDateColor = 0x80FFFFFF;
                cfg.pastWeekColor = 0x55FFFFFF;

                cfg.futureDateColor = 0xFFFFFFFF;
                cfg.futureWeekColor = 0xB3FFFFFF;
                break;
            case 3:
                cfg.backgroundColor = 0xE6FFFFFF;
                cfg.todayBgColor = 0xFF2196F3;
                cfg.otherBgColor = 0x00000000;

                cfg.todayDateColor = 0xFFFFFFFF;
                cfg.todayWeekColor = 0xFFFFFFFF;

                cfg.pastDateColor = 0xFF9E9E9E;
                cfg.pastWeekColor = 0xFFBDBDBD;

                cfg.futureDateColor = 0xFF212121;
                cfg.futureWeekColor = 0xFF616161;
                break;
            case 4:
                cfg.backgroundColor = 0xE61B1B1B;
                cfg.todayBgColor = 0xFFFFC107;
                cfg.otherBgColor = 0x00000000;

                cfg.todayDateColor = 0xFF000000;
                cfg.todayWeekColor = 0xFF000000;

                cfg.pastDateColor = 0x66FFFFFF;
                cfg.pastWeekColor = 0x44FFFFFF;

                cfg.futureDateColor = 0xFFFFFFFF;
                cfg.futureWeekColor = 0xCCFFFFFF;
                break;
            case 0:
            default:
                cfg.backgroundColor = 0xE6222222;
                cfg.todayBgColor = 0xFF4285F4;
                cfg.otherBgColor = 0x00000000;

                cfg.todayDateColor = 0xFFFFFFFF;
                cfg.todayWeekColor = 0xFFFFFFFF;

                cfg.pastDateColor = 0x99FFFFFF;
                cfg.pastWeekColor = 0x66FFFFFF;

                cfg.futureDateColor = 0xCCFFFFFF;
                cfg.futureWeekColor = 0x99FFFFFF;
                break;
        }

        return cfg;
    }

    private static class StyleConfig {
        int backgroundColor;
        int todayBgColor;
        int otherBgColor;

        int todayDateColor;
        int todayWeekColor;

        int pastDateColor;
        int pastWeekColor;

        int futureDateColor;
        int futureWeekColor;
    }

    private Intent getLaunchIntentForDay(Context context, int dayIndex) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String pkg = prefs.getString(KEY_PKG_PREFIX + dayIndex, null);
        String cls = prefs.getString(KEY_CLS_PREFIX + dayIndex, null);
        if (pkg == null || cls == null) {
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(pkg, cls));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}

