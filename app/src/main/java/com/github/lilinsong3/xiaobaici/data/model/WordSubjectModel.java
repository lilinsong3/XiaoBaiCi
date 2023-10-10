package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;

import com.github.lilinsong3.xiaobaici.data.local.entities.HanziWord;

public class WordSubjectModel {
    public final Long id;
    public final String subject;

    public WordSubjectModel(Long id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    public static WordSubjectModel from(HanziWord hanziWord) {
        return new WordSubjectModel(hanziWord.id, hanziWord.subject);
    }

    @NonNull
    @Override
    public String toString() {
        return "[id: " + id + ", subject: " + subject + "]";
    }
}
