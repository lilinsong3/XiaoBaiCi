package com.github.lilinsong3.xiaobaici.data.model;

public class SearchSuggestionModel {
    public final Long id;
    public final String suggestion;

    public SearchSuggestionModel(Long id, String suggestion) {
        this.id = id;
        this.suggestion = suggestion;
    }

    public SearchSuggestionModel from(WordSubjectModel model) {
        return new SearchSuggestionModel(model.id, model.subject);
    }
}
