package com.github.lilinsong3.xiaobaici.util;

import android.content.res.AssetManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Range;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class StringUtil {
    private static final String TAG = "StringUtil";

    public static boolean isNonBlank(@Nullable String str) {
        return str != null && !str.isEmpty() && str.codePoints().anyMatch(codepoint -> codepoint != ' ' && codepoint != '\t' && !Character.isWhitespace(codepoint));
    }

    public static List<Range<Integer>> computeMatchableRanges(String sourceString, String subString) {
        List<Range<Integer>> result = new ArrayList<>();
        int startIndex = 0;
        int lower;
        do {
            lower = sourceString.indexOf(subString, startIndex);
            if (lower == -1) {
                break;
            }
            int upper = lower + subString.length();
            result.add(Range.create(lower, upper));
            startIndex = upper;
        } while (true);
        return result;
    }

    @Nullable
    public static SpannableString highlightString(@Nullable String targetString, String matchableString, @ColorInt int highlightColor) {
        if (targetString == null || targetString.isEmpty()) {
            return null;
        }
        List<Range<Integer>> matchableRanges =  computeMatchableRanges(targetString, matchableString);
        SpannableString highlightString = new SpannableString(targetString);
        for (Range<Integer> range : matchableRanges) {
            highlightString.setSpan(new ForegroundColorSpan(highlightColor), range.getLower(), range.getUpper(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return highlightString;
    }

    @NonNull
    public static String getStringFromAssetsFile(@NonNull AssetManager assetManager, @NonNull String filename) {
        List<String> lines = getStringLinesFromAssetsFile(assetManager, filename);
        StringBuilder fileStr = new StringBuilder();
        for (String line : lines) {
            fileStr.append(line);
        }
        return fileStr.toString();
//        try {
//            Scanner scanner = new Scanner(assetManager.open(filename)).useDelimiter("\\A");
//            return scanner.hasNext() ? scanner.next() : "";
//        } catch (IOException e) {
//            return "";
//        }
    }

    @NonNull
    public static List<String> getStringLinesFromAssetsFile(@NonNull AssetManager assetManager, @NonNull String filename) {
        List<String> lines = new ArrayList<>();
        try {
            InputStream assetInputStream = assetManager.open(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines;
        } catch (IOException e) {
            return lines;
        }
    }

}
