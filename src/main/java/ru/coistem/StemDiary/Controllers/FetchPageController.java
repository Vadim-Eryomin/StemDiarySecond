package ru.coistem.StemDiary.Controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import ru.coistem.StemDiary.Database;
import ru.coistem.StemDiary.JSONer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.coistem.StemDiary.Controllers.WebSocketController.getFromJSONString;

@RestController
public class FetchPageController {
    @PostMapping("/auth")
    public String auth(@RequestBody String data) throws SQLException {
        String login = getFromJSONString(data, "login");
        String password = getFromJSONString(data, "password");
        ResultSet set = Database.query("select * from (select * from login where login='"+login+"' and password='"+password+"') as L join profile on L.id = profile.id");
        if (set.next()){
            JSONObject obj = JSONer.toJSON(set, new String[]{"id", "login", "password", "name", "surname", "img"});
            return obj.put("auth", true).toString();
        }
        return new JSONObject().put("auth", false).toString();
    }

    @PostMapping("/profile")
    public String profile(){


        return null;
    }
    // TODO: 31.07.2020 развести auth и profile

    @PostMapping("/shop")
    public String shop(@RequestBody String data) throws SQLException {
        System.out.println(data);
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            ResultSet set = Database.query("SELECT * FROM product");
            return JSONer.toJSONList(set, new String[]{"id", "cost", "quantity", "title", "about", "img"}).toString();
        }
        return null;
    }

    @PostMapping("/timetable")
    public String timetable(){
        return null;
    }

    @PostMapping("/lesson")
    public String lesson(){
        return null;
    }

    @PostMapping("/basket")
    public String basket(@RequestBody String data) throws SQLException {
        System.out.println(data);
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String id = getFromJSONString(data, "id");
            String query = "select *, " +
                    "(select status from basket where id = "+id+")" +
                    "from product where id in " +
                    "(select product from basket where id = "+id+")" +
                    "union all " +
                    "(select *, 'not' as status from product where id in " +
                    "(select product from uncon where id = "+id+"))";
            ResultSet product = Database.query(query);
            return JSONer.toJSONList(product, new String[]{"id", "cost", "quantity", "title", "about", "img", "status"}).toString();
        }
        return null;
    }

    @PostMapping("/admin")
    public String admin(){
        return null;
    }

    @PostMapping("/admintimetable")
    public String adminTimetable(){
        return null;
    }

    @PostMapping("/adminprofile")
    public String adminProfile(){
        return null;
    }

    @PostMapping("/adminshop")
    public String adminShop(){
        return null;
    }

    @PostMapping("/adminbasket")
    public String adminBasket(){
        return null;
    }

    public static boolean checkAuth(String login, String password) throws SQLException {
        return Database.query("SELECT * FROM login where login = '"+login+"' and password = '"+password+"'").next();
    }
}
