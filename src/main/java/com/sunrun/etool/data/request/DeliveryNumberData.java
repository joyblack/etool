package com.sunrun.etool.data.request;

public class DeliveryNumberData {
    private String filePath;
    private String resultPath;
    private String targetCell;

    public DeliveryNumberData() {
    }

    public DeliveryNumberData(String filePath, String resultPath, String targetCell) {
        this.filePath = filePath;
        this.resultPath = resultPath;
        this.targetCell = targetCell;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public String getTargetCell() {
        return targetCell;
    }

    public void setTargetCell(String targetCell) {
        this.targetCell = targetCell;
    }
}
