package kr.jongyeol.springstudy;

import kr.jongyeol.springstudy.study.week2.TextTransformService;
import kr.jongyeol.springstudy.study.week2.Week2Controller;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class Week2Tests {
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new Week2Controller(new TextTransformService()))
            .build();

    @Test
    void upperEndpoint() throws Exception {
        mockMvc.perform(get("/week2/text/upper").param("text", "spring"))
                .andExpect(status().isOk())
                .andExpect(content().string("SPRING"));
    }

    @Test
    void lowerEndpoint() throws Exception {
        mockMvc.perform(get("/week2/text/lower").param("text", "SPRING"))
                .andExpect(status().isOk())
                .andExpect(content().string("spring"));
    }

    @Test
    void reverseEndpoint() throws Exception {
        mockMvc.perform(get("/week2/text/reverse").param("text", "spring"))
                .andExpect(status().isOk())
                .andExpect(content().string("gnirps"));
    }

    @Test
    void lengthEndpoint() throws Exception {
        mockMvc.perform(get("/week2/text/length").param("text", "spring"))
                .andExpect(status().isOk())
                .andExpect(content().string("6"));
    }

    @Test
    void replaceEndpoint() throws Exception {
        mockMvc.perform(post("/week2/text/replace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"banana\",\"from\":\"na\",\"to\":\"*\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("ba**"));
    }

    @Test
    void trimEndpoint() throws Exception {
        mockMvc.perform(post("/week2/text/trim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"  spring boot study  \"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("spring boot study"));
    }

    @Test
    void wordCountEndpoint() throws Exception {
        mockMvc.perform(post("/week2/text/word-count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"spring   boot study\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void maskEndpoint() throws Exception {
        mockMvc.perform(post("/week2/text/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"springboot\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("sp******ot"));
    }

    @Test
    void repeatEndpoint() throws Exception {
        mockMvc.perform(post("/week2/text/repeat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"go\",\"times\":3}"))
                .andExpect(status().isOk())
                .andExpect(content().string("gogogo"));
    }

    @Test
    void palindromeEndpoint() throws Exception {
        mockMvc.perform(post("/week2/text/palindrome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"level\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void blankTextReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/week2/text/lower").param("text", " "))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("text must not be blank"));
    }

    @Test
    void replaceFromBlankReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/week2/text/replace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"banana\",\"from\":\" \",\"to\":\"*\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("from must not be blank"));
    }

    @Test
    void repeatWithTooSmallTimesReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/week2/text/repeat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"go\",\"times\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("times must be between 1 and 10"));
    }

    @Test
    void repeatWithTooLargeTimesReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/week2/text/repeat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"go\",\"times\":11}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("times must be between 1 and 10"));
    }

    @Test
    void missingQueryParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/week2/text/upper"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("request is invalid"));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/week2/text/trim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"oops\""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("request is invalid"));
    }

    @Test
    void missingBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/week2/text/word-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("request is invalid"));
    }
}