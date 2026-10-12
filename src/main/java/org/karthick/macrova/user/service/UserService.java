package org.karthick.macrova.user.service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.karthick.macrova.exception.BadRequestException;
import org.karthick.macrova.exception.EntityNotFoundException;
import org.karthick.macrova.shared.Mapper;
import org.karthick.macrova.user.dto.UserDTO;
import org.karthick.macrova.user.entity.User;
import org.karthick.macrova.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Mapper mapper;

  public User findUserByUsername(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      throw new EntityNotFoundException("Username not found");
    }
    return user.get();
  }

  public UserDTO findUserDTOByUsername(String username) {
    return toUserDTO(findUserByUsername(username));
  }

  public UserDTO registerUser(User user) {
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new BadRequestException("Username is already taken!");
    }

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new BadRequestException("Email is already in use!");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return toUserDTO(userRepository.save(user));
  }

  private UserDTO toUserDTO(User user) {
    return mapper.convertToDto(user, UserDTO.class);
  }
}
