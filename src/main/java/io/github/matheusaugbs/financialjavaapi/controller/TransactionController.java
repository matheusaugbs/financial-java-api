package io.github.matheusaugbs.financialjavaapi.controller;

import io.github.matheusaugbs.financialjavaapi.model.Transaction;
import io.github.matheusaugbs.financialjavaapi.service.TransactionService;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController

public class TransactionController {

    private static final Logger logger = Logger.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @GetMapping(path = "/transactions")
    public ResponseEntity<List<Transaction>> find() {
        if (transactionService.find().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        logger.info(transactionService.find());
        return ResponseEntity.ok(transactionService.find());
    }

    @DeleteMapping(path = "/transactions")
    public ResponseEntity<Boolean> delete() {
        try {
            transactionService.delete();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(path = "/transactions")
    @ResponseBody
    public ResponseEntity<Transaction> create(@RequestBody JSONObject transaction) {
        try {
            if (transactionService.isJSONValid(transaction.toString())) {
                Transaction transactionCreated = transactionService.create(transaction);
                var uri = ServletUriComponentsBuilder.fromCurrentRequest().path(transactionCreated.getNsu()).build().toUri();

                if (transactionService.isTransactionInFuture(transactionCreated)) {
                    logger.error("The transaction date is in the future.");
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
                } else {
                    transactionService.add(transactionCreated);
                    return ResponseEntity.created(uri).body(null);
                }
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("JSON fields are not parsable. " + e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }

    @PutMapping(path = "/transactions/{id}", produces = {"application/json"})
    public ResponseEntity<Transaction> create(@PathVariable("id") long id, @RequestBody JSONObject transaction) {
        try {
            if (transactionService.isJSONValid(transaction.toString())) {
                Transaction transactionToUpdate = transactionService.findById(id);
                if (transactionToUpdate == null) {
                    logger.error("The transaction not found.");
                    return ResponseEntity.notFound().build();
                } else {
                    Transaction transactionUpdated = transactionService.update(transactionToUpdate, transaction);
                    return ResponseEntity.ok(transactionUpdated);
                }
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("JSON fields are not parsable." + e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }
}
