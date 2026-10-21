package com.trading.app.contractmaster.model;

public class InstrumentUploadResponse {

    private String fileName;
    private String arrayName;
    private int recordsFound;
    private int uniqueValidRecords;
    private int recordsSaved;
    private int duplicatesInFile;
    private int invalidRecordsSkipped;
    private String message;

    public InstrumentUploadResponse(
            String fileName,
            String arrayName,
            int recordsFound,
            int uniqueValidRecords,
            int recordsSaved,
            int duplicatesInFile,
            int invalidRecordsSkipped,
            String message) {
        this.fileName = fileName;
        this.arrayName = arrayName;
        this.recordsFound = recordsFound;
        this.uniqueValidRecords = uniqueValidRecords;
        this.recordsSaved = recordsSaved;
        this.duplicatesInFile = duplicatesInFile;
        this.invalidRecordsSkipped = invalidRecordsSkipped;
        this.message = message;
    }

    public InstrumentUploadResponse() {

    }

    public String getFileName() {
        return fileName;
    }

    public String getArrayName() {
        return arrayName;
    }

    public int getRecordsFound() {
        return recordsFound;
    }

    public int getUniqueValidRecords() {
        return uniqueValidRecords;
    }

    public int getRecordsSaved() {
        return recordsSaved;
    }

    public int getDuplicatesInFile() {
        return duplicatesInFile;
    }

    public int getInvalidRecordsSkipped() {
        return invalidRecordsSkipped;
    }

    public String getMessage() {
        return message;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public void setRecordsFound(int recordsFound) {
        this.recordsFound = recordsFound;
    }

    public void setUniqueValidRecords(int uniqueValidRecords) {
        this.uniqueValidRecords = uniqueValidRecords;
    }

    public void setRecordsSaved(int recordsSaved) {
        this.recordsSaved = recordsSaved;
    }

    public void setDuplicatesInFile(int duplicatesInFile) {
        this.duplicatesInFile = duplicatesInFile;
    }

    public void setInvalidRecordsSkipped(int invalidRecordsSkipped) {
        this.invalidRecordsSkipped = invalidRecordsSkipped;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}