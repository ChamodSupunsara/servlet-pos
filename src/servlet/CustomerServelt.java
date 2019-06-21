package servlet;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = "/customerServelt")
public class CustomerServelt extends HttpServlet {

    @Resource(name = "jdbc/finalpossystemproject")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        JsonReader reader=null;
        JsonObject empObj = null;

        try {
            reader = Json.createReader(request.getReader());
            empObj=reader.readObject();

            String cusid = empObj.getString("id");
            String name = empObj.getString("name");
            String address = empObj.getString("address");
            Integer salary = empObj.getInt("salary");

            System.out.println(cusid);

            Connection connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");
            pstm.setObject(1,cusid);
            pstm.setObject(2,name);
            pstm.setObject(3,address);
            pstm.setObject(4,salary);
            boolean value=pstm.executeUpdate()>0;

            if (value){
                response.setStatus(HttpServletResponse.SC_CREATED);
            }else{
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        }catch (JsonParsingException | NullPointerException  ex){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }catch (Exception ex){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



        PrintWriter printWriter=response.getWriter();
        response.setContentType("application/json");

        Connection connection= null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{

            connection=dataSource.getConnection();

            String sql= "select * from customer";
            statement=connection.createStatement();

            resultSet=statement.executeQuery(sql);

            JsonArrayBuilder customers = Json.createArrayBuilder();

            while(resultSet.next()){

                String cusid=resultSet.getString(1);
                String name=resultSet.getString(2);
                String address=resultSet.getString(3);
                Integer salary=resultSet.getInt(4);

                JsonObject customer = Json.createObjectBuilder()
                        .add("id", cusid)
                        .add("name", name)
                        .add("address",address)
                        .add("salary", salary)
                        .build();
                customers.add(customer);
            }

            printWriter.println(customers.build().toString());
            connection.close();

        }catch (Exception ex){
            response.sendError(500, ex.getMessage());
            ex.printStackTrace();
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement("delete from Customer where id=?");
            pstm.setObject(1,req.getParameter("id"));
            boolean value=pstm.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonReader reader=null;
        JsonObject empObj = null;

        try {
            reader = Json.createReader(req.getReader());
            empObj=reader.readObject();

            String cusid = empObj.getString("id");
            String name = empObj.getString("name");
            String address = empObj.getString("address");
            Integer salary = empObj.getInt("salary");

            System.out.println(cusid);

            Connection connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement("update Customer set name=?, address=?, salary=? where id=?");
            pstm.setObject(1,cusid);
            pstm.setObject(2,name);
            pstm.setObject(3,address);
            pstm.setObject(4,salary);
            boolean value=pstm.executeUpdate()>0;

            if (value){
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }else{
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        }catch (JsonParsingException | NullPointerException  ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }catch (Exception ex){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
