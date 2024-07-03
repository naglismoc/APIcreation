package org.example;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<User> users = new ArrayList<>();
    public static Gson gson = new Gson();
    public static void main(String[] args) throws IOException {
        loadUsers();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000),0);
        server.createContext("/createUser",new UserHandler());
        server.createContext("/getUsers",new UserHandler());
        server.createContext("/getUser",new UserHandler());
        server.createContext("/updateUser",new UserHandler());
        server.createContext("/deleteUser",new UserHandler());

        server.setExecutor(null);
        server.start();
    }



    public static void loadUsers(){
        try(FileReader reader = new FileReader("users.json")){
            // Parse the JSON file
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            // Iterate through the JSON array
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Extract fields from JSON object
                long id = jsonObject.get("id").getAsLong();
                String name = jsonObject.get("firstName").getAsString();
                String surname = jsonObject.get("lastName").getAsString();
                String avatar = jsonObject.get("avatar").getAsString();
                String email = jsonObject.get("email").getAsString();
//                // Create User object and add to list
                User user = new User();
                user.setId(id);
                user.setFirstName(name);
                user.setLastName(surname);
                user.setEmail(email);
                user.setAvatar(avatar);
                users.add(user);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void saveUsers(){
        try (FileWriter writer = new FileWriter("users.json")){
            gson.toJson(users,writer);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}