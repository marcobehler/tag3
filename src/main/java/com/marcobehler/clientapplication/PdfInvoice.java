package com.marcobehler.clientapplication;

public class PdfInvoice {

    private String url;

    public PdfInvoice() {
    }

    public PdfInvoice(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
