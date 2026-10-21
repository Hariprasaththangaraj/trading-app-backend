package com.trading.app.contractmaster.service;

import com.trading.app.contractmaster.model.InstrumentUploadResponse;
import com.trading.app.contractmaster.service.impl.ContractMasterServiceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ContractMasterService   {
    InstrumentUploadResponse getUpload(MultipartFile file) throws IOException;
}
