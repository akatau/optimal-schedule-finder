import java.time.LocalTime;
import java.time.DayOfWeek;


public class CourseSlot{
    private String courseName;
    DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public CourseSlot(String courseName, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.courseName = courseName.trim();
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean overlaps(CourseSlot otherSlot) {
        // Check if the day of the week and time range overlap
        return dayOfWeek == otherSlot.dayOfWeek &&
               (startTime.isBefore(otherSlot.endTime) && endTime.isAfter(otherSlot.startTime) ||
                otherSlot.startTime.isBefore(endTime) && otherSlot.endTime.isAfter(startTime) ||
                startTime.equals(otherSlot.startTime) || endTime.equals(otherSlot.endTime)
                );
    }


    @Override
    public String toString(){
        return '\n' + "Title: " + getCourseName() + '\n'
            + getDayOfWeek() + ": " + getStartTime() + " : "+ getEndTime() + '\n';
    }
}