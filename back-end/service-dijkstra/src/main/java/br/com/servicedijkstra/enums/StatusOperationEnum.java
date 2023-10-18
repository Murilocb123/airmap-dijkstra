package br.com.servicedijkstra.enums;


public enum StatusOperationEnum {
    SUCCESS(200),
    ERROR(500),

    NOTFOUND(404);

    private int status;

    StatusOperationEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
