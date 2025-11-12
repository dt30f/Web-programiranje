package rs.raf.demo.repositories.user;

import rs.raf.demo.entities.User;
import rs.raf.demo.entities.dto.UserDto;

import java.util.List;

public interface UserRepository {
    public UserDto findById(int id);
    public User findByEmail(String email);
    public int findIdByName(String name);

    List<UserDto> findAll();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto toggleActive(int id, boolean isActive);
}
