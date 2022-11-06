package com.jemsa.adm_api;

public class ErrorResponseDTO {
    public String errorText;

    public ErrorResponseDTO(String message) {
        this.errorText = message;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getErrorText() {
        return errorText;
    }
}
