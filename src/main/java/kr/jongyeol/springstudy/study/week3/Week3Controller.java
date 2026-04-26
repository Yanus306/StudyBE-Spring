package kr.jongyeol.springstudy.study.week3;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/week3/datetime")
public class Week3Controller {
    private final DateTimeService dateTimeService;

    public Week3Controller(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @GetMapping("/format-date")
    public ResponseEntity<String> formatDate(@RequestParam String date, @RequestParam String pattern) {
        // TODO
        throw new RuntimeException();
    }

    @GetMapping("/day-of-week")
    public ResponseEntity<String> dayOfWeek(@RequestParam String date) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/add-days")
    public ResponseEntity<String> addDays(@RequestBody AddDaysRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/days-between")
    public ResponseEntity<String> daysBetween(@RequestBody DateRangeRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/convert-zone")
    public ResponseEntity<String> convertZone(@RequestBody ZoneConvertRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validate(@RequestBody DateFormatRequest request) {
        // TODO
        throw new RuntimeException();
    }
}
