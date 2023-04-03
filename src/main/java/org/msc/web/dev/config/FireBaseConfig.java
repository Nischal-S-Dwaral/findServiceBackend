package org.msc.web.dev.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class FireBaseConfig{

    @Bean
    public void createFireBaseApp() {

        try {
            File configFile = ResourceUtils.getFile("classpath:config/findservice-firebase-adminsdk.json");

            FileInputStream serviceAccount =
                    new FileInputStream(configFile);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://findservice-4922d-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception exception) {
            log.error("Failed to initialise connection to FireBase: "+exception.getMessage());
        }
    }

}
