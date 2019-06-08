package nl.vea.samples.spring.security.boot.auth2login.encrypt;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Security;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordEncryptionIT {
    private static final Logger logger = LoggerFactory.getLogger(PasswordEncryptionIT.class);

    // Derived from Spring Boot Application startup logging:
    // c.u.j.encryptor.DefaultLazyEncryptor     :
    // String Encryptor custom Bean not found with name 'jasyptStringEncryptor'.
    // Initializing Default String Encryptor
    @Autowired
    private DefaultLazyEncryptor jasyptStringEncryptor;

    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Value("${jasypt.encryptor.providerName}")
    private String provider;

    @Value("${application.test.fake-password}")
    private String testPassword;

    @Value("${application.test.enc-fake-password}")
    private String decryptedTestPassword;

    @Before
    public void setUp() throws Exception {
        logger.debug("jasypt.encryptor.algorithm={}", algorithm);
        logger.debug("jasypt.encryptor.providerName={}", provider);

    }

    @Test
    public void test(){
        logger.debug("unencrypted testPassword={}", testPassword);
        logger.debug("decrypted testPassword={}", decryptedTestPassword);
        final String localEncryptedPassword = jasyptStringEncryptor.encrypt(testPassword);
        logger.debug("Encrypted value={}", localEncryptedPassword);
        assertEquals(testPassword, jasyptStringEncryptor.decrypt(localEncryptedPassword));
        assertEquals(testPassword, decryptedTestPassword);
    }

}
