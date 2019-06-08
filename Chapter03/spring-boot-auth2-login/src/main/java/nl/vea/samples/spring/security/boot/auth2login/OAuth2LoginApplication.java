package nl.vea.samples.spring.security.boot.auth2login;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
@EnableEncryptableProperties
public class OAuth2LoginApplication {

    static {
        // Adding the security provider as early as possible during classloading
        Security.addProvider(new BouncyCastleProvider());
        LoggerFactory.getLogger(OAuth2LoginApplication.class).debug("Loaded BouncyCastle as Provider during classloading of BareJasyptApplication");
    }

    public static void main(String[] args) {
        SpringApplication.run(OAuth2LoginApplication.class, args);
    }
}
