<!-- WEEK_RANKING_START -->
## Week3 랭킹

![Week3 랭킹](https://raw.githubusercontent.com/Yanus306/StudyBE-Spring/database/.docs/week/3.svg)
<!-- WEEK_RANKING_END -->

# Week3

날짜/시간 API를 구현하며 Spring Boot의 기초 기능을 익히는 주차입니다.

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
| 날짜/시간 형식 파싱 실패 | 400 | `invalid date format` |
| `pattern` 값이 잘못된 경우 | 400 | `invalid pattern` |
| `zoneId` 값이 잘못된 경우 | 400 | `invalid zone id` |
| 필수 Query Parameter 누락 | 400 | `request is invalid` |
| JSON Body 누락 또는 형식 오류 | 400 | `request is invalid` |

예시
```text
invalid date format
```

## 엔드포인트 명세
### GET 엔드포인트
* `GET /week3/datetime/format-date?date={date}&pattern={pattern}`
  * 작업: `date(yyyy-MM-dd)`를 `pattern` 형식 문자열로 변환
  * Return: `String`
  * 예시: `2026/04/26`
* `GET /week3/datetime/day-of-week?date={date}`
  * 작업: 입력 날짜의 요일 계산
  * Return: `String`
  * 예시: `SUNDAY`

### POST 엔드포인트
* `POST /week3/datetime/add-days`
  * 작업: `date`에 `days`를 더한 결과 날짜 반환 (`days`는 음수 허용)
  * Return: `String`
  * 예시: `2026-05-01`
* `POST /week3/datetime/days-between`
  * 작업: `from`부터 `to`까지 날짜 차이(일수) 계산
  * Return: `String`
  * 예시: `25`
* `POST /week3/datetime/convert-zone`
  * 작업: `dateTime`을 `fromZone` 기준에서 `toZone` 기준 시각으로 변환 후 `pattern`으로 출력
  * Return: `String`
  * 예시: `2026-04-26 00:00:00`
* `POST /week3/datetime/validate`
  * 작업: `text`가 `pattern`을 만족하는 유효한 날짜/시간인지 검사
  * Return: `String`
  * 예시: `true` 또는 `false`

## POST Input Body 상세 명세
### 1) `POST /week3/datetime/add-days`
* Body 타입: `AddDaysRequest`
* 필드
  * `date` (`String`, 필수): 기준 날짜 (`yyyy-MM-dd`)
  * `days` (`int`, 필수): 더할 일수 (음수 허용)

요청 예시
```json
{
  "date": "2026-04-26",
  "days": 5
}
```

### 2) `POST /week3/datetime/days-between`
* Body 타입: `DateRangeRequest`
* 필드
  * `from` (`String`, 필수): 시작 날짜 (`yyyy-MM-dd`)
  * `to` (`String`, 필수): 종료 날짜 (`yyyy-MM-dd`)

요청 예시
```json
{
  "from": "2026-04-01",
  "to": "2026-04-26"
}
```

### 3) `POST /week3/datetime/convert-zone`
* Body 타입: `ZoneConvertRequest`
* 필드
  * `dateTime` (`String`, 필수): 기준 날짜/시간 (`yyyy-MM-dd'T'HH:mm:ss`)
  * `fromZone` (`String`, 필수): 기준 타임존 (예: `Asia/Seoul`)
  * `toZone` (`String`, 필수): 변환 대상 타임존 (예: `UTC`)
  * `pattern` (`String`, 필수): 반환 포맷 패턴

요청 예시
```json
{
  "dateTime": "2026-04-26T09:00:00",
  "fromZone": "Asia/Seoul",
  "toZone": "UTC",
  "pattern": "yyyy-MM-dd HH:mm:ss"
}
```

### 4) `POST /week3/datetime/validate`
* Body 타입: `DateFormatRequest`
* 필드
  * `text` (`String`, 필수): 검증할 날짜/시간 문자열
  * `pattern` (`String`, 필수): 검증 기준 패턴

요청 예시
```json
{
  "text": "2026-02-28",
  "pattern": "yyyy-MM-dd"
}
```

## 구현 체크리스트
* `Controller`는 입력 수집/응답 작성(문자열 반환) 담당
* 날짜/시간 변환 로직은 `Service`로 분리
* 숫자/불리언 결과는 `Controller`에서 문자열로 변환해 반환
