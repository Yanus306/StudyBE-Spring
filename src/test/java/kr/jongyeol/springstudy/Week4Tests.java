package kr.jongyeol.springstudy;

import kr.jongyeol.springstudy.study.week4.TextService;
import kr.jongyeol.springstudy.study.week4.Week4Controller;
import kr.jongyeol.springstudy.study.week4.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class Week4Tests {
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new Week4Controller(new TextService()))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	void echoSuccess() throws Exception {
		mockMvc.perform(get("/week4/echo").param("text", "hello"))
				.andExpect(status().isOk())
				.andExpect(content().string("hello"));
	}

	@Test
	void uppercaseSuccess() throws Exception {
		mockMvc.perform(get("/week4/uppercase").param("text", "hello"))
				.andExpect(status().isOk())
				.andExpect(content().string("HELLO"));
	}

	@Test
	void createUserSuccess() throws Exception {
		String body = "{\"name\":\"Hong\",\"age\":30,\"email\":\"hong@example.com\"}";
		mockMvc.perform(post("/week4/user").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.not("")));
	}

	@Test
	void headerEchoSuccess() throws Exception {
		mockMvc.perform(post("/week4/header-echo")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Client-Id", "client-1")
						.content("{\"text\":\"hello\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("5"));
	}

	@Test
	void headerEchoWrongValueReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/week4/header-echo")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Client-Id", "client-2")
						.content("{\"text\":\"hello\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("invalid header"));
	}

	@Test
	void missingQueryParam() throws Exception {
		mockMvc.perform(get("/week4/echo"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("request is invalid"));
	}

	@Test
	void blankTextReturnsBadRequest() throws Exception {
		mockMvc.perform(get("/week4/echo").param("text", " "))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("value must not be blank"));
	}

	@Test
	void headerMissingReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/week4/header-echo")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"text\":\"hello\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("invalid header"));
	}

	@Test
	void missingBodyReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/week4/user").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("request is invalid"));
	}
}

