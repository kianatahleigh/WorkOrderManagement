package ticketmanagement.workorderrunner;

import ticketmanagement.model.WorkOrder;
import ticketmanagement.dao.WorkOrderDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.sql.*;

public class WorkOrderSystem {


    public WorkOrderDAO getWorkOrderDAO() {
        return workOrderDAO;
    }

    private PriorityQueue<WorkOrder> workOrderQueue;
    private Map<String, WorkOrder> workOrderMap;
    private int ticketCounter = 1;
    private WorkOrderDAO workOrderDAO;

    public WorkOrderSystem() {

        this.workOrderDAO = new WorkOrderDAO();


        workOrderQueue = new PriorityQueue<>((o1, o2) -> {
            int scoreComparison = o2.getPriorityScore() - o1.getPriorityScore();
            if (scoreComparison == 0) {
                return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
            }
            return scoreComparison;
        });
        workOrderMap = new HashMap<>();
    }

    public void addWorkOrder(String name, String phoneNumber, String address, LocalDate submissionDate, String conditions) throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/workordermanagementsystemdb", "x", "x")) {
            WorkOrderDAO workOrderDAO = new WorkOrderDAO();
            workOrderDAO.setConnection(connection);


            int tenantID = workOrderDAO.addTenant(name, phoneNumber, address);

            String ticketNumber = assignTicketNumber();
            int dateScore = calculateScore(submissionDate);
            int conditionScore = calculateConditionScore(conditions);
            int weightedConditionScore = conditionScore * 2;
            int priorityScore = dateScore + weightedConditionScore;
            String determinedPriority = determinePriority(priorityScore);



            WorkOrder workOrder = new WorkOrder(
                    tenantID,
                    name,
                    phoneNumber,
                    address,
                    submissionDate,
                    conditions,
                    priorityScore,
                    determinedPriority,
                    ticketNumber,
                    WorkOrder.Status.NEW,
                    0,
                    "",
                    this.workOrderDAO

            );


            workOrderDAO.createWorkOrder(workOrder);


            workOrderQueue.add(workOrder);
            workOrderMap.put(ticketNumber, workOrder);
        }
    }

    public WorkOrder getNextWorkOrder() {
        WorkOrder nextWorkOrder = workOrderQueue.poll();
        if (nextWorkOrder != null) {
            workOrderMap.put(nextWorkOrder.getTicketNumber(), nextWorkOrder);
        }
        return nextWorkOrder;
    }

    public WorkOrder getWorkOrderByTicketNumber(String ticketNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/workordermanagementsystemdb", "x", "x")) {
            workOrderDAO.setConnection(connection);
            return workOrderDAO.getWorkOrder(ticketNumber);
        }
    }

    public static int calculateScore(LocalDate submissionDate) {
        LocalDate currentDate = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(submissionDate, currentDate);

        if (daysDifference <= 7) {
            return 1; // Low
        } else if (daysDifference <= 15) {
            return 2; // Medium
        } else {
            return 3; // High
        }
    }

    public static int calculateConditionScore(String conditions) {
        switch (conditions.toLowerCase()) {
            case "monthly air filter change":
            case "monthly pest control":
                return 1; // Low
            case "pest control":
            case "broken blinds":
                return 2; // Medium
            case "flooding":
            case "broken hvac unit":
                return 3; // High
            default:
                return 0; // Unknown condition
        }
    }

    private String determinePriority(int priorityScore) {
        if (priorityScore <= 3) {
            return "Low";
        } else if (priorityScore <= 6) {
            return "Medium";
        } else if (priorityScore <= 8) {
            return "High";
        } else {
            return "Critical";
        }
    }


    private String assignTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static void main(String[] args) {
        WorkOrderSystem calculator = new WorkOrderSystem();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Are you a tenant or an admin? (Enter 'tenant' or 'admin')");
        String userType = scanner.nextLine().trim().toLowerCase();

        if ("tenant".equals(userType)) {
            runTenantSection(scanner, calculator);
        } else if ("admin".equals(userType)) {
            runAdminSection(scanner, calculator);
        } else {
            System.out.println("Invalid user type. Please restart the program and enter either 'tenant' or 'admin'.");
        }

        scanner.close();
    }

    private static void runTenantSection(Scanner scanner, WorkOrderSystem calculator) {
        System.out.println("Please enter your name:");
        String name = scanner.nextLine();

        System.out.println("Please enter your phone number:");
        String phoneNumber = scanner.nextLine();

        System.out.println("Please enter your address:");
        String address = scanner.nextLine();

        System.out.println("Please enter the submission date (YYYY-MM-DD):");
        LocalDate submissionDate = null;
        while (submissionDate == null) {
            try {
                submissionDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
            }
        }

        System.out.println("Please choose a condition from the following options:");
        System.out.println("1. Monthly Air Filter Change");
        System.out.println("2. Monthly Pest Control");
        System.out.println("3. Pest Control");
        System.out.println("4. Broken Blinds");
        System.out.println("5. Flooding");
        System.out.println("6. Broken HVAC Unit");

        String conditions = null;
        while (conditions == null) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        conditions = "monthly air filter change";
                        break;
                    case 2:
                        conditions = "monthly pest control";
                        break;
                    case 3:
                        conditions = "pest control";
                        break;
                    case 4:
                        conditions = "broken blinds";
                        break;
                    case 5:
                        conditions = "flooding";
                        break;
                    case 6:
                        conditions = "broken hvac unit";
                        break;
                    default:
                        System.out.println("Invalid choice. Please choose a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number corresponding to a condition.");
                scanner.nextLine();
            }
        }


        try {
            calculator.addWorkOrder(name, phoneNumber, address, submissionDate, conditions);
        } catch (SQLException e) {
            System.out.println("Failed to add work order: " + e.getMessage());
        }


        WorkOrder nextWorkOrder = calculator.getNextWorkOrder();
        if (nextWorkOrder != null) {
            System.out.println("Work Order Details:");
            System.out.println("Name: " + nextWorkOrder.getName());
            System.out.println("Phone number: " + nextWorkOrder.getPhoneNumber());
            System.out.println("Address: " + nextWorkOrder.getAddress());
            System.out.println("Ticket Number: " + nextWorkOrder.getTicketNumber());
            System.out.println("Priority Level: " + nextWorkOrder.getPriorityLevel());
            System.out.println("Condition: " + nextWorkOrder.getConditions());
            System.out.println("Status: " + nextWorkOrder.getStatus());
        }
    }

    private static void runAdminSection(Scanner scanner, WorkOrderSystem calculator) {
        System.out.println("\nAdmin Console");
        System.out.println("Please enter your name:");
        String adminName = scanner.nextLine();

        Connection connection = null;
        try {

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/workordermanagementsystemdb", "x", "x");
            WorkOrderDAO workOrderDAO = calculator.getWorkOrderDAO();
            workOrderDAO.setConnection(connection);

            int adminID = workOrderDAO.addAdmin(adminName);
            System.out.println("Admin ID generated: " + adminID);

            System.out.println("Please enter the ticket number of the work order you'd like to view:");
            String ticketNumber = scanner.nextLine();

            WorkOrder workOrder = workOrderDAO.getWorkOrder(ticketNumber);
            if (workOrder != null) {
                System.out.println("Work Order Details:");
                System.out.println("Name: " + workOrder.getName());
                System.out.println("Phone number: " + workOrder.getPhoneNumber());
                System.out.println("Address: " + workOrder.getAddress());
                System.out.println("Ticket Number: " + workOrder.getTicketNumber());
                System.out.println("Priority Level: " + workOrder.getPriorityLevel());
                System.out.println("Condition: " + workOrder.getConditions());
                System.out.println("Current Status: " + workOrder.getStatus());

                System.out.println("\nWhat would you like to do with this work order?");
                System.out.println("1. Change Status");
                System.out.println("2. Delete Work Order");

                int adminChoice = scanner.nextInt();
                scanner.nextLine();

                if (adminChoice == 1) {

                    System.out.println("\nChange the status of this work order:");
                    System.out.println("1. IN PROGRESS");
                    System.out.println("2. COMPLETED");
                    System.out.println("3. CLOSED");
                    System.out.println("4. CANCELLED");

                    WorkOrder.Status newStatus = null;
                    while (newStatus == null) {
                        try {
                            int statusChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch (statusChoice) {
                                case 1:
                                    newStatus = WorkOrder.Status.IN_PROGRESS;
                                    break;
                                case 2:
                                    newStatus = WorkOrder.Status.COMPLETED;
                                    break;
                                case 3:
                                    newStatus = WorkOrder.Status.CLOSED;
                                    break;
                                case 4:
                                    newStatus = WorkOrder.Status.CANCELLED;
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please choose a valid option.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number corresponding to a status.");
                            scanner.nextLine();
                        }
                    }


                    try {
                        workOrderDAO.updateWorkOrderStatus(workOrder.getTicketNumber(), newStatus, adminID);


                        workOrder.setStatus(newStatus);
                        System.out.println("Work Order status has been updated to: " + workOrder.getStatus());
                    } catch (SQLException e) {
                        System.out.println("Failed to update work order status: " + e.getMessage());
                    }

                } else if (adminChoice == 2) {

                    System.out.println("Are you sure you want to delete this work order? (yes/no)");
                    String confirmation = scanner.nextLine().trim().toLowerCase();
                    if ("yes".equals(confirmation)) {
                        try {
                            workOrderDAO.deleteWorkOrder(ticketNumber);
                            System.out.println("Work Order has been deleted successfully.");
                        } catch (SQLException e) {
                            System.out.println("Failed to delete work order: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Deletion cancelled.");
                    }
                } else {
                    System.out.println("Invalid choice. Please restart the program and try again.");
                }
            } else {
                System.out.println("No work order found with that ticket number.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
        } finally {

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close the connection: " + e.getMessage());
                }
            }
        }
    }
}