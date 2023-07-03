# API 명세와 URL

## 1. 회원 관련 API 명세
### 파트너 회원 가입 API
- URL: /members/owner/signup, POST
- 명세
```json
{
  "email": "email",
  "password": "password",
  "name": "name",
  "phoneNumber": "phone number",
  "gender": "gender[Female, Male]"
}
```
- Email 속성이 유일해야 합니다. 중복된 Email 이 있을 경우 회원가입이 제한됩니다.
- 성공 결과 json 명세
```json
{
    "id": 1,
    "email": "email",
    "password": "$2a$10$BpAKTkYgkvlS5EvT.hj6seivWoRmlUw9KxNmUSYPrQbIIMMmLAAk.",
    "name": "name",
    "phoneNumber": "phone number",
    "gender": "Gender[FEMALE, MALE]",
    "roles": [
        "ROLE_PARTNER","ROLE_USER"
    ]
}
```
- 비밀번호는 암호화되어 DB에 저장됩니다.
- 발생 가능한 예외
  - `DuplicateException` - 중복된 email 이 감지될 때 발생합니다.
  - `MethodArgumentNotVaildException` - 입력값에 대한 검증이 실패하였을 때 발생합니다.

### 일반 회원 가입 API
- URL: /members/user/signup, POST
- 명세(파트너 회원 가입과 동일합니다.)
```json
{
  "email": "email",
  "password": "password",
  "name": "name",
  "phoneNumber": "phone number",
  "gender": "gender[Female, Male]"
}
```
- Email 속성이 유일해야 합니다. 중복된 Email 이 있을 경우 회원가입이 제한됩니다.
- 성공 결과 json 명세
```json
{
    "id": 1,
    "email": "email",
    "password": "$2a$10$BpAKTkYgkvlS5EvT.hj6seivWoRmlUw9KxNmUSYPrQbIIMMmLAAk.",
    "name": "name",
    "phoneNumber": "phone number",
    "gender": "gender[FEMALE, MALE]",
    "roles": [
        "ROLE_PARTNER","ROLE_USER"
    ]
}
```
- 발생 가능한 예외
    - `DuplicateException` - 중복된 email 이 감지될 때 발생합니다.
    - `MethodArgumentNotVaildException` - 입력값에 대한 검증이 실패하였을 때 발생합니다.


### 로그인 API
- URL: /members/signin
- 입력 명세
```json
{
  "email": "email",
  "password": "password"
}
```
- 결과 명세
```text
JWT token
```

- 결과로는 JWT 토큰이 반환되며 회원 가입이 필요한 곳에서 해당 JWT 토큰을 HTTP Header의 Authority에 `Bearer JWT_token`의 형식으로 넣어주셔야 합니다.

- 발생 가능한 예외
  - `LoginException` - 패스워드 불일치의 경우 발생합니다.
  - `NotExistsException` - 입력받은 `email`이 DB에 존재하지 않을 경우 발생합니다.

## 2. 매장 관련 API
### 매장 리스트 조회 API
- URL: /stores, GET
- 입력 명세는
```json
{
  "keyword": "keyword (optional)"
}
```
- `keyword` 속성은 비어있는체로 제공되어도 좋습니다. 단 입력 명세를 아예 넣지 않으면 안됩니다.
  - `keyword` 속성이 비어있는 경우, 현재 등록되어 있는 모든 매장을 반환합니다.
  - `keyword` 에 데이터가 들어가는 경우, 해당 키워드를 사용하여 키워드가 존재하는 매장 데이터를 반환합니다.
- 결과 명세(`keyword = ""`)
```json
[
    {
        "storeId": 1,
        "ownerName": "owner",
        "storeName": "store1",
        "legion": "legion",
        "city": "city",
        "street": "street",
        "description": "store1's description"
    },
    {
        "storeId": 2,
        "ownerName": "owner",
        "storeName": "store2",
        "legion": "legion",
        "city": "city",
        "street": "street",
        "description": "store2's description"
    }
]
```

### 매장 상세 조회 API
- URL: /stores/{storeId}, GET
- 입력 명세는 없습니다.
- 결과 명세
```json
{
  "storeId": 1,
  "ownerName": "owner",
  "storeName": "store1",
  "legion": "legion",
  "city": "city",
  "street": "street",
  "description": "store1's description"
}
```

- 발생할 수 있는 예외
  - `NotExistsException` - 상세 조회할 매장이 존재하지 않을 때 발생합니다.


### 매장 등록 API
- URL: /stores/new
- 입력 명세
```json
{
    "storeName": "store1",
    "legion": "legion",
    "city": "city",
    "street": "street",
    "zipcode": "zipcode",
    "description": "store1's description"
}
```

- 매장의 이름은 유일해야 합니다. 중복된 매장 이름이 들어오는 경우 등록이 제한됩니다.
- 결과 명세
```json
{
    "storeId": 1,
    "storeName": "참새정",
    "owner": "kim",
    "legion": "경상남도",
    "city": "김해시",
    "street": "삼계로",
    "zipcode": "50899",
    "description": "맛있는 술집"
}
```

- 발생 가능한 예외
  - `DuplicateException` - 동일한 매장 이름이 이미 존재할 때 발생합니다.
  - `MethodArgumentNotVaildException` - 입력에 대한 검증이 실패하였을 때 발생합니다.

## 3. 예약 관련 API
### 예약 생성 API - (1)
- 예약 생성은 두 가지 방식으로 가능합니다.
- 첫 번째는 매장 상세 정보로 이동하지 않은 상태에서 예약을 생성하는 방법입니다.
- URL : /reserve/new
- 입력 명세
```json
{
    "reserveTime": "2023-07-03 17:30:05",
    "storeName": "store1"
}
```
- `reserveTime`의 경우 반드시 `yyyy-MM-dd hh:mm:ss`의 형식으로 만들어야 합니다.


- 결과 명세
```json
{
    "id": 3,
    "reserveDateTime": "2023-07-03T17:30:05",
    "reserveStatus": "VALID",
    "memberEmail": "memberEmail",
    "storeName": "store1"
}
```

- 발생 가능한 예외
  - `NotExistsException` - 예약을 진행하려는 매장이 존재하지 않을 때 발생합니다.


### 예약 생성 API - (2)
- 예약을 생성하는 다른 방법은 매장의 상세 정보에서 예약을 추가하는 것입니다.
- URL : /stores/{storeId}/add-reserve
- 입력 명세
```json
{
    "reserveTime": "2023-07-03 15:10:00"
}
```

- 매장의 이름은 URL에 있는 매장의 id를 사용하여 가져올 수 있습니다. 따라서 입력 명세에서 `storeName`을 제거했습니다.
- 결과 명세
```json
{
    "id": 1,
    "reserveDateTime": "2023-07-03T15:10:00",
    "reserveStatus": "VALID",
    "memberEmail": "memberEmail",
    "storeName": "store1"
}
```

### 점장이 운영하는 모든 가계에 대한 예약 조회 API
- URL : /reserves/owner
- 입력 명세는 없습니다.
- 결과 명세
```json
[
    {
        "id": 1,
        "reserveDateTime": "2023-07-03T04:30:05",
        "reserveStatus": "VALID",
        "memberEmail": "memberEmail",
        "storeName": "store1"
    },
    {
        "id": 2,
        "reserveDateTime": "2023-07-03T18:30:05",
        "reserveStatus": "VALID",
        "memberEmail": "memberEmail",
        "storeName": "store2"
    }
]
```
- 단건 조회는 /reserves/owner/{reserveId}이며 결과 명세는 다음과 같습니다. (`reserveId = 1`)
```json
{
  "id": 1,
  "reserveDateTime": "2023-07-03T04:30:05",
  "reserveStatus": "VALID",
  "memberEmail": "memberEmail",
  "storeName": "store1"
}
```

- 발생할 수 있는 예외
  - `NotExistsException` - 단건 조회에서 존재하지 않는 예약을 조회하려고 할 때 발생합니다.

### 점장측에서 예약 취소 API
- URL: /reserves/owner/{reserveId}/cancel
- 입력 명세는 없습니다.
- 결과 명세 (`reserveId = 1`)
```json
{
  "id": 1,
  "reserveDateTime": "2023-07-03T04:30:05",
  "reserveStatus": "CANCEL",
  "memberEmail": "memberEmail",
  "storeName": "store1"
}
```

- 발생할 수 있는 예외
    - `NotExistsException` - 존재하지 않는 예약을 취소하려고 할 때 발생합니다.
    - `NotMatchException` - 다른 점장에게 들어온 예약을 취소시키려고 할 때 발생합니다.

### 회원이 등록한 예약 조회
- URL: /reserves/user
- 입력 명세는 없습니다.
- 결과 명세
```json
[
    {
        "id": 1,
        "reserveDateTime": "2023-07-03T04:30:05",
        "reserveStatus": "VALID",
        "memberEmail": "memberEmail",
        "storeName": "store1"
    },
    {
        "id": 2,
        "reserveDateTime": "2023-07-03T18:30:05",
        "reserveStatus": "VALID",
        "memberEmail": "memberEmail",
        "storeName": "store2"
    }
]
```

- 단건 조회는 /reserves/user/{reserveId}이며 결과 명세는 다음과 같습니다. (`reserveId = 1`)
```json
{
  "id": 1,
  "reserveDateTime": "2023-07-03T04:30:05",
  "reserveStatus": "VALID",
  "memberEmail": "memberEmail",
  "storeName": "store1"
}
```

- 발생할 수 있는 예외
    - `NotExistsException` - 단건 조회에서 존재하지 않는 예약을 조회하려고 할 때 발생합니다.

### 회원측에서 예약 취소 API
- URL: /reserves/user/{reserveId}/cancel
- 입력 명세는 없습니다.
- 결과 명세 (`reserveId = 1`)
```json
{
  "id": 1,
  "reserveDateTime": "2023-07-03T04:30:05",
  "reserveStatus": "CANCEL",
  "memberEmail": "memberEmail",
  "storeName": "store1"
}
```

- 발생할 수 있는 예외
    - `NotExistsException` - 존재하지 않는 예약을 취소하려고 할 때 발생합니다.
    - `NotMatchException` - 다른 회원이 수행한 예약을 취소시키려고 할 때 발생합니다.

### 예약 방문 처리 API
- URL: /reserves/user/{reserveId}/checkin
- 입력 명세는 없습니다.
- 결과 명세 (`reserveId = 1`)
```json
{
  "id": 1,
  "reserveDateTime": "2023-07-03T04:30:05",
  "reserveStatus": "COMPLETE",
  "memberEmail": "memberEmail",
  "storeName": "store1"
}
```

- 발생할 수 있는 예외
  - `NotMatchException` - 다른 회원이 예약한 것을 방문 처리하려고 할 때 발생합니다.
  - `InvalidReserveException` - 취소된 예약을 방문 처리하려고 할 때 발생합니다.
  - `InvalidReserveException` - 예약시간 10분 이전에 도착하지 못했을 때 발생합니다.

## 리뷰 관련 API
### 리뷰 생성 API
- URL: /reserve/user/{reserveId}/add-review
- 입력 명세
```json
{
    "rating": 4,
    "reviewContent": "요리가 매우 훌륭했습니다."
}
```

- 결과 명세
```json
{
  "id": 1,
  "rating": 4,
  "reviewContent": "요리가 매우 훌륭했습니다."
}
```

- 발생 가능한 예외
    - `NotMatchException` - 다른 회원의 예약에 대해 리뷰하려고 할 때 발생합니다.
    - `InvalidReviewException` - 이미 리뷰한 예약에 대해 다시 리뷰하려고 할 때 발생합니다.
    - `NotMatchException` - 방문 처리가 되지 않은 예약에 대해 리뷰하려고 할 때 발생합니다.
    - `InvalidReviewException` - 방문한 지 7일이 초과한 시점에서 리뷰하려고 할 때 발생합니다.

### 회원이 생성한 리뷰 조회 API
- URL: /reviews/user
- 입력 명세는 없습니다.
- 결과 명세
```json
[
    {
        "id": 1,
        "rating": 4,
        "reviewContent": "요리가 매우 훌륭했습니다."
    }
]
```

### 점장이 받은 모든 리뷰 조회 API
- URL: /reviews/owner
- 입력 명세는 없습니다.
- 결과 명세
```json
[
    {
        "id": 1,
        "rating": 4,
        "reviewContent": "요리가 매우 훌륭했습니다."
    }
]
```

### 가계가 받은 모든 리뷰 조회 API
- URL: /stores/{storeId}/reviews
- 입력 명세는 없습니다.
- 결과 명세
```json
[
    {
        "id": 1,
        "rating": 4,
        "reviewContent": "요리가 매우 훌륭했습니다."
    }
]
```