## Imsimarket API v.1.0.0

---

### 1. Авторизация
Авторизация в Imsimarket API основана на OAuth2/OIDC стандартах и протоколах.

Каждое обращение к API должно сопровождаться токеном доступа (access token).
Для получения токена доступа необходимо пройти процесс авторизации (см. далее). 

Время жизни access token относительно недолгое, и для продолжения работы с API 
необходимо этот токен регулярно обновлять. Для этих целей в процессе авторизации, 
помимо токена доступа, пользователь получает токен обновления (refresh token),
чьё время жизни существенно больше, и который может быть использован для обновления 
токенов. (описание процедуры обновления - см. далее)

### 1.1 Получение токенов
Для получения токенов клиент должен отправить HTTP запрос на endpoint авторизации.

#### Запрос
Authorization endpoint: https://mit.imsipay.com/auth/token  
HTTP method: POST  
Content-Type: application/x-www-form-urlencoded  
Request params:

| Name | Description |
| ---- | ---- |
| grant_type | password (согласно спецификации Oauth2) |
| username | имя пользователя, полученное от Imsimarket |
| password | пароль, полученный от Imsimarket | 

#### Ответ
JSON, содержащий access и refresh токены, а также время их жизни.
```
{
    "access_token": "<access token contents>",
    "refresh_token": "<refresh token contents>",
    "expires_in": 300,
    "refresh_expires_in": 1800,
    "token_type": "Bearer"
}
```
### 1.2 Обновление токенов
Обновление токенов очень похоже на их получение. Только вместо пары username-password необходимо отправить
на endpoint авторизации refresh token.

#### Запрос
Authorization endpoint: https://mit.imsipay.com/auth/token  
HTTP method: POST  
Content-Type: application/x-www-form-urlencoded  
Request params:

| Name | Description |
| ---- | ---- |
| grant_type | refresh_token (согласно спецификации Oauth2) |
| refresh_token | refresh token, полученный при авторизации |

#### Ответ
JSON, содержащий access и refresh токены, а также время их жизни.
```
{
    "access_token": "<access token contents>",
    "refresh_token": "<refresh token contents>",
    "expires_in": 300,
    "refresh_expires_in": 1800,
    "token_type": "Bearer"
}
```
#### Ошибки
В случае неправильных/отсутствующих параметров, а также в случае ошибки авторизации, сервер возвращает
HTTP Error Code 400 (Bad Request), и краткую строку описания ошибки.

### 2. Вызов методов API
После получения токенов можно вызывать методы API. Каждый вызов должен содержать access token в HTTP
заголовке авторизации:  
Authorization: Bearer {access_token}

Если API endpoint возвращает Http Error Code 401 (Unauthorized), то необходимо обновить access_token,
или заново авторизоваться.

### 2.1 Get Api Version
Самый простой вызов. Возвращает версию API и имя клиента. Этот вызов может быть полезен для проверки
корректности прохождения авторизации и настройки HTTP заголовков запроса.

#### Запрос
Authorization: Bearer  
Endpoint: https://mit.imsipay.com/api/v1  
HTTP method: GET

#### Ответ
JSON, содержащий версию API и имя клиента
```
{
    "product": "Imsimarket Reseller API",
    "version": "1.0.0",
    "reseller": "client@clientdomain.com"
}
```

### 2.2 Get SIM info by ICCID
Получение информации о SIM профиле. В качестве критерия поиска используется ICCID. ICCID указывается
как Path parameter запроса.

#### Запрос
Authorization: Bearer  
Endpoint: https://mit.imsipay.com/api/v1/sim/iccid/{ICCID}  
Path parameter: ICCID интересующей SIM  
HTTP method: GET

#### Ответ
JSON, содержащий информацию о SIM
```
{
    "iccid": "1111222233334444",
    "imsi": "12121212121212",
    "msisdn": "1234567",
    "balance": 19.19
}
```

### 2.3 Get SIM info by IMSI
Получение информации о SIM профиле. В качестве критерия поиска используется IMSI. IMSI указывается
как Path parameter запроса.

#### Запрос
Authorization: Bearer  
Endpoint: https://mit.imsipay.com/api/v1/sim/imsi/{IMSI}  
Path parameter: IMSI интересующей SIM  
HTTP method: GET

#### Ответ
JSON, содержащий информацию о SIM
```
{
    "iccid": "1111222233334444",
    "imsi": "12121212121212",
    "msisdn": "1234567",
    "balance": 19.19
}
```

### 2.4 Get SIM info by MSISDN
Получение информации о SIM профиле. В качестве критерия поиска используется MSISDN. MSISDN указывается
как Path parameter запроса.

#### Запрос
Authorization: Bearer  
Endpoint: https://mit.imsipay.com/api/v1/sim/msisdn/{MSISDN}  
Path parameter: MSISDN интересующей SIM  
HTTP method: GET

#### Ответ
JSON, содержащий информацию о SIM
```
{
    "iccid": "1111222233334444",
    "imsi": "12121212121212",
    "msisdn": "1234567",
    "balance": 19.19
}
```

### 2.5 SIM Balance top-up
Пополнение баланса SIM. Для пополнения передаётся JSON с параметрами (см. ниже).

#### Запрос
Authorization: Bearer  
Endpoint: https://mit.imsipay.com/api/v1/balance/topup  
HTTP method: POST  
Content-Type: application/json  
JSON params:

| Name | Description |
| ---- | ---- |
| type | тип параметра для определения SIM. Может быть ICCID, MSISDN или IMSI |
| param | параметр для определения SIM. Должен соответствовать типу, указанному в type |
| amount | сумма пополнения баланса |

Пример:
```
{
    "type":"ICCID",
    "param":"1111222233334444",
    "amount": 10.00
}
```

#### Ответ
В случае успешного пополнения - HTTP Status 200 (OK)

### 2.6 Ошибки
В случае ошибок API возвращает соответствующие статусы HTTP. Например, если SIM профиль не найден - 
возвращается Status 404 (NotFound), если запрос сформирован неправильно, возвращается 400 (BadRequest),
если закончилось время жизни access token - 401 (Unauthorized).

В большинстве случаев вместе со статусом отдаётся JSON, содержащий краткое описание причины ошибки.

Пример:
```
{
    "status": 404,
    "reason": "sim not found"
}
```