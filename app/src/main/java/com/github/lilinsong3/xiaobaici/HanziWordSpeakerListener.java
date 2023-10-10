package com.github.lilinsong3.xiaobaici;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.Locale;

public class HanziWordSpeakerListener implements DefaultLifecycleObserver {

    @NonNull
    private Boolean initialized = false;
    @NonNull
    private Boolean chineseAvailable = false;
    private TextToSpeech tts;
    private final Context context;

    @NonNull
    private final OnSpeakAction onSpeakStartAction;
    @NonNull
    private final OnSpeakAction onSpeakDoneAction;

    public HanziWordSpeakerListener(
            @NonNull Context context,
            @NonNull OnSpeakAction onSpeakStartAction,
            @NonNull OnSpeakAction onSpeakDoneAction
    ) {
        this.context = context;
        this.onSpeakStartAction = onSpeakStartAction;
        this.onSpeakDoneAction = onSpeakDoneAction;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        tts = new TextToSpeech(context, status -> {
            initialized = status == TextToSpeech.SUCCESS;
            int checkLangAvailable = tts.isLanguageAvailable(Locale.CHINESE);
            chineseAvailable = checkLangAvailable != TextToSpeech.LANG_MISSING_DATA && checkLangAvailable != TextToSpeech.LANG_NOT_SUPPORTED;
            if (initialized && chineseAvailable) {
                tts.setLanguage(Locale.CHINESE);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        onSpeakStartAction.act(utteranceId);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        onSpeakDoneAction.act(utteranceId);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Toast.makeText(context, context.getString(R.string.long_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        tts.shutdown();
    }

    public void speak(@NonNull String hanziWord, @NonNull Long id) {
        if (checkAvailable()) {
            tts.speak(hanziWord, TextToSpeech.QUEUE_ADD, null, String.valueOf(id));
        }
    }

    public void stop() {
        if (checkAvailable()) {
            tts.stop();
        }
    }

    public boolean isSpeaking() {
        return checkAvailable() && tts.isSpeaking();
    }

    public boolean checkAvailable() {
        if (initialized && chineseAvailable) {
            return true;
        }
        Toast.makeText(context, context.getString(R.string.long_tts_invalid), Toast.LENGTH_SHORT).show();
        return false;
    }

    @FunctionalInterface
    public interface OnSpeakAction {
        void act(String id);
    }
}
