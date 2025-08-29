package com.example.bfh.service;

import org.springframework.stereotype.Component;
@Component
public class SqlSolver {

    public String solve(boolean isOdd) {
        if (isOdd) {
            return "/* Your SQL for Question 1 */\nSELECT ...";
        } else {
            return "/* Final SQL for Question 2 */\n" +
                "SELECT \n" +
                "    e.EMP_ID,\n" +
                "    e.FIRST_NAME,\n" +
                "    e.LAST_NAME,\n" +
                "    d.DEPARTMENT_NAME,\n" +
                "    COUNT(CASE WHEN e2.DOB > e.DOB THEN 1 END) AS YOUNGER_EMPLOYEES_COUNT\n" +
                "FROM EMPLOYEE e\n" +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID\n" +
                "LEFT JOIN EMPLOYEE e2 ON e.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e.DOB\n" +
                "GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME\n" +
                "ORDER BY e.EMP_ID DESC;";
        }
    }

}
