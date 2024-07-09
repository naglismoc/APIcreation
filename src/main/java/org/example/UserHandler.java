package org.example;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import static org.example.Main.*;

public class UserHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        handleCORS(exchange);
        if (path.equals("/createUser") && method.equals("POST")) {
            handleCreateUser(exchange);
        }
        if (path.equals("/getUsers") && method.equals("GET")) {
            handleGetUsers(exchange);
        }
        if (path.equals("/getUser") && method.equals("GET")) {
            handleGetUserByid(exchange);
        }
        if (path.equals("/updateUser") && method.equals("POST")) {
            handleUpdateUser(exchange);
        }
        if (path.equals("/deleteUser") && method.equals("POST")) {
            handleDeleteUser(exchange);
        }

        exchange.sendResponseHeaders(400, -1);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    /**
     * cia sudokumentuojam metodą, ir nenaudojam "komentarų" kode
     * @param exchange
     */
    private void handleCORS(HttpExchange exchange) {
        // Allow requests from all origins
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        // Allow specific methods
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        // Allow specific headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "*");
        // Allow credentials, if needed
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        users.add(requestUser(exchange));
        saveUsers();
        String response = "{\"message\": \"User has been created successfully\"}";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private User requestUser(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        String dataString = "";
        String line;
        while ((line = reader.readLine()) != null) {
            dataString += line;
        }
        reader.close();
        User user = gson.fromJson(dataString, User.class);
        return user;
    }

    private void handleDeleteUser(HttpExchange exchange) throws IOException {
        System.out.println("delete user");
        User userToDelete = requestUser(exchange);
        boolean removed = users.removeIf(u -> u.getId() == userToDelete.getId());
        if (removed) {
            saveUsers();
            String response = "User has been deleted successfully";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleUpdateUser(HttpExchange exchange) throws IOException {
        User userToUpdate = requestUser(exchange);
        users.stream()
                .filter(u -> u.getId() == userToUpdate.getId())
                .findFirst()
                .map(existingUser -> {
                    users.set(users.indexOf(existingUser), userToUpdate);
                    return true;
                })
                .orElseGet(() -> {
                    users.add(userToUpdate);
                    return false;
                });
        saveUsers();
        String response = "User has been updated successfully";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleGetUsers(HttpExchange exchange) throws IOException {
        String response = gson.toJson(users);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleGetUserByid(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        long id = Long.parseLong(query.split("=")[1]);
        User user = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        if (user != null) {
            String response = gson.toJson(user);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }
}