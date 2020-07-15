# Check-In Service API server
Default url: "host IP"/checkIN/
## API for PC Client

### **POST /signUp/verifyEmail<br>**
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

### **POST /signUp/signAccount<br>**
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
    "otpEnalbe": "OTP 사용 여부"(사용 = 1, 미사용 = 0),
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

### **POST /update/accountName<br>**
:update account name<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "accountName": "계정 이름"
  }
~~~

#### Response Data
~~~
  body:
  {
    "result": "요청 결과"(성공 = 1, 실패 0)
  }
~~~

### **POST /update/deviceEnable<br>**
:update device enable<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "deviceID": "기기 고유 ID",
    "deviceEnable": "기기 사용 여부"(사용 = true, 미사용 = false)
  }
~~~

#### Response Data
~~~
  body:
  {
    "result": "요청 결과"(성공 = 1, 실패 0)
  }
~~~

### **POST /update/otpEnable<br>**
:update otp enable<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "otpEnable": "기기 사용 여부"(사용 = 1, 미사용 = 0)
  }
~~~

#### Response Data
~~~
  body:
  {
    "result": "요청 결과"(성공 = 1, 실패 0)
  }
~~~

### **POST /deviceRead<br>**
:read all devices<br>
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
    <List>
    "agentID": "계정 이메일",
    "deviceID": "기기 고유 ID",
    "deviceName": "사용자 정의 기기 이름",
    "enrollmentDate": "기기 등록일",
    "deviceEnable": "기기 사용 여부"
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
    "verify_code": "One-time alternative login number"
  }
~~~
#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 2)
  }
~~~

### **POST /verifyCode<br>**
:e-mailing which include verifying code<br>
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
    "verify_code": "이메일로 발송된 확인 코드",
    "result": "요청 결과"(성공 = 1, 실패 = 2)
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
  }
~~~

## API for Mobile App
### **POST /signIn_M<br>**
:sign-in the Check-In service app<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "agentPW": "계정 비밀번호",
    "deviceID": "기기 고유 ID",
    "deviceName": "사용자 정의 기기 이름"
  }
~~~

#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0, 디바이스 사용 미설정 = 2)
  }
~~~

### **POST /signOut_M<br>**
:sign-out from the Check-In service app<br>
#### Request Data
~~~
  body:
  {
    "agentID": "회원가입 할 계정에 대한 이메일",
    "jwt": "JSON Web Token"
  }
~~~

#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

### **POST /remoteSignOut<br>**
:remote sign-out from the Check-In service<br>
#### Request Data
~~~
  body:
  {
    "agentID": "회원가입 할 계정에 대한 이메일",
    "jwt": "JSON Web Token"
  }
~~~

#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

### **POST /loginNumber/create<br>**
:create and get one-time alternative login number<br>
#### Request Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "deviceID": "기기 고유 ID",
    "jwt": "JSON Web Token"
  }
~~~
#### Response Data
~~~
  body:
  {
    "agentID": "계정 이메일",
    "deviceID": "기기 고유 ID",
    "loginNumber": "일회용 로그인 번호",
    "jwt": "JSON Web Token",
    "result": "요청 결과"(성공 = 1, 실패 = 0)
  }
~~~

### **POST /accessLog<br>**
:get access log<br>
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
    
    <List>
    "agentID": "계정 이메일",
    "loginStatus": "계정 접속 로그",
    "accessIP": "접속 IP",
    "accessTime": "접속 시간"
  }
~~~
