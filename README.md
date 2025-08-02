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
도메인 영역이    영속성 영역을 의존한다는 의미

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

- 도메인 계층에서 영속성 계층을 사용하게 된다면  도메인 계층이 영속성 계층에 의존적이게 되어 후에 분리할 수 없게 된다.

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
