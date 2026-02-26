package com.cl.week;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppConfigActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WeekWidgetPrefs";
    private static final String KEY_PKG_PREFIX = "pkg_";
    private static final String KEY_CLS_PREFIX = "cls_";
    private static final String KEY_LABEL_PREFIX = "label_";

    private static final String KEY_STYLE_INDEX = "style_index";
    private static final String KEY_FONT_INDEX = "font_index";

    private static final String KEY_BG_COLOR = "custom_bg";
    private static final String KEY_TODAY_DATE_COLOR = "custom_today_date";
    private static final String KEY_TODAY_WEEK_COLOR = "custom_today_week";
    private static final String KEY_PAST_DATE_COLOR = "custom_past_date";
    private static final String KEY_PAST_WEEK_COLOR = "custom_past_week";
    private static final String KEY_FUTURE_DATE_COLOR = "custom_future_date";
    private static final String KEY_FUTURE_WEEK_COLOR = "custom_future_week";

    private TextView[] labelViews = new TextView[7];
    private TextView tvStyleSummary;

    private android.widget.EditText etBgColor;
    private android.widget.EditText etTodayDateColor;
    private android.widget.EditText etTodayWeekColor;
    private android.widget.EditText etPastDateColor;
    private android.widget.EditText etPastWeekColor;
    private android.widget.EditText etFutureDateColor;
    private android.widget.EditText etFutureWeekColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_config);

        initViews();
        loadLabels();
        loadStyleSummary();
        loadCustomColorFields();
    }

    private void initViews() {
        labelViews[0] = findViewById(R.id.tv_app_mon);
        labelViews[1] = findViewById(R.id.tv_app_tue);
        labelViews[2] = findViewById(R.id.tv_app_wed);
        labelViews[3] = findViewById(R.id.tv_app_thu);
        labelViews[4] = findViewById(R.id.tv_app_fri);
        labelViews[5] = findViewById(R.id.tv_app_sat);
        labelViews[6] = findViewById(R.id.tv_app_sun);

        tvStyleSummary = findViewById(R.id.tv_style_summary);

        etBgColor = findViewById(R.id.et_bg_color);
        etTodayDateColor = findViewById(R.id.et_today_date_color);
        etTodayWeekColor = findViewById(R.id.et_today_week_color);
        etPastDateColor = findViewById(R.id.et_past_date_color);
        etPastWeekColor = findViewById(R.id.et_past_week_color);
        etFutureDateColor = findViewById(R.id.et_future_date_color);
        etFutureWeekColor = findViewById(R.id.et_future_week_color);

        int[] btnIds = {
                R.id.btn_pick_mon,
                R.id.btn_pick_tue,
                R.id.btn_pick_wed,
                R.id.btn_pick_thu,
                R.id.btn_pick_fri,
                R.id.btn_pick_sat,
                R.id.btn_pick_sun
        };

        for (int i = 0; i < 7; i++) {
            final int index = i;
            Button btn = findViewById(btnIds[i]);
            btn.setOnClickListener(v -> showAppPicker(index));
        }

        Button btnPickStyle = findViewById(R.id.btn_pick_style);
        btnPickStyle.setOnClickListener(v -> showStylePicker());

        Button btnPickFont = findViewById(R.id.btn_pick_font);
        btnPickFont.setOnClickListener(v -> showFontPicker());

        Button btnApplyCustom = findViewById(R.id.btn_apply_custom_style);
        btnApplyCustom.setOnClickListener(v -> applyCustomStyle());

        Button btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(v -> {
            updateAllWidgets();
            finish();
        });
    }

    private void loadLabels() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String[] weekNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        for (int i = 0; i < 7; i++) {
            String label = prefs.getString(KEY_LABEL_PREFIX + i, null);
            if (label == null) {
                labelViews[i].setText(weekNames[i] + "：未配置");
            } else {
                labelViews[i].setText(weekNames[i] + "：" + label);
            }
        }
    }

    private void loadStyleSummary() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int styleIndex = prefs.getInt(KEY_STYLE_INDEX, 0);
        int fontIndex = prefs.getInt(KEY_FONT_INDEX, 0);

        String styleText;
        if (styleIndex >= 0 && styleIndex <= 4) {
            styleText = "内置样式 " + (styleIndex + 1);
        } else {
            styleText = "自定义样式";
        }

        String[] fontNames = {"默认", "加粗无衬线", "紧凑无衬线", "衬线体", "等宽体"};
        String fontText = (fontIndex >= 0 && fontIndex < fontNames.length) ? fontNames[fontIndex] : "默认";

        tvStyleSummary.setText("当前样式：" + styleText + "，字体：" + fontText);
    }

    private void loadCustomColorFields() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        etBgColor.setText(colorToHex(prefs.getInt(KEY_BG_COLOR, 0xE6222222)));
        etTodayDateColor.setText(colorToHex(prefs.getInt(KEY_TODAY_DATE_COLOR, 0xFFFFFFFF)));
        etTodayWeekColor.setText(colorToHex(prefs.getInt(KEY_TODAY_WEEK_COLOR, 0xFFFFFFFF)));
        etPastDateColor.setText(colorToHex(prefs.getInt(KEY_PAST_DATE_COLOR, 0x99FFFFFF)));
        etPastWeekColor.setText(colorToHex(prefs.getInt(KEY_PAST_WEEK_COLOR, 0x66FFFFFF)));
        etFutureDateColor.setText(colorToHex(prefs.getInt(KEY_FUTURE_DATE_COLOR, 0xCCFFFFFF)));
        etFutureWeekColor.setText(colorToHex(prefs.getInt(KEY_FUTURE_WEEK_COLOR, 0x99FFFFFF)));
    }

    private void showAppPicker(int dayIndex) {
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        if (apps == null) {
            apps = new ArrayList<>();
        }

        Collections.sort(apps, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                CharSequence la = a.loadLabel(pm);
                CharSequence lb = b.loadLabel(pm);
                if (la == null) la = "";
                if (lb == null) lb = "";
                return la.toString().compareToIgnoreCase(lb.toString());
            }
        });

        final List<ActivityInfo> activities = new ArrayList<>();
        final List<String> labels = new ArrayList<>();

        for (ResolveInfo info : apps) {
            ActivityInfo ai = info.activityInfo;
            if (ai != null) {
                activities.add(ai);
                CharSequence label = info.loadLabel(pm);
                if (label == null) {
                    label = ai.packageName;
                }
                labels.add(label.toString());
            }
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("选择应用");
        builder.setItems(labels.toArray(new String[0]), (dialog, which) -> {
            ActivityInfo ai = activities.get(which);
            String pkg = ai.packageName;
            String cls = ai.name;
            String label = labels.get(which);

            saveMapping(dayIndex, pkg, cls, label);
            loadLabels();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showStylePicker() {
        final String[] items = {
                "内置样式 1",
                "内置样式 2",
                "内置样式 3",
                "内置样式 4",
                "内置样式 5",
                "自定义样式"
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("选择样式");
        builder.setItems(items, (dialog, which) -> {
            int styleIndex = (which == 5) ? 5 : which;
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(KEY_STYLE_INDEX, styleIndex).apply();
            loadStyleSummary();
            updateAllWidgets();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showFontPicker() {
        final String[] items = {
                "默认（无衬线）",
                "加粗无衬线",
                "紧凑无衬线",
                "衬线体",
                "等宽体"
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("选择字体");
        builder.setItems(items, (dialog, which) -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(KEY_FONT_INDEX, which).apply();
            loadStyleSummary();
            updateAllWidgets();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void applyCustomStyle() {
        int bg = parseColor(etBgColor.getText().toString(), 0xE6222222);
        int todayDate = parseColor(etTodayDateColor.getText().toString(), 0xFFFFFFFF);
        int todayWeek = parseColor(etTodayWeekColor.getText().toString(), 0xFFFFFFFF);
        int pastDate = parseColor(etPastDateColor.getText().toString(), 0x99FFFFFF);
        int pastWeek = parseColor(etPastWeekColor.getText().toString(), 0x66FFFFFF);
        int futureDate = parseColor(etFutureDateColor.getText().toString(), 0xCCFFFFFF);
        int futureWeek = parseColor(etFutureWeekColor.getText().toString(), 0x99FFFFFF);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putInt(KEY_STYLE_INDEX, 5)
                .putInt(KEY_BG_COLOR, bg)
                .putInt(KEY_TODAY_DATE_COLOR, todayDate)
                .putInt(KEY_TODAY_WEEK_COLOR, todayWeek)
                .putInt(KEY_PAST_DATE_COLOR, pastDate)
                .putInt(KEY_PAST_WEEK_COLOR, pastWeek)
                .putInt(KEY_FUTURE_DATE_COLOR, futureDate)
                .putInt(KEY_FUTURE_WEEK_COLOR, futureWeek)
                .apply();

        loadStyleSummary();
        updateAllWidgets();
    }

    private String colorToHex(int color) {
        return String.format("#%08X", color);
    }

    private int parseColor(String text, int defaultColor) {
        if (text == null) return defaultColor;
        text = text.trim();
        if (text.isEmpty()) return defaultColor;
        try {
            return android.graphics.Color.parseColor(text);
        } catch (IllegalArgumentException e) {
            return defaultColor;
        }
    }

    private void saveMapping(int dayIndex, String pkg, String cls, String label) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_PKG_PREFIX + dayIndex, pkg)
                .putString(KEY_CLS_PREFIX + dayIndex, cls)
                .putString(KEY_LABEL_PREFIX + dayIndex, label)
                .apply();
    }

    private void updateAllWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, WeekCalendarWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        WeekCalendarWidgetProvider provider = new WeekCalendarWidgetProvider();
        provider.onUpdate(this, appWidgetManager, appWidgetIds);
    }
}

