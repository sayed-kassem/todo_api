package com.example.todoapp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet("/todos")
public class TodoServlet extends HttpServlet {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/todo_app_db";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1k6y9qm87q0j";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try(Connection connection = DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PASSWORD)){
            String sql = "SELECT * From todos";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder json = new StringBuilder("[");
            while (resultSet.next()){
                if(json.length() > 1) json.append(" , ");
                json.append("{\"id\":").append(resultSet.getInt("id")).append(",");
                json.append("\"title\":\"").append(resultSet.getString("title")).append("\",");
                json.append("\"description\":\"").append(resultSet.getString("description")).append("\",");
                json.append("\"is_complete\":").append(resultSet.getBoolean("is_completed")).append("}");
            }
            json.append("]");
            out.print(json.toString());
        }
        catch (SQLException e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database connection problem: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String title = req.getParameter("title");
        String description = req.getParameter("description");

        try(Connection connection = DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PASSWORD)){
            String sql = "INSERT INTO todos (title, description) VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,title);
            statement.setString(2,description);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted >0) {
                out.print("{\"message\": \"Tod item added successfully\"}");
            }
            else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to add todo item\"}");

            }
        }
        catch (SQLException e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database connection problem: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        int id ;

        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid ID format\"}");
            return;
        }


        String title = req.getParameter("title");
        String description = req.getParameter("description");
        boolean isCompleted = Boolean.parseBoolean(req.getParameter("is_completed"));

        try(Connection connection= DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PASSWORD)) {

            String sql = "UPDATE todos SET title =?, description= ?, is_completed= ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, title);
            statement.setString(2, description);
            statement.setBoolean(3, isCompleted);
            statement.setInt(4, id);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                out.print("{\"message\": \"Todo item updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to update todo item\"}");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database connection problem: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws  ServletException, IOException{
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        int id ;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid ID format\"}");
            return;
        }

        try(Connection connection = DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PASSWORD)){
            String sql = "DELETE FROM todos WHERE id= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,id);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0 ){
                out.print("{\"message\": \"Todo item deleted successfully\"}");
            }
            else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to deleted todo item\"}");
            }
        }catch(SQLException e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database connection problem: " + e.getMessage() + "\"}");
        }
    }

}
