package com.github.lilinsong3.xiaobaici.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.github.lilinsong3.xiaobaici.data.local.dao.BrowsingHistoryDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.FavoriteDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.FavoriteHanziWordDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.HanziWordDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.SearchHistoryDao;
import com.github.lilinsong3.xiaobaici.data.local.entities.BrowsingHistory;
import com.github.lilinsong3.xiaobaici.data.local.entities.Favorite;
import com.github.lilinsong3.xiaobaici.data.local.entities.FavoriteHanziWordCrossDef;
import com.github.lilinsong3.xiaobaici.data.local.entities.HanziWord;
import com.github.lilinsong3.xiaobaici.data.local.entities.SearchHistory;

// initial dev version
//@Database(entities = {HanziWords.class, Favorite.class, SearchHistory.class}, version = 1)
//public abstract class AppDatabase extends RoomDatabase {
////    private static final String DATABASE_NAME = "app";
////    private static volatile AppDatabase INSTANCE;
//    public abstract HanziWordDao hanziWordDao();
//
//    public abstract SearchHistoryDao searchHistoryDao();
//
////    public static AppDatabase getInstance(Context context) {
////        if (INSTANCE == null) {
////            synchronized (AppDatabase.class) {
////                if (INSTANCE == null) {
////                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
////                }
////            }
////        }
////        return INSTANCE;
////    }
//}

//@RewriteQueriesToDropUnusedColumns
@Database(
        entities = {
                HanziWord.class,
                Favorite.class,
                FavoriteHanziWordCrossDef.class,
                SearchHistory.class,
                BrowsingHistory.class
        },
        version = 1
//        autoMigrations = {
//                @AutoMigration(
//                        from = 1,
//                        to = 2,
//                        spec = AppDatabase.V1ToV2AutoMigrationSpec.class
//                ),
//                @AutoMigration(
//                        from = 2,
//                        to = 3,
//                        spec = AppDatabase.V2ToV3AutoMigrationSpec.class
//                )
//        }
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract HanziWordDao hanziWordDao();

    public abstract SearchHistoryDao searchHistoryDao();

    public abstract FavoriteDao favoriteDao();

    public abstract FavoriteHanziWordDao favoriteHanziWordDao();

    public abstract BrowsingHistoryDao browsingHistoryDao();
//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("DELETE FROM SearchHistory WHERE id NOT IN (SELECT MIN(id) FROM SearchHistory GROUP BY word)");
//            database.execSQL("CREATE UNIQUE INDEX index_SearchHistory_word ON SearchHistory (word)");
//        }
//    };

//    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE `HanziWord` ADD COLUMN `pinyin` TEXT DEFAULT NULL");
//            database.execSQL("ALTER TABLE `HanziWord` ADD COLUMN `abbrPinyin` TEXT DEFAULT NULL");
//        }
//    };

//    public static final Migration MIGRATION_3_4 = new Migration(2, 3) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("INSERT INTO HanziWord(HanziWord) VALUES('rebuild');");
//        }
//    };
    // for dev, job is done
//    @DeleteTable(tableName = "Favorite")
//    @RenameTable(fromTableName = "hanzi_words", toTableName = "HanziWord")
//    public static class V1ToV2AutoMigrationSpec implements AutoMigrationSpec {}
//
//    @RenameTable(fromTableName = "MyFavorite", toTableName = "Favorite")
//    public static class V2ToV3AutoMigrationSpec implements AutoMigrationSpec {}
}
