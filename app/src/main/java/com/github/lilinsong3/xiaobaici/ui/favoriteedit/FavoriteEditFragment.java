package com.github.lilinsong3.xiaobaici.ui.favoriteedit;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.lilinsong3.xiaobaici.MainActivityViewModel;
import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.VMVBFragment;
import com.github.lilinsong3.xiaobaici.databinding.FragmentFavoriteEditBinding;
import com.github.lilinsong3.xiaobaici.util.RxUtil;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

@AndroidEntryPoint
public class FavoriteEditFragment extends VMVBFragment<FavoriteEditViewModel, FragmentFavoriteEditBinding> {

    @Override
    protected void setupViewData() {

        MainActivityViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        if (viewModel.isCreateOperation()) {
            mainViewModel.setAppBarTitle(getString(R.string.short_create_favorite));
        } else {
            mainViewModel.setAppBarTitle(getString(R.string.short_edit_favorite));
        }

        // 加载编辑数据
        viewModel.loadModel()
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(model -> {
                    viewBinding.feInputName.setText(model.name);
                    viewBinding.feCbDefault.setChecked(model.isDefault);
                })
                .to(RxUtil.autoDispose(getViewLifecycleOwner()))
                .subscribe();

        // 焦点监听
        viewBinding.feInputName.setOnFocusChangeListener(((v, hasFocus) -> {
            if (viewBinding.feInputLayout.hasFocus()) {
                checkInput();
            }
        }));
        viewBinding.feInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 点击保存
        viewBinding.feBtnSave.setOnClickListener(v -> {
            Editable nameData = viewBinding.feInputName.getText();
            if (checkInput() && nameData != null) {
                viewModel.saveData(nameData.toString().trim(), viewBinding.feCbDefault.isChecked())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            Toast.makeText(requireContext(), R.string.long_operation_success, Toast.LENGTH_SHORT).show();
                            NavHostFragment.findNavController(this).popBackStack();
                        })
                        .doOnError(throwable -> Toast.makeText(
                                requireContext(),
                                R.string.long_operation_failed,
                                Toast.LENGTH_SHORT
                        ).show())
                        .to(RxUtil.autoDispose(getViewLifecycleOwner()))
                        .subscribe();
            }
        });
    }

    private boolean checkInput() {
        Editable nameData = viewBinding.feInputName.getText();

        if (nameData == null || nameData.toString().isEmpty()) {
            viewBinding.feInputLayout.setError(getString(R.string.long_not_empty));
        } else {
            viewBinding.feInputLayout.setError(null);
        }

        return viewBinding.feInputLayout.getError() == null;
    }

    @NonNull
    @Override
    protected Class<FavoriteEditViewModel> provideVMClass() {
        return FavoriteEditViewModel.class;
    }

    @Override
    public FragmentFavoriteEditBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentFavoriteEditBinding.inflate(inflater, container, false);
    }
}