package book.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class JwtUtils {
    //密钥
    private static final String SING = "BaiSheHuaSheng";
    /**
     * 生成token
     */
    public String getToken(Long userId){
        Calendar instance = Calendar.getInstance();
        //默认7天过期
        instance.add(Calendar.HOUR,5);
        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();

        String user = String.valueOf(userId);

        return builder
                .withIssuer("http://loclhost:8080")
                .withSubject(user)
                .withExpiresAt(instance.getTime())//有效期
                .sign(Algorithm.HMAC256(SING));
    }
}