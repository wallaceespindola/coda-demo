package com.example.coda.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CodaGeneratorTest
{
   @Test
   void testGenerate()
   {
      CodaGenerator gen = new CodaGenerator();
      String out = gen.generate("BELFIUS", "ACC123", "EUR", LocalDate.now(), new BigDecimal("1000.00"), List.of());
      assertTrue(out.contains("000000001"));
      assertTrue(out.contains("100000002"));
   }

   @Test
   void testGenerateWithBelgianIban()
   {
      CodaGenerator gen = new CodaGenerator();
      String bank = "BELFIUS";
      String belgianIban = "BE68 5390 0754 7034"; // Belgian IBAN with spaces
      LocalDate date = LocalDate.of(2025, 1, 15);

      String out = gen.generate(bank, belgianIban, "EUR", date, new BigDecimal("1000.00"), List.of());

      // Basic record markers should be present
      assertTrue(out.contains("000000001"), "Header record marker missing");
      assertTrue(out.contains("100000002"), "Opening balance record marker missing");

      // Should include the provided Belgian IBAN, bank name and currency
      assertTrue(out.contains(belgianIban), "Output should contain the Belgian IBAN");
      assertTrue(out.contains(bank), "Output should contain the bank name");
      assertTrue(out.contains("EUR"), "Output should contain the currency");

      // Date should be formatted as yyyyMMdd
      assertTrue(out.contains("20250115"), "Output should contain the formatted statement date (yyyyMMdd)");
   }
}
