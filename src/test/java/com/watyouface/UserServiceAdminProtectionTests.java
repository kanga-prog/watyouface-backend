package com.watyouface;

import com.watyouface.entity.User;
import com.watyouface.entity.enums.Role;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceAdminProtectionTests {

    @Test
    void lastAdministratorCannotBeDeletedEvenByAnotherAdministrator() {
        UserRepository users = mock(UserRepository.class);
        User target = new User();
        target.setId(1L);
        target.setRole(Role.ADMIN);
        when(users.findById(1L)).thenReturn(Optional.of(target));
        when(users.countByRole(Role.ADMIN)).thenReturn(1L);

        UserService service = new UserService(users);

        assertThrows(IllegalStateException.class,
                () -> service.deleteUserSecured(1L, 2L, true));
        verify(users, never()).delete(target);
    }

    @Test
    void administratorCannotDeleteItsOwnAccount() {
        UserRepository users = mock(UserRepository.class);
        User target = new User();
        target.setId(1L);
        target.setRole(Role.ADMIN);
        when(users.findById(1L)).thenReturn(Optional.of(target));

        UserService service = new UserService(users);

        assertThrows(IllegalStateException.class,
                () -> service.deleteUserSecured(1L, 1L, true));
        verify(users, never()).delete(target);
    }
}
