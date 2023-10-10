package com.github.lilinsong3.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxJavaTestCase {
    public static void testFlowableCase_1() {
        int emitNum = 1;
        Flowable<Integer> testObservable = Flowable.just(emitNum);
        Disposable disposable = testObservable.subscribe(integer -> System.out.println("> Result: onSuccess emitNum = " + integer));
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            emitNum = 2;
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            disposable.dispose();
            System.out.println("> Result: 1s later and disposed after reset emitNum to 2, emitNum = " + emitNum);
        } catch (InterruptedException e) {
            System.out.println("> Error: msg = " + e.getLocalizedMessage());
        }
    }

    public static void testFlowableCase_2() {
        final int size = 3;
        Single.zip(
                        Single.just(1),
                        Single.just(320350),
                        (origin, bound) -> ThreadLocalRandom.current().nextLong(origin, bound)
                ).repeat(size)
                .doOnNext(id -> System.out.println("id after repeat: " + id))
                .toList()
                // 查询
                .concatMap(longs -> {
                    System.out.println("into: " + longs);
                    if (longs.stream().allMatch(aLong -> aLong < 250000)) {
                        return Single.just(longs);
                    }
                    return Single.just(new ArrayList<Long>());
                })
                .doOnSuccess(list -> System.out.println("final: " + list))
                .subscribe();
    }

    // subscribe之后会抛出异常
    public static Single<List<Long>> exec(int size) {
        Maybe<List<Long>> listMaybe = Flowable.fromSupplier(() -> ThreadLocalRandom.current().nextInt(1, 320350))
                // 重复生成
                .repeat(size)
                // 收集
                .toList()
                .map(integerList -> integerList.stream().mapToLong(Integer::intValue).boxed().collect(Collectors.toList()))
                // 查询
                .concatMap(longs -> {
                    System.out.println("into: " + longs);
                    if (longs.stream().allMatch(aLong -> aLong > 320350)) {
                        return Single.just(longs);
                    }
                    return Single.just(new ArrayList<Long>());
                })
                // 是否满足数量
                .filter(ids -> {
                    System.out.println("ids: " + ids);
                    return ids.size() == size;
                });
        try {
            return listMaybe.switchIfEmpty(exec(size));
        } catch (StackOverflowError error) {
            return Single.error(new Throwable());
        }
                // 不满足数量，递归，继续
//                .isEmpty();
//                .switchIfEmpty(exec(size));
//                .switchIfEmpty(Single.defer(() -> {
//                    System.out.println("exec");
//                    try {
//                        return exec(size);
//                    } catch (StackOverflowError error) {
//                        return Single.just(Collections.emptyList());
//                    }
//                }));
    }

    public static void testConcatThreadCase_1() {
        System.out.println("===== test start =====");
        Disposable d = Observable.fromSupplier(() -> "test")
                .doOnSubscribe(disposable -> System.out.println("thread: " + Thread.currentThread().getName()))
                .concatMap(
                        item -> {
                    System.out.println("into item: " + item);
                    return Observable.just("concat " + item)
                            .subscribeOn((Schedulers.newThread()))
                            .doOnSubscribe(disposable -> System.out.println("concat thread: " + Thread.currentThread().getName()));
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    System.out.println("doOnError: " + throwable.getMessage());
                })
                .doOnNext(item -> System.out.println("doOnNext: " + item))
                .subscribe(item -> System.out.println("onSuccess: " + item));
        try {
            Thread.sleep(1000); // 等待1秒，让新线程执行完毕
            d.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testConcatThreadCase_2() {
        Observable.just("test")
                .doOnSubscribe(disposable -> System.out.println("thread: " + Thread.currentThread().getName()))
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    System.out.println("doOnError: " + throwable.getMessage());
                })
                .doOnNext(item -> System.out.println("doOnNext: " + item))
                .subscribeOn(Schedulers.newThread())
                .subscribe(item -> System.out.println("onSuccess: " + item));
        try {
            Thread.sleep(1000); // 等待1秒，让新线程执行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
