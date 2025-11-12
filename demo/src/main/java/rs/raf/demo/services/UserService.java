package rs.raf.demo.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.apache.commons.codec.digest.DigestUtils;
import rs.raf.demo.entities.Active;
import rs.raf.demo.entities.User;
import rs.raf.demo.entities.dto.UserDto;
import rs.raf.demo.repositories.user.UserRepository;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class UserService {
    @Inject
    UserRepository userRepository;

    public String login(String email, String password) {
        String hashedPassword = DigestUtils.sha256Hex(password);

        User user = this.userRepository.findByEmail(email);

        if (user == null || !user.getPassword().equals(hashedPassword)) {
            throw new RuntimeException("Invalid credentials");
        }
        if (user.getStatus() == Active.INACTIVE) {
            throw new RuntimeException("User inactive");
        }

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 24 * 60 * 60 * 1000); // One day

        Algorithm algorithm = Algorithm.HMAC256("secret");

        return JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withSubject(email)
                .withClaim("role", user.getUserType().toString())
                .sign(algorithm);
    }


    public boolean isAuthorized(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            String email = jwt.getSubject();

            User user = this.userRepository.findByEmail(email);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    public List<UserDto> findAll() {return userRepository.findAll();}
    public UserDto create(UserDto userDto) {return userRepository.addUser(userDto);}
    public UserDto update(UserDto userDto) {return userRepository.updateUser(userDto);}
    public UserDto toggleActive(int id, boolean isActive) {return userRepository.toggleActive(id, isActive);}

    public String getUserRoleFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            // Izvlačimo claim "role" iz tokena
            String role = jwt.getClaim("role").asString().toLowerCase();
            return role;

        } catch (Exception e) {
            return null; // token nije validan
        }
    }
}
