package core.basesyntax;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.dao.StorageDaoImpl;
import core.basesyntax.db.Storage;
import core.basesyntax.exceptions.ValidationException;
import core.basesyntax.model.User;
import core.basesyntax.service.RegistrationService;
import core.basesyntax.service.RegistrationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Feel free to remove this class and create your own.
 */
class RegistrationServiceImplTest {
    private static final int MIN_AGE = 18;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String EXPECTED_EXCEPTION =
            ValidationException.class.getSimpleName();
    private static RegistrationService registrationService;
    private static StorageDao storageDao;

    @BeforeAll
    static void beforeAll() {
        registrationService = new RegistrationServiceImpl();
        storageDao = new StorageDaoImpl();
    }

    @AfterEach
    void tearDown() {
        Storage.people.clear();
    }

    @Test
    void register_positiveTest_ok() {
        User user = new User();
        user.setAge(19);
        user.setLogin("User");
        user.setPassword("123456");
        storageDao.add(user);
    }

    @Test
    void register_newLogin_Ok() {
        User user = new User();
        user.setLogin("User");
        user.setPassword("123456");
        user.setAge(21);
        registrationService.register(user);
        User actual = storageDao.get(user.getLogin());
        Assertions.assertEquals(user, actual,
                "User should be added if user login doesnt exist");
    }

    @Test
    void register_nullUser_notOk() {
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(null),
                String.format("Should throw %s when user is null", EXPECTED_EXCEPTION));
    }

    @Test
    void register_nullLogin_notOk() {
        User user = new User();
        user.setAge(19);
        user.setPassword("123456");
        user.setLogin(null);
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(user),
                String.format("Should throw %s when login is null", EXPECTED_EXCEPTION));
    }

    @Test
    void register_WrongAre_notOk() {
        User user = new User();
        user.setLogin("User");
        user.setPassword("123456");
        user.setAge(17);
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(user),
                String.format("Should throw %s when age under %d",
                        EXPECTED_EXCEPTION, MIN_AGE));
    }

    @Test
    void register_nullAge_notOk() {
        User user = new User();
        user.setLogin("User");
        user.setPassword("123456");
        user.setAge(null);
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(user),
                String.format("Should throw %s when age is null", EXPECTED_EXCEPTION));
    }

    @Test
    void register_existingLogin_notOk() {
        User firstUser = new User();
        firstUser.setAge(19);
        firstUser.setPassword("123456");
        firstUser.setLogin("User");
        storageDao.add(firstUser);
        User secondUser = new User();
        secondUser.setAge(19);
        secondUser.setPassword("123456");
        secondUser.setLogin("User");
        Assertions.assertThrows(ValidationException
                        .class, () -> registrationService.register(secondUser),
                String.format("Should throw %s when login is already exists",
                        EXPECTED_EXCEPTION));
    }

    @Test
    void register_validPassword_Ok() {
        User user = new User();
        user.setLogin("User");
        user.setPassword("12345678910");
        user.setAge(21);
        registrationService.register(user);
        User actual = storageDao.get(user.getLogin());
        Assertions.assertEquals(user, actual,
                "User should be added if user password is valid");
    }
}