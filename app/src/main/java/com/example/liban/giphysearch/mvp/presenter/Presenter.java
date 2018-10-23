package com.example.liban.giphysearch.mvp.presenter;

import android.content.Context;
import android.util.Log;

import com.example.liban.giphysearch.Api;
import com.example.liban.giphysearch.mvp.model.Constants;
import com.example.liban.giphysearch.mvp.model.ListData;
import com.example.liban.giphysearch.mvp.view.MainView;
import com.example.liban.giphysearch.networking.NetworkClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liban on 15.08.2018.
 */

public class Presenter {
    private MainView mView;
    private Context mContext;
    private Disposable disposable;


    public Presenter(MainView mView, Context context) {
        this.mView = mView;
        mContext = context;
        mView.showProgress(true);
        requestTrending(0);
    }


    public void requestTrending(int offset) {
        Log.e("OFFSET", String.valueOf(offset));
        getTrendingsObservable(offset).subscribeWith(getTrendingsObserver(Constants.TRENDINGS));
    }


    public void requestSearch(String query, int offset) {
        getSearchObservable(query, offset).subscribeWith(getSearchObserver(Constants.SEARCH));
    }

    public void refresh() {
        mView.onRefresh();
    }


    private io.reactivex.Observable<ListData> getTrendingsObservable(int offset) {
        return NetworkClient.getRetrofit()
                .create(Api.class)
                .getTrending(Constants.API_KEY, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private io.reactivex.Observable<ListData> getSearchObservable(String query, int offset) {
        return NetworkClient.getRetrofit()
                .create(Api.class)
                .search(query, Constants.API_KEY, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<ListData> getSearchObserver(int KEY) {

        return new DisposableObserver<ListData>() {
            @Override
            public void onNext(ListData listData) {
                mView.showProgress(false);
                mView.onRequestSearch(listData);
                dispose();
            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e.getMessage());
                mView.showProgress(false);
            }

            @Override
            public void onComplete() {

            }
        };

    }

    private DisposableObserver<ListData> getTrendingsObserver(int KEY) {

        return new DisposableObserver<ListData>() {
            @Override
            public void onNext(ListData listData) {
                mView.showProgress(false);
                mView.onRequestTrending(listData);
                dispose();
            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e.getMessage());
                mView.showProgress(false);
            }

            @Override
            public void onComplete() {

            }
        };

    }


}
