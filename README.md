# hexagonal

https://www.yes24.com/product/goods/105138479

# 들어가기 전.

## 도메인계층

- 비즈니스 규칙과 상태 그리고 그에따른 로직
- 외부의 다른 계층에 의존하지 않는 순수한 비즈니스 객체

## 서비스 계층

- 도메인 계층에 정의된 핵심 로직들을 엮어 실제 사용 사례(UseCase)나 비즈니스 프로세스를 완성시키는
  역할
- 데이터 영속성 처리, 트랜잭션 고나리, 외부 API 호출 등

## 만약에

사용자가 자신의 비밀번호를 변경하는 상황입니다.

### 도메인 계층

```jsx
public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 비밀번호 변경이라는 핵심 비즈니스 로직을 수행합니다.
     * 이 로직은 User 객체의 상태를 직접 변경합니다.
     */
    public void changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("새로운 비밀번호는 비어 있을 수 없습니다.");
        }
        this.password = newPassword;
    }

    // Getter 및 기타 메소드
```

### 서비스 계층

```jsx
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * '사용자 비밀번호 변경'이라는 사용 사례(Use Case)를 처리합니다.
     * 여러 컴포넌트(도메인, 리포지토리)의 흐름을 조율합니다.
     */
    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        // 1. 사용자 조회 (데이터베이스 상호작용)
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 2. 핵심 로직 위임 (도메인 객체의 비즈니스 로직 호출)
        user.changePassword(oldPassword, newPassword);

        // 3. 변경된 상태 저장 (데이터베이스 상호작용)
        userRepository.save(user);

        // 4. (필요 시) 변경 완료 이메일 발송 등 추가 작업
        // emailService.sendPasswordChangeNotification(user.getEmail());
    }
}
```

패스워드의 유효성검사는 서비스 계층에서 충분히 이루어질수 있는 상황입니다.

굳이 도메인 계층에서 changePassword를 호출하여야 할까요?

e.g)

```jsx
    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        // 1. 사용자 조회 (데이터베이스 상호작용)
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

				validaitonPassword(oldPassword, newPassword);
				// 올바르지 않은 패스워드는 validationPassword 에서 걸러지므로
        user.setPassword(newPassword);
        userRepository.save(user);

        
        
    }
    public void validationPassword(String oldPassword, String newPassword){
		    if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("새로운 비밀번호는 비어 있을 수 없습니다.");
        }
    }
```

이렇게 진행한다면 좀 더 직관적인 코드를 볼 수 있으니 유리하지 않나 싶습니다

하지만 해당 플로우로 패스워드 변경 기능수행을 한다면

User 도메인은 UserService에 의존적이게 됩니다. (스스로 패스워드를 바꾸기 위한 규칙을 지정할 수 없으므로)

→ 분리 된 예시를 다시한번 본다면 User 도메인은 changePassword 메소드 자체로 UserService 없이 패스워드 유효성 검사 진행 후 변경이 가능해집니다.

## 단일책임원칙

- 하나의 책임을 가져야 하고 하나의 변경 이유만을 가져야 하는 원칙
- e.g) 유저 도메인에 변경 사항이 생겼다면 회원 포인트 규칙을 변경한다고 하였을 때 포인트 도메인만 변경되어야 합니다.

## 도메인 영역에 대한 영속성 영역 의존

```jsx
     [Domain Layer]
     ┌────────────────────────┐
     │     UserService        │
     │  (도메인 서비스 클래스)  │
     └────────────────────────┘
                │
      직접 의존 ▼
     ┌────────────────────────┐
     │    UserRepository      │
     │ (JPA, MyBatis 구현체)   │
     └────────────────────────┘
       [Persistence Layer]
```

- UserService가 UserRepository를 의존
  도메인 영역이 영속성 영역을 의존한다는 의미

### 의존성 역전 원칙을 통해 해결

```jsx
     [Domain Layer]
     ┌────────────────────────┐
     │     UserService        │
     │  (도메인 서비스 클래스)  │
     └────────────────────────┘
                │
                ▼
     [Interface: Repository Port]
     ┌────────────────────────┐
     │    IUserRepository     │◀─────── Interface (Domain 내 정의)
     │  (도메인 레이어 인터페이스) │
     └────────────────────────┘
                ▲
                │ implements
                ▼
     [Persistence Layer]
     ┌────────────────────────┐
     │    UserRepository      │
     │ (JPA, MyBatis 등 구현체)│
     └────────────────────────┘
```

- 실제 도메인 영역인 UserService는 IUserRepository를 의존하여 의존성을 역전시킴
  → 영속성 레이어가 변경되어도 해당 인터페이스에 맞게 개발된다면 도메인영역에선 변경점이 생기지않음

### 빈약한 도메인 모델

```jsx
// 도메인 객체
public class Account {
    private double balance;
    // getters and setters ...
}

// 서비스 계층
public class AccountService {
    public void transfer(Account source, Account target, double amount) {
        if (source.getBalance() < amount) {
            throw new InsufficientFundsException();
        }
        source.setBalance(source.getBalance() - amount);
        target.setBalance(target.getBalance() + amount);
    }
}
출처: https://sunrise-min.tistory.com/entry/빈약한-도메인-모델Anemic-Domain-Model [내가 보기 위한 기록:티스토리]
```

### 풍부한 도메인 모델

```jsx
// 도메인 객체
public class Account {
    private double balance;

    public void debit(double amount) {
        if (this.balance < amount) {
            throw new InsufficientFundsException();
        }
        this.balance -= amount;
    }

    public void credit(double amount) {
        this.balance += amount;
    }

    // getters and setters ...
}

// 서비스 계층
public class AccountService {
    public void transfer(Account source, Account target, double amount) {
        source.debit(amount);
        target.credit(amount);
    }
}
출처: https://sunrise-min.tistory.com/entry/빈약한-도메인-모델Anemic-Domain-Model [내가 보기 위한 기록:티스토리]
```

### 빈약한 도메인 모델이 안티패턴인 이유

- 객체는 상태와 행위를 가질 수 있는데, 빈약한 도메인 모델에서는 행위가 객체 외부에 있기때문에 
객체지향의 장점과 특성을 활용 할 수없음
- 서비스 계층에 분산되어 있으면 비슷한 로직이 여러 서비스에서 중복될 수 있음

## 헥사고날 아키텍처 선택 이유

### 계층형 아키텍처에서는 도메인 코드와 영속성 코드의 분리가 어렵다.

- 도메인 계층에서 영속성 계층을 사용하게 된다면 도메인 계층이 영속성 계층에 의존적이게 되어 후에 분리할 수 없게 된다.

### 지름길을 택하기 쉬워진다.

계층형 아키텍처는 기본적으로 위에서 아래로 의존하는 규칙이 정해져 있습니다.

그래서 개발을 진행하다 보면 Service 객체에서 유틸리티 로직을 사용할 일이 생기는데 ,

그럴 경우 유틸리티 객체를 영속성 레이어로 내려야 합니다.(위의 규칙떄문)

# 헥사고날 아키텍처

## 입력유효성 검증

- 구문 상의 유효성을 검증하는 것

### 비즈니스 규칙 검증

- 유스케이스 맥락 속에서 의미적인 유효성을 검증

## 입력유효성 검증과 비즈니스 규칙 차이점

e.g) 출금 계좌는 초과 출금해서는 안된다 

→ 출금 계좌와 입금 계좌가 존재하는지 모델의 상태에 접근해야 하기 떄문에 비즈니스 규칙

e.g) 송금되는 금액은 0보다 커야한다.

→ 모델에 접근하지 않고도 금액이라는 데이터로 검증될 수 있으므로 입력 유효성 검증

## 비즈니스 규칙 검증 구현 위치

- 풍부한 도메인 모델을 따라서 도메인 엔티티에 넣음

```kotlin
class Acount{
...
	fun withDraw(money:Money, targetAccountId:AccountId){
		return mayWithdraw(money)
	}
}
```

- 계좌에 대한 규칙을 도메인 모델에서 선언하면 재사용 할수있으며, 객체의 상태를 자체적으로 검증할 수 있음

### 실제 데이터베이스에 접근 후 검증해야하는 비즈니스 규칙이라면?

- 유스케이스에서 비즈니스 규칙을 검증해도 되지만 해당 유스케이스에서 데이터까지만 로드하고 위 코드처럼 도메인 레벨에서 검증하는 것이 더 좋아보임 (개인적인 생각)
→ e.g) “이석운” 사용자는 출금계좌에 초과 출금을 해도된다라는 비즈니스 규칙이라면 
이석운 사용자에 대한 데이터로드를 유스케이스 코드에서 구현하고 실제 규칙은 Account 도메인에서 구현하는 방법

### 왜 풍부한 도메인 모델이 좋아보입니까?

- 어떠한 비즈니스 규칙을 유스케이스 레벨에서 구현한다면 각 유스케이스마다 비즈니스 규칙을 검증하거나 공통된 함수를 만들어 적용해야 함.

### 그럼 빈약한 도메인 모델을 사용 안해도 되나요?

- 구현하는 프로젝트에 비즈니스 규칙 자체가 공통되는 부분이 많이 없거나, 유스케이스 별로 다른 규칙을 가져가야한다면 오히려 도메인 레벨에서 규칙검증을 하는 것이 더 헷갈릴지도?

## 어플리케이션 영역

비즈니스 로직과 외부 영역(I/O, DB)을 조율하는 영역

### 유스케이스

특정 비즈니스 시나리오에 대한 처리 단위

- 회원가입등의 특정 시나리오에 대한 처리단위를 묶은 객체

### Port

- 외부로부터 입력을 받고 도메인에 전달하기 위한 객체
- Input Port : 애플리케이션 핵심로직에 대한 요청을 받는 인터페이스
- OutPut Port: 애플리케이션이 외부로 데이터를 전달할때 사용하는 인터페이스

### DTO, Command, Query

데이터 전달객체

- DTO : 단순한 데이터를 주고받기 위한 객체
- Command : CQRS에 따른 쓰기전용 DTO
- Query : CQRS에 따른 읽기전용 DTO

## 웹 어댑터

- 웹 요청을 처리하는 영역

### 역할

입력 유효성 검증 : 웹 어댑터 영역의 입력 유효성 검증

→ 유스케이스에서 진행하는 입력 유효성 검증과는 다름. 각 비즈니스 규칙에 따라 검증해야 하는 입력유효성 검증이 달라질수 있기 떄문

유스케이스 호출(Input Port) 

유스케이스의 출력을 HTTP로 매핑

HTTP 응답을 반환

### 애플리케이션 영역에서는 HTTP 요청에 대한 로직이 들어가면 안됩니다.

애플리케이션 영역에 HTTP 등 외부 요인에 대한 의존도가 생기면 HTTP 관련된 요청만 처리할 수 있게됨.

관련된 책임은 웹 어댑터 영역에서 해결하고 애플리케이션 영역에서는 도메인에 대한 역할만 수행해야함.

## 영속성 어댑터

- Output Port
- 데이터 관련 역할을 처리하는 영역

### 역할

1. 입력을 데이터베이스 포맷으로 변환 
2. 데이터베이스에 데이터를 전달
3. 데이터베이스 출력을 에플리케이션 포맷으로 변환
4. 출력 반환

## 테스트

### 단위테스트

- Domain
- Usecase

### 통합테스트

- 어댑터

### 시스템 테스트

- 사용자가 취할 수 있는 중요 애플리케이션 경로는 시스템 테스트로 커버

### 헥사고날 아키텍처에서 테스트코드

각 영역별 분리가 잘 되어있기때문에 명확하게 테스트 코드를 분리할 수 있다.

각 Port는 모킹 데이터로 채워넣어 테스트 할수도 있고 

도메인 영역은 단위테스트 영역으로 충분히 커버할 수 있음

### 시스템 테스트는 단위, 통합 테스트와 겹치는 부분이 많던데 꼭 구현해야합니까?

각 영역 별 매핑 데이터에 대한 검증이 가능해지기도 하고, 시스템 테스트를 통해 중요 시나리오들이 커버 된다면

최신 변경사항들이 애플리케이션을 망가뜨리지 않았음을 가정 할 수 있음

### 특정 코드를 어디서 테스트할 지 가늠이 안된다면

잘못된 아키텍처로 개발하고 있다는 신호이기도 하다.

테스트는 아키텍처에 대해 경고하고 유지보수 가능한 코드를 만들기 위한 가이드의 역할을 할 수 있다
## 경계 간 매핑하기

### 매핑하지 않기 전략

웹 계층부터 영속성 계층까지 모두 같은 도메인 모델을 사용하는 것

- 매핑 코드에 대한 오버헤드가 줄어듬
- 해당 도메인 모델은 각 계층과 관련된 이유로 변경되어야 하기 때문에 단일 책임 원칙 위반
- 간단한 CRUD 유스케이스만 있다면 오히려 계층 별 매핑 작업은 불필요함
- 어플리케이션 영역에서 영속성 변경 작업을 진행해야 한다면 양방향 매핑으로 변경

### 양방향 매핑

각 영역별 모델을 따로 두는 것, 웹 계층에서는 웹 모델을 인커밍 포트에서 필요한 도메인 모델로 매핑하고

인커밍포트에 의해 반한된 도메인 객체를 다시 웹 모델로 매핑

영속성 계층은 아웃고잉 포트가 사용하는 도메인 모델과 영속성 모델 간의 매핑과 유사한 매핑을 담당한다

- 단일 책임 원칙을 만족함
- 웹이나 영속성 관심사로 오염되지 않은 깨끗한 도메인 모델 유지가능
- 보일러 플레이트 코드가 많이 생김
- 도메인 모델이 각 계층 경계를 넘어서 통신하는 데 사용되고 있음

### 완전 매핑

각 연산마다 별도의 입출력 모델을 사용

Controller : 웹 모델

Usecase : Command 

Service : Domain Model

OutPort : StateCommand

Adapter : 영속성 모델 (Entity)

- 여러 유스케이스 요구사항을 함께 다뤄야 하는 매핑에 비해 구현하고 유지보수하기 쉽다
- 보일러 플레이트 코드가 굉장히 많이 생긴다.

### 단방향 매핑

도메인 모델이 의존하는 인터페이스 모델을 사용하는 것, 각 계층은 해당 인터페이스를 구현한다.

- 도메인 모델 자체에는 풍부한 행동을 구현할 수 있고
- 도메인 모델이 각 계층에 노출되어 있지 않음
- 실수로 도메인 계층의 상태를 변경하는 일이 발생하지 않음

### 해당 매핑 전략들은 언제언제 사용하는 것이 좋은가?

- 각 매핑전략을 사용한 근거만 있으면 문제되지 않음

**변경 유스케이스(쓰기)** 

- 웹 계층과 애플리케이션 계층 사이에는 완전 매핑전략이 좋음
    
    → 유스케이스 별 유효성 검증 규칙이 명확해지고 특정 유스케이스에서 필요하지 않은 필드를 다루지 않아도됨
    
- 애플리케이션과 영속성 계층사이에는 매핑 오버헤드를 줄이기 위한 매핑하지 않기 전략을 선택지로 둠
    - 애플리케이션 계층에서 영속성 문제를 다루게 된다면 양방향 매핑 전략으로

**쿼리 유스케이스 (읽기)**

- 매핑하지 않기 전략이 첫번째 선택지가 되어야 하나, 영속성 문제를 각 영역에서 다뤄야 한다면 각각 양방향 매핑전략이 좋음

- ## 아키텍처 경계 강제하기

### Package-Private 접근 제한자
 
- 모듈 내에 있는 클래스들은 서로 접근 가능하지만 패키지 바깥에서 접근할 수 없다.
    
    → 어댑터를 바깥 영역에서 의존하는 잘못된 아키텍처를 방지할 수 있음
    
- 자바 패키지를 통해 클래스들을 응집적인 모듈로 만들어 줌
- 의존성 주입은 리플렉션을 이용하기 때문에 package-private 제한자여도 주입할 수 있음

## 인커밍 포트는 필요없지 않을까?

인프라 영역과 어플리케이션 영역 분리를 위한 아웃고잉 포트를 사용하는 것은 합당하다고 봅니다만,

굳이 프레젠테이션 영역과 어플리케이션 분리를 위해 인커밍 포트를 써야할까?

프레젠테이션 영역은 인프라스트럭쳐 영역과 달리 선택지가 많이 없고, 거의 다 REST API로 구축이 될텐데..?

### 프로젝트 규모에 따라 선택해야한다.

모든 제어 흐름을 인커밍 포트의 도움 없이 단숨에 파악할 수 있다면 없는 것이 편한 게 맞음

그러나 애플리케이션 규모가 이후로도 작게 유지 된단 보장도 없고 인커밍 어댑터가 계속 하나밖에 없을 것이라고 확신할 수 없음

또한 현재는 일반적인 REST API를 지원하나, 추후에 GraphQL이 도입이 될수도 있기 때문에 분리해두는 것이 유리함

→ 단 가능하려면 어플리케이션 영역의 단일 책임 원칙이 잘 지켜져야 함
