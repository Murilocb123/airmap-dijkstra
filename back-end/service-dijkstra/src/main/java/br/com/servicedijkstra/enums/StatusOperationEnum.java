package br.com.servicedijkstra.enums;


public enum StatusOperationEnum {
    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    private String status;

    StatusOperationEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
