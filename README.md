# termproject_catchmind


Socket.io를 이용한 실시간 양방향 통신을 구현하기 위해 실시간 통신을 사용하는 여러 게임들을 찾아보았다.

그 중 캐치마인드란 게임이 한 캔버스를 모든 유저가 공유해 볼 수 있다는 점에서 통신과 관계가 있어 해당 게임의 기능 구현을 목적으로 프로젝트를 수행했다.

캐치마인드 : 주어진 제시어를 이용하여 한 명의 유저가 그림을 그리고, 그 밖의 다른 유저가 그린 그림을 보고 제시어를 유추하여 맞추는 게임.

## 개발환경
사용 언어 : java 11
사용 기술 : socket.io


## 기능

### 게임방 관리
 - 모든 유저가 게임방을 만들거나 입장할 수 있다.
 - 참가 인원을 제한하고 현재 몇명이 참가 중인지, 게임 중 추가 입장을 금지한다.
### 그림 그리기
- 들어온 순서대로 모든 유저가 문제 하나당 그림을 그린다.
### 쪽지(1:1 통신)
- 한 유저가 다른 유저에게 쪽지를 보낼 수 있다.

## UI/UX

시작화면
![mainImage](https://user-images.githubusercontent.com/55067985/221176350-bd874e6a-9321-472a-a57f-77135929c56d.PNG)

로비화면
![lobby](https://user-images.githubusercontent.com/55067985/221177203-080d0c59-95ea-41a0-af4a-aeb550b0729b.PNG)

게임방
![gameroom](https://user-images.githubusercontent.com/55067985/221177427-c896cafe-e324-41c2-8ffa-f6276cf259ec.PNG)

게임시작

### 프로토콜
|프로토콜|용도|방향|
|-----|-----|-----|
|100|로그인|Clinet->Server->Client|
|101|유저리스트 갱신|Server->Client|
|103|쪽지함 메세지 송수신|Client->Server->Client|
