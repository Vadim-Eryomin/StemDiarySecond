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

        return notifyStatus("profile");
    }



    public static String notifyStatus(String mode){
        return new JSONObject().put("status", mode).toString();
    }
    public static String getFromJSONString(String json, String need){
        JSONObject obj = new JSONObject(json);
        return obj.has(need) ? (obj.get(need) + "") : null;
    }


}
