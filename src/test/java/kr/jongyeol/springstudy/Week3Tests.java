package kr.jongyeol.springstudy;

import kr.jongyeol.springstudy.study.week3.DateTimeService;
import kr.jongyeol.springstudy.study.week3.Week3Controller;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class Week3Tests {
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new Week3Controller(new DateTimeService()))
            .build();

    @Test
    void formatDateEndpoint() throws Exception {
        mockMvc.perform(get("/week3/datetime/format-date")
                        .param("date", "2026-04-26")
                        .param("pattern", "yyyy/MM/dd"))
                .andExpect(status().isOk())
                .andExpect(content().string("2026/04/26"));
    }

    @Test
    void dayOfWeekEndpoint() throws Exception {
        mockMvc.perform(get("/week3/datetime/day-of-week")
                        .param("date", "2026-04-26"))
                .andExpect(status().isOk())
                .andExpect(content().string("SUNDAY"));
    }

    @Test
    void addDaysEndpoint() throws Exception {
        mockMvc.perform(post("/week3/datetime/add-days")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-04-26\",\"days\":5}"))
                .andExpect(status().isOk())
                .andExpect(content().string("2026-05-01"));
    }

    @Test
    void daysBetweenEndpoint() throws Exception {
        mockMvc.perform(post("/week3/datetime/days-between")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"from\":\"2026-04-01\",\"to\":\"2026-04-26\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }

    @Test
    void convertZoneEndpoint() throws Exception {
        mockMvc.perform(post("/week3/datetime/convert-zone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateTime\":\"2026-04-26T09:00:00\",\"fromZone\":\"Asia/Seoul\",\"toZone\":\"UTC\",\"pattern\":\"yyyy-MM-dd HH:mm:ss\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("2026-04-26 00:00:00"));
    }

    @Test
    void validateEndpoint() throws Exception {
        mockMvc.perform(post("/week3/datetime/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"2026-02-28\",\"pattern\":\"yyyy-MM-dd\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void blankValueReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/week3/datetime/day-of-week").param("date", " "))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("value must not be blank"));
    }

    @Test
    void invalidDateFormatReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/week3/datetime/format-date")
                        .param("date", "2026/04/26")
                        .param("pattern", "yyyy-MM-dd"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("invalid date format"));
    }

    @Test
    void invalidPatternReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/week3/datetime/format-date")
                        .param("date", "2026-04-26")
                        .param("pattern", "invalid-pattern"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("invalid pattern"));
    }

    @Test
    void invalidZoneReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/week3/datetime/convert-zone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateTime\":\"2026-04-26T09:00:00\",\"fromZone\":\"Asia/Seoul\",\"toZone\":\"ABC/DEF\",\"pattern\":\"yyyy-MM-dd HH:mm:ss\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("invalid zone id"));
    }

    @Test
    void missingQueryParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/week3/datetime/format-date").param("date", "2026-04-26"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("request is invalid"));
    }

    @Test
    void missingBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/week3/datetime/add-days")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("request is invalid"));
    }
}
