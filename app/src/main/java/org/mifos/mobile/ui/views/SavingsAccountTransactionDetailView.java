package org.mifos.mobile.ui.views;

import org.mifos.mobile.models.accounts.savings.Transactions;
import org.mifos.mobile.ui.views.base.MVPView;


public interface SavingsAccountTransactionDetailView extends MVPView {

    void showTransactionDetails(Transactions transactions);

    void showErrorFetchingSavingAccountsDetail(String message);

}