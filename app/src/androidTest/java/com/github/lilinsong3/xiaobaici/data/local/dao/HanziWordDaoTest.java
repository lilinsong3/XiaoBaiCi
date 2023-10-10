package com.github.lilinsong3.xiaobaici.data.local.dao;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.github.lilinsong3.xiaobaici.data.local.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HanziWordDaoTest {

    private AppDatabase appDatabase;
    private HanziWordDao dao;

    CompositeDisposable disposables;

    @Before
    public void setUp() throws Exception {
        appDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class).build();
        dao = appDatabase.hanziWordDao();
        disposables = new CompositeDisposable();
    }

    @After
    public void tearDown() throws Exception {
        disposables.dispose();
        appDatabase.close();
    }

    @Test
    public void queryFavorites() {
//        boolean assertResult = false;
//        List<WordsSubjectModel> resultFavoriteHanziWordList = new ArrayList<>();
//        disposables.add(dao.queryFavoriteHanziWordList(20, 0).subscribe(resultFavoriteHanziWordList::addAll, throwable -> assertThat("query error", throwable == null)));
//        assertResult = resultFavoriteHanziWordList.isEmpty();
//        assertThat("favorites should be empty", assertResult);
    }
}