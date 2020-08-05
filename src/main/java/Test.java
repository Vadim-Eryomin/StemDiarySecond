import java.util.Calendar;
import java.util.GregorianCalendar;

public class Test {
    public static void main(String[] args) {
        GregorianCalendar calendar = new GregorianCalendar();
        GregorianCalendar calendar1 = new GregorianCalendar();
        calendar1.add(Calendar.WEEK_OF_YEAR, 1);
        System.out.println(calendar1.getTime().getTime() - calendar.getTime().getTime());

    }
}
