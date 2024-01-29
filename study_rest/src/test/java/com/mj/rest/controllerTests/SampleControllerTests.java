// REST 방식의 테스트
// GET 방식이 아니고 POST 등의 방식으로 지정되어 있으면서 JSON 형태의 데이터를 처리할 때
// @RestController를 쉽게 테스트 하는 방법
// 1. rest 방식의 데이터를 전송하는 툴을 이용하거나
// 1-1) 크롬 확장프로그램에서 Yet Another REST Client 등을 이용해도 됨 
// 2. jUnit과 spring-test를 이용해서 테스트 하는 방식

// JSON 데이터를 테스트 해야 함 
// SampleController의 convert()는 JSON으로 전달되는 데이터를 받아서 Ticket타입으로 변환 함
// 이를 위해서 해당 데이터가 JSON이라는 것을 명시해 줄 필요가 있음
// MockMvc는 contentType()을 이용해서 전달하는 데이터가 무엇인지 알려줄 수 있음
// Gson 라이브러리는  Java의 객체를 JSON 문자열로 변환하기 위해 사용

package com.mj.rest.controllerTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.mj.rest.domain.Ticket;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class)

// Test for Controller
@WebAppConfiguration

@ContextConfiguration({"file:src/main/webapp/WEB-INF/spring/root-context.xml",
						"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
// Java Config
// @ConatextConfiguration(classes {
//	com.mj.rest.config.RootConfig.class,
//	com.mj.rest.config.ServletConfig.class}
@Log4j
public class SampleControllerTests {
	
	@Setter(onMethod_ = {@Autowired})
	private WebApplicationContext ctx;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
	}
	
	// SampleController의 convert() 메서드를 테스트하기 위해 작성
	@Test
	public void testConvert() throws Exception {
		
		Ticket ticket = new Ticket();
		ticket.setTno(123);
		ticket.setOwner("Admin");
		ticket.setGrade("AAA");
		
		String jsonStr = new Gson().toJson(ticket);
		
		log.info(jsonStr);
		
		mockMvc.perform(post("/sample/ticket")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonStr))
			.andExpect(status().is(200));
	}

}
