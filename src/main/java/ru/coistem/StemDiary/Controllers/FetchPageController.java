package ru.coistem.StemDiary.Controllers;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import ru.coistem.StemDiary.Database;
import ru.coistem.StemDiary.JSONer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.coistem.StemDiary.Controllers.WebSocketController.getFromJSONString;

@RestController
public class FetchPageController {
    @GetMapping("/profile")
    public String profile() throws SQLException {
        ResultSet set = Database.query("SELECT * FROM login");
        set.next();
        return JSONer.toJSONString(set, new String[]{"login", "password"});
    }

    @PostMapping("/auth")
    public String auth(@RequestBody String data) throws SQLException {
        String login = getFromJSONString(data, "login");
        String password = getFromJSONString(data, "password");
        ResultSet set = Database.query("select * from (select * from login where login='"+login+"' and password='"+password+"') as L join profile on L.id = profile.id");
        if (set.next()){
            JSONObject obj = JSONer.toJSON(set, new String[]{"login", "password", "name", "surname", "img"});
            return obj.put("auth", true).toString();
        }
        return new JSONObject().put("auth", false).toString();
    }
}
