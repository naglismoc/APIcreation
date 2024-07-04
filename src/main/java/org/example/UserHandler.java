package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.example.Main.*;

public class UserHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

//        System.out.println(path);
//        System.out.println(method);
        if(path.equals("/createUser") && method.equals("POST")) {// +
            handleCreateUser(exchange);
        }
        if(path.equals("/getUsers") && method.equals("GET")) {// +
            handleGetUsers(exchange);
        }
        if(path.equals("/getUser") && method.equals("GET")) {// +
            handleGetUserByid(exchange);
        }
        if(path.equals("/updateUser") && method.equals("POST")) {// +
            handleUpdateUser(exchange);
        }
        if(path.equals("/deleteUser") && method.equals("POST")) {// +
            handleDeleteUser(exchange);
        }

        exchange.sendResponseHeaders(400, -1);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        String query =exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);

        long id = Long.parseLong(params.get("id"));
        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        String avatar = params.get("avatar");
        String email = params.get("email");

        User user = new User(id, firstName, lastName, avatar, email);
        users.add(user);
        saveUsers();
        String response = "User has been created successfully";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleDeleteUser(HttpExchange exchange) throws IOException {
        String query =exchange.getRequestURI().getQuery();
        long id = Long.parseLong(query.split("=")[1]);
            boolean removed = users.removeIf(u -> u.getId() == id);

            if (removed) {
                saveUsers();
                String response = "User has been deleted successfully";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }else {
                exchange.sendResponseHeaders(404,-1);
            }
    }

    private void handleUpdateUser(HttpExchange exchange) throws IOException {
        String query =exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);

        long id = Long.parseLong(params.get("id"));
        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        String avatar = params.get("avatar");
        String email = params.get("email");

        User user = new User(id, firstName, lastName, avatar, email);
        users.stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst()
                .map(existingUser -> {
                    users.set(users.indexOf(existingUser), user);
                    return true;
                })
                .orElseGet(() -> {
                    users.add(user);
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
       // if(exchange.getRequestMethod().equals("GET")) {
            String response = gson.toJson(users);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
       // }else {
      //      exchange.sendResponseHeaders(405,-1);//method not allowed
        //}
    }
    private void handleGetUserByid(HttpExchange exchange) throws IOException {
       // if(exchange.getRequestMethod().equals("GET")) {
            String query =exchange.getRequestURI().getQuery();
            long id = Long.parseLong(query.split("=")[1]);
//            System.out.println(query);
            User user = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
            if(user != null) {
                String response = gson.toJson(user);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }else {
                exchange.sendResponseHeaders(404,-1);
            }


      //  }else {
        //    exchange.sendResponseHeaders(405,-1);//method not allowed
       // }
    }
    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            } else {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), "");
            }
        }
        return result;
    }
}
