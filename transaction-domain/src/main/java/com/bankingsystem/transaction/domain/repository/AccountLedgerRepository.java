package com.bankingsystem.transaction.domain.repository;

import com.bankingsystem.transaction.domain.model.AccountLedger;
import java.util.List;

public interface AccountLedgerRepository {

    List<AccountLedger> saveAll(List<AccountLedger> accountLedgers);
}
