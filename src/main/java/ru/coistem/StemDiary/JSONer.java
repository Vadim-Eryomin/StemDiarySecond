package ru.coistem.StemDiary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JSONer {
    public static String toJSONString(ResultSet set, String[] props) throws SQLException {
        String json = "";
        JSONObject object = new JSONObject();
        for (String s : props) object.put(s, set.getString(s));
        json = object.toString();
        return json;
    }

    public static JSONObject toJSON(ResultSet set, String[] props) throws SQLException {
        JSONObject object = new JSONObject();
        for (String s : props) object.put(s, set.getString(s));
        return object;
    }

    public static JSONArray toJSONList(ResultSet set, String[] props) throws SQLException {
        JSONArray array = new JSONArray();
        while (set.next()){
            array.put(toJSON(set, props));
        }
        return array;
    }
}
