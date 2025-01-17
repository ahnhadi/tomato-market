package com.team8.shop.tomatomarket.util.jwt;

import com.team8.shop.tomatomarket.entity.UserRoleEnum;
import com.team8.shop.tomatomarket.security.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Component
@NoArgsConstructor
public class JwtUtils {

    private UserDetailsServiceImpl userDetailsService;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60*60*1000L;

    public JwtUtils(UserDetailsServiceImpl userDetailsService){
        this.userDetailsService = userDetailsService;
    }


    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    @PostConstruct// 객체 생성시 초기화 해주는 기능
    public void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }



    // 토큰 생성
    public String createToken(String username, UserRoleEnum role){
        Date date = new Date();
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 토큰 정보 안에 username을 넣어줌
                        .claim(AUTHORIZATION_KEY, role) // 권한 가져오기
                        .setExpiration(new Date(date.getTime()+TOKEN_TIME))  // 토큰 만료 기한 설정
                        .setIssuedAt(date) // 토큰 생성 시점
                        .signWith(key, signatureAlgorithm) // secret key를 이용해 만든 key를 어떤 알고리즘으로 암호화 할 것인지
                        .compact();
    }


    // Header에서 토큰 가져오기
    public String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }


    // 토큰 검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            // 토큰 검증 시스템
            return true;
        } catch (SecurityException | MalformedJwtException e){
            throw new IllegalArgumentException("유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e){
            throw new IllegalArgumentException("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e){
            throw new IllegalArgumentException("지원되지 않은 토큰입니다.");
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }
    }


    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


    // 인증 객체 생성
    public Authentication createAuthentication(String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

