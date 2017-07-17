package jp.toastkid.calendar.calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.analytics.LogSender;
import jp.toastkid.calendar.databinding.FragmentCalendarBinding;
import jp.toastkid.calendar.libs.intent.IntentFactory;
import jp.toastkid.calendar.main.MainActivity;
import jp.toastkid.calendar.search.SearchFragment;

/**
 * Calendar fragment.
 *
 * @author toastkidjp
 */
public class CalendarFragment extends Fragment {

    /** Data binding object. */
    private FragmentCalendarBinding binding;

    /** Analytics logger wrapper. */
    private LogSender logSender;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        logSender = new LogSender(context);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
        initCalendarView();
        return binding.getRoot();
    }

    /**
     * Initialize calendar view.
     */
    private void initCalendarView() {
        binding.calendar.setDate(System.currentTimeMillis());
        binding.calendar.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {
                    final Context context = getContext();
                    final String dateTitle = DateTitleFactory.makeDateTitle(context, month, dayOfMonth);
                    new AlertDialog.Builder(context)
                            .setTitle(dateTitle)
                            .setItems(R.array.calendar_menu, (d, index) -> {
                                final Bundle bundle = new Bundle();
                                bundle.putString("daily", dateTitle);
                                if (index == 0) {
                                    logSender.send("cal_wkp", bundle);
                                    new CalendarArticleLinker(context, month, dayOfMonth).invoke();
                                    return;
                                }
                                if (index == 1) {
                                    logSender.send("cal_schdl", bundle);
                                    startActivity(IntentFactory.makeCalendar(view.getDate()));
                                    return;
                                }
                                if (index == 2) {
                                    logSender.send("cal_srch", bundle);
                                    startActivity(MainActivity.makeSearchIntent(context, dateTitle));
                                }
                            })
                            .setCancelable(true)
                            .setOnCancelListener(v -> logSender.send("cal_x"))
                            .setPositiveButton(R.string.close, (d, i) -> d.dismiss())
                            .show();
                });
    }
}
