<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/next-step/atdd-subway-fare">
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

`2021.03.04 ~ 2021.04.07` 진행한 NEXTSTEP ATDD, 클린 코드 with Spring 3기 미션 구현 코드입니다.</br>
Legacy 코드를 리팩토링 하며 서비스 개선을 했으며 인수테스트, 단위 테스트, TDD 리팩토링, Rest Docs를 이용한 테스트 기반 문서화를 진행했습니다.

이 과정에서 지하철 노선 관리, 구간 관리, 최단 경로 조회, 로그인, 즐겨찾기, 노선 별 요금 정책 기능을 구현했으며 128개의 테스트코드를 작성했습니다.

- [API 문서화 링크](https://limhangyeol.github.io/)

코드리뷰 링크
- 1주차 미션: 
  - [Step1 - 지하철 노선 관리](https://github.com/next-step/atdd-subway-map/pull/50)
  - [Step2 - 인수 테스트 리팩토링](https://github.com/next-step/atdd-subway-map/pull/70)
  - [Step3 - 지하철 구간 관리](https://github.com/next-step/atdd-subway-map/pull/104)
- 2주차 미션: 
  - [Step1 - 구간 추가 기능 변경](https://github.com/next-step/atdd-subway-path/pull/113)
  - [Step2 - 구간 제거 기능 변경](https://github.com/next-step/atdd-subway-path/pull/125)
  - [Step3 - 경로 조회](https://github.com/next-step/atdd-subway-path/pull/138)
- 3주차 미션:
  - [Step1 - 토큰 기반 로그인 기능](https://github.com/next-step/atdd-subway-favorite/pull/79)
  - [Step2 - 로그인 리팩토링](https://github.com/next-step/atdd-subway-favorite/pull/99)
  - [Step3 - 즐겨찾기 기능 구현](https://github.com/next-step/atdd-subway-favorite/pull/120)
- 4주차 미션:
  - [Step1 - 문서화 실습](https://github.com/next-step/atdd-subway-fare/pull/35)
  - [Step2 - 요금 조회](https://github.com/next-step/atdd-subway-fare/pull/51)
  - [Step3 - 요금 정책 추가](https://github.com/next-step/atdd-subway-fare/pull/64)
  - [Step3.1 - 리팩토링](https://github.com/next-step/atdd-subway-fare/pull/70)

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Reive Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-fare/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-fare/blob/master/LICENSE.md) licensed.
