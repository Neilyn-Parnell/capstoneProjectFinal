package ac.za.mycput.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private EducatorRepository educatorRepository;

    // Method to show the attendance marking form
    @GetMapping("/markAttendanceForm")
    public String showMarkAttendanceForm(Model model) {
        List<Student> students = studentRepository.findAll();
        List<Course> courses = courseRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll();
        List<Educator> educators = educatorRepository.findAll();

        model.addAttribute("students", students);
        model.addAttribute("courses", courses);
        model.addAttribute("educators", educators);
        model.addAttribute("subjects", subjects);

        // create attendance form  object to hold student form data
        Attendance attendance = new Attendance();
        model.addAttribute("attendance", attendance);
        return "AttendanceFunctionality/attendanceForm"; // Create a Thymeleaf template for the form
    }

    // Method to process the attendance marking form

    @PostMapping("/mark")
    @Transactional
    public String processMarkAttendanceForm(@ModelAttribute AttendanceDTO attendanceDTO) {
        // Create and save the attendance record
        Attendance attendance = new Attendance();

        // Set properties directly from the DTO
        attendance.setStudentName(studentRepository.getOne(attendanceDTO.getStudentId()));
        attendance.setCourseName(courseRepository.getOne(attendanceDTO.getCourseId()));
        attendance.setEducatorName(educatorRepository.getOne(attendanceDTO.getEducatorId()));
        attendance.setSubjectName(subjectRepository.getOne(attendanceDTO.getSubjectId()));
        attendance.setDate(attendanceDTO.getDate());
        attendance.setPresent(attendanceDTO.getIsPresent());

        attendanceRepository.save(attendance);

        return "redirect:/markAttendanceForm?success";
    }


    // Method to generate attendance reports
    @GetMapping("/attendanceReport")
    public String generateAttendanceReport(Model model) {
        // Implement logic to fetch and generate attendance reports
        List<Attendance> attendanceRecords = attendanceRepository.findAll();

        model.addAttribute("attendanceRecords", attendanceRecords);

        return "AttendanceFunctionality/attendanceReport"; // Create a Thymeleaf template for the report
    }
    // Method to handle parent search
    @GetMapping("/parent")
    public String parentForm(Model model) {
        model.addAttribute("parentForm", new ParentForm());
        return "parentForm"; // Create a Thymeleaf template for the parent form
    }

    @PostMapping("/parent")
    public String searchStudent(@ModelAttribute("parentForm") ParentForm parentForm, Model model) {
        String studentName = parentForm.getStudentName();
        List<AttendanceRecord> studentAttendanceRecords = AttendanceDatabase.getAllAttendanceRecords()
                .stream()
                .filter(record -> record.getStudentName().equalsIgnoreCase(studentName))
                .collect(Collectors.toList());

        model.addAttribute("studentAttendanceRecords", studentAttendanceRecords);

        return "parentResult"; // Create a Thymeleaf template to display the results
    }
}

