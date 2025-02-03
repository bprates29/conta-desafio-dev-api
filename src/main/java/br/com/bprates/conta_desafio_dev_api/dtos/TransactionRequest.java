package br.com.bprates.conta_desafio_dev_api.dtos;

import java.math.BigDecimal;

public record TransactionRequest(BigDecimal amount) {
}
