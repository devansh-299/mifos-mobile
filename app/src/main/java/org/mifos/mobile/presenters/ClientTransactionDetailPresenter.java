package org.mifos.mobile.presenters;

import android.content.Context;

import org.mifos.mobile.R;
import org.mifos.mobile.api.DataManager;
import org.mifos.mobile.injection.ApplicationContext;
import org.mifos.mobile.models.Transaction;
import org.mifos.mobile.presenters.base.BasePresenter;
import org.mifos.mobile.ui.views.ClientTransactionDetailView;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class ClientTransactionDetailPresenter extends
        BasePresenter<ClientTransactionDetailView> {

    private final DataManager dataManager;
    private CompositeDisposable compositeDisposables;

    /**
     * Initialises the ClientTransactionDetailsPresenter by automatically injecting
     * an instance of {@link DataManager} and {@link Context}.
     * @param dataManager DataManager class that provides access to the data
     *                    via the API.
     * @param context     Context of the view attached to the presenter. In this case
     *                    it is that of an {@link androidx.appcompat.app.AppCompatActivity}
     */
    @Inject
    public ClientTransactionDetailPresenter(DataManager dataManager,
                                            @ApplicationContext Context context) {
        super(context);
        this.dataManager = dataManager;
        compositeDisposables = new CompositeDisposable();
    }

    @Override
    public void attachView(ClientTransactionDetailView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposables.clear();
    }

    public void loadTransactionDetails(long transactionId) {
        checkViewAttached();
        getMvpView().showProgress();
        compositeDisposables.add(dataManager.getClientTransaction(transactionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Transaction>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        getMvpView().hideProgress();
                        getMvpView().showErrorFetchingSavingAccountsDetail(
                                context.getString(R.string.error_transaction_details_loading));
                    }

                    @Override
                    public void onNext(Transaction transaction) {
                        getMvpView().hideProgress();
                        getMvpView().showTransactionDetails(transaction);
                    }
                })
        );
    }

}
