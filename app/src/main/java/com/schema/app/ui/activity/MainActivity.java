package com.schema.app.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;
import com.schema.app.R;
import com.schema.app.data.model.Event;
import com.schema.app.databinding.ActivityMainBinding;
import com.schema.app.ui.adapter.EventListAdapter;
import com.schema.app.ui.viewmodel.MainViewModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private EventListAdapter eventListAdapter;

    private LocalDate selectedDate = LocalDate.now();
    private List<Event> allEvents = new ArrayList<>();
    private Map<LocalDate, List<Event>> eventsByDate;
    private boolean isShowingAllEvents = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- UI 색상 강제 설정 ---
        // 상태 표시줄 색상 변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.dark_background));
        }
        // 캘린더와 리스트 배경 색상 변경
        binding.calendarContainer.setBackgroundColor(getColor(R.color.dark_surface));
        binding.listContainer.setBackgroundColor(getColor(R.color.dark_surface));
        // --- ---

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        setupCalendarView();
        setupClickListeners();
        requestNotificationPermission();
        checkExactAlarmPermission();

        mainViewModel.getAllEvents().observe(this, events -> {
            allEvents = events;
            eventsByDate = events.stream().collect(Collectors.groupingBy(event -> 
                new Date(event.getEventTimeMillis()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            ));
            binding.calendarView.notifyCalendarChanged();
            updateEventListView();
        });
    }

    private void setupClickListeners() {
        binding.fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra("selected_date_epoch_day", selectedDate.toEpochDay());
            startActivity(intent);
        });

        binding.fabSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        binding.btnToggleView.setOnClickListener(v -> {
            isShowingAllEvents = !isShowingAllEvents;
            updateEventListView();
        });

        binding.btnPastEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, PastEventsActivity.class);
            startActivity(intent);
        });
    }

    private void updateEventListView() {
        if (isShowingAllEvents) {
            binding.calendarContainer.setVisibility(View.GONE);
            binding.btnToggleView.setText("달력 보기");
            eventListAdapter.submitList(allEvents);
        } else {
            binding.calendarContainer.setVisibility(View.VISIBLE);
            binding.btnToggleView.setText("전체 일정 보기");
            filterEventsForSelectedDate(selectedDate);
        }
    }

    private void setupRecyclerView() {
        eventListAdapter = new EventListAdapter(new EventListAdapter.EventDiff(), event -> {
            Intent intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("event_id", event.getId());
            startActivity(intent);
        });
        binding.recyclerviewEvents.setAdapter(eventListAdapter);
        binding.recyclerviewEvents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupCalendarView() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(100);
        YearMonth endMonth = currentMonth.plusMonths(100);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        binding.calendarView.scrollToMonth(currentMonth);

        binding.calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
                container.textView.setText(String.valueOf(day.getDate().getDayOfMonth()));
                if (day.getPosition() == DayPosition.MonthDate) {
                    container.textView.setVisibility(View.VISIBLE);
                    if (day.getDate().isEqual(selectedDate)) {
                        container.textView.setBackgroundResource(R.drawable.calendar_selected_day_background);
                    } else {
                        container.textView.setBackgroundResource(0);
                    }

                    if (eventsByDate != null && eventsByDate.containsKey(day.getDate())) {
                        container.eventDot.setVisibility(View.VISIBLE);
                    } else {
                        container.eventDot.setVisibility(View.GONE);
                    }

                    container.getView().setOnClickListener(v -> {
                        if (isShowingAllEvents) return;
                        LocalDate oldDate = selectedDate;
                        selectedDate = day.getDate();
                        binding.calendarView.notifyDateChanged(oldDate);
                        binding.calendarView.notifyDateChanged(selectedDate);
                        filterEventsForSelectedDate(selectedDate);
                    });
                } else {
                    container.textView.setVisibility(View.GONE);
                    container.eventDot.setVisibility(View.GONE);
                    container.getView().setOnClickListener(null);
                }
            }
        });

        binding.calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, @NonNull CalendarMonth month) {
                String monthTitle = month.getYearMonth().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
                container.textView.setText(String.format("%s %d", monthTitle, month.getYearMonth().getYear()));
            }
        });

        binding.calendarView.post(() -> updateEventListView());
    }

    private static class DayViewContainer extends ViewContainer {
        public final TextView textView;
        public final View eventDot;

        public DayViewContainer(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
            eventDot = view.findViewById(R.id.calendarEventDot);
        }
    }

    private static class MonthViewContainer extends ViewContainer {
        public final TextView textView;

        public MonthViewContainer(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.calendarMonthHeaderText);
        }
    }

    private void filterEventsForSelectedDate(LocalDate date) {
        if (isShowingAllEvents) return;
        List<Event> events = eventsByDate != null ? eventsByDate.getOrDefault(date, new ArrayList<>()) : new ArrayList<>();
        eventListAdapter.submitList(events);
    }

    private void requestNotificationPermission() {
        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) {
                new AlertDialog.Builder(this)
                        .setTitle("알림 권한 필요")
                        .setMessage("알림을 받으려면 설정에서 알림 권한을 허용해야 합니다.")
                        .setPositiveButton("설정으로 이동", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                            startActivity(intent);
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle("정확한 알람 권한 필요")
                        .setMessage("앱의 핵심 기능인 자동 알람 예약을 위해 '정확한 알람 설정' 권한이 반드시 필요합니다. 설정 화면으로 이동하여 권한을 허용해주세요.")
                        .setPositiveButton("설정으로 이동", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("취소", (dialog, which) -> {
                            Toast.makeText(this, "권한이 거부되어 앱의 핵심 기능을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    }
}