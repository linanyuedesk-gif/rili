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
import android.widget.Button;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;

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

    private View previewBg;
    private View previewTodayDate;
    private View previewTodayWeek;
    private View previewPastDate;
    private View previewPastWeek;
    private View previewFutureDate;
    private View previewFutureWeek;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_config);

        initViews();
        loadLabels();
        loadStyleSummary();
        loadColorPreviews();
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

        previewBg = findViewById(R.id.view_bg_color);
        previewTodayDate = findViewById(R.id.view_today_date_color);
        previewTodayWeek = findViewById(R.id.view_today_week_color);
        previewPastDate = findViewById(R.id.view_past_date_color);
        previewPastWeek = findViewById(R.id.view_past_week_color);
        previewFutureDate = findViewById(R.id.view_future_date_color);
        previewFutureWeek = findViewById(R.id.view_future_week_color);

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

        View.OnClickListener colorClickListener;

        Button btnBgColor = findViewById(R.id.btn_bg_color);
        btnBgColor.setOnClickListener(v -> showColorPicker(KEY_BG_COLOR, 0xE6222222, previewBg));
        previewBg.setOnClickListener(v -> showColorPicker(KEY_BG_COLOR, 0xE6222222, previewBg));

        Button btnTodayDateColor = findViewById(R.id.btn_today_date_color);
        btnTodayDateColor.setOnClickListener(v -> showColorPicker(KEY_TODAY_DATE_COLOR, 0xFFFFFFFF, previewTodayDate));
        previewTodayDate.setOnClickListener(v -> showColorPicker(KEY_TODAY_DATE_COLOR, 0xFFFFFFFF, previewTodayDate));

        Button btnTodayWeekColor = findViewById(R.id.btn_today_week_color);
        btnTodayWeekColor.setOnClickListener(v -> showColorPicker(KEY_TODAY_WEEK_COLOR, 0xFFFFFFFF, previewTodayWeek));
        previewTodayWeek.setOnClickListener(v -> showColorPicker(KEY_TODAY_WEEK_COLOR, 0xFFFFFFFF, previewTodayWeek));

        Button btnPastDateColor = findViewById(R.id.btn_past_date_color);
        btnPastDateColor.setOnClickListener(v -> showColorPicker(KEY_PAST_DATE_COLOR, 0x99FFFFFF, previewPastDate));
        previewPastDate.setOnClickListener(v -> showColorPicker(KEY_PAST_DATE_COLOR, 0x99FFFFFF, previewPastDate));

        Button btnPastWeekColor = findViewById(R.id.btn_past_week_color);
        btnPastWeekColor.setOnClickListener(v -> showColorPicker(KEY_PAST_WEEK_COLOR, 0x66FFFFFF, previewPastWeek));
        previewPastWeek.setOnClickListener(v -> showColorPicker(KEY_PAST_WEEK_COLOR, 0x66FFFFFF, previewPastWeek));

        Button btnFutureDateColor = findViewById(R.id.btn_future_date_color);
        btnFutureDateColor.setOnClickListener(v -> showColorPicker(KEY_FUTURE_DATE_COLOR, 0xCCFFFFFF, previewFutureDate));
        previewFutureDate.setOnClickListener(v -> showColorPicker(KEY_FUTURE_DATE_COLOR, 0xCCFFFFFF, previewFutureDate));

        Button btnFutureWeekColor = findViewById(R.id.btn_future_week_color);
        btnFutureWeekColor.setOnClickListener(v -> showColorPicker(KEY_FUTURE_WEEK_COLOR, 0x99FFFFFF, previewFutureWeek));
        previewFutureWeek.setOnClickListener(v -> showColorPicker(KEY_FUTURE_WEEK_COLOR, 0x99FFFFFF, previewFutureWeek));

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

    private void loadColorPreviews() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        setPreviewColor(previewBg, prefs.getInt(KEY_BG_COLOR, 0xE6222222));
        setPreviewColor(previewTodayDate, prefs.getInt(KEY_TODAY_DATE_COLOR, 0xFFFFFFFF));
        setPreviewColor(previewTodayWeek, prefs.getInt(KEY_TODAY_WEEK_COLOR, 0xFFFFFFFF));
        setPreviewColor(previewPastDate, prefs.getInt(KEY_PAST_DATE_COLOR, 0x99FFFFFF));
        setPreviewColor(previewPastWeek, prefs.getInt(KEY_PAST_WEEK_COLOR, 0x66FFFFFF));
        setPreviewColor(previewFutureDate, prefs.getInt(KEY_FUTURE_DATE_COLOR, 0xCCFFFFFF));
        setPreviewColor(previewFutureWeek, prefs.getInt(KEY_FUTURE_WEEK_COLOR, 0x99FFFFFF));
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

    private void setPreviewColor(View view, int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    private void showColorPicker(String key, int defaultColor, View preview) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentColor = prefs.getInt(key, defaultColor);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_color_picker, null, false);
        View previewView = dialogView.findViewById(R.id.view_color_preview);
        android.widget.SeekBar seekR = dialogView.findViewById(R.id.seek_r);
        android.widget.SeekBar seekG = dialogView.findViewById(R.id.seek_g);
        android.widget.SeekBar seekB = dialogView.findViewById(R.id.seek_b);
        android.widget.SeekBar seekA = dialogView.findViewById(R.id.seek_a);
        TextView tvR = dialogView.findViewById(R.id.tv_r_value);
        TextView tvG = dialogView.findViewById(R.id.tv_g_value);
        TextView tvB = dialogView.findViewById(R.id.tv_b_value);
        TextView tvA = dialogView.findViewById(R.id.tv_a_value);

        int a = (currentColor >> 24) & 0xFF;
        int r = (currentColor >> 16) & 0xFF;
        int g = (currentColor >> 8) & 0xFF;
        int b = (currentColor) & 0xFF;

        seekA.setMax(100);
        seekR.setMax(255);
        seekG.setMax(255);
        seekB.setMax(255);

        seekA.setProgress((int) (a / 255f * 100));
        seekR.setProgress(r);
        seekG.setProgress(g);
        seekB.setProgress(b);

        tvA.setText(String.valueOf(seekA.getProgress()));
        tvR.setText(String.valueOf(r));
        tvG.setText(String.valueOf(g));
        tvB.setText(String.valueOf(b));

        final int[] colorHolder = new int[1];

        android.widget.SeekBar.OnSeekBarChangeListener listener = new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                int alpha = (int) (seekA.getProgress() / 100f * 255);
                int red = seekR.getProgress();
                int green = seekG.getProgress();
                int blue = seekB.getProgress();

                colorHolder[0] = android.graphics.Color.argb(alpha, red, green, blue);
                previewView.setBackgroundColor(colorHolder[0]);

                tvA.setText(String.valueOf(seekA.getProgress()));
                tvR.setText(String.valueOf(red));
                tvG.setText(String.valueOf(green));
                tvB.setText(String.valueOf(blue));
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        };

        seekA.setOnSeekBarChangeListener(listener);
        seekR.setOnSeekBarChangeListener(listener);
        seekG.setOnSeekBarChangeListener(listener);
        seekB.setOnSeekBarChangeListener(listener);

        // Initialize preview color
        listener.onProgressChanged(seekA, seekA.getProgress(), false);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("选择颜色和透明度");
        builder.setView(dialogView);
        builder.setPositiveButton("确定", (dialog, which) -> {
            int chosenColor = colorHolder[0];
            prefs.edit()
                    .putInt(KEY_STYLE_INDEX, 5)
                    .putInt(key, chosenColor)
                    .apply();
            setPreviewColor(preview, chosenColor);
            loadStyleSummary();
            updateAllWidgets();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
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

