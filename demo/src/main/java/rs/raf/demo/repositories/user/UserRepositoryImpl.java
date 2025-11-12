package rs.raf.demo.repositories.user;

import org.apache.commons.codec.digest.DigestUtils;
import rs.raf.demo.entities.Active;
import rs.raf.demo.entities.User;
import rs.raf.demo.entities.UserType;
import rs.raf.demo.entities.dto.UserDto;
import rs.raf.demo.repositories.MySqlAbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl extends MySqlAbstractRepository implements UserRepository {
    @Override
    public UserDto findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String email = rs.getString("email");
                    int userId = rs.getInt("id");
                    UserDto userDto = new UserDto(userId, firstName, lastName, email);
                    return userDto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int userId = rs.getInt("id");
                    String password = rs.getString("password");
                    String userType = rs.getString("user_type");
                    String status = rs.getString("status");
                    User userDto = new User(userId, firstName, lastName, email, password, userType, status);
                    return userDto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int findIdByName(String name) {
        String sql = "SELECT id FROM users WHERE first_name = ?";


        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try {
                        System.out.println(rs.getString("id"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return rs.getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email, user_type, status FROM users";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserDto u = new UserDto();
                u.setId(rs.getInt("id"));
                u.setFirstName(rs.getString("first_name"));
                u.setLastName(rs.getString("last_name"));
                u.setEmail(rs.getString("email"));
                u.setType(rs.getString("user_type"));
                String status = rs.getString("status");
                u.setActive("ACTIVE".equalsIgnoreCase(status));
                users.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }


    @Override
    public UserDto addUser(UserDto userDto) {
        String sql = "INSERT INTO users (first_name, last_name, email, user_type, password, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, userDto.getFirstName());
            ps.setString(2, userDto.getLastName());
            ps.setString(3, userDto.getEmail());
            if(userDto.getType().equalsIgnoreCase("EVENT_CREATOR")){
                ps.setString(4, UserType.EVENT_CREATOR.name());
            }
            else if(userDto.getType().equalsIgnoreCase("ADMIN")){
                ps.setString(4, UserType.ADMIN.name());
            }
            String hashedPassword = DigestUtils.sha256Hex(userDto.getPassword());
            ps.setString(5, hashedPassword);
            ps.setBoolean(6, true);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    userDto.setId(keys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userDto;
    }


    @Override
    public UserDto updateUser(UserDto userDto) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, user_type = ? WHERE id = ?";
        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userDto.getFirstName());
            ps.setString(2, userDto.getLastName());
            ps.setString(3, userDto.getEmail());
            ps.setString(4, userDto.getType());
            ps.setInt(5, userDto.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userDto;
    }


    @Override
    public UserDto toggleActive(int id, boolean isActive) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if(isActive){
                ps.setString(1, Active.ACTIVE.name());
            }else {
                ps.setString(1, Active.INACTIVE.name());
            }
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return findById2(id);
    }

    public UserDto findById2(int id) {
        String sql = "SELECT id, first_name, last_name, email, user_type, status FROM users WHERE id = ?";
        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserDto u = new UserDto();
                    u.setId(rs.getInt("id"));
                    u.setFirstName(rs.getString("first_name"));
                    u.setLastName(rs.getString("last_name"));
                    u.setEmail(rs.getString("email"));
                    u.setType(rs.getString("user_type"));
                    String status = rs.getString("status");
                    u.setActive("ACTIVE".equalsIgnoreCase(status));
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
