import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

public class ScheduleGenerator {


    public static List<Course> expandCoursesWithSections(List<Course> courses) {
        List<Course> expandedCourses = new ArrayList<>();
        for (Course course : courses) {
            if (course.getName().endsWith("Lab") || course.getName().endsWith("Tutorial")) {
                if (course.getTimeSlots().size() >= 1) {
                    for (int i = 0; i < course.getTimeSlots().size(); i++) {
                        String newCourseName = course.getName() + " Section " + (i + 1);
                        // System.out.println(course.getName());
                        expandedCourses.add(new Course(newCourseName, Collections.singletonList(course.getTimeSlots().get(i))));
                    }
                } else {
                    expandedCourses.add(course);
                }
            } else {
                expandedCourses.add(course);
            }
        }
        return expandedCourses;
    }

    public static String properName(Course course, int sectionOrGroupNumber){
        if(course.getName().contains("Lecture")){
            return course.getName() + " Group " + sectionOrGroupNumber;
        }
        else{
            return course.getName() + " Section " + sectionOrGroupNumber;
        }
    }

    public static int timeSpentPerWeek(List<CourseSlot> shcedule){
        int time = 0;

        LocalTime sunStartTime = LocalTime.of(4, 0);
        LocalTime sunEndTime = LocalTime.of(9, 0);
        LocalTime mondStartTime = LocalTime.of(4, 0);
        LocalTime mondEndTime = LocalTime.of(9, 0);
        LocalTime tuesStartTime = LocalTime.of(4, 0);
        LocalTime tuesEndTime = LocalTime.of(9, 0);
        LocalTime wednesdayStartTime = LocalTime.of(4, 0);
        LocalTime wednesdayEndTime = LocalTime.of(9, 0);
        LocalTime thursStartTime = LocalTime.of(4, 0);
        LocalTime thursEndTime = LocalTime.of(9, 0);

        for(CourseSlot slot: shcedule){
            if(slot.getDayOfWeek() == DayOfWeek.valueOf("SUNDAY")){
                sunStartTime = slot.getStartTime().isBefore(sunStartTime) ? slot.getStartTime(): sunStartTime;
                sunEndTime = slot.getEndTime().isAfter(sunEndTime) ? slot.getEndTime(): sunEndTime;
            }
            if(slot.getDayOfWeek() ==DayOfWeek.valueOf("MONDAY")){
                mondStartTime = slot.getStartTime().isBefore(mondStartTime) ? slot.getStartTime(): mondStartTime;
                mondEndTime = slot.getEndTime().isAfter(mondEndTime) ? slot.getEndTime(): mondEndTime;
                
            }
            if(slot.getDayOfWeek() ==DayOfWeek.valueOf("TUESDAY")){
                tuesStartTime = slot.getStartTime().isBefore(tuesStartTime) ? slot.getStartTime(): tuesStartTime;
                tuesEndTime = slot.getEndTime().isAfter(tuesEndTime) ? slot.getEndTime(): tuesEndTime;
            }
            if(slot.getDayOfWeek() ==DayOfWeek.valueOf("WEDNESDAY")){
                wednesdayStartTime = slot.getStartTime().isBefore(wednesdayStartTime) ? slot.getStartTime(): wednesdayStartTime;
                wednesdayEndTime = slot.getEndTime().isAfter(wednesdayEndTime) ? slot.getEndTime(): wednesdayEndTime;
            }
            if(slot.getDayOfWeek() == DayOfWeek.valueOf("THURSDAY")){
                thursStartTime = slot.getStartTime().isBefore(thursStartTime) ? slot.getStartTime(): thursStartTime;
                thursEndTime = slot.getEndTime().isAfter(thursEndTime) ? slot.getEndTime(): thursEndTime;
            }
        }

        time += sunEndTime.toSecondOfDay() - sunStartTime.toSecondOfDay();
        time += mondEndTime.toSecondOfDay() - mondStartTime.toSecondOfDay();
        time += tuesEndTime.toSecondOfDay() - tuesStartTime.toSecondOfDay();
        time += wednesdayEndTime.toSecondOfDay() - wednesdayStartTime.toSecondOfDay();
        time += thursEndTime.toSecondOfDay() - thursStartTime.toSecondOfDay();

        return time;
    }

    public static List<List<Course>> sectionSpecificCombinations(List<Course> courses){
        int k = courses.size();
        List<List<Course>> possibleCombinations = new ArrayList<>();
        
        if (k == 1){
            for(int i = 0; i < courses.get(0).getTimeSlots().size(); i++){
                Course currentCourse = new Course(properName(courses.get(0), i+1),           //courses.get(0).getName(), 
                                                  Collections.singletonList(courses.get(0).getTimeSlots().get(i)));
            possibleCombinations.add(Collections.singletonList(currentCourse));
            }
        }
        else{
            List<List<Course>> possiblePathesAhead = sectionSpecificCombinations(courses.subList(0+1, k));
            for(int ii = 0; ii < courses.get(0).getTimeSlots().size(); ii++){
                Course currentCourse = new Course(properName(courses.get(0), ii+1), 
                                                  Collections.singletonList(courses.get(0).getTimeSlots().get(ii)));


                List<List<Course>> newPathesAhead = new ArrayList<>();
                for (List<Course> path : possiblePathesAhead) {
                    newPathesAhead.add(new ArrayList<>(path)); // Create a modifiable copy
                    newPathesAhead.get(newPathesAhead.size() - 1).add(currentCourse); // Modify the copy
                }

                possibleCombinations.addAll(newPathesAhead);
            }
        }
        return possibleCombinations;
    }


    public static String getBaseName(CourseSlot slot){
        return slot.getCourseName().substring(0, " section 2".length());
    }
    public static String getBaseName(String courseName){
        return courseName.substring(0, " section 2".length());
    }


    public static boolean filterForSameSectionPerCourse (List<CourseSlot> schedule)
    {
        boolean result = true;
        Iterator<CourseSlot> tutorialIterator = schedule.stream().filter(course -> course.getCourseName().contains("Tutorial")).iterator();
        
        while(tutorialIterator.hasNext()){
            Iterator<CourseSlot> labIterator = schedule.stream().filter(course -> course.getCourseName().contains("Lab")).iterator();
            String tutorialFullName = tutorialIterator.next().getCourseName();
            String tutorialName = getBaseName(tutorialFullName);
            String coreqCourseName1 = tutorialName.split(":")[0];

            while(labIterator.hasNext()){
                String labFullName = labIterator.next().getCourseName();
                String labName = getBaseName(labFullName);
                String coreqCourseName2 = labName.split(":")[0];
                if(coreqCourseName1.equals(coreqCourseName2)){
                    result = result && (labFullName.charAt(labFullName.length()-1) == tutorialFullName.charAt(tutorialFullName.length()-1));
                } 
            }
        }
        return result;
    }
    
    public static List<List<CourseSlot>> generateSchedules(List<Course> courses) {
        List<Course> expandedCourses = expandCoursesWithSections(courses);
        List<List<CourseSlot>> schedules = new ArrayList<>();
        generateSchedulesHelper(schedules, expandedCourses, new ArrayList<>(), 0);
        return schedules;
    }

    private static void generateSchedulesHelper(List<List<CourseSlot>> schedules,
            List<Course> courses, List<CourseSlot> currentSchedule, int courseIndex) {
        if (courseIndex == courses.size()) {
            schedules.add(new ArrayList<>(currentSchedule));
            return;
        }

        Course course = courses.get(courseIndex);
        // Check if this course is a lab or tutorial
        boolean isLabOrTutorial = course.getName().endsWith("Lab") || course.getName().endsWith("Tutorial");

        for (String[] timeslot : course.getTimeSlots()) {
            CourseSlot slot = new CourseSlot(course.getName(), DayOfWeek.valueOf(timeslot[0]),
                    LocalTime.parse(timeslot[1]), LocalTime.parse(timeslot[2]));
            // Check for conflicts
            if (!hasConflict(currentSchedule, slot)) {
                currentSchedule.add(slot);
                // If this is a lab or tutorial, we don't increment the courseIndex as we
                // explore all sections
                int nextCourseIndex = isLabOrTutorial ? courseIndex : courseIndex + 1;
                generateSchedulesHelper(schedules, courses, currentSchedule, nextCourseIndex);
                currentSchedule.remove(currentSchedule.size() - 1); // Backtrack
            }
        }
    }

    private static boolean hasConflict(List<CourseSlot> schedule, CourseSlot newSlot) {
        for (CourseSlot slot : schedule) {
            if (slot.overlaps(newSlot)) {
                return true;
            }
        }
        return false;
    }


    public static void printTimetable(List<CourseSlot> schedule) {
        // Map days of week to empty lists for storing course slots
        HashMap<DayOfWeek, List<CourseSlot>> dayMap = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayMap.put(day, new ArrayList<>());
        }

        // Add each course slot to its corresponding day list
        for (CourseSlot slot : schedule) {
            dayMap.get(slot.getDayOfWeek()).add(slot);
        }

        // Calculate the maximum course name length and find longest day of week name
        int maxCourseNameLength = 0;
        int longestDayOfWeekLength = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            maxCourseNameLength = Math.max(maxCourseNameLength,
                    dayMap.get(day).stream().map(CourseSlot::getCourseName).mapToInt(String::length).max().orElse(0));
            longestDayOfWeekLength = Math.max(longestDayOfWeekLength, day.toString().length());
        }

        // Calculate total cell width based on longest day of week and course name
        int totalCellWidth = Math.max(longestDayOfWeekLength + 2, maxCourseNameLength + 2);

        // Header row with days of the week
        System.out.print("    ");
        for (DayOfWeek day : dayMap.keySet()) {
            System.out.printf("| %-" + totalCellWidth + "s", day.toString());
        }
        System.out.println(" |");

        // Time slots
        for (int hour = 9; hour <= 15; hour++) {
            System.out.printf("%2d:00", hour);
            for (DayOfWeek day : dayMap.keySet()) {
                List<CourseSlot> daySlots = dayMap.get(day);
                boolean foundSlot = false;
                for (CourseSlot slot : daySlots) {
                    if (slot.getStartTime().getHour() == hour) {
                        // Format course name to fit max length
                        String formattedName = String.format("%-" + maxCourseNameLength + "s", slot.getCourseName());
                        System.out.printf("| %-" + totalCellWidth + "s", formattedName);
                        foundSlot = true;
                        // break;
                    }
                }
                if (!foundSlot) {
                    // Use same width for empty cells
                    System.out.printf("| %-" + totalCellWidth + "s", "");
                }
            }
            System.out.println(" |");
        }
    }

    public static void exportSchedulesToFile(String filename, List<List<CourseSlot>> schedules) throws IOException {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            for (int i = 0; i < schedules.size(); i++) {
                writer.println("Schedule " + (i + 1) + ": " + schedules.get(i).size() + " Slots");
                writer.println("Time spent on campus per week: " + timeSpentPerWeek(schedules.get(i))/(60*60) + " hours");
                printTimetableToFile(schedules.get(i), writer);
                writer.println();
            }
        }
    }

    private static void printTimetableToFile(List<CourseSlot> schedule, PrintWriter writer) {
        // Calculate maximum course name length and longest day of week name
        int maxCourseNameLength = 0;
        int longestDayOfWeekLength = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            maxCourseNameLength = Math.max(maxCourseNameLength, schedule.stream().filter(s -> s.getDayOfWeek() == day)
                    .map(CourseSlot::getCourseName).mapToInt(String::length).max().orElse(0));
            longestDayOfWeekLength = Math.max(longestDayOfWeekLength, day.toString().length());
        }

        // Calculate total cell width based on longest day of week and course name
        int totalCellWidth = Math.max(longestDayOfWeekLength + 2, maxCourseNameLength + 2);

        // Header row with days of the week
        writer.print("    ");
        for (DayOfWeek day : DayOfWeek.values()) {
            writer.printf("| %-" + totalCellWidth + "s", day.toString());
        }
        writer.println(" |");

        // Time slots
        for (int hour = 9; hour <= 15; hour++) {
            writer.printf("%2d:00", hour);
            for (DayOfWeek day : DayOfWeek.values()) {
                boolean foundSlot = false;
                for (CourseSlot slot : schedule) {
                    if (slot.getDayOfWeek() == day && slot.getStartTime().getHour() == hour) {
                        // Format course name to fit max length
                        String formattedName = String.format("%-" + maxCourseNameLength + "s", slot.getCourseName());
                        writer.printf("| %-" + totalCellWidth + "s", formattedName);
                        foundSlot = true;
                        // break;
                    }
                }
                if (!foundSlot) {
                    // Use same width for empty cells
                    writer.printf("| %-" + totalCellWidth + "s", "");
                }
            }
            writer.println(" |");
        }
    }

    public static void sortSchedulesByDayAndTime(List<List<CourseSlot>> schedules) {
        schedules.forEach(schedule -> schedule.sort((slot1, slot2) -> {
            if (slot1.getDayOfWeek().compareTo(slot2.getDayOfWeek()) != 0) {
                return slot1.getDayOfWeek().compareTo(slot2.getDayOfWeek());
            } else {
                return slot1.getStartTime().compareTo(slot2.getStartTime());
            }
        }));
    }


    public static void main(String[] args) throws IOException {
        // Example usage (modify time slots and day of week)
        List<Course> courses = new ArrayList<>();

        // All available time slots

        courses.add(new Course("Compilers: Lecture",
            Collections.singletonList(new String[] { "TUESDAY", "09:00", "10:30" }
            )));
        courses.add(new Course("Compilers: Tutorial", List.of(
            new String[] { "THURSDAY", "12:30", "13:15" },
            new String[] { "THURSDAY", "14:15", "15:00" },
            new String[] { "MONDAY", "12:30", "01:15" }

            )));
            
        courses.add(new Course("Compilers: Lab", List.of(
            new String[] { "TUESDAY", "12:30", "14:00" },
            new String[] { "THURSDAY", "09:00", "10:30" },
            new String[] { "WEDNESDAY", "12:30", "14:00" } 
            )));

        courses.add(new Course("Networks: Lecture", List.of(
            new String[] { "TUESDAY", "09:00", "10:30" }, 
            new String[] { "TUESDAY", "10:45", "12:15" }
            )));

        courses.add(new Course("Algorithms: Lecture", List.of(
            new String[] { "WEDNESDAY", "10:45", "12:15" }, 
            new String[] { "WEDNESDAY", "14:15","15:45" }
            )));

        
        courses.add(new Course("Embedded Systems: Lecture", List.of(
            new String[] { "TUESDAY", "12:30", "14:00" },
            new String[] { "TUESDAY", "14:15", "15:45" }
            )));

        courses.add(new Course("Netowrks: Lab", List.of(
            new String[] { "MONDAY", "09:00", "10:30" },
            new String[] { "MONDAY", "12:30", "14:00" },
            new String[] { "WEDNESDAY", "09:00", "10:30" },
            new String[] { "WEDNESDAY", "10:45", "12:15" },
            new String[] { "MONDAY", "14:15", "15:45" }
            )));

        courses.add(new Course("Netowrks: Tutorial", List.of(
            new String[] { "TUESDAY", "15:00", "15:45" }, 
            new String[] { "WEDNESDAY", "09:00", "09:45" },
            new String[] { "MONDAY", "10:45", "11:30" },
            new String[] {"MONDAY", "11:30", "12:15"},
            new String[] { "WEDNESDAY", "09:45", "10:30"}

            )));

        courses.add(new Course("Algorithms: Tutorial", List.of(
            new String[] { "THURSDAY", "10:45", "12:15" }, 
            new String[] { "THURSDAY", "14:15","15:45" }, 
            new String[] { "MONDAY", "09:00", "10:30" },
            new String[] { "MONDAY", "12:30", "14:00"},
            new String[] { "TUESDAY", "12:30", "14:00"}
            )));


        courses.add(new Course("Embedded Systems: Lab", List.of(
            new String[] { "MONDAY", "12:30", "14:00" },
            new String[] { "MONDAY", "10:45", "12:15" },
            new String[] { "TUESDAY", "10:45", "12:15" },
            new String[] {"MONDAY", "14:15", "15:45"}, 
            new String[] { "MONDAY", "09:00", "10:30" }
            )));

        courses.add(new Course("Embedded Systems: Tutorial", List.of(
            new String[] { "WEDNESDAY", "15:00", "15:45" },
            new String[] { "TUESDAY", "14:15", "15:00" },
            new String[] { "WEDNESDAY", "14:15", "15:00" },
            new String[] { "TUESDAY", "13:15", "14:00" }, 
            new String[] { "TUESDAY", "09:45", "10:30"}
            )));

        courses.add(new Course("Public Policy", Collections.singletonList(
            // new String[] { "WEDNESDAY","12:30", "14:00" }//, 
            new String[] { "WEDNESDAY", "14:15", "15:45" }
            )));                        
        


        List<List<Course>> allPossiblePathes = sectionSpecificCombinations(courses);
        List<List<CourseSlot>> schedules = new ArrayList<>();
        
        int pathesCount = 0;
        for(List<Course> path: allPossiblePathes){
            pathesCount++;
            System.out.println("Path " + pathesCount + " : " + path.size());
            schedules.addAll(generateSchedules(path));

        }

        schedules = schedules.stream().filter(course -> filterForSameSectionPerCourse(course)).collect(Collectors.toList());

        sortSchedulesByDayAndTime(schedules);
        // sortByGaps(schedules);
        Collections.sort(schedules, new Comparator<List<CourseSlot>>(){
            public int compare(List<CourseSlot> schedule1, List<CourseSlot> schedule2){
                return timeSpentPerWeek(schedule1) - timeSpentPerWeek(schedule2);
            }
        });

        String scheduleTitle = "Best: No SWE, LRA";
        exportSchedulesToFile(scheduleTitle, schedules);


        System.out.println("Number of possible schedules: " + schedules.size());

    }

}