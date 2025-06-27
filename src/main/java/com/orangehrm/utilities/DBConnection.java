package com.orangehrm.utilities;

import com.orangehrm.base.BaseClass;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/orangehrm";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final Logger logger = BaseClass.logger;


    public static Connection getDBConnection() {
        try {
            logger.info("Starting DB connection");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("DB connection successful");
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException("DB connection failed", e);
        }
    }

    /**
     * Returns a map of employee details for the given employee id.
     * The returned map contains the following keys:
     * <ul>
     * <li>firstName: employee's first name</li>
     * <li>lastName: employee's last name</li>
     * <li>middleName: employee's middle name</li>
     * </ul>
     * If no records are found, an empty map is returned.
     *
     * @param empId the employee id to fetch details for
     * @return a map of employee details
     */
    public static Map<String, String> getEmployeeDetails(String empId) {
        Map<String, String> employeeDetails = new HashMap<>();
        try {
            String query = "SELECT emp_firstname,emp_middle_name,emp_lastname FROM hs_hr_employee WHERE employee_id = " + empId;

            try (Connection connection = getDBConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                logger.info("Executing query: {}", query);
                if (resultSet.next()) {
                    employeeDetails.put("firstName", resultSet.getString("emp_firstname"));
                    employeeDetails.put("lastName", resultSet.getString("emp_lastname"));
                    employeeDetails.put("middleName", resultSet.getString("emp_middle_name") != null ? resultSet.getString("emp_middle_name") : "");
                    logger.info("Query executed successfully - Employee Data Fetched");
                }
                else {
                    logger.error("No records found - No Employee Data Fetched");
                }
            }
        } catch (SQLException e) {
            logger.error("Query execution failed - No Employee Data Fetched");
        }
        return employeeDetails;
    }


}
