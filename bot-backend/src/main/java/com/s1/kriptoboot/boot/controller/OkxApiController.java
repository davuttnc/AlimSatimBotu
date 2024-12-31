package com.s1.kriptoboot.boot.controller;

import com.s1.kriptoboot.boot.model.SpotBalance;
import com.s1.kriptoboot.boot.model.TotalBalance;
import com.s1.kriptoboot.boot.service.OkxApiService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/okx")
public class OkxApiController {

    private final OkxApiService okxApiService;

    public OkxApiController(OkxApiService okxApiService) {
        this.okxApiService = okxApiService;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {
        try {
            Object[] balances = (Object[]) okxApiService.getBalance();
            if (balances != null) {
                TotalBalance totalBalance = (TotalBalance) balances[0];
                List<SpotBalance> spotBalances = (List<SpotBalance>) balances[1];

                // Hem toplam bakiyeyi hem de spot bakiyeleri döndürüyoruz
                return ResponseEntity.ok(new Object[] { totalBalance, spotBalances });
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching balance: " + e.getMessage());
        }
    }
}
