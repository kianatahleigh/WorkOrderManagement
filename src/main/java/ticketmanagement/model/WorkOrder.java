package ticketmanagement.model;

import ticketmanagement.dao.WorkOrderDAO;

import java.sql.*;
import java.time.LocalDate;

public class WorkOrder {
    private int tenantID;
    private String name;
    private String phoneNumber;
    private String address;
    private LocalDate submissionDate;
    private String conditions;
    private int priorityScore;
    private String priorityLevel;
    private String ticketNumber;
    public Status status;
    private int adminID;
    private String adminName;
    private WorkOrderDAO workOrderDAO;

    public WorkOrder(int tenantID, String name, String phoneNumber, String address, LocalDate submissionDate, String conditions, int priorityScore, String priorityLevel, String ticketNumber, Status status, int adminID, String adminName, WorkOrderDAO workOrderDAO) {
        this.tenantID = tenantID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.submissionDate = submissionDate;
        this.conditions = conditions;
        this.priorityScore = priorityScore;
        this.priorityLevel = priorityLevel;
        this.ticketNumber = ticketNumber;
        this.status = status;
        this.adminID = adminID;
        this.adminName = adminName;
        this.workOrderDAO = workOrderDAO;
    }

    public enum Status {
        NEW, IN_PROGRESS, COMPLETED, CLOSED, CANCELLED
    }

    public int getTenantID() {
        return tenantID;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public String getConditions() {
        return conditions;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public Status getStatus() {
        return status;
    }

    public int getAdminID() {
        return adminID;
    }

    public String getAdminName() {
        return adminName;
    }


    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;

    }
    public String getTicketNumber() {
        return String.valueOf(ticketNumber);
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }


    public void setStatus(Status newStatus) throws SQLException {
        this.status = newStatus;
        // Call DAO method to update database
        workOrderDAO.updateWorkOrderStatus(this.ticketNumber, newStatus, this.adminID);
    }


    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }


    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setWorkOrderDAO(WorkOrderDAO workOrderDAO) {
        this.workOrderDAO = workOrderDAO;
    }


    @Override
    public String toString() {
        return "WorkOrder{" +
                "tenantID=" + tenantID +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", submissionDate=" + submissionDate +
                ", conditions='" + conditions + '\'' +
                ", priorityScore=" + priorityScore +
                ", priorityLevel='" + priorityLevel + '\'' +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", status=" + status +
                ", adminID=" + adminID +
                ", adminName='" + adminName + '\'' +
                ", workOrderDAO=" + workOrderDAO +
                '}';
    }
}