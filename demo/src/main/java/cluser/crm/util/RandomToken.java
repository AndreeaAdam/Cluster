package cluser.crm.util;

import cluser.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/**
 * @Description Generates the tokens, keys and codes
 * @title: Random Token Class
 */
@Service
public class RandomToken {
    
    @Autowired
    UserRepository userRepository;
    
    /**
     * @Description Generates a random code for registration mails
     * @return random code
     */
    public String generateRandomCode(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[9];
        random.nextBytes(bytes);
        String randomCode;
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        randomCode =encoder.encodeToString(bytes);
        return randomCode;
    }
    /**
     * @Description: Generates a random string
     * @return String
     */
    public String randomString(int stringLength){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for(int i = 0; i < stringLength; i++) {
            int index = r.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

}
