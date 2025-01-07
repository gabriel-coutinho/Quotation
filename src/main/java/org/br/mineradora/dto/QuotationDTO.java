package org.br.mineradora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Jacksonized
@Data
@Builder
@AllArgsConstructor
public class QuotationDTO {
    private LocalDateTime date;
    private BigDecimal currencyPrice;
}
