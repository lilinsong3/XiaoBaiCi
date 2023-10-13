package com.github.lilinsong3.xiaobaici.ui.histories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.MainActivityViewModel;
import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.ItemLoadStateAdapter;
import com.github.lilinsong3.xiaobaici.common.VMVBFragment;
import com.github.lilinsong3.xiaobaici.databinding.FragmentHistoriesBinding;
import com.github.lilinsong3.xiaobaici.util.TimeUtil;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;

@AndroidEntryPoint
public class HistoriesFragment extends VMVBFragment<HistoriesViewModel, FragmentHistoriesBinding> {

    private static final String TAG = "HistoriesFragment";
    private NavController navController;
    private MaterialDatePicker<Pair<Long, Long>> datePicker;

    private BrowsingHistoryPageAdapter adapter;

    @Override
    protected void setupViewData() {
        navController = NavHostFragment.findNavController(this);
        setupToolbar();
        setupDateFilterChips();
        setupRecycler();
    }

    private void setupToolbar() {
        viewBinding.historiesToolbar.inflateMenu(R.menu.toolbar_histories_menu);
        NavigationUI.setupWithNavController(viewBinding.historiesToolbar, navController);

        viewBinding.historiesToolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.mi_histories_clear) {
                ViewUtil.openNormalDialog(
                        requireContext(),
                        getViewLifecycleOwner(),
                        R.string.short_clear_browsing_histories,
                        R.string.long_clear_browsing_histories,
                        viewModel.clear()
                );
                return true;
            }
            return false;
        });
    }

    private void setupDateFilterChips() {
        setupDateFromDatePicker();
        viewBinding.historiesChipSelectDate.setOnClickListener(v -> {
            viewBinding.historiesChipSelectDate.setClickable(false);
            viewBinding.historiesChipSelectDate.setEnabled(false);
            datePicker.show(getParentFragmentManager(), "SELECT_DATE");
        });
        viewBinding.historiesChipsDate.check(R.id.histories_chip_all);
        viewBinding.historiesChipsDate.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.size() == 1) {
                if (checkedIds.get(0).equals(R.id.histories_chip_all)) {
                    viewModel.selectDatetimeRange(Pair.create(TimeUtil.EPOCH, TimeUtil.nowDatetime()));
                    return;
                }

                if (checkedIds.get(0).equals(R.id.histories_chip_today)) {
                    viewModel.selectDatetimeRange(Pair.create(TimeUtil.nowDatetime(TimeUtil.DATETIME_FORMAT_START), TimeUtil.nowDatetime()));
                    return;
                }

                if (checkedIds.get(0).equals(R.id.histories_chip_yesterday)) {
                    viewModel.selectDatetimeRange(Pair.create(TimeUtil.yesterdayStart(), TimeUtil.yesterdayEnd()));
//                    return;
                }

//                if (checkedIds.get(0).equals(R.id.histories_chip_select_date)) {
//                    datePicker.show(getParentFragmentManager(), "SELECT_DATE");
//                }
            }
        });
    }

    private void setupDateFromDatePicker() {
        datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.short_select_date)
                .setSelection(Pair.create(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        TimeUtil.nowMillis()
                ))
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build())
                .setPositiveButtonText(R.string.short_confirm)
                .setNegativeButtonText(R.string.short_cancel)
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> viewModel.selectDatetimeRange(Pair.create(
                TimeUtil.utcMillisecondsToDatetime(selection.first),
                TimeUtil.utcMillisecondsToDatetime(selection.second, TimeUtil.DATETIME_FORMAT_END)
        )));

        datePicker.addOnDismissListener(dialog -> {
            viewBinding.historiesChipSelectDate.setClickable(true);
            viewBinding.historiesChipSelectDate.setEnabled(true);
        });
    }

    private void setupRecycler() {
        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        final Context context = requireContext();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        if (adapter == null) {
            adapter = new BrowsingHistoryPageAdapter(hanziWordId -> ViewUtil.openNormalDialog(
                    context,
                    lifecycleOwner,
                    R.string.short_delete,
                    R.string.long_confirm_delete,
                    viewModel.delete(hanziWordId)
            ), id -> navController.navigate(com.github.lilinsong3.xiaobaici.NavGraphDirections.actionGlobalHanziWord(id)));
        }

        ItemLoadStateAdapter itemLoadStateAdapter = new ItemLoadStateAdapter(adapter::retry);
        adapter.addOnPagesUpdatedListener(() -> {
            viewModel.setLoading(false);
            return Unit.INSTANCE;
        });
        viewBinding.historiesRecycler.setAdapter(adapter.withLoadStateFooter(itemLoadStateAdapter));
        final MaterialDividerItemDecoration materialDividerItemDecoration = new MaterialDividerItemDecoration(context, RecyclerView.VERTICAL);
        materialDividerItemDecoration.setLastItemDecorated(false);
        viewBinding.historiesRecycler.addItemDecoration(materialDividerItemDecoration);
        viewModel.browsingHistoryModelPage.observe(lifecycleOwner, pagingData -> adapter.submitData(lifecycleOwner.getLifecycle(), pagingData));

        // loading
        viewModel.showLoading.observe(lifecycleOwner, mainActivityViewModel::setGlobalLoading);
    }

    @NonNull
    @Override
    public Class<HistoriesViewModel> provideVMClass() {
        return HistoriesViewModel.class;
    }

    @Override
    public FragmentHistoriesBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentHistoriesBinding.inflate(inflater, container, false);
    }
}
