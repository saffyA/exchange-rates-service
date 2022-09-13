package com.example.exchangerates.dto;

import java.util.Objects;

public class UpdateExchangeRatesResponse {
    public enum UpdateStatus {
        SUCCESS,
    }

    private UpdateStatus updateStatus;
    private String fileName;

    public UpdateExchangeRatesResponse(UpdateStatus updateStatus, String fileName) {
        this.updateStatus = updateStatus;
        this.fileName = fileName;
    }

    public UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(UpdateStatus updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateExchangeRatesResponse that = (UpdateExchangeRatesResponse) o;
        return updateStatus == that.updateStatus && Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updateStatus, fileName);
    }
}
