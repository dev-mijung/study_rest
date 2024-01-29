/**
 * REST 방식은 URI와 같이 결합하므로 회원(Member)이라는 자원을 대상으로 전송방식을 결합한다면
 * 등록 /POST > /member/new
 * 조회 /GET > /member/{id}
 * 수정/PUT > /member/{id} + body(json 데이터 등)
 * 삭제/DELETE > /member/{id}
 * 
 * POST 방식도 그렇지만 PUT,DELETE,방식은 브라우저에서 테스트 하기 어렵기 때문에,
 * 개발시 jUnit이나 Restlet Client 등과 같은 도구를 이용하여 테스트하고 개발해야 함
 */

package com.mj.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mj.rest.domain.SampleVO;
import com.mj.rest.domain.Ticket;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping("/sample")
@Log4j
public class SampleController {
	
	// produces 속성  : 해당 메서드가 생산하는 MIME타입을 의미
	// 문자열로 지정할 수도 있고 메서드 내의 MediaType이라는 클래스를 이용할 수도 있음
	@GetMapping(value = "/getText", produces = "text/plain; charset=UTF-8")
	public String getText() {
		
		log.info("MIME TYPE: " + MediaType.TEXT_PLAIN_VALUE);
		
		return "안녕하세요";
	}
	
	
	// XML과 JSON 방식의 데이터를 생성할 수 있도록 작성 됨 
	// APPLICATION_JSON_UTF8_VALUE는 스프링 5.2 버전 부터는 Deprecated 되고
	// APPLICATION_JSON_VALUE로 사용 됨 
	// produces 속성은 반드시 지정해야 하는 것은 아니므로 생략가능
	// http://localhost:8080/sample/getSample : XML형태
	// http://localhost:8080/sample/getSample.json : json 형태
	@GetMapping(value = "/getSample", 
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE,
						MediaType.APPLICATION_XML_VALUE})
	public SampleVO getSample() {
		
		return new SampleVO(112, "스타", "로드");
	}
	
	
	@GetMapping("/getSample2")
	public SampleVO getSample2() {
		
		return new SampleVO(113, "로켓", "라쿤");
	}
	
	// http://localhost:8080/sample/getList
	// http://localhost:8080/sample/getList.json
	@GetMapping(value = "/getList")
	public List<SampleVO> getList(){
		
		return IntStream.range(1, 10).mapToObj(i -> new SampleVO(i, i+"First", i+ "Last"))
				.collect(Collectors.toList());
	}
	
	// Map을 이용하는 경우에는 키(key)에 속하는 데이터는 XML로 변환되는 경우에 태그의 이름이 되기 때문에 문자열을 지정 함
	@GetMapping(value = "/getMap")
	public Map<String, SampleVO> getMap(){
		
		Map<String, SampleVO> map = new HashMap<>();
		map.put("First", new SampleVO(111, "그루트", "주니어"));
		
		return map;
	}
	
	// REST 방식으로 호출하는 경우는 화면 자체가 아니라 데이터 자체를 전송하는 방식으로 처리
	// 데이터를 요청한 쪽에서는 정상적인 데이터인지 비정상적인 데이터인지를 구분할 수 있는 확실한 방법 제공해야 함 
	// ResponseEntity는 데이터와 함께  HTTP 헤더의 상태 메시지 등을 같이 전달하는 용도로 사용
	// HTTP의 상태 코드와 에러 메시지 등을 함께 데이터를 전달할 수 있기 때문에 받는 입장에서는 확실하게 결과를 알 수 있음
	@GetMapping(value = "/check", params = {"height", "weight"})
	public ResponseEntity<SampleVO> check(Double height, Double weight){
		
		SampleVO vo = new SampleVO(0, "" + height , ""+weight);
		
		ResponseEntity<SampleVO> result = null;
		
		// height가 150 미만인 경우에 502(bad gateway) 상태 코드와 데이터를 전송
		// 그렇지 않다면 200(ok) 코드와 데이터를 전송
		if(height < 150) {
			result = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(vo);
		} else {
			result = ResponseEntity.status(HttpStatus.OK).body(vo);
		}
		
		return result;
	}
	
	// http://localhost:8080/sample/product/bags/1234
	// @PathVariable을 적용하고 싶은 경우 {}을 이용하여 변수명을 지정하고 
	// @PathVariable을 이용해서 지정된 이름의 변수값을 얻을 수 있음
	// 값을 얻을 때에는 int, double과 같은 기본 자료형은 사용할 수 없음  
	@GetMapping("/product/{cat}/{pid}")
	public String[] getPath(
			@PathVariable("cat") String cat,
			@PathVariable("pid") Integer pid) {
		
				return new String[] {"category: " + cat, "product Id: " + pid};
	}
	
	// @RequestBody를 사용하는 이유
	// 요청(request)한 내용(body)을 처리하기 때문에 일반적인 파라미터 전달방식을 사용할 수 없기 때문
	// JSON으로 전달되는 데이터를 받아서 Ticket타입으로 변환 함
	@PostMapping("/ticket")
	public Ticket convert(@RequestBody Ticket ticket) {
		
		log.info("convert............ticket" + ticket);
		
		return ticket;
	}
}
