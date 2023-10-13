package com.github.lilinsong3.xiaobaici.ui.hanziword;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.lilinsong3.xiaobaici.HanziWordSpeakerListener;
import com.github.lilinsong3.xiaobaici.MainActivityViewModel;
import com.github.lilinsong3.xiaobaici.NavGraphDirections;
import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.databinding.FragmentHanziWordBinding;
import com.github.lilinsong3.xiaobaici.util.RxUtil;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import static com.github.lilinsong3.xiaobaici.util.ViewUtil.showTextIfNonNull;

@AndroidEntryPoint
public class HanziWordFragment extends Fragment {
    
    private static final String TAG = "HanziWordFragment";

    public static final String HANZI_WORD_BUNDLE_KEY = "hanziWordId";

    private FragmentHanziWordBinding binding;

    private NavController navController;

    private LifecycleOwner lifecycleOwner;

    private HanziWordViewModel hanziWordViewModel;

    private AnimationDrawable volumeAnim;
    private HanziWordSpeakerListener hanziWordSpeakerListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hanziWordSpeakerListener = new HanziWordSpeakerListener(requireContext(), id -> {
            // tts开始朗读
            if (volumeAnim != null) {
                volumeAnim.start();
            }
        }, id -> {
            // tts结束朗读
            if (volumeAnim != null) {
                volumeAnim.stop();
                volumeAnim.selectDrawable(0);
            }
        });
        getLifecycle().addObserver(hanziWordSpeakerListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHanziWordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);
        hanziWordViewModel = new ViewModelProvider(this).get(HanziWordViewModel.class);
        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        lifecycleOwner = getViewLifecycleOwner();
        lifecycleOwner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event.equals(Lifecycle.Event.ON_RESUME)) {
                // 记录浏览历史
                hanziWordViewModel.addBrowsingHistory()
                        .observeOn(AndroidSchedulers.mainThread())
                        .to(RxUtil.autoDispose(lifecycleOwner))
                        .subscribe();
            }
        });

        hanziWordViewModel.getLoading().observe(lifecycleOwner, mainActivityViewModel::setGlobalLoading);

        // 喇叭播放动画
        // TextView_drawableEnd
        final Drawable volumeDrawable = binding.hwTextPinyin.getCompoundDrawablesRelative()[2];
        if (volumeDrawable instanceof AnimationDrawable) {
            volumeAnim = (AnimationDrawable) volumeDrawable;
        }

        hanziWordViewModel.hanziWordModel.observe(lifecycleOwner, model -> {
            binding.hwTextSubject.setText(model.subject);
            binding.hwTextPinyin.setText(model.pinyin);
            showTextIfNonNull(binding.hwTextMeaning, model.meaning);
            showTextIfNonNull(binding.hwTextUsage, model.usage);
            showTextIfNonNull(binding.hwTextExample, model.example);

            // 喇叭按钮
            binding.hwTextPinyin.setOnClickListener(v -> {
                if (hanziWordSpeakerListener.isSpeaking()) {
                    hanziWordSpeakerListener.stop();
                    return;
                }
                hanziWordSpeakerListener.speak(model.subject, model.id);
            });

            // 复制按钮
            binding.hwBtnCopy.setOnClickListener(v -> {
                ClipboardManager clipboardManager = (ClipboardManager)requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.short_words), model.subject));
                Toast.makeText(requireContext(), getString(R.string.long_copy_words_notice), Toast.LENGTH_SHORT).show();
            });
            hanziWordViewModel.setLoading(false);
        });

        hanziWordViewModel.getThisFavorite().observe(lifecycleOwner, isFavorite -> binding.hwCbFavorite.setChecked(isFavorite));


        binding.hwCbFavorite.setOnClickListener(v -> hanziWordViewModel.loadFavoriteIds()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(argIds -> {
                    if (argIds.length == 0) {
                        // 未收藏 => 直接收藏到默认收藏夹
                        hanziWordViewModel.addThisToDefault()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> hanziWordViewModel.setThisFavorite(true))
                                .doOnError(t -> Toast.makeText(requireContext(), R.string.long_operation_failed, Toast.LENGTH_SHORT).show())
                                .to(RxUtil.autoDispose(lifecycleOwner))
                                .subscribe();
                    } else if (argIds.length == 1) {
                        // 收藏 => 直接取消收藏
                        hanziWordViewModel.cancelFavorite()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> hanziWordViewModel.setThisFavorite(false))
                                .doOnError(t -> Toast.makeText(requireContext(), R.string.long_operation_failed, Toast.LENGTH_SHORT).show())
                                .to(RxUtil.autoDispose(lifecycleOwner))
                                .subscribe();
                    } else {
                        navController.navigate(NavGraphDirections.actionGlobalFavoriteSelection().setHanziWordId(hanziWordViewModel.argHanziWordId));
                    }
                })
                .doOnError(throwable -> Toast.makeText(requireContext(), R.string.long_operation_failed, Toast.LENGTH_SHORT).show())
                .to(RxUtil.autoDispose(lifecycleOwner))
                .subscribe()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
