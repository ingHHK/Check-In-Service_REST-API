# Check-In Service API server

## API for Android
### **POST /checkIN/signUp<br>**
:sign-up the Check-In service<br>
#### Request Data
~~~
  body:
  {
    "agentID": "회원가입 할 계정에 대한 이메일",
    "agentPW": "회원가입 할 계정에 대한 비밀번호",
    "name": "사용자 이름"
    "errorCount": "로그인 시도 오류 횟수", (Default value = 0)
    "numberOfDevice": "추가 등록한 모바일 기기의 개수", (Default value = 0)
  }
~~~
