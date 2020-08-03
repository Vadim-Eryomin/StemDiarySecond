package ru.coistem.StemDiary.Controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import ru.coistem.StemDiary.Database;
import ru.coistem.StemDiary.JSONer;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static ru.coistem.StemDiary.Controllers.WebSocketController.getFromJSONString;

@RestController
public class FetchPageController {
    @PostMapping("/auth")
    public String auth(@RequestBody String data) throws SQLException {
        String login = getFromJSONString(data, "login");
        String password = getFromJSONString(data, "password");
        ResultSet set = Database.query("select * from (select * from login where login='"+login+"' and password='"+password+"') as L join profile on L.id = profile.id");
        if (set.next()){
            JSONObject obj = JSONer.toJSON(set, new String[]{"id", "login", "password", "name", "surname", "img", "coins"});
            return obj.put("auth", true).toString();
        }
        return new JSONObject().put("auth", false).toString();
    }

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
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String id = getFromJSONString(data, "id");
            String query = "select basket.id, basket.status, product.cost, product.quantity, " +
                    "product.title, product.about, product.img " +
                    "from basket " +
                    "join product on basket.product = product.id " +
                    "where basket.profile = "+id+" " +
                    "union all " +
                    "(select uncon.id, uncon.status, product.cost, product.quantity, " +
                    "product.title, product.about, product.img " +
                    "from uncon " +
                    "join product on uncon.product = product.id " +
                    "where profile = "+id+");";
            ResultSet product = Database.query(query);
            return JSONer.toJSONList(product, new String[]{"id", "cost", "quantity", "title", "about", "img", "status"}).toString();
        }
        return null;
    }

    @PostMapping("/buy")
    public String buy(@RequestBody String data) throws SQLException {
        System.out.println(data);
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            if (Integer.parseInt(Objects.requireNonNull(getFromJSONString(data, "coins"))) >=
                    Integer.parseInt(Objects.requireNonNull(getFromJSONString(data, "cost")))){

                String id = getFromJSONString(data, "id");
                String buy = getFromJSONString(data, "buy");
                String query = "insert into uncon values(default, "+id+", "+buy+", 'not')";
                Database.query(query);
                return new JSONObject().put("correct", "true").toString();
            }
            else return new JSONObject().put("correct", "not enough").toString();
        }
        return new JSONObject().put("correct", "whoami").toString();
    }

    @PostMapping("/confirm")
    public String confirm(@RequestBody String data) throws SQLException {
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String confirm = getFromJSONString(data, "confirm");
            Database.query("insert into basket (select * from uncon where id = "+confirm+")");
            Database.query("update basket set status='didnt read' where id = "+confirm+"");
            Database.query("delete from uncon where id = "+confirm);
        }
        return null;
    }

    @PostMapping("/decline")
    public String decline(@RequestBody String data) throws SQLException {
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String decline = getFromJSONString(data, "decline");
            Database.query("delete from uncon where id = "+decline);
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
