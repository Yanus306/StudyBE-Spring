# 관리자 가이드

## Base코드와 Test코드 작성하기
스터디를 시작하기 위해서는 Base코드와 Test코드를 작성해야 합니다.<p>
Base 코드는 다음 폴더에 위치하여야 합니다.
```
src/main/java/{Package}/study/weekN/
```
Test 코드는 다음 폴더에 위치하여야 합니다.
```
src/test/java/{Package}/WeekNTests.java
```
Base 코드로는 Controller를 주고 Test 코드로는 Controller에 대한 테스트 코드를 작성하는 것을 권장드립니다.<p>
MockMvc를 이용하여 테스트 코드를 작성하면 Controller에 대한 테스트 코드를 작성하기 편리합니다.<p>
MockMvc를 이용하면 다음과 같이 테스트 코드를 작성할 수 있습니다.
```java
private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new Week1HelloController()).build();

@Test
void helloWorldEndpoint() throws Exception {
    mockMvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello World!"));
}
```

## 자동 merge 날짜 변경하기
자동 merge 날짜는 [weekly-merge.yml](../.github/workflows/weekly-merge.yml) 파일에서 변경할 수 있습니다.<p>
```yaml
on:
  schedule:
    - cron: '0 15 * * 0'
```
기본적으로 매주 일요일 15시(UTC)에 자동 merge가 진행되도록 설정되어 있습니다.<p>
[cron 표현식](https://www.freeformatter.com/cron-expression-generator-quartz.html)을 이용하여 원하는 날짜와 시간으로 변경할 수 있습니다.

## Self-hosted Runner 설정하기
규모가 큰 스터디의 경우 GitHub Actions의 무료 사용량을 초과할 수 있습니다.<p>
이 경우 Self-hosted Runner를 설정하여 GitHub Actions를 이용할 수 있습니다.<p>
우선 [Self-hosted Runner를 설정해야 됩니다](https://docs.github.com/en/actions/hosting-your-own-runners/about-self-hosted-runners).
그 후 [weekly-merge.yml](../.github/workflows/weekly-merge.yml) 파일에서 다음과 같이 수정하셔야 됩니다.
```yaml
jobs:
  merge:
    runs-on: self-hosted
```

## 수동 Merge하기
만약 자동 Merge가 진행되지 않거나 실수로 Main브랜치에 코드를 추가하지 않았을 경우 수동으로 Actions를 통한 Merge를 할 수 있습니다.<p>
위쪽 `Actions` 탭에서 `Weekly Merge`를 선택하여 `Run workflow`를 누르면 수동으로 Merge됩니다.<p>
만약 충돌의 이유로 Merge가 진행되지 않을 경우 Issue가 자동으로 생성됩니다. 그 경우 Actions를 통해 Merge할 수 없으므로 직접 Merge하셔야 됩니다.