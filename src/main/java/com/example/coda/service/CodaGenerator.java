package com.example.coda.service;

import com.example.coda.model.BankTransaction;
import com.example.coda.model.TransactionType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CodaGenerator
{
   private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

   public String generate(String bankName, String accountNumber, String currency, LocalDate statementDate,
         BigDecimal openingBalance, List<BankTransaction> txs)
   {
      StringBuilder sb = new StringBuilder();
      int lineNo = 1;

      sb.append(record("000", lineNo++)).append(padRight(bankName, 12)).append(padRight(accountNumber, 18)).append(
            padRight(currency, 3)).append("\\n");

      sb.append(record("100", lineNo++)).append(statementDate.format(YYYYMMDD)).append(
            padLeft(amountToCents(openingBalance), 15, '0')).append("\\n");

      BigDecimal totalCredits = BigDecimal.ZERO;
      BigDecimal totalDebits = BigDecimal.ZERO;

      for (BankTransaction tx : txs)
      {
         boolean credit = tx.getType() == TransactionType.CREDIT;
         if (credit)
         {
            totalCredits = totalCredits.add(tx.getAmount());
         }
         else
         {
            totalDebits = totalDebits.add(tx.getAmount());
         }
      }

      BigDecimal closingBalance = openingBalance.add(totalCredits).subtract(totalDebits);
      sb.append(record("800", lineNo++)).append("TOTALS\\n");
      sb.append(record("900", lineNo++)).append("END\\n");
      return sb.toString();
   }

   private String record(String code, int lineNo)
   {
      return code + String.format("%06d", lineNo);
   }

   private String padRight(String s, int n)
   {
      return String.format("%-" + n + "s", s);
   }

   private String padLeft(String s, int n, char c)
   {
      return String.format("%" + n + "s", s).replace(' ', c);
   }

   private String amountToCents(BigDecimal amt)
   {
      return amt.setScale(2, RoundingMode.HALF_UP).movePointRight(2).toBigInteger().toString();
   }
}
