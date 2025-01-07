package org.br.mineradora.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Data
@AllArgsConstructor
public class USDBRL {
    private String code;
    private String codein;
    private String name;
    private String high;
    private String low;
    private String varBid;
    private String pctChange;
    private String bid;
    private String ask;
    private String timestamp;
    private String create_date;
}
