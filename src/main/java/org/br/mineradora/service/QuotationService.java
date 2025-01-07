package org.br.mineradora.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.br.mineradora.client.CurrencyPriceClient;
import org.br.mineradora.dto.CurrencyPriceDTO;
import org.br.mineradora.dto.QuotationDTO;
import org.br.mineradora.entity.QuotationEntity;
import org.br.mineradora.message.KafkaEvents;
import org.br.mineradora.repository.QuotationRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class QuotationService {
    private final Logger LOG = LoggerFactory.getLogger(QuotationService.class);
    private static final String PAIR = "USD-BRL";

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrencyPrice() {
        CurrencyPriceDTO currencyPriceInfo = currencyPriceClient.getPriceByPair(PAIR);

        if(updateCurrentInfoPrice(currencyPriceInfo)) {
            kafkaEvents.sendNewKafkaEvent(QuotationDTO
                    .builder()
                    .currencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()))
                    .date(LocalDateTime.now())
                    .build());
        }
    }

    // Update only if the new price are higher than last price
    private boolean updateCurrentInfoPrice(@NotNull CurrencyPriceDTO currencyPriceInfo) {
        BigDecimal currentPrice = new BigDecimal(currencyPriceInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = quotationRepository.findAll().list();

        if(quotationList.isEmpty()) {
            saveQuotation(currencyPriceInfo);
            updatePrice = true;
        } else {
            BigDecimal lastPrice = quotationList.get(quotationList.size() - 1).getCurrencyPrice();
            if(currentPrice.compareTo(lastPrice) > 0) {
                saveQuotation(currencyPriceInfo);
                updatePrice = true;
            }
        }

        return updatePrice;
    }

    private void saveQuotation(@NotNull CurrencyPriceDTO currencyPriceInfo) {
        QuotationEntity quotation = new QuotationEntity();

        quotation.setCurrencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()));
        quotation.setDate(LocalDateTime.now());
        quotation.setPair(PAIR);
        quotation.setPctChange(currencyPriceInfo.getUSDBRL().getPctChange());

        String formattedDate = quotation.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("Persisting new quotation {currencyPrice: %s, date: %s, pair: %s, pctChange: %s}",
                quotation.getCurrencyPrice(), formattedDate, quotation.getPair(), quotation.getPctChange());
        LOG.info(logMessage);

        quotationRepository.persist(quotation);
    }
}
