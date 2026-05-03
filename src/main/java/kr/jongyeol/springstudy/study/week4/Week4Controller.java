package kr.jongyeol.springstudy.study.week4;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/week4")
public class Week4Controller {
    private final TextService textService;

    public Week4Controller(TextService textService) {
        this.textService = textService;
    }

    @GetMapping("/echo")
    public ResponseEntity<String> echo(@RequestParam(required = false) String text) {
        // TODO
        throw new RuntimeException();
    }

    @GetMapping("/uppercase")
    public ResponseEntity<String> uppercase(@RequestParam(required = false) String text) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody(required = false) UserRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/header-echo")
    public ResponseEntity<String> headerEcho(@RequestHeader(value = "X-Client-Id", required = false) String clientId,
                                             @RequestBody(required = false) TextRequest request) {
        // TODO
        throw new RuntimeException();
    }
}
