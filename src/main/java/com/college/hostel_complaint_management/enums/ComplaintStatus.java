package com.college.hostel_complaint_management.enums;

public enum ComplaintStatus {
    PENDING,                          // just created, visible to entire department's staff queue
    ASSIGNED,                         // a specific staff member has claimed it
    RESOLUTION_PENDING_CONFIRMATION,  // staff uploaded proof, waiting on student to confirm
    RESOLVED,                         // student confirmed the fix
    ESCALATED                         // student reported to admin after 3+ days stuck
}