package com.team8.shop.tomatomarket.service;

import com.team8.shop.tomatomarket.dto.LoginReqDto;
import com.team8.shop.tomatomarket.dto.LoginRespDto;
import com.team8.shop.tomatomarket.dto.SignupReqDto;
import com.team8.shop.tomatomarket.entity.User;
import com.team8.shop.tomatomarket.entity.UserRoleEnum;
import com.team8.shop.tomatomarket.repository.UserRepository;
import com.team8.shop.tomatomarket.util.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Spy
    JwtUtils jwtUtils;

    @Spy
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void prepare() {
        // name : jwtUtil의 secretKey값이 저장될 변수
        // value : secretKey의 값
        ReflectionTestUtils.setField(jwtUtils,
                "secretKey",
                "7ZWt7ZW0OTntmZTsnbTtjIXtlZzqta3snYTrhIjrqLjshLjqs4TroZzrgpjslYTqsIDsnpDtm4zrpa3tlZzqsJzrsJzsnpDrpbzrp4zrk6TslrTqsIDsnpA=");
        // jwtUtil에서 @PostConstructor가 동작하지 않기 때문에, 임의로 실행시켜야 함
        this.jwtUtils.init();
    }

    @Test
    @DisplayName("회원가입(유저)")
    void signup() {

        // given
        SignupReqDto request = SignupReqDto.builder()
                .adminKey(null)
                .username("user1")
                .password("1")
                .nickname("유저1")
                .build();

        lenient().when(userRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.empty());

        // when
        userService.signup(request);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("로그인(유저)")
    void login() {

        //given - 값 넣어주는 부분 , 본인이 생각했을 때 테스트할때 필요한 값들을 지정해주는 부분
        LoginReqDto reqDto = LoginReqDto.builder().username("user").password("password").build();
        User user = new User("user", null, "password", UserRoleEnum.CUSTOMER);
        String token = "7ZWt7ZW0OTntmZTsnbTtjIXtlZzqta3snYTrhIjrqLjshLjqs4TroZzrgpjslYTqsIDsnpDtm4zrpa3tlZzqsJzrsJzsnpDrpbzrp4zrk6TslrTqsIDsnpA=";

//        when(userService._getUser(user.getUsername())).thenReturn(user); // 테스트 코드를 작성하려는 단의 메서드는 따로 when으로 지정하지 못한다. -> injectMock 특징
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
        when(user.isValidPassword(user.getPassword(), passwordEncoder)).thenReturn(true);
//        when(jwtUtils.createToken(user.getUsername(), user.getRole())).thenReturn(token);

        //when - 해당 코드가 테스트 되는 부분
        LoginRespDto login = userService.login(reqDto);

        //then - 검증에서 본인이 체크하고자 하는 메서드들을 기입
        verify(jwtUtils, times(1)).createToken(user.getUsername(), user.getRole());
    }
}