package org.learning.rxjava;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.InterruptedIOException;
import java.io.StringReader;
import java.util.Arrays;

/**
 * Created by iUser on 11/2/16.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        //a();
        //b();
        //c();
        //d();
        //e();
        //f();
        //g();
        //h();
        //i();

        Observable.create(new Observable.OnSubscribe<Integer>(){



            public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("OnSubscribe call, " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                subscriber.onStart();
                subscriber.onNext(1);
            }
        }).doOnSubscribe(new Action0() {
            public void call() {
                System.out.println("doOnSubscribe call, " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());

            }
        }).subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onStart() {
                System.out.println("Subscriber onStart, " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
            }

            public void onCompleted() {

            }

            public void onError(Throwable e) {

            }

            public void onNext(Integer integer) {
                System.out.println("Subscriber onNext, " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
            }
        });

        Thread.sleep(5000);
    }

    private static void i() {
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>(){

            public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("OnSubscribe call, " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                subscriber.onNext(1);
            }
        });

//        Observable<Integer> observable = Observable.just(1);

        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
        .map(new Func1<Integer, String>() {

            public String call(Integer s) {
                System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + ", " + s);
                return s.toString();
            }
        }).observeOn(Schedulers.io())
        .map(new Func1<String, Integer>() {
            public Integer call(String s) {
                System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + ", " + s);
                return Integer.valueOf(s);
            }
        }).observeOn(Schedulers.computation())
        .map(new Func1<Integer, String>() {
            public String call(Integer s) {
                System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + ", " + s);
                return s.toString();
            }
        }).observeOn(Schedulers.io())
        .subscribe(new Subscriber<String>() {
            public void onCompleted() {

            }

            public void onError(Throwable e) {

            }

            public void onNext(String s) {
                System.out.println("onNext, " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + ", " + s);
            }
        });
    }

    private static void h() {
        Observable
                .just("a", "b", "c")
                .lift(new Observable.Operator<Character, String>(){


                    public Subscriber<? super String> call(final Subscriber<? super Character> subscriber) {
                        return new Subscriber<String>() {
                            public void onCompleted() {

                            }

                            public void onError(Throwable e) {

                            }

                            public void onNext(String s) {
                                Character character = s.charAt(0);
                                subscriber.onNext(character);
                            }
                        };
                    }
                })
                .lift(new Observable.Operator<Integer, Character>(){

                    public Subscriber<? super Character> call(final Subscriber<? super Integer> subscriber) {
                        return new Subscriber<Character>() {
                            public void onCompleted() {

                            }

                            public void onError(Throwable e) {

                            }

                            public void onNext(Character character) {
                                Integer integer = character.charValue() + 0;
                                subscriber.onNext(integer);
                            }
                        };
                    }
                })
                .lift(new Observable.Operator<String, Integer>(){

                    public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                        return new Subscriber<Integer>() {
                            public void onCompleted() {

                            }

                            public void onError(Throwable e) {

                            }

                            public void onNext(Integer integer) {
                                String string  = String.valueOf(integer);
                                subscriber.onNext(string);
                            }
                        };
                    }
                }).subscribe(new Subscriber<String>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {

                    }

                    public void onNext(String s) {
                        System.out.println(s);
                    }
        });

        Observable.Transformer<String, String> transformer = new Observable.Transformer<String, String>() {
            public Observable<String> call(Observable<String> observable) {
                return observable
                        .lift(new Observable.Operator<Character, String>(){

                            public Subscriber<? super String> call(final Subscriber<? super Character> subscriber) {
                                return new Subscriber<String>() {
                                    public void onCompleted() {

                                    }

                                    public void onError(Throwable e) {

                                    }

                                    public void onNext(String s) {
                                        Character character = s.charAt(0);
                                        subscriber.onNext(character);
                                    }
                                };
                            }
                        })
                        .lift(new Observable.Operator<Integer, Character>(){

                            public Subscriber<? super Character> call(final Subscriber<? super Integer> subscriber) {
                                return new Subscriber<Character>() {
                                    public void onCompleted() {

                                    }

                                    public void onError(Throwable e) {

                                    }

                                    public void onNext(Character character) {
                                        Integer integer = character.charValue() + 0;
                                        subscriber.onNext(integer);
                                    }
                                };
                            }
                        })
                        .lift(new Observable.Operator<String, Integer>(){

                            public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                                return new Subscriber<Integer>() {
                                    public void onCompleted() {

                                    }

                                    public void onError(Throwable e) {

                                    }

                                    public void onNext(Integer integer) {
                                        String string  = String.valueOf(integer);
                                        subscriber.onNext(string);
                                    }
                                };
                            }
                        });
            }
        };

        Observable
                .just("a", "b", "c")
                .compose(transformer)
                .subscribe(new Subscriber<String>() {
                    public void onCompleted() {
                    }

                    public void onError(Throwable e) {

                    }

                    public void onNext(String s) {
                        System.out.println(s);
                    }
                });
        Observable
                .just("d", "e", "f")
                .compose(transformer)
                .subscribe(new Subscriber<String>() {
                    public void onCompleted() {
                    }

                    public void onError(Throwable e) {

                    }

                    public void onNext(String s) {
                        System.out.println(s);
                    }
                });
    }

    private static void g() {
        Observable
                .just("1", "2", "3")
                .lift(new Observable.Operator<Integer, String>(){


                    public Subscriber<? super String> call(final Subscriber<? super Integer> subscriber) {
                        return new Subscriber<String>() {
                            public void onCompleted() {

                            }

                            public void onError(Throwable e) {

                            }

                            public void onNext(String s) {
                                System.out.println("string: " + s);
                                subscriber.onNext(Integer.parseInt(s));
                            }
                        };
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {

                    }

                    public void onNext(Integer s) {
                        System.out.println("Integer: " + s);
                    }
                });
    }

    private static void f() {
        Action1<String> onNextAction = new Action1<String>() {
            public void call(String s) {
                System.out.println("onNextAction.call(" + s + ")");
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>(){

            public void call(Throwable throwable) {
                System.err.println("onErrorAction.call(" + throwable.getMessage() + ")");
            }
        };
        Action0 onCompletedAction = new Action0() {

            public void call() {
                System.out.println("onCompletedAction.call()");
            }
        };

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>(){

            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("onNext");
                throw new IllegalArgumentException("IllegalArgumentException");
            }
        });

        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    private static void e() throws InterruptedException {
        final Observer<String> observerA = new Subscriber<String>() {
            @Override
            public void onStart() {
                System.out.println("observerA onStart at Thread: " + Thread.currentThread().getName());
            }

            public void onCompleted() {
                System.out.println("observerA onCompleted at Thread: " + Thread.currentThread().getName());
            }

            public void onError(Throwable e) {
                System.err.println("observerA onError at Thread: " + Thread.currentThread().getName() + " with error: " + e.getMessage());
            }

            public void onNext(String s) {
                System.out.println("observerA onNext at Thread: " + Thread.currentThread().getName() + " with parameter: " + s);
            }
        };
        final Observer<String> observerB = new Subscriber<String>() {
            @Override
            public void onStart() {
                System.out.println("observerB onStart at Thread: " + Thread.currentThread().getName());
            }

            public void onCompleted() {
                System.out.println("observerB onCompleted at Thread: " + Thread.currentThread().getName());
            }

            public void onError(Throwable e) {
                System.err.println("observerB onError at Thread: " + Thread.currentThread().getName() + " with error: " + e.getMessage());
            }

            public void onNext(String s) {
                System.out.println("observerB onNext at Thread: " + Thread.currentThread().getName() + " with parameter: " + s);
            }
        };
        final Observer<String> observerC = new Subscriber<String>() {
            @Override
            public void onStart() {
                System.out.println("observerC onStart at Thread: " + Thread.currentThread().getName());
            }

            public void onCompleted() {
                System.out.println("observerC onCompleted at Thread: " + Thread.currentThread().getName());
            }

            public void onError(Throwable e) {
                System.err.println("observerC onError at Thread: " + Thread.currentThread().getName() + " with error: " + e.getMessage());
            }

            public void onNext(String s) {
                System.out.println("observerC onNext at Thread: " + Thread.currentThread().getName() + " with parameter: " + s);
            }
        };

        Observable
                //.from(Arrays.asList("Hello, i'm observableA", "Nice to meet you, i'm observableA", "How do you do? i'm observableA"))
                .create(new Observable.OnSubscribe<String>() {

                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onStart();
                        subscriber.onNext("Hello, i'm observableA");
                        subscriber.onNext("Nice to meet you, i'm observableA");
                        subscriber.onNext("How do you do? i'm observableA");
                        subscriber.onCompleted();
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(observerB);

        Thread.sleep(1000);

        Observable
                .just("Hello, i'm observableB", "Nice to meet you, i'm observableB", "How do you do? i'm observableB")
                .observeOn(Schedulers.computation())
                .subscribe(observerB);

        Thread.sleep(1000);

        Observable
                .from(new String[]{"Hello, i'm observableC", "Nice to meet you, i'm observableC", "How do you do? i'm observableC"})
                .observeOn(Schedulers.newThread())
                .subscribe(observerB);
    }

    private static void d() {
        final Observer<String> observerA = new Subscriber<String>() {
            @Override
            public void onStart() {
                System.out.println("observerA onStart at Thread: " + Thread.currentThread().getName());
            }

            public void onCompleted() {
                System.out.println("observerA onCompleted at Thread: " + Thread.currentThread().getName());
            }

            public void onError(Throwable e) {
                System.err.println("observerA onError at Thread: " + Thread.currentThread().getName() + " with error: " + e.getMessage());
            }

            public void onNext(String s) {
                System.out.println("observerA onNext at Thread: " + Thread.currentThread().getName() + " with parameter: " + s);
            }
        };
        final Observer<String> observerB = new Subscriber<String>() {
            @Override
            public void onStart() {
                System.out.println("observerB onStart at Thread: " + Thread.currentThread().getName());
            }

            public void onCompleted() {
                System.out.println("observerB onCompleted at Thread: " + Thread.currentThread().getName());
            }

            public void onError(Throwable e) {
                System.err.println("observerB onError at Thread: " + Thread.currentThread().getName() + " with error: " + e.getMessage());
            }

            public void onNext(String s) {
                System.out.println("observerB onNext at Thread: " + Thread.currentThread().getName() + " with parameter: " + s);
            }
        };
        final Observer<String> observerC = new Subscriber<String>() {
            @Override
            public void onStart() {
                System.out.println("observerC onStart at Thread: " + Thread.currentThread().getName());
            }

            public void onCompleted() {
                System.out.println("observerC onCompleted at Thread: " + Thread.currentThread().getName());
            }

            public void onError(Throwable e) {
                System.err.println("observerC onError at Thread: " + Thread.currentThread().getName() + " with error: " + e.getMessage());
            }

            public void onNext(String s) {
                System.out.println("observerC onNext at Thread: " + Thread.currentThread().getName() + " with parameter: " + s);
            }
        };

        Thread threadA = new Thread(new Runnable() {
            public void run() {
                Observable<String> observableA = Observable
//                        .from(Arrays.asList("Hello, i'm observableA", "Nice to meet you, i'm observableA", "How do you do? i'm observableA"))
                        .create(new Observable.OnSubscribe<String>() {

                            public void call(Subscriber<? super String> subscriber) {
                                subscriber.onStart();
                                subscriber.onNext("Hello, i'm observableA");
                                subscriber.onNext("Nice to meet you, i'm observableA");
                                subscriber.onNext("How do you do? i'm observableA");
                                subscriber.onCompleted();
                            }
                        });
                observableA
                        .observeOn(Schedulers.newThread())
                        .subscribe(observerA);


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadA.setName("threadA");
        threadA.start();

        Thread threadB = new Thread(new Runnable() {
            public void run() {
                Observable<String> observableB = Observable
                        .just("Hello, i'm observableB", "Nice to meet you, i'm observableB", "How do you do? i'm observableB");
                observableB
                        .observeOn(Schedulers.newThread())
                        .subscribe(observerA);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadB.setName("threadB");
        threadB.start();

        Thread threadC = new Thread(new Runnable() {
            public void run() {
                Observable<String> observableC = Observable
                        .from(new String[]{"Hello, i'm observableC", "Nice to meet you, i'm observableC", "How do you do? i'm observableC"});
                observableC
                        .observeOn(Schedulers.newThread())
                        .subscribe(observerA);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadC.setName("threadC");
        threadC.start();
    }

    private static void c() {
        Observable
                .just("asdf", "qwer", "1234")
                .doOnSubscribe(new Action0() {
                    public void call() {
                        System.out.println("Action0 call doOnSubscribe");
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onStart() {
                        System.out.println("onStart");
                    }

                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    public void onError(Throwable e) {
                        System.err.println("onError: " + e.getMessage());
                    }

                    public void onNext(String s) {

                        System.out.println("onNext: " + s);
                    }
                });
    }

    private static void b() {
        Observer<String> observer = new Observer<String>() {

            public void onCompleted() {
                System.out.println("onCompleted");
            }

            public void onError(Throwable e) {
                System.out.println("onError");
            }

            public void onNext(String s) {
                System.out.println("onNext");
            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            public void onError(Throwable e) {
                System.out.println("onError");
            }

            public void onNext(String s) {
                System.out.println("onNext");
            }
        };
    }

    private static void a() {
        File projectDirectory = new File("/Users/iUser/IntelliJIDEAProjects/Learning/");

        Observable
                .from(projectDirectory.listFiles())
                .filter(new Func1<File, Boolean>() {
                    public Boolean call(File file) {
                        if (file.isDirectory()) {
                            System.out.println(file.getName() + ":");
                            return true;
                        }
                        return false;
                    }
                })
                .flatMap(new Func1<File, Observable<File>>() {
                    public Observable<File> call(File file) {
                        return Observable.from(file.listFiles());
                    }
                })
                .filter(new Func1<File, Boolean>() {

                    public Boolean call(File file) {
                        return file.getName().endsWith(".jar");
                    }
                })
                .map(new Func1<File, String>() {

                    public String call(File file) {
                        return file.getName();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    public void onError(Throwable e) {
                        System.err.println("onError: " + e.getMessage());
                    }

                    public void onNext(String s) {
                        System.out.println("\t" + s);
                    }
                });
    }
}
