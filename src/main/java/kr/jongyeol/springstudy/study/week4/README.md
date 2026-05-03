# Week4

HTTP 요청 검증과 예외 처리, ResponseEntity 사용법을 연습하는 주차입니다.

## 요구사항
### 공통
* `Controller`와 `Service`를 적절히 분리해 사용하세요. (`Controller`: 요청/응답 처리, `Service`: 비즈니스 로직 처리)
* 성공, 실패 응답은 **문자열(String)만** 반환합니다.
* 모든 엔드포인트의 Return 타입은 `ResponseEntity<String>`입니다.

## 응답 규약
### 성공
* HTTP Status: 200
* Body: 결과 문자열

### 실패
| 상황 | HTTP Status | Body |
|---|---:|---|
| 필수 값이 `null`, 빈 문자열, 공백만 있는 경우 | 400 | `value must not be blank` |
| 잘못된 형식(예: 이메일) | 400 | `invalid format` |
| 범위 검사 실패 | 400 | `value out of range` |
| 필수 Query Parameter 누락 | 400 | `request is invalid` |
| JSON Body 누락 또는 형식 오류 | 400 | `request is invalid` |
| 특정 Header 누락 또는 값 오류 | 400 | `invalid header` |

예시
```text
value must not be blank
```

## 엔드포인트 명세
### GET 엔드포인트
* `GET /week4/echo?text={text}`
  * 작업: 요청받은 `text`를 그대로 반환
  * Return: `String`
  * 예시: `hello`
* `GET /week4/uppercase?text={text}`
  * 작업: `text`를 대문자로 변환하여 반환
  * Return: `String`
  * 예시: `HELLO`

### POST 엔드포인트
* `POST /week4/user`
  * 작업: `UserRequest`를 검증하고 임의의 정수 ID를 반환
  * Return: `String`
  * 예시: `123456`
* `POST /week4/header-echo`
  * 작업: Header `X-Client-Id: client-1` 확인 후 `text` 길이를 반환
  * Return: `String`
  * 예시: `5`

## POST Input Body 상세 명세
### 1) `POST /week4/user`
* Body 타입: `UserRequest`
* 필드
  * `name` (`String`, 필수): 사용자 이름
  * `age` (`Integer`, 필수): 나이 (0 ~ 120)
  * `email` (`String`, 필수): 이메일

요청 예시
```json
{
  "name": "Hong",
  "age": 30,
  "email": "hong@example.com"
}
```

응답 예시
```text
123456
```

### 2) `POST /week4/header-echo`
* Body 타입: `TextRequest`
* 필드
  * `text` (`String`, 필수)

요청 예시
```json
{
  "text": "hello"
}
```

응답 예시
```text
5
```

## 구현 체크리스트
* `Controller`는 입력 수집/응답 작성(문자열 반환) 담당
* 비즈니스 로직은 `Service`로 분리
* 숫자/불리언 결과는 `Controller`에서 문자열로 변환해 반환
