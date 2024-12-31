package com.s1.kriptoboot.boot.controller;

import com.s1.kriptoboot.boot.model.CryptoInfo;
import com.s1.kriptoboot.boot.service.CryptoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/crypto")
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/info/{cryptoSymbol}")
    public CryptoInfo getCryptoInfo(@PathVariable String cryptoSymbol) {
        CryptoInfo cryptoInfo = cryptoService.getCryptoInfo(cryptoSymbol);
        if (cryptoInfo != null) {
            return cryptoInfo;
        } else {
            throw new RuntimeException("Crypto information not found for symbol: " + cryptoSymbol);
        }
    }
}
