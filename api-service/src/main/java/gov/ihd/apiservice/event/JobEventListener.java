package gov.ihd.apiservice.event;

public interface JobEventListener {
    void onJobProgress(String jobId, int recordsProcessed);
    void onJobCompleted(String jobId, int recordsProcessed);
    void onJobFailed(String jobId, String errorMessage);
}
