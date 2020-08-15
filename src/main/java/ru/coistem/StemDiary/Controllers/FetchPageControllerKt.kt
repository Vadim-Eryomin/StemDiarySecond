package ru.coistem.StemDiary.Controllers

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.coistem.StemDiary.Database
import java.util.*
import kotlin.math.ceil
import java.sql.ResultSet as ResultSet

@RestController
class FetchPageControllerKt {
    private val defaultHomework: String = "nothing"

    @PostMapping("/auth")
    fun auth(@RequestBody data: String) = query("auth", data).jsonStringFirst()

    @PostMapping("/shop")
    fun shop(@RequestBody data: String) =
            if (checkAuth(data)) query("shop", data).jsonString()
            else "{}"

    @PostMapping("/timetable")
    fun timetable(@RequestBody data: String): String {
        if (checkAuth(data)){
            val role = query("role", data).jsonFirst()
            return when {
                role.getBoolean("admin") -> query("timetableAdmin", data).jsonString()
                role.getBoolean("teacher") -> query("timetableTeacher", data).jsonString()
                else -> query("timetable", data).jsonString()
            }
        }
        return "{}"
    }

    @PostMapping("/lesson")
    fun lesson(@RequestBody data: String): String {
        if (checkAuth(data)){
            val role = query("role", data).jsonFirst()
            val firstDate: String = query("firstDate", data).jsonFirst().get("firstdate").toString()
            var date: String = data.jsonString("date")
            if (date == "" || date == "return") date = (firstDate.toLong() + ceil((Date().time - firstDate.toLong()) / 604800000.0) * 604800000).toString()
            createLessonIfNotExists(data.jsonString("course"), date)
            val innerData = JSONObject(data).put("date", date).toString()
            val set = query("lesson", innerData)
            return set.jsonFirst().put("pupils", query("pupils", innerData).json()).toString()
        }
        return "{}"
    }

    @PostMapping("/mark")
    fun mark(@RequestBody data: String) =
            if (checkAuth(data) && isAdminOrTeacher(query("role", data).jsonFirst())) query("mark", data).jsonString()
            else  "{}"


    @PostMapping("/setmark")
    fun setMark(@RequestBody data: String){
        if (checkAuth(data) && isAdminOrTeacher(query("role", data).jsonFirst()))
            saveMark(data)
    }

    @PostMapping("/homework")
    fun homework(@RequestBody data: String) =
        if (checkAuth(data) && isAdminOrTeacher(query("role", data).jsonFirst())) query("homework", data).jsonStringFirst()
        else "{}"

    @PostMapping("/sethomework")
    fun setHomework(@RequestBody data: String){
        if (checkAuth(data) && isAdminOrTeacher(query("role", data).jsonFirst()))
            query("sethomework", data)
    }

    @PostMapping("/basket")
    fun basket(@RequestBody data: String) =
        if (checkAuth(data)) query("basket", data).jsonString()
        else "{}"

    @PostMapping("/buy")
    fun buy(@RequestBody data: String): String{
        if (checkAuth(data)) {
            return if (data.jsonString("coins").toInt() >= data.jsonString("cost").toInt()) {
                query("buy", data)
                JSONObject().put("correct", "true").toString()
            } else JSONObject().put("correct", "false").toString()
        }
        return "{}"
    }

    @PostMapping("/confirm")
    fun confirm(@RequestBody data: String){
        if (checkAuth(data))
            query("confirm", data)
    }

    @PostMapping("/decline")
    fun decline(@RequestBody data: String){
        if(checkAuth(data))
            query("decline", data)
    }

    @PostMapping("/admintimetable")
    fun adminTimetable(@RequestBody data: String) =
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            query("adminTimetable", data).jsonString()
        else "{}"

    @PostMapping("/adminprofile")
    fun adminProfile(@RequestBody data: String) =
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            query("adminProfile", data).jsonString()
        else ""

    @PostMapping("/adminshop")
    fun adminShop(@RequestBody data: String) =
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            query("adminShop", data).jsonString()
        else "{}"

    @PostMapping("/adminbasket")
    fun adminBasket(@RequestBody data: String) =
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            query("adminBasket", data).jsonString()
        else "{}"

    @PostMapping("/saveprofile")
    fun saveProfile(@RequestBody data: String){
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            adminSaveProfile(data)
    }

    @PostMapping("/savebasket")
    fun saveBasket(@RequestBody data: String){
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            query("saveBasket", data)
    }

    @PostMapping("/saveshop")
    fun saveShop(@RequestBody data: String){
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            adminSaveShop(data)
    }

    @PostMapping("/teachers")
    fun teachers(@RequestBody data: String): String{
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            return query("teachers", data).jsonString()
        return "{}"
    }

    @PostMapping("/savetimetable")
    fun saveTimetable(@RequestBody data: String): String{
        if (checkAuth(data) && query("role", data).jsonFirst().getBoolean("admin"))
            adminSaveTimetable(data)
        return "{}"
    }

    private fun query(method: String, data: String): ResultSet {
        return Database.query(when (method) {
            "checkAuth"         -> "SELECT * FROM login WHERE login = '${data.jsonString("login")}' and password = '${data.jsonString("password")}'"
            "role"              -> "SELECT role.admin, role.teacher FROM role JOIN login on login.id = role.id WHERE login.login = '${data.jsonString("login")}' and login.password = '${data.jsonString("password")}'"
            "auth"              -> "SELECT *, 't' as auth FROM login JOIN profile on login.id = profile.id WHERE login.login = '${data.jsonString("login")}' and password = '${data.jsonString("password")}'"
            "shop"              -> "SELECT * FROM product"
            "timetableAdmin"    -> "SELECT course.id as id, course.name as course, course.img as courseimg, profile.name, profile.surname, profile.img as teacherimg FROM course JOIN profile ON profile.id = course.teacher"
            "timetableTeacher"  -> "SELECT course.id as id, course.name as course, course.img as courseimg, profile.name, profile.surname, profile.img as teacherimg FROM course JOIN profile ON profile.id = course.teacher WHERE teacher = " + data.jsonString("id")
            "timetable"         -> "SELECT course.id as id, course.name as course, course.img as courseimg, profile.name, profile.surname, profile.img as teacherimg FROM course JOIN profile ON profile.id = course.teacher WHERE course.id in (SELECT course FROM pupil WHERE pupil = ${data.jsonString("id")})"
            "firstDate"         -> "SELECT firstdate FROM course WHERE id = '${data.jsonString("course")}'"
            "lesson"            -> "SELECT course.id as course, lesson.id as lesson, course.name as coursename, course.img as courseimg, lesson.date, lesson.homework, profile.name as teachername, profile.surname as teachersurname, profile.img as teacherimg FROM course JOIN lesson on course.id = lesson.course JOIN profile on course.teacher = profile.id WHERE lesson.date = ${data.jsonString("date")} and course.id = ${data.jsonString("course")}"
            "hasLesson"         -> "SELECT * FROM lesson WHERE course = '${data.jsonString("id")}' and date = ${data.jsonString("date")}"
            "createLesson"      -> "INSERT INTO lesson VALUES(default, ${data.jsonString("id")}, ${data.jsonString("date")}, '$defaultHomework')"
            "pupils"            -> "SELECT id, name, surname FROM profile WHERE id IN (SELECT pupil FROM pupil WHERE course = ${data.jsonString("course")})"
            "mark"              -> "SELECT course.id as course, course.name as coursename, pupil.pupil, profile.name, profile.surname, ${data.jsonString("lesson")} as lesson, coalesce(mark.a, 0) as a, coalesce(mark.b, 0) as b, coalesce(mark.c, 0) as c FROM course JOIN pupil ON pupil.course = course.id LEFT JOIN mark ON mark.pupil = pupil.pupil and mark.lesson = ${data.jsonString("lesson")} JOIN profile ON pupil.pupil = profile.id WHERE course.id = ${data.jsonString("course")}"
            "getmark"           -> "SELECT * FROM mark WHERE lesson = ${data.jsonString("lesson")} and pupil = ${data.jsonString("pupil")}"
            "insertmark"        -> "INSERT INTO mark VALUES (default, ${data.jsonString("lesson")}, ${data.jsonString("pupil")}, ${data.jsonString("a")}, ${data.jsonString("b")}, ${data.jsonString("c")})"
            "updatemark"        -> "UPDATE mark SET a = ${data.jsonString("a")}, b = ${data.jsonString("b")}, c = ${data.jsonString("c")} WHERE pupil = ${data.jsonString("pupil")} and lesson = ${data.jsonString("lesson")}"
            "homework"          -> "SELECT * FROM lesson WHERE id = ${data.jsonString("lesson")}"
            "sethomework"       -> "UPDATE lesson SET homework = '${data.jsonString("homework")}' WHERE id = ${data.jsonString("lesson")}"
            "basket"            -> "SELECT basket.id, basket.status, product.cost, product.quantity, product.title, product.about, product.img FROM basket JOIN product on basket.product = product.id WHERE basket.profile = ${data.jsonString("id")} UNION ALL (SELECT uncon.id, uncon.status, product.cost, product.quantity, product.title, product.about, product.img FROM uncon JOIN product on uncon.product = product.id WHERE profile = ${data.jsonString("id")})"
            "buy"               -> "INSERT INTO uncon values(default, ${data.jsonString("id")}, ${data.jsonString("buy")}, 'not')"
            "confirm"           -> "INSERT INTO basket (SELECT * FROM uncon WHERE id = ${data.jsonString("confirm")});  UPDATE basket SET status='didnt read' WHERE id = ${data.jsonString("confirm")};  DELETE FROM uncon WHERE id = ${data.jsonString("confirm")}"
            "decline"           -> "DELETE FROM uncon WHERE id = ${data.jsonString("decline")}"
            "adminTimetable"    -> "SELECT course.id, course.name as coursename, course.img as courseimg, course.firstdate as coursefirstdate, profile.id as teacher, profile.name, profile.surname, profile.img FROM course JOIN profile on profile.id = course.teacher"
            "adminProfile"      -> "SELECT profile.id, profile.name, profile.surname, profile.img, role.admin, role.teacher, login.login, login.password, profile.coins FROM profile RIGHT JOIN login ON login.id = profile.id LEFT JOIN role ON role.id = profile.id"
            "adminShop"         -> "SELECT * FROM product"
            "adminBasket"       -> "SELECT basket.id, basket.status, product.title, product.img, profile.name, profile.surname FROM basket JOIN product ON product.id = basket.product JOIN profile ON profile.id = basket.profile"
            "insertProfile"     -> "INSERT INTO login VALUES(default, '${data.jsonString("savelogin")}', '${data.jsonString("savepassword")}'); INSERT INTO profile VALUES((SELECT id FROM login WHERE login = '${data.jsonString("savelogin")}' and password = '${data.jsonString("savepassword")}'), '${data.jsonString("savename")}',  '${data.jsonString("savesurname")}', '${data.jsonString("saveimg")}', ${data.jsonString("savecoins")});  INSERT INTO role VALUES((SELECT id FROM login WHERE login = '${data.jsonString("savelogin")}' and password = '${data.jsonString("savepassword")}'), '${data.jsonString("saveadmin")}', '${data.jsonString("saveteacher")}')"
            "updateProfile"     -> "UPDATE profile SET name = '${data.jsonString("savename")}', surname = '${data.jsonString("savesurname")}', img = '${data.jsonString("saveimg")}', coins = ${data.jsonString("savecoins")} WHERE id = ${data.jsonString("saveid")};  UPDATE login SET login = '${data.jsonString("savelogin")}', password = '${data.jsonString("savepassword")}' WHERE id = ${data.jsonString("saveid")}; UPDATE role SET teacher = '${data.jsonString("saveteacher")}', admin = '${data.jsonString("saveadmin")}' WHERE id = ${data.jsonString("saveid")}"
            "saveBasket"        -> "UPDATE basket SET status = '${data.jsonString("savestatus")}' WHERE id = ${data.jsonString("saveid")}"
            "insertShop"        -> "INSERT INTO product(id, title, img, about, cost, quantity) VALUES(default, '${data.jsonString("savetitle")}', '${data.jsonString("saveimg")}', '${data.jsonString("saveabout")}', ${data.jsonString("savecost")}, ${data.jsonString("savequantity")})"
            "updateShop"        -> "UPDATE product SET title = '${data.jsonString("savetitle")}', img = '${data.jsonString("saveimg")}', about = '${data.jsonString("saveabout")}', cost = ${data.jsonString("savecost")}, quantity = '${data.jsonString("savequantity")}' WHERE id = ${data.jsonString("saveid")}"
            "teachers"          -> "SELECT profile.id, profile.name, profile.surname, profile.img FROM profile JOIN role on role.id = profile.id WHERE role.teacher = 't'"
            "insertTimetable"   -> "INSERT INTO product(id, title, img, about, cost, quantity) VALUES(default, '${data.jsonString("savetitle")}', '${data.jsonString("saveimg")}', '${data.jsonString("saveabout")}', ${data.jsonString("savecost")}, ${data.jsonString("savequantity")})"
            "updateTimetable"   -> "UPDATE course SET name = '${data.jsonString("savename")}', img = '${data.jsonString("saveimg")}', firstdate = ${data.jsonString("savedate")}, teacher = '${data.jsonString("saveteacher")}' WHERE id = ${data.jsonString("saveid")}"
            else                -> "SELECT 't' as nothing"
        }) ?: Database.query("SELECT 't' as nothing")
    }

    private fun checkAuth(data: String) = query("checkAuth", data).next()
    private fun String.jsonString(need: String): String {
        val clean = listOf("\\", "'", "(", ")", ";", "<", ">")
        var data = this
        for (char in clean){
            val split = data.split(char)
            data = ""
            for (elem in split)
                data += elem
        }
        val obj = JSONObject(data)
        if (obj.has(need)){
            if (obj.get(need) == ""){
                return "return"
            }
            else return obj.get(need).toString()
        }
        return "default"
    }
    private fun ResultSet.jsonString(): String {
        val meta = this.metaData
        val list = JSONArray()
        while (next()) {
            val obj = JSONObject()
            for (i in 1..meta.columnCount)
                obj.put(meta.getColumnLabel(i), this.getObject(meta.getColumnLabel(i)))
            list.put(obj)
        }
        return list.toString();
    }
    private fun ResultSet.json(): JSONArray {
        val meta = this.metaData
        val list = JSONArray()
        while (next()) {
            val obj = JSONObject()
            for (i in 1..meta.columnCount)
                obj.put(meta.getColumnLabel(i), this.getObject(meta.getColumnLabel(i)))
            list.put(obj)
        }
        return list;
    }
    private fun ResultSet.jsonStringFirst(): String {
        val meta = this.metaData
        next()
        val obj = JSONObject()
        for (i in 1..meta.columnCount)
            obj.put(meta.getColumnLabel(i), this.getObject(meta.getColumnLabel(i)))

        return obj.toString();
    }
    private fun ResultSet.jsonFirst(): JSONObject {
        val meta = this.metaData
        next()
        val obj = JSONObject()
        for (i in 1..meta.columnCount)
            obj.put(meta.getColumnLabel(i), this.getObject(meta.getColumnLabel(i)))

        return obj;
    }
    private fun createLessonIfNotExists(id: String, date: String){
        val data = JSONObject().put("id", id).put("date", date).toString()
        if (query("hasLesson", data).next()) return
        query("createLesson", data)
    }
    private fun saveMark(data: String){
        if (query("getmark", data).next())
            query("updatemark", data)
        else
            query("insertmark", data)
    }
    private fun adminSaveProfile(data: String){
        if (data.jsonString("saveid") == "default")
            query("insertProfile", data)
        else query("updateProfile", data)
    }
    private fun adminSaveShop(data: String){
        if (data.jsonString("saveid") == "default")
            query("insertShop", data)
        else query("updateShop", data)
    }
    private fun adminSaveTimetable(data: String){
        println(data)
        if (data.jsonString("saveid") == "default")
            query("insertTimetable", data)
        else query("updateTimetable", data)
    }
    private fun isAdminOrTeacher(data: JSONObject) = data.getBoolean("admin") || data.getBoolean("teacher")
}