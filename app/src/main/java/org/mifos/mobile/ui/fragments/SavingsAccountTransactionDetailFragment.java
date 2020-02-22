package org.mifos.mobile.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.therajanmaurya.sweeterror.SweetUIErrorHandler;

import org.mifos.mobile.R;
import org.mifos.mobile.models.accounts.savings.PaymentDetailData;
import org.mifos.mobile.models.accounts.savings.Transactions;
import org.mifos.mobile.presenters.SavingAccountsTransactionDetailsPresenter;
import org.mifos.mobile.ui.activities.base.BaseActivity;
import org.mifos.mobile.ui.fragments.base.BaseFragment;
import org.mifos.mobile.ui.views.SavingsAccountTransactionDetailView;
import org.mifos.mobile.utils.Constants;
import org.mifos.mobile.utils.CurrencyUtil;
import org.mifos.mobile.utils.DateHelper;
import org.mifos.mobile.utils.Network;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SavingsAccountTransactionDetailFragment extends BaseFragment implements
        SavingsAccountTransactionDetailView {

    @BindView(R.id.ll_transaction)
    LinearLayout transactionLayout;

    @BindView(R.id.layout_error)
    View layoutError;

    @BindView(R.id.tv_transaction_id)
    TextView tvTransactionId;

    @BindView(R.id.tv_transaction_type)
    TextView tvTransactionType;

    @BindView(R.id.tv_transaction_date)
    TextView tvTransactionDate;

    @BindView(R.id.tv_transaction_currency)
    TextView tvTransactionCurrency;

    @BindView(R.id.tv_transaction_amount)
    TextView tvTransactionAmount;

    @BindView(R.id.tv_payment_type)
    TextView tvPaymentType;

    @BindView(R.id.tv_payment_acc_no)
    TextView tvPaymentAccountNumber;

    @BindView(R.id.tv_receipt_no)
    TextView tvReceiptNumber;

    @Inject
    SavingAccountsTransactionDetailsPresenter savingAccountsTransactionDetailsPresenter;

    private View rootView;
    private SweetUIErrorHandler sweetUIErrorHandler;

    private long accountId;
    private long transactionId;
    private Transactions transaction;


    public static SavingsAccountTransactionDetailFragment newInstance(
            long accountId, long transactionId) {
        SavingsAccountTransactionDetailFragment fragment =
                new SavingsAccountTransactionDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ACCOUNT_ID, accountId);
        args.putLong(Constants.TRANSACTION_ID, transactionId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accountId = getArguments().getLong(Constants.ACCOUNT_ID);
            transactionId = getArguments().getLong(Constants.TRANSACTION_ID);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        setToolbarTitle(getString(R.string.transaction_details));
        ButterKnife.bind(this, rootView);
        savingAccountsTransactionDetailsPresenter.attachView(this);
        sweetUIErrorHandler = new SweetUIErrorHandler(getContext(), rootView);

        if (savedInstanceState == null) {
            savingAccountsTransactionDetailsPresenter.loadTransactionDetails(accountId,
                    transactionId);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.SAVINGS_TRANSACTION, transaction);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            showTransactionDetails((Transactions) savedInstanceState.
                    getParcelable(Constants.SAVINGS_TRANSACTION));
        }
    }

    @Override
    public void showTransactionDetails(Transactions transaction) {
        this.transaction = transaction;
        String currencySymbol = transaction.getCurrency().getDisplaySymbol();
        PaymentDetailData paymentDetailData = transaction.getPaymentDetailData();

        tvTransactionId.setText(String.valueOf(transaction.getId()));
        tvTransactionType.setText(transaction.getTransactionType().getValue());
        tvTransactionDate.setText(DateHelper.getDateAsString(transaction.getDate()));
        tvTransactionCurrency.setText(transaction.getCurrency().getName());
        tvTransactionAmount.setText(getString(R.string.string_and_string,
                currencySymbol, CurrencyUtil.formatCurrency(getActivity(),
                        transaction.getAmount())));
        tvPaymentType.setText(paymentDetailData.getPaymentType().getName());
        tvPaymentAccountNumber.setText(paymentDetailData.getAccountNumber());
        tvReceiptNumber.setText(paymentDetailData.getReceiptNumber());

    }

    @Override
    public void showErrorFetchingSavingAccountsDetail(String message) {
        if (!Network.isConnected(getContext())) {
            sweetUIErrorHandler.showSweetNoInternetUI(transactionLayout, layoutError);
            Toast.makeText(getContext(), getString(R.string.internet_not_connected),
                    Toast.LENGTH_SHORT).show();
        } else {
            sweetUIErrorHandler.showSweetErrorUI(message, transactionLayout, layoutError);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_help_line_number)
    void dialHelpLineNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + getString(R.string.help_line_number)));
        startActivity(intent);
    }

    @OnClick(R.id.btn_try_again)
    void onRetry() {
        if (!Network.isConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.internet_not_connected),
                    Toast.LENGTH_SHORT).show();
        } else {
            sweetUIErrorHandler.hideSweetErrorLayoutUI(transactionLayout, layoutError);
            savingAccountsTransactionDetailsPresenter.loadTransactionDetails(accountId,
                    transactionId);
        }
    }

    @Override
    public void showProgress() {
        transactionLayout.setVisibility(View.GONE);
        showProgressBar();
    }

    @Override
    public void hideProgress() {
        transactionLayout.setVisibility(View.VISIBLE);
        hideProgressBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideProgressBar();
        savingAccountsTransactionDetailsPresenter.detachView();
    }

}
