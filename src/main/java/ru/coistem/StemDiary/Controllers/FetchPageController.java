package ru.coistem.StemDiary.Controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import ru.coistem.StemDiary.Database;
import ru.coistem.StemDiary.JSONer;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            ResultSet set = Database.query("SELECT * FROM product");
            return JSONer.toJSONList(set, new String[]{"id", "cost", "quantity", "title", "about", "img"}).toString();
        }
        return null;
    }

    @PostMapping("/timetable")
    public String timetable(@RequestBody String data) throws SQLException {
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String id = getFromJSONString(data, "id");
            ResultSet role = Database.query("SELECT admin, teacher FROM role WHERE id = " + id);
            role.next();
            boolean admin = role.getBoolean("admin");
            boolean teacher = role.getBoolean("teacher");

            ResultSet set;
            if (admin) set = Database.query("SELECT course.id as id, course.name as course, course.img as courseimg, " +
                    "profile.name, profile.surname, profile.img as teacherimg FROM course " +
                    "JOIN profile ON profile.id = course.teacher");
            else if (teacher) set = Database.query("SELECT course.id as id, course.name as course, course.img as courseimg, " +
                    "profile.name, profile.surname, profile.img as teacherimg FROM course " +
                    "JOIN profile ON profile.id = course.teacher WHERE teacher = " + id);
            else set = Database.query(" SELECT course.id as id, course.name as course, course.img as courseimg, " +
                        "profile.name, profile.surname, profile.img as teacherimg FROM course " +
                        "JOIN profile ON profile.id = course.teacher " +
                        "WHERE course.id in (SELECT course FROM pupil WHERE pupil = "+id+")");
            return JSONer.toJSONList(set, new String[]{"id", "course", "courseimg", "name", "surname", "teacherimg"}).toString();
        }
        return null;
    }

    @PostMapping("/lesson")
    public String lesson(@RequestBody String data) throws SQLException {
        System.out.println(data);
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String id = getFromJSONString(data, "id");
            ResultSet role = Database.query("SELECT admin, teacher FROM role WHERE id = " + id);
            role.next();
            boolean admin = role.getBoolean("admin");
            boolean teacher = role.getBoolean("teacher");

            String date = getFromJSONString(data, "date");
            String firstDate = (String) JSONer.nextToJSON(Database.query("SELECT firstdate FROM course WHERE id = " + getFromJSONString(data, "course")), new String[]{"firstdate"}).get("firstdate");
            if (date == null)  date = (Long.parseLong(firstDate) + Math.ceil((new Date().getTime() - Long.parseLong((String) firstDate)) / 604800000.0) * 604800000) + "";
            System.out.println(date);
            System.out.println(new SimpleDateFormat("dd.MM.yyyy hh:mm").format(Double.parseDouble(date)));

            createLessonIfNotExists(getFromJSONString(data, "course"), date);
            ResultSet set = Database.query("SELECT course.id as course, lesson.id as lesson, " +
                    "course.name as coursename, course.img as courseimg, lesson.date, lesson.homework, " +
                    "profile.name as teachername, profile.surname as teachersurname, profile.img as teacherimg " +
                    "FROM course " +
                    "JOIN lesson on course.id = lesson.course " +
                    "JOIN profile on course.teacher = profile.id " +
                    "WHERE lesson.date = "+date+" and course.id = " + getFromJSONString(data, "course"));
            if (set.next()) {
                JSONObject obj = JSONer.toJSON(set, new String[]{"course", "courseimg", "coursename", "teachername",
                        "teachersurname", "teacherimg", "lesson", "date", "homework"})
                        .put("admin", admin).put("teacher", teacher);
                ResultSet pupil = Database.query("SELECT id, name, surname FROM profile WHERE id IN " +
                        "(SELECT pupil FROM pupil WHERE course = " + obj.get("course") + ")");
                JSONArray pupils = JSONer.toJSONList(pupil, new String[]{"id", "name", "surname"});
                obj.put("pupils", pupils);

                return obj.toString();
            }
        }
        return null;
    }

    @PostMapping("/mark")
    public String mark(@RequestBody String data) throws SQLException {
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String query = "SELECT course.id as course, course.name as coursename, " +
                    "pupil.pupil, profile.name, profile.surname, " +
                    getFromJSONString(data, "lesson") + " as lesson, " +
                    "coalesce(mark.a, 0) as a, coalesce(mark.b, 0) as b, " +
                    "coalesce(mark.c, 0) as c " +
                    "FROM course " +
                    "JOIN pupil ON pupil.course = course.id " +
                    "LEFT JOIN mark ON mark.pupil = pupil.pupil " +
                    "and mark.lesson = " + getFromJSONString(data, "lesson") +
                    "JOIN profile ON pupil.pupil = profile.id " +
                    "WHERE course.id = " + getFromJSONString(data, "course");
            ResultSet set = Database.query(query);
            System.out.println(query);
            JSONArray obj = JSONer.toJSONList(set, new String[]{"course", "coursename", "pupil", "name", "surname", "a", "b", "c", "lesson"});
            System.out.println(obj.toString());
            return obj.toString();
        }
        return null;
    }

    @PostMapping("/setmark")
    public String setmark(@RequestBody String data) throws SQLException {
        if (checkAuth(getFromJSONString(data, "login"), getFromJSONString(data, "password"))){
            String lesson = getFromJSONString(data, "lesson");
            String pupil = getFromJSONString(data, "pupil");
            String a = getFromJSONString(data, "a");
            String b = getFromJSONString(data, "b");
            String c = getFromJSONString(data, "c");

            if (Database.query("SELECT * FROM mark WHERE lesson = "+lesson+" and pupil = " + pupil).next()){
                Database.query("UPDATE mark SET a = " + a + ", b = " + b + ", c = " + c + " WHERE pupil = " + pupil + " and lesson = " + lesson);
            }
            else Database.query("INSERT INTO mark VALUES (default, "+lesson+", "+pupil+", "+a+", "+b+", "+c+")");
        }
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

    public static void createLessonIfNotExists(String course, String date) throws SQLException {
        if (Database.query("SELECT * FROM lesson WHERE course = " + course + " and date = " + date).next()) return;
        Database.query("INSERT INTO lesson VALUES(default, "+course+", "+date+", 'nothing')");
    }
}
