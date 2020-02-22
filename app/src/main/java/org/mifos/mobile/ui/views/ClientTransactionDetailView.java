package org.mifos.mobile.ui.views;

import org.mifos.mobile.models.Transaction;
import org.mifos.mobile.ui.views.base.MVPView;

public interface ClientTransactionDetailView extends MVPView {

    void showTransactionDetails(Transaction transaction);

    void showErrorFetchingSavingAccountsDetail(String message);
}
