package org.br.mineradora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPriceDTO {
    @JsonProperty("USDBRL")
    private USDBRL USDBRL;
}
