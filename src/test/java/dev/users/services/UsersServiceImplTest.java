package dev.users.services;

import dev.rest.pedido.models.Pedido;
import dev.rest.pedido.repositories.PedidoRepository;
import dev.rest.users.dto.UserInfoResponse;
import dev.rest.users.dto.UserRequest;
import dev.rest.users.dto.UserResponse;
import dev.rest.users.exceptions.UserNotFound;
import dev.rest.users.exceptions.UsernameOrEmailExist;
import dev.rest.users.mapper.UsersMapper;
import dev.rest.users.models.User;
import dev.rest.users.repositories.UsersRepository;
import dev.rest.users.services.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceImplTest {
    private final UserRequest userRequest = UserRequest.builder()
            .nombre("User")
            .apellidos("User")
            .username("user")
            .email("user@gmail.com")
            .password("user1234")
            .build();

    private final User user = User.builder()
            .nombre("User")
            .apellidos("User")
            .password("user1234")
            .username("user")
            .email("user@gmail.com")
            .build();

    private final UserResponse userResponse = UserResponse.builder()
            .id(5L)
            .nombre("user")
            .apellidos("user")
            .username("user")
            .email("user@gmail.com")
            .build();
    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
            .id(5L)
            .nombre("user")
            .apellidos("user")
            .username("user")
            .email("user@gmail.com")
            .build();

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private PedidoRepository pedidosRepository;
    @Mock
    private UsersMapper usersMapper;
    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void findAll() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        Page<User> page = new PageImpl<>(users);
        when(usersRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(usersMapper.toUserResponse(any(User.class))).thenReturn(new UserResponse());

        Page<UserResponse> result = usersService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Pageable.unpaged());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.getTotalElements())
        );

        verify(usersRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
     void findById() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pedidosRepository.findByIdUsuario(userId, Pageable.unpaged())).thenReturn(Page.empty());
        when(usersMapper.toUserInfoResponse(user, List.of())).thenReturn(userInfoResponse);

        UserInfoResponse result = usersService.findById(userId, Pageable.unpaged());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userResponse.getUsername(), result.getUsername()),
                () -> assertEquals(userResponse.getEmail(), result.getEmail())
        );

        verify(usersRepository, times(1)).findById(userId);
        verify(pedidosRepository, times(1)).findByIdUsuario(userId, Pageable.unpaged());
        verify(usersMapper, times(1)).toUserInfoResponse(user, List.of());
    }

    @Test
     void findByIdUserNotFound() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> usersService.findById(userId, Pageable.unpaged()));
        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
     void save() {
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString())).thenReturn(Optional.empty());
        when(usersMapper.toUser(userRequest)).thenReturn(user);
        when(usersMapper.toUserResponse(user)).thenReturn(userResponse);
        when(usersRepository.save(user)).thenReturn(user);

        UserResponse result = usersService.save(userRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userRequest.getUsername(), result.getUsername()),
                () -> assertEquals(userRequest.getEmail(), result.getEmail())
        );

        verify(usersRepository, times(1)).findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString());
        verify(usersMapper, times(1)).toUser(userRequest);
        verify(usersMapper, times(1)).toUserResponse(user);
        verify(usersRepository, times(1)).save(user);
    }

    @Test
     void saveExists() {
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString())).thenReturn(Optional.of(new User()));
        assertThrows(UsernameOrEmailExist.class, () -> usersService.save(userRequest));
    }

    @Test
     void update() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString())).thenReturn(Optional.empty());
        when(usersMapper.toUser(userRequest, userId)).thenReturn(user);
        when(usersMapper.toUserResponse(user)).thenReturn(userResponse);
        when(usersRepository.save(user)).thenReturn(user);

        UserResponse result = usersService.update(userId, userRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userRequest.getUsername(), result.getUsername()),
                () -> assertEquals(userRequest.getEmail(), result.getEmail())
        );

        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, times(1)).findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString());
        verify(usersMapper, times(1)).toUser(userRequest, userId);
        verify(usersMapper, times(1)).toUserResponse(user);
        verify(usersRepository, times(1)).save(user);
    }

/*    @Test
     void updateUserExist() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString())).thenReturn(Optional.of(user));
        assertThrows(UsernameOrEmailExist.class, () -> usersService.update(userId, userRequest));
        verify(usersRepository, times(1)).findById(userId);
    }*/

    @Test
     void updateUserNotFound() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> usersService.update(userId, userRequest));
        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
     void delete() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pedidosRepository.existsById(userId)).thenReturn(false);
        usersService.deleteById(userId);
        verify(usersRepository, times(1)).delete(user);
        verify(pedidosRepository, times(1)).existsById(userId);
    }

    @Test
     void deleteByIdLogico() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pedidosRepository.existsById(userId)).thenReturn(true);
        doNothing().when(usersRepository).updateIsDeletedToTrueById(userId);
        usersService.deleteById(userId);
        verify(usersRepository, times(1)).updateIsDeletedToTrueById(userId);
        verify(pedidosRepository, times(1)).existsById(userId);
    }

    @Test
     void deleteByIdNotExist() {
        Long userId = 1L;
        User user = new User();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pedidosRepository.existsById(userId)).thenReturn(true);
        usersService.deleteById(userId);
        verify(usersRepository, times(1)).updateIsDeletedToTrueById(userId);
        verify(pedidosRepository, times(1)).existsById(userId);
    }

}
