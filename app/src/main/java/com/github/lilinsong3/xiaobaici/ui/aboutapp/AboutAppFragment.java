package com.github.lilinsong3.xiaobaici.ui.aboutapp;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.github.lilinsong3.xiaobaici.BuildConfig;
import com.github.lilinsong3.xiaobaici.MainActivityViewModel;
import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.VMVBFragment;
import com.github.lilinsong3.xiaobaici.databinding.FragmentAboutAppBinding;
import com.github.lilinsong3.xiaobaici.util.RxUtil;
import com.github.lilinsong3.xiaobaici.util.StringUtil;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AboutAppFragment extends VMVBFragment<AboutAppViewModel, FragmentAboutAppBinding> {

    private static final String TAG = "AboutAppFragment";

    @Override
    protected void setupViewData() {
        new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class)
                .getGlobalLoading()
                .observe(getViewLifecycleOwner(), viewModel::setLoading);
        viewBinding.aaTextVersionInfo.setText(getString(R.string.app_version_info, BuildConfig.VERSION_NAME));
        viewBinding.aaTextMd.setMovementMethod(ScrollingMovementMethod.getInstance());
        // 读取文件，获取行列表
        Single.fromSupplier(() -> StringUtil.getStringLinesFromAssetsFile(requireActivity().getAssets(), "ABOUTAPP.md"))
                // 按行发射处理
                .flattenAsFlowable(lines -> lines)
                // 解析每一行
                .map(Parser.builder().build()::parse)
                // 渲染每一行为一个段落
                .map(HtmlRenderer.builder().build()::render)
                // 每个段落后加一个换行标签
                .map(p -> p + "<br>")
                // 累加为一个字符串
                .reduce("", (seed, p) -> seed + p)
                // 转换
                .map(content -> Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(viewBinding.aaTextMd::setText)
                .doOnError(throwable -> Toast.makeText(requireContext(), R.string.long_error, Toast.LENGTH_SHORT))
                .to(RxUtil.autoDispose(getViewLifecycleOwner()))
                .subscribe();
    }

    @NonNull
    @Override
    protected Class<AboutAppViewModel> provideVMClass() {
        return AboutAppViewModel.class;
    }

    @Override
    public FragmentAboutAppBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentAboutAppBinding.inflate(inflater, container, false);
    }
}
