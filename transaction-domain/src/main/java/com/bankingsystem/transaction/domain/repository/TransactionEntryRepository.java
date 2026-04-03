package com.bankingsystem.transaction.domain.repository;

import com.bankingsystem.transaction.domain.model.TransactionEntry;
import java.util.List;

public interface TransactionEntryRepository {

    List<TransactionEntry> saveAll(List<TransactionEntry> transactionEntries);
}
