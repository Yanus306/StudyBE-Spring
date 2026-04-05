package kr.jongyeol.springstudy.study.week2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/week2/text")
public class Week2Controller {
    private final TextTransformService textTransformService;

    public Week2Controller(TextTransformService textTransformService) {
        this.textTransformService = textTransformService;
    }

    @GetMapping("/upper")
    public ResponseEntity<String> upper(@RequestParam String text) {
        // TODO
        throw new RuntimeException();
    }

    @GetMapping("/lower")
    public ResponseEntity<String> lower(@RequestParam String text) {
        // TODO
        throw new RuntimeException();
    }

    @GetMapping("/reverse")
    public ResponseEntity<String> reverse(@RequestParam String text) {
        // TODO
        throw new RuntimeException();
    }

    @GetMapping("/length")
    public ResponseEntity<String> length(@RequestParam String text) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/replace")
    public ResponseEntity<String> replace(@RequestBody ReplaceRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/word-count")
    public ResponseEntity<String> wordCount(@RequestBody TextRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/trim")
    public ResponseEntity<String> trim(@RequestBody TextRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/mask")
    public ResponseEntity<String> mask(@RequestBody TextRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/repeat")
    public ResponseEntity<String> repeat(@RequestBody RepeatRequest request) {
        // TODO
        throw new RuntimeException();
    }

    @PostMapping("/palindrome")
    public ResponseEntity<String> palindrome(@RequestBody TextRequest request) {
        // TODO
        throw new RuntimeException();
    }
}