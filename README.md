# ORDER-WITH(오더위드)

###### ORDER-WITH-FRONT with ANDROID

> ORDER-WITH : 시각장애인을 위한 음성 키오스크를 안드로이드 애플리케이션으로 구현했습니다.

지도교수님 : 이종우 교수님

팀원: 유채원(숙명여자대학교), 김미진(숙명여자대학교)

***



### 📌목차

- `ORDER-WITH` 소개

- 주요기능 & 시스템 구성 

- 알고리즘 소개

- 사용 기술

- Preview

  

***



### 📱 ORDER-WITH 소개

- 개발 동기

  ```
  ✔️ 4차 산업혁명 시대의 기술 발전 속에 설 곳을 잃은 디지털 소외계층
  ✔️ 무인계산대 키오스크(KIOSK)가 점차 늘어나는 추세
  ✔️ 키오스크는 장애인 접근성이 매우 낮음
  ```

- 목표

  ```
  현재 터치스크린 기반 무인 결제단말기의 한계점을 보완
  👇
  무인화 기기에 음성 시스템을 도입하여 디지털 소외계층(장애인, 노인)의 무인화 기기 사용 도모
  ```

  

### 🖥  주요기능 & 시스템 구성

- 주요 기능

  ```
  1. 음성 안내에 따라 STT로 키워드 인식
  2. 메뉴 추천 랭킹 알고리즘
  3. 음성으로 3가지 메뉴 추천
  ```

  > - 음식점 메뉴 음성 안내
  > - 음성을 통한 주문 가이드
  > - 사용자가 음성으로 메뉴 주문 가능
  > - 기기가 유사 단어로 인식할 경우 유사 메뉴를 추천해주는 랭킹 시스템

- 시스템 구성

   <img src="/Reference/images/swArc.png" width="50%" height="50%"></img>



### 🧐 알고리즘 소개

1. 메뉴를 인덱싱 하며, 메뉴 input이 들어오면 빈도수를 계산
2. 빈도수 높은 메뉴들을 저장
3. LCS(Longest Common Subsequence)를 사용하여, 빈도수 계산 이후 글자가 먼저 나오는 순서로 계산의 정확도를 높임.

 <img src="/Reference/images/Algorithm.png" width="50%" height="50%"></img>



### 👩‍💻 사용 기술

- Backend

  ```
  - Spring Framework
  - Java
  ```

- FrontEnd

  ```
  - Android
  - Kotlin
  - Retrofit
  - RecyclerView
  ```




### 🔎 Preview





### 🔗 발표 자료

> ☝️ [중간발표](https://github.com/yoo-chaewon/ORDER-WITH-ANDROID/blob/dev_yoo/Reference/졸업작품제안서_김미진유채원.pdf)
>
> ✌️ [최종발표](https://github.com/yoo-chaewon/ORDER-WITH-ANDROID/blob/dev_yoo/Reference/졸업작품 최종심사_김미진,유채원.pdf)