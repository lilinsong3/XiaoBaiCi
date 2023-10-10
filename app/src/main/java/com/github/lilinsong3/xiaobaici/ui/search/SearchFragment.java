package com.github.lilinsong3.xiaobaici.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ConcatAdapter;

import com.github.lilinsong3.xiaobaici.MainActivityViewModel;
import com.github.lilinsong3.xiaobaici.NavGraphDirections;
import com.github.lilinsong3.xiaobaici.databinding.FragmentSearchBinding;
import com.github.lilinsong3.xiaobaici.ui.adapter.ItemLoadStateAdapter;
import com.github.lilinsong3.xiaobaici.util.RxUtil;
import com.github.lilinsong3.xiaobaici.util.StringUtil;
import com.google.android.material.chip.Chip;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import kotlin.Unit;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private NavController navController;
    private SearchViewModel mViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private LifecycleOwner lifecycleOwner;
    private FragmentSearchBinding binding;
    private EditText searchInput;

    private SearchSuggestionListAdapter searchSuggestionListAdapter;
    private SearchResultPagingAdapter searchResultPagingAdapter;
    private ConcatAdapter searchResultPagingStateAdapter;
    private ItemLoadStateAdapter itemLoadStateAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBack();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 返回处理
    private void handleBack() {
        if (binding.fSearchRecycler.getVisibility() == View.VISIBLE
                && binding.fSearchRecycler.getAdapter() == searchResultPagingStateAdapter) {
            showSearchHistoryView();
            return;
        }
        navController.navigateUp();
    }

    private void showSearchResultView(String searchKeyword) {
        mainActivityViewModel.setGlobalLoading(true);
        setSearchResultAdapterForRecyclerView(searchKeyword);
        showRecyclerView();
    }

    private void showSearchSuggestionView(String inputtingKeyword) {
        setSearchSuggestionAdapterForRecyclerView(inputtingKeyword);
        showRecyclerView();
    }

    // 搜索结果adapter
    private void setSearchResultAdapterForRecyclerView(String searchKeyword) {
        searchResultPagingAdapter = new SearchResultPagingAdapter(
                itemId -> navController.navigate(NavGraphDirections.actionGlobalHanziWord(itemId))
        );
        if (itemLoadStateAdapter == null) {
            itemLoadStateAdapter = new ItemLoadStateAdapter(searchResultPagingAdapter::retry);
        }
        searchResultPagingAdapter.addOnPagesUpdatedListener(() -> {
            mainActivityViewModel.setGlobalLoading(false);
            return Unit.INSTANCE;
        });
        searchResultPagingStateAdapter = searchResultPagingAdapter.withLoadStateFooter(itemLoadStateAdapter);
        searchResultPagingAdapter.setSearchWord(searchKeyword);
        binding.fSearchRecycler.setAdapter(searchResultPagingStateAdapter);
    }

    // 搜索建议adapter
    private void setSearchSuggestionAdapterForRecyclerView(String inputtingKeyword) {
        searchSuggestionListAdapter = new SearchSuggestionListAdapter(mViewModel::search);
        searchSuggestionListAdapter.setLiveInputKeyword(inputtingKeyword);
        if (binding.fSearchRecycler.getAdapter() == searchSuggestionListAdapter) {
            return;
        }
        binding.fSearchRecycler.setAdapter(searchSuggestionListAdapter);
    }

    private void showRecyclerView() {
        if (binding.fSearchViewHistory.getVisibility() == View.GONE
                && binding.fSearchRecycler.getVisibility() == View.VISIBLE) {
            return;
        }
        binding.fSearchViewHistory.setVisibility(View.GONE);
        binding.fSearchRecycler.setVisibility(View.VISIBLE);
    }

    private void showSearchHistoryView() {
        if (binding.fSearchViewHistory.getVisibility() == View.VISIBLE
                && binding.fSearchRecycler.getVisibility() == View.GONE) {
            return;
        }
        binding.fSearchViewHistory.setVisibility(View.VISIBLE);
        binding.fSearchRecycler.setVisibility(View.GONE);
    }

    private void submitSearch() {
        Editable keyword = searchInput.getText();
        if (keyword != null) {
            mViewModel.search(keyword.toString());
        }
    }

    // 搜索栏
    private void setupSearchBar() {
        searchInput = binding.fFragmentInput.getEditText();
        if (searchInput != null) {
            // 搜索框输入监听
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (searchInput.hasFocus()) {
                        mViewModel.onKeywordInput(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            // 无焦点 -> 重新获焦点
            searchInput.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    mViewModel.onKeywordInput(searchInput.getText().toString());
                }
            });

            // 点击提交关键词，执行搜索
            searchInput.setOnEditorActionListener((v, actionId, event) -> {
                submitSearch();
                return true;
            });

            // 点击搜索按钮
            binding.fSearchBtnSubmit.setOnClickListener(v -> submitSearch());

            // 搜索词已提交，清除焦点，设置搜索词，为recyclerView设置搜索结果adapter，并显示recyclerView
            mViewModel.submitKeyword.observe(lifecycleOwner, submitKeyword -> {
                if (StringUtil.isNonBlank(submitKeyword)) {
                    if (binding.fFragmentInput.hasFocus()) {
                        binding.fFragmentInput.clearFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputMethodManager != null) {
                            inputMethodManager.hideSoftInputFromWindow(binding.fFragmentInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                    searchInput.setText(submitKeyword);
                    showSearchResultView(submitKeyword);
                }
            });

            // 搜索框有焦点，正在输入关键词，设置搜索结果adapter，显示recyclerview
            // 否则显示搜索历史视图
            mViewModel.getInputtingKeyword().observe(lifecycleOwner, inputtingKeyword -> {
                if (!StringUtil.isNonBlank(inputtingKeyword)) {
                    showSearchHistoryView();
                    return;
                }
                if (binding.fFragmentInput.hasFocus()) {
                    showSearchSuggestionView(inputtingKeyword);
                }
            });
        }
    }

    // 搜索历史
    private void setupSearchHistory() {
        mViewModel.searchHistoryList.observe(lifecycleOwner, histories -> {
            binding.fSearchChipsHistories.removeAllViews();
            histories.forEach(history -> {
                Chip chip = new Chip(requireContext());
                chip.setText(history.word);
                chip.setCloseIconVisible(true);
                chip.setOnClickListener(v -> mViewModel.search(history.word));
                chip.setOnCloseIconClickListener(v -> mViewModel.deleteHistory(history)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> binding.fSearchChipsHistories.removeView(chip))
                        .to(RxUtil.autoDispose(lifecycleOwner))
                        .subscribe()
                );
                binding.fSearchChipsHistories.addView(chip);
            });
        });
        binding.fSearchBtnClear.setOnClickListener(v -> mViewModel.clearHistories()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> binding.fSearchChipsHistories.removeAllViews())
                .to(RxUtil.autoDispose(lifecycleOwner))
                .subscribe()
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        NavigationUI.setupWithNavController(binding.fSearchToolbar, navController);
        binding.fSearchToolbar.setNavigationOnClickListener(v -> handleBack());
        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        lifecycleOwner = getViewLifecycleOwner();

        // 输入搜索词
        // 提交关键词：点击按钮搜索/点击软键盘搜索
        // 点击搜索历史/点击搜索建议：设置搜索框关键词、提交关键词

        setupSearchBar();
        setupSearchHistory();

        // 搜索建议
        searchSuggestionListAdapter = new SearchSuggestionListAdapter(mViewModel::search);
        mViewModel.searchSuggestions.observe(
                lifecycleOwner,
                suggestions -> searchSuggestionListAdapter.submitList(suggestions)
        );

        // 搜索结果
        mViewModel.searchResults.observe(
                lifecycleOwner,
                hanziWordModelPagingData -> searchResultPagingAdapter.submitData(
                        lifecycleOwner.getLifecycle(),
                        hanziWordModelPagingData
                )
        );
//        showHistoryView();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}