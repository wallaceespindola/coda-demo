package com.example.coda.web;

import com.example.coda.model.CodaRequest;
import com.example.coda.service.CodaGenerator;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CodaController
{
   private final CodaGenerator generator = new CodaGenerator();

   @GetMapping(value = "/coda", produces = MediaType.TEXT_PLAIN_VALUE)
   public ResponseEntity<String> getCoda(
         @RequestParam(defaultValue = "BELFIUS") String bankName,
         @RequestParam(defaultValue = "BE68 5390 0754 7034") String account,
         @RequestParam(defaultValue = "EUR") String currency,
         @RequestParam(required = false) String date, @RequestParam(defaultValue = "1200.00") String opening)
   {
      LocalDate d = (date == null) ? LocalDate.now() : LocalDate.parse(date);
      String body = generator.generate(bankName, account, currency, d, new BigDecimal(opening), List.of());
      HttpHeaders h = new HttpHeaders();
      h.setContentType(MediaType.TEXT_PLAIN);
      h.setContentDisposition(ContentDisposition.inline().filename("statement.coda").build());
      return ResponseEntity.ok().headers(h).body(body);
   }

   @PostMapping(value = "/coda/json", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
         MediaType.TEXT_PLAIN_VALUE)
   public ResponseEntity<String> postCoda(@Valid @RequestBody CodaRequest req)
   {
      String body = generator.generate(req.getBankName(), req.getAccount(), req.getCurrency(), req.getDate(),
            req.getOpening(), List.of());
      HttpHeaders h = new HttpHeaders();
      h.setContentType(MediaType.TEXT_PLAIN);
      h.setContentDisposition(ContentDisposition.inline().filename("statement.coda").build());
      return ResponseEntity.ok().headers(h).body(body);
   }
}
