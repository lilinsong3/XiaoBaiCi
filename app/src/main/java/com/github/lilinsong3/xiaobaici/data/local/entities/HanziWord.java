package com.github.lilinsong3.xiaobaici.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.FtsOptions;
import androidx.room.PrimaryKey;

@Fts4(tokenizer = FtsOptions.TOKENIZER_ICU, tokenizerArgs = "zh_CN")
@Entity
public class HanziWord {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    public Long id;
    public String subject;
    public String pinyin;
    public String pinyinAbbr;
    public String meaning;
    public String usage;
    public String example;
}
