<!-- WEEK_RANKING_START -->
## Week2 랭킹

![Week2 랭킹](https://raw.githubusercontent.com/Yanus306/StudyBE-Spring/database/.docs/week/2.svg)
<!-- WEEK_RANKING_END -->

# Week2

텍스트 변환 API를 구현하며 Spring Web 기초를 익히는 주차입니다.

## 요구사항
### 공통
* `Controller`와 `Service`를 적절히 분리해 사용하세요. (`Controller`: 요청/응답 처리, `Service`: 비즈니스 로직 처리)
* 성공, 실패 응답은 **문자열(String)만** 반환합니다.

## 응답 규약
### 성공
* HTTP Status: 200
* Body: 결과 문자열

### 실패
| 상황 | HTTP Status | Body |
|---|---:|---|
| `text` 값이 `null`, 빈 문자열, 공백만 있는 경우 | 400 | `text must not be blank` |
| `replace` 요청에서 `from` 값이 `null`, 빈 문자열, 공백만 있는 경우 | 400 | `from must not be blank` |
| `repeat` 요청에서 `times < 1` 또는 `times > 10`인 경우 | 400 | `times must be between 1 and 10` |
| 필수 Query Parameter 누락 (예: `GET /upper`에서 `text` 없음) | 400 | `request is invalid` |
| JSON Body 누락 또는 형식 오류 | 400 | `request is invalid` |

예시
```text
text must not be blank
```

## 엔드포인트 명세 (Return 타입)
### GET 엔드포인트
* `GET /week2/text/upper?text={text}`
  * 작업: 입력 `text`를 전부 대문자로 변환
  * Return: `String`
  * 예시: `SPRING`
* `GET /week2/text/lower?text={text}`
  * 작업: 입력 `text`를 전부 소문자로 변환
  * Return: `String`
  * 예시: `spring`
* `GET /week2/text/reverse?text={text}`
  * 작업: 입력 `text`의 문자 순서를 뒤집음
  * Return: `String`
  * 예시: `gnirps`
* `GET /week2/text/length?text={text}`
  * 작업: 입력 `text`의 길이를 계산하고 문자열로 반환
  * Return: `String`
  * 예시: `6`

### POST 엔드포인트
* `POST /week2/text/replace`
  * 작업: `text`에서 `from`을 모두 찾아 `to`로 치환 (`to`가 없으면 빈 문자열)
  * Return: `String`
  * 예시: `ba**`
* `POST /week2/text/word-count`
  * 작업: `text`를 공백 기준으로 단어 수 계산 (연속 공백은 하나의 구분자로 처리)
  * Return: `String`
  * 예시: `3`
* `POST /week2/text/trim`
  * 작업: `text`의 앞뒤 공백 제거
  * Return: `String`
  * 예시: `spring boot study`
* `POST /week2/text/mask`
  * 작업: 문자열 길이가 4 초과면 앞 2글자/뒤 2글자를 제외한 중간을 `*`로 마스킹, 4 이하이면 전체 마스킹
  * Return: `String`
  * 예시: `sp******ot`
* `POST /week2/text/repeat`
  * 작업: `text`를 `times` 횟수만큼 반복 (`times`는 1~10)
  * Return: `String`
  * 예시: `gogogo`
* `POST /week2/text/palindrome`
  * 작업: `text`가 회문인지 검사하여 `true` 또는 `false` 문자열 반환
  * Return: `String`
  * 예시: `true` 또는 `false`

## POST Input Body 상세 명세
### 1) `POST /week2/text/replace`
* Body 타입: `ReplaceRequest`
* 필드
  * `text` (`String`, 필수): 원본 문자열
  * `from` (`String`, 필수): 치환 대상 문자열
  * `to` (`String`, 선택): 치환 결과 문자열 (없으면 빈 문자열로 처리)

요청 예시
```json
{
  "text": "banana",
  "from": "na",
  "to": "*"
}
```

### 2) `POST /week2/text/word-count`
### 3) `POST /week2/text/trim`
### 4) `POST /week2/text/mask`
### 5) `POST /week2/text/palindrome`
* Body 타입: `TextRequest`
* 필드
  * `text` (`String`, 필수): 문자열

요청 예시
```json
{
  "text": "  spring boot study  "
}
```

### 6) `POST /week2/text/repeat`
* Body 타입: `RepeatRequest`
* 필드
  * `text` (`String`, 필수): 반복할 문자열
  * `times` (`int`, 필수): 반복 횟수 (`1 ~ 10`)

요청 예시
```json
{
  "text": "go",
  "times": 3
}
```

## 구현 체크리스트
* `Controller`는 입력 수집/응답 작성(문자열 반환) 담당
* 문자열 변환 로직은 `Service`로 분리