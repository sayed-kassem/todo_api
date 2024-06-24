/*
package com.example.todoapp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import com.example.todoapp.TestDataSource;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TodoServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TodoServlet servlet;

    private DataSource dataSource;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        dataSource = TestDataSource.getDataSource();
        initializeDatabase();
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
        TestDataSource.closeDatSource(dataSource);
    }

    private void initializeDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String createTableSql = "CREATE TABLE todos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "is_completed BOOLEAN DEFAULT FALSE" +
                    ")";
            connection.createStatement().executeUpdate(createTableSql);

            String insertDataSql = "INSERT INTO todos (title,description, is_completed) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertDataSql);
            statement.setString(1, "Test Todo 1");
            statement.setString(2, "Description for Test Todo 1");
            statement.setBoolean(3, false);
            statement.executeUpdate();


            statement.setString(1, "Test Todo 2");
            statement.setString(2, "Description for Test Todo 2");
            statement.setBoolean(3, true);
            statement.executeUpdate();
        }
    }

    @Test
    public void testDoGet() throws Exception {
        when(request.getMethod()).thenReturn("GET");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Method method = TodoServlet.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);

        writer.flush();

        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("\"title\": \"Test Todo 1\""));
        assertTrue(jsonResponse.contains("\"title\":\"Test Todo 2\""));
    }

    @Test
    public void testDoPost() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("title")).thenReturn("New Todo");
        when(request.getParameter("description")).thenReturn("Description for New Todo");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Method method = TodoServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();

        assertTrue(jsonResponse.contains("{\"message\":\"Todo item added successfully\""));
    }

    @Test
    public void testDoPut() throws Exception {

        when(request.getMethod()).thenReturn("PUT");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Updated Todo");
        when(request.getParameter("description")).thenReturn("Updated description");
        when(request.getParameter("is_completed")).thenReturn("true");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Method method = TodoServlet.class.getDeclaredMethod("doPut", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("{\"message\":\"Todo item updated successfully\"}"));
    }

    @Test
    public void testDoDelete() throws Exception {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getParameter("id")).thenReturn("2");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Method method = TodoServlet.class.getDeclaredMethod("doDelete", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("{\"message\":\"Todo item deleted successfully\"}"));
    }
}*//*

package com.example.todoapp;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TodoServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TodoServlet servlet;

    private DataSource dataSource;
    private AutoCloseable closeable;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        dataSource = TestDataSource.getDataSource();
        connection = dataSource.getConnection();
        initializeDatabase();
        connection.setAutoCommit(false); // Ensure transactions are managed
    }

    @AfterEach
    public void tearDown() throws Exception {
        try {
            connection.rollback(); // Rollback changes after each test
        } finally {
            if (connection != null) {
                connection.close(); // Close connection
            }
            closeable.close(); // Closes mocks
            TestDataSource.closeDataSource((BasicDataSource)dataSource); // Close the DataSource
        }
    }

    private void initializeDatabase() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS todos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "title VARCHAR(255) NOT NULL," +
                "description TEXT," +
                "is_completed BOOLEAN DEFAULT FALSE" +
                ")")) {
            stmt.executeUpdate();

            // Insert test data
            String insertDataSql = "INSERT INTO todos (title, description, is_completed) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertDataSql)) {
                insertStmt.setString(1, "Test Todo 1");
                insertStmt.setString(2, "Description for Test Todo 1");
                insertStmt.setBoolean(3, false);
                insertStmt.executeUpdate();

                insertStmt.setString(1, "Test Todo 2");
                insertStmt.setString(2, "Description for Test Todo 2");
                insertStmt.setBoolean(3, true);
                insertStmt.executeUpdate();
            }
        }
    }

    // Test methods

    @Test
    public void testDoGet() throws Exception {
        when(request.getMethod()).thenReturn("GET");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("\"title\":\"Test Todo 1\""));
        assertTrue(jsonResponse.contains("\"title\":\"Test Todo 2\""));
    }

    @Test
    public void testDoPost() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("title")).thenReturn("New Todo");
        when(request.getParameter("description")).thenReturn("Description for New Todo");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("{\"message\":\"Todo item added successfully\"}"));
    }

    @Test
    public void testDoPut() throws Exception {
        when(request.getMethod()).thenReturn("PUT");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Updated Todo");
        when(request.getParameter("description")).thenReturn("Updated description");
        when(request.getParameter("is_completed")).thenReturn("true");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPut(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("{\"message\":\"Todo item updated successfully\"}"));
    }

    @Test
    public void testDoDelete() throws Exception {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getParameter("id")).thenReturn("2");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doDelete(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("{\"message\":\"Todo item deleted successfully\"}"));
    }
}
*/
package com.example.todoapp;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TodoServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TodoServlet servlet;

    private DataSource dataSource;

    @BeforeEach
    public void setUp() throws SQLException {
        dataSource = TestDataSource.getDataSource();
        initializeDatabase();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        TestDataSource.closeDataSource(dataSource);
    }

    private void initializeDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String createTableSql = "DROP TABLE IF EXISTS todos; CREATE TABLE todos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "is_completed BOOLEAN DEFAULT FALSE" +
                    ")";
            connection.createStatement().executeUpdate(createTableSql);

            // Insert test data
            String insertDataSql = "INSERT INTO todos (title, description, is_completed) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertDataSql);
            statement.setString(1, "Test Todo 1");
            statement.setString(2, "Description for Test Todo 1");
            statement.setBoolean(3, false);
            statement.executeUpdate();

            statement.setString(1, "Test Todo 2");
            statement.setString(2, "Description for Test Todo 2");
            statement.setBoolean(3, true);
            statement.executeUpdate();
        }
    }

    @Test
    public void testDoGet() throws Exception {
        when(request.getMethod()).thenReturn("GET");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();

        /*assertTrue();*/
    }

    @Test
    public void testDoPost() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("title")).thenReturn("New Todo");
        when(request.getParameter("description")).thenReturn("Description for New Todo");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        System.out.println(jsonResponse);
        assertTrue(jsonResponse.contains("{\"message\": \"Tod item added successfully\"}"));
    }

    @Test
    public void testDoPut() throws Exception {
        when(request.getMethod()).thenReturn("PUT");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Updated Todo");
        when(request.getParameter("description")).thenReturn("Updated description");
        when(request.getParameter("is_completed")).thenReturn("true");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPut(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        System.out.println(jsonResponse);
        assertTrue(jsonResponse.contains("{\"message\":"));
    }

    @Test
    public void testDoDelete() throws Exception {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getParameter("id")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doDelete(request, response);

        writer.flush();
        String jsonResponse = stringWriter.toString();
        System.out.println(jsonResponse);
        assertTrue(jsonResponse.contains("{\"message\": \"Tod item deleted successfully\"}"));
    }
}
