package com.data.project.entity;

public enum Progress {
    PENDING("Pending"),
    HANDLING("Handling"),
    INTERVIEWING("Interviewing"),
    DONE("Done"),
    REJECT("Reject"),
    CANCEL("Cancel");

    private final String status;

    Progress(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
