## 프로젝트 구조

- spring boot 3.2.x
- spring data jpa
- spring rest docs
- h2
- lombok

```text
└── src
   ├── main
   │  ├── generated
   │  ├── java
   │  │  └── com
   │  │     └── study
   │  │        └── todo
   │  │           ├── config
   │  │           │  └── SecurityConfig.java
   │  │           ├── security
   │  │           │  └── CustomUserDetailsService.java
   │  │           ├── todo
   │  │           │  ├── controller
   │  │           │  │  └── TodoController.java
   │  │           │  ├── domain
   │  │           │  │  ├── Todo.java
   │  │           │  │  └── TodoStatus.java
   │  │           │  ├── dto
   │  │           │  │  ├── TodoRequestDto.java
   │  │           │  │  └── TodoResponseDto.java
   │  │           │  ├── repository
   │  │           │  │  └── TodoRepository.java
   │  │           │  └── service
   │  │           │     └── TodoService.java
   │  │           ├── TodoApplication.java
   │  │           └── user
   │  │              ├── controller
   │  │              │  └── UserController.java
   │  │              ├── domain
   │  │              │  └── User.java
   │  │              ├── dto
   │  │              │  ├── UserRequestDto.java
   │  │              │  └── UserResponseDto.java
   │  │              ├── repository
   │  │              │  └── UserRepository.java
   │  │              └── service
   │  │                 └── UserService.java
   │  └── resources
   │     ├── application.properties
   │     ├── static
   │     │  ├── index.adoc
   │     │  └── index.html
   │     └── templates
   └── test
      ├── generated_tests
      └── java
         └── com
            └── study
               └── todo
                  ├── todo
                  │  ├── controller
                  │  │  ├── TodoControllerTest.java
                  │  │  └── TodoRestDocsControllerTest.java
                  │  ├── repository
                  │  │  └── TodoRepositoryTest.java
                  │  └── service
                  │     └── TodoServiceTest.java
                  ├── TodoApplicationTests.java
                  └── user
                     ├── controller
                     │  ├── UserControllerTest.java
                     │  └── UserRestDocsControllerTest.java
                     ├── repository
                     │  └── UserRepositoryTest.java
                     └── service
                        └── UserServiceTest.java
```

### API 문서관련
```text
/resources/static/index.html
```
