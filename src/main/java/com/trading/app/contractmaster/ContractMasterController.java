package com.trading.app.contractmaster;

import com.trading.app.contractmaster.model.InstrumentUploadResponse;
import com.trading.app.contractmaster.service.ContractMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/contractmaster")
public class ContractMasterController {

    @Autowired
    ContractMasterService contractMasterService;


    @PostMapping("/upload")
    public ResponseEntity<InstrumentUploadResponse> getUpload(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            InstrumentUploadResponse response = new InstrumentUploadResponse();
            response.setMessage("Issue with upload: File not found");

            return ResponseEntity.badRequest().body(response);
        }

        InstrumentUploadResponse response =
                contractMasterService.getUpload(file);

        return ResponseEntity.ok(response);
    }
}
