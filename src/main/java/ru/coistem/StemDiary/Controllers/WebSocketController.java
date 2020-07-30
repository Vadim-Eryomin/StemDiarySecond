package ru.coistem.StemDiary.Controllers;

import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.coistem.StemDiary.Database;

import javax.xml.crypto.Data;
import java.sql.SQLException;

@Controller
public class WebSocketController {
    @MessageMapping("/profile")
    @SendTo("/api/status")
    public String setProfile(String data) throws SQLException {
        String id = getFromJSONString(data, "id");
        String login = getFromJSONString(data, "login");
        String password = getFromJSONString(data, "password");
        String name = getFromJSONString(data, "name");
        String surname = getFromJSONString(data, "surname");
        String img = getFromJSONString(data, "img");

        if (id != null && Database.query("SELECT * FROM login WHERE id = " + id).next()){
            Database.query("UPDATE login SET login = '" + login + "', password = '" + password + "' WHERE id = " + id);
            Database.query("UPDATE profile SET name = '" + name + "', surname = '" + surname + "', img = '" + img + "' WHERE id = " + id);
        }
        else {
            Database.query("INSERT INTO login(id, login, password) VALUES(DEFAULT, '" + login + "', '" + password + "')");
//            Database.query("INSERT INTO password VALUES(null, '" + name + "', '" + surname + "', '" + img + "')");
        }
        return notifyStatus("profile");
    }



    public static String notifyStatus(String mode){
        return new JSONObject().put("status", mode).toString();
    }
    public static String getFromJSONString(String json, String need){
        JSONObject obj = new JSONObject(json);
        return obj.has(need) ? obj.getString(need) : null;
    }


}
