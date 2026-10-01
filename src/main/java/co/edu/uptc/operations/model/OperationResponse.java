package co.edu.uptc.operations.model;

public class OperationResponse {

    private final String container;
    private final float result;

    public OperationResponse(String container, float result) {
        this.container = container;
        this.result = result;
    }

    public String getContainer() {
        return container;
    }

    public float getResult() {
        return result;
    }
}
