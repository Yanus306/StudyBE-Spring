package kr.jongyeol.springstudy.study.week2;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> upper(@RequestParam(required = false) String text) {
        if(text == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(text.isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.upper(text), HttpStatus.OK);
    }

    @GetMapping("/lower")
    public ResponseEntity<String> lower(@RequestParam(required = false) String text) {
        if(text == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(text.isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.lower(text), HttpStatus.OK);
    }

    @GetMapping("/reverse")
    public ResponseEntity<String> reverse(@RequestParam(required = false) String text) {
        if(text == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(text.isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.reverse(text), HttpStatus.OK);
    }

    @GetMapping("/length")
    public ResponseEntity<String> length(@RequestParam(required = false) String text) {
        if(text == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(text.isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.length(text) + "", HttpStatus.OK);
    }

    @PostMapping("/replace")
    public ResponseEntity<String> replace(@RequestBody(required = false) ReplaceRequest request) {
        if(request == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(request.text() == null || request.text().isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        if(request.from() == null || request.from().isBlank())
            return new ResponseEntity<>("from must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.replace(request), HttpStatus.OK);
    }

    @PostMapping("/word-count")
    public ResponseEntity<String> wordCount(@RequestBody(required = false) TextRequest request) {
        if(request == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(request.text() == null || request.text().isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.wordCount(request.text()) + "", HttpStatus.OK);
    }

    @PostMapping("/trim")
    public ResponseEntity<String> trim(@RequestBody(required = false) TextRequest request) {
        if(request == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(request.text() == null || request.text().isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.trim(request.text()), HttpStatus.OK);
    }

    @PostMapping("/mask")
    public ResponseEntity<String> mask(@RequestBody(required = false) TextRequest request) {
        if(request == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(request.text() == null || request.text().isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.mask(request.text()), HttpStatus.OK);
    }

    @PostMapping("/repeat")
    public ResponseEntity<String> repeat(@RequestBody(required = false) RepeatRequest request) {
        if(request == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(request.text() == null || request.text().isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        if(request.times() < 1 || request.times() > 10)
            return new ResponseEntity<>("times must be between 1 and 10", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.repeat(request), HttpStatus.OK);
    }

    @PostMapping("/palindrome")
    public ResponseEntity<String> palindrome(@RequestBody(required = false) TextRequest request) {
        if(request == null)
            return new ResponseEntity<>("request is invalid", HttpStatus.BAD_REQUEST);
        if(request.text() == null || request.text().isBlank())
            return new ResponseEntity<>("text must not be blank", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(textTransformService.palindrome(request.text()) + "", HttpStatus.OK);
    }
}