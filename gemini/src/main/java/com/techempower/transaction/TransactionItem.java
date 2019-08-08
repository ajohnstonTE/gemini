package com.techempower.transaction;

public interface TransactionItem
{
  void rollback();
  void commit();
}
