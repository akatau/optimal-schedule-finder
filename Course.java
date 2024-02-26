import java.util.List;

public class Course {
    private String name;
    private List<String[]> timeSlots;

    public Course(String name, List<String[]> timeSlots) {
        setName(name);
        this.timeSlots = timeSlots;
    }







































































    

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name.trim();
    }

    public List<String[]> getTimeSlots() {
        return timeSlots;
    }
}