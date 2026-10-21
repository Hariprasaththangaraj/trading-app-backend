
package com.trading.app.contractmaster.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.app.contractmaster.model.InstrumentMaster;
import com.trading.app.contractmaster.model.InstrumentMasterRequest;
import com.trading.app.contractmaster.model.InstrumentUploadResponse;
import com.trading.app.contractmaster.repo.ContractMasterRepo;
import com.trading.app.contractmaster.service.ContractMasterService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContractMasterServiceImpl
        implements ContractMasterService {

    private static final int BATCH_SIZE = 1000;

    private final ContractMasterRepo contractMasterRepo;
    private final ObjectMapper objectMapper;

    public ContractMasterServiceImpl(
            ContractMasterRepo contractMasterRepo,
            ObjectMapper objectMapper) {

        this.contractMasterRepo = contractMasterRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public InstrumentUploadResponse getUpload(
            MultipartFile file) throws IOException {

        // 1. Validate uploaded file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Uploaded file is empty or missing."
            );
        }

        // 2. Parse JSON
        JsonNode rootNode;

        try (InputStream inputStream = file.getInputStream()) {
            rootNode = objectMapper.readTree(inputStream);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Invalid JSON file: "
                            + file.getOriginalFilename()
                            + ". Use valid JSON with double-quoted "
                            + "field names and string values.",
                    e
            );
        }

        if (rootNode == null || !rootNode.isObject()) {
            throw new IllegalArgumentException(
                    "Expected a JSON object containing an instrument array."
            );
        }

        // 3. Find the instrument array: NSE, NFO, or INDICES
        JsonNode instrumentsNode = null;
        String arrayName = null;

        Iterator<Map.Entry<String, JsonNode>> fields =
                rootNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();

            if (field.getValue().isArray()) {
                instrumentsNode = field.getValue();
                arrayName = field.getKey();
                break;
            }
        }

        if (instrumentsNode == null) {
            throw new IllegalArgumentException(
                    "No instrument array found in uploaded JSON."
            );
        }

        if (instrumentsNode.isEmpty()) {
            throw new IllegalArgumentException(
                    "The uploaded instrument array is empty."
            );
        }

//        System.out.println("File: " + file.getOriginalFilename());
//        System.out.println("Array: " + arrayName);
//        System.out.println(
//                "Records found: " + instrumentsNode.size()
//        );

        // 4. Validate and deduplicate records within the file
        Map<String, InstrumentMasterRequest> uniqueInstruments =
                new LinkedHashMap<>();

        int invalidRecords = 0;
        int duplicateRecords = 0;

        for (JsonNode instrumentNode : instrumentsNode) {

            if (!instrumentNode.isObject()) {
                invalidRecords++;
                continue;
            }

            InstrumentMasterRequest request;

            try {
                request = objectMapper.treeToValue(
                        instrumentNode,
                        InstrumentMasterRequest.class
                );
            } catch (JsonProcessingException e) {
                invalidRecords++;

                System.err.println(
                        "Invalid instrument record: "
                                + e.getMessage()
                );

                continue;
            }

            // Exchange and token are mandatory
            if (isBlank(request.getExchange())
                    || isBlank(request.getToken())) {

                invalidRecords++;

                if (invalidRecords <= 5) {
                    System.err.println(
                            "Skipping record: missing exchange/token: "
                                    + instrumentNode
                    );
                }

                continue;
            }

            // Indices files may not provide instrument type
            if ("INDICES".equalsIgnoreCase(arrayName)
                    && isBlank(request.getInstrumentType())) {

                request.setInstrumentType("INDEX");
            }

            // Other records must have an instrument type
            if (isBlank(request.getInstrumentType())) {
                invalidRecords++;

                if (invalidRecords <= 5) {
                    System.err.println(
                            "Skipping record: missing instrument type: "
                                    + instrumentNode
                    );
                }

                continue;
            }

            String exchange = request.getExchange().trim();
            String token = request.getToken().trim();

            // Normalize values before duplicate comparison
            request.setExchange(exchange);
            request.setToken(token);

            String key = exchange + ":" + token;

            if (uniqueInstruments.putIfAbsent(key, request) != null) {
                duplicateRecords++;
            }
        }

        if (uniqueInstruments.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid instruments found. Check JSON field "
                            + "mappings for exchange, token, and "
                            + "instrument type."
            );
        }

        // 5. Save only instruments that do not exist in the database
        List<InstrumentMaster> batch =
                new ArrayList<>(BATCH_SIZE);

        int savedCount = 0;
        int databaseDuplicateCount = 0;

        for (InstrumentMasterRequest request
                : uniqueInstruments.values()) {

            String exchange = request.getExchange().trim();
            String token = request.getToken().trim();

            // Skip existing exchange + token combinations
            if (contractMasterRepo.existsByExchangeAndToken(
                    exchange, token)) {

                databaseDuplicateCount++;
                continue;
            }

            InstrumentMaster entity = new InstrumentMaster();

            entity.setExchange(exchange);
            entity.setToken(token);
            entity.setSymbol(request.getSymbol());
            entity.setInstrumentName(request.getInstrumentName());
            entity.setInstrumentType(request.getInstrumentType());
            entity.setUnderlying(request.getUnderlying());
            entity.setStrikePrice(request.getStrikePrice());
            entity.setOptionType(request.getOptionType());
            entity.setExpiryDate(request.getExpiryDate());
            entity.setLotSize(request.getLotSize());
            entity.setTradingSymbol(request.getTradingSymbol());

            entity.setFormattedInstrumentName(
                    request.getFormattedInstrumentName()
            );

            entity.setExchangeSegment(
                    request.getExchangeSegment()
            );

            entity.setTickSize(request.getTickSize());
            entity.setSourceFile(file.getOriginalFilename());

            entity.setActive(
                    request.getActive() != null
                            ? request.getActive()
                            : Boolean.TRUE
            );

            batch.add(entity);

            // Save when batch reaches the configured size
            if (batch.size() >= BATCH_SIZE) {

                int currentBatchSize = batch.size();

                contractMasterRepo.saveAll(batch);
                contractMasterRepo.flush();

                savedCount += currentBatchSize;
                batch.clear();
            }
        }

        // Save the remaining records
        if (!batch.isEmpty()) {

            int currentBatchSize = batch.size();

            contractMasterRepo.saveAll(batch);
            contractMasterRepo.flush();

            savedCount += currentBatchSize;
            batch.clear();
        }

        // 6. Log upload summary
//        System.out.println("Instrument upload completed.");
//        System.out.println(
//                "Total records: " + instrumentsNode.size()
//        );
//        System.out.println(
//                "Unique valid records: " + uniqueInstruments.size()
//        );
//        System.out.println(
//                "New instruments inserted: " + savedCount
//        );
//        System.out.println(
//                "Database duplicates skipped: "
//                        + databaseDuplicateCount
//        );
//        System.out.println(
//                "Duplicates within file skipped: " + duplicateRecords
//        );
//        System.out.println(
//                "Invalid records skipped: " + invalidRecords
//        );

        // 7. Return upload summary
        return new InstrumentUploadResponse(
                file.getOriginalFilename(),
                arrayName,
                instrumentsNode.size(),
                uniqueInstruments.size(),
                savedCount,
                duplicateRecords,
                invalidRecords,
                "Instrument upload completed successfully."
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
