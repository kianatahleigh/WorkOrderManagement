package ticketmanagement.dao;

import ticketmanagement.model.WorkOrder;

import java.time.LocalDate;
import java.sql.*;


public class WorkOrderDAO {

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // Method to create a work order
    public void createWorkOrder(WorkOrder workOrder) throws SQLException {
        String ticketNumber = workOrder.getTicketNumber();

        String query = "INSERT INTO WorkOrders (tenant_id, submission_date, conditions, priority_level, ticket_number, status, admin_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, workOrder.getTenantID());
            statement.setDate(2, java.sql.Date.valueOf(workOrder.getSubmissionDate()));
            statement.setString(3, workOrder.getConditions());
            statement.setString(4, workOrder.getPriorityLevel());
            statement.setString(5, ticketNumber);
            statement.setString(6, workOrder.getStatus().name());
            statement.setInt(7, workOrder.getAdminID());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {

            throw new SQLException("Duplicate entry for ticket number: " + ticketNumber);
        }
    }


    public WorkOrder getWorkOrder(String ticketNumber) throws SQLException {
        String query = "SELECT * FROM WorkOrders JOIN Tenants ON WorkOrders.tenant_id = Tenants.tenant_id WHERE ticket_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, ticketNumber);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int tenantID = resultSet.getInt("tenant_id");
            String name = resultSet.getString("name");
            String phoneNumber = resultSet.getString("phone_number");
            String address = resultSet.getString("address");
            LocalDate submissionDate = resultSet.getDate("submission_date").toLocalDate();
            String conditions = resultSet.getString("conditions");
            String priorityLevel = resultSet.getString("priority_level");
            String ticketID = resultSet.getString("ticket_number");
            int adminID = resultSet.getInt("admin_id");
            String statusStr = resultSet.getString("status");
            WorkOrder.Status status = WorkOrder.Status.valueOf(statusStr.toUpperCase());

            WorkOrderDAO workOrderDAO = new WorkOrderDAO();
            workOrderDAO.setConnection(connection);


            return new WorkOrder(
                    tenantID,
                    name,
                    phoneNumber,
                    address,
                    submissionDate,
                    conditions,
                    0,
                    priorityLevel,
                    ticketID,
                    status,
                    adminID,
                    null,
                    workOrderDAO
            );
        } else {
            return null;
        }
    }


    public int addAdmin(String adminName) throws SQLException {
        String query = "INSERT INTO Admins (admin_name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1,
                    adminName);
            statement.executeUpdate();


            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating admin failed, no ID obtained.");
                }
            }
        }
    }


    public void updateWorkOrderStatus(String ticketNumber, WorkOrder.Status newStatus, int adminID) throws SQLException {
        String updateQuery = "UPDATE workorders SET status = ?, admin_id = ? WHERE ticket_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newStatus.name());
            preparedStatement .setInt(2, adminID);
            preparedStatement.setString(3, ticketNumber);
            preparedStatement.executeUpdate();
        }
    }


    public boolean deleteWorkOrder(String ticketNumber) throws SQLException {
        String deleteQuery = "DELETE FROM workorders WHERE ticket_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setString(1, ticketNumber);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public int addTenant(String name, String phoneNumber, String address) throws SQLException {
        String insertSQL = "INSERT INTO Tenants (name, phone_number, address) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, address);
            pstmt.executeUpdate();

            // Retrieve the generated tenant ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating tenant failed, no ID obtained.");
                }
            }
        }
    }



}