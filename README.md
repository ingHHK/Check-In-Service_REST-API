# Check-In Service API server

## API for PC Client

### **POST /checkIN/signUp/verifyEmail<br>**
:check if the e-mail is duplicate<br>
#### Request Data
~~~
  body:
  {
    "agentID": "회원가입 할 계정에 대한 이메일",
    "agentPW": "회원가입 할 계정에 대한 비밀번호",
    "name": "사용자 이름"
  }
~~~

#### Response Data
~~~
  body:
  {
    "result": "요청 결과", (성공 = 1, 실패 = 0)
  }
~~~

### **POST /checkIN/signUp/signAccount<br>**
:insert the account information to the database<br>
#### Request Data
~~~
  body:
  {
    "agentID": "회원가입 할 계정에 대한 이메일",
    "agentPW": "회원가입 할 계정에 대한 비밀번호",
    "name": "사용자 이름"
  }
~~~

#### Response Data
~~~
  body:
  {
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

### **POST /signIn<br>**
:login to the Check-In service<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "agentPW": "계정 비밀번호"
  }
~~~

#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

### **POST /signOut<br>**
:logout from the Check-In service<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token"
  }
~~~

#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 0)
  }
~~~

### **POST /siteAdd<br>**
:add the web site information<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "name": "웹 사이트 이름",
    "URL": "웹 사이트 URL",
    "ID": "웹 사이트 ID",
    "PW": "웹 사이트 PW",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 0)
  }
~~~

### **POST /siteEdit<br>**
:edit the web site information<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "name": "웹 사이트 이름",
    "URL": "웹 사이트 URL",
    "ID": "웹 사이트 ID",
    "PW": "웹 사이트 PW",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 0)
  }
~~~

### **POST /siteDelete<br>**
:delete the web site information<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "name": "웹 사이트 이름",
    "URL": "웹 사이트 URL",
    "ID": "웹 사이트 ID",
    "PW": "웹 사이트 PW",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 0)
  }
~~~

### **POST /siteRead<br>**
:read the web site information<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

#### Response Data
~~~
  body:
  {
    <List>
    "agentID": "계정 이메일",
    "name": "웹 사이트 이름",
    "URL": "웹 사이트 URL",
    "ID": "웹 사이트 ID",
    "PW": "웹 사이트 PW",
    "jwt": "JSON Web Token"
  }
~~~

### **POST /loginNumber/verify<br>**
:Verifying the one-time alternative login number<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "loginNumber": "One-time alternative login number"
    "jwt": "JSON Web Token"
  }
~~~
#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과", (성공 = 1, 실패 = 2)
  }
~~~

### **POST /verifyCode<br>**
:sending an email which include verifying code<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token"
  }
~~~
#### Response Data
~~~
  body:
  {
    "result": "요청 결과", (성공 = 1, 실패 = 2),
    "verify_code": "이메일로 발송된 확인 코드"
  }
~~~

### **POST /verifyOTP<br>**
:Verifying the one-time-password number<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "verify_code": "생성된 OTP 코드"
  }
~~~
#### Response Data
~~~
  body:
  {
    "jwt": "JSON Web Token",
    "result": "요청 결과", (성공 = 1, 실패 = 2),
    "jwt": "JSON Web Token"
  }
~~~

## API for Mobile App
### **POST /signIn_M<br>**
:sign-in the Check-In service app<br>
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

### **POST /signOut_M<br>**
:sign-in the Check-In service app<br>
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

### **POST /remoteSignOut<br>**
:sign-in the Check-In service app<br>
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

### **POST /loginNumber/create<br>**
:Create and get one-time alternative login number<br>
#### Request Data
~~~
  body:
  {
    "agentID": "ID",
    "deviceID": "Mobile device ID",
    "jwt": "JSON Web Token", (Default value = 0)
  }
~~~
#### Response Data
~~~
  body:
  {
    "agentID": "ID",
    "deviceID": "Mobile device ID",
    "loginNumber": "One-time alternative login number"
    "jwt": "JSON Web Token", (Default value = 0)
    "result": "Result", (True = 1, False = 0)
  }
~~~

### **POST /accessLog<br>**
:sign-in the Check-In service app<br>
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
