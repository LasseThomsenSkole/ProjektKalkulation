package repository;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.PSource;

@org.springframework.stereotype.Repository
public class Repository {
    @Value("${spring.datasource.url}")
    private String db_url;
    @Value("${spring.application.name}")
    private String username;
    @Value("${spring.datasource.password}")
    private String pwd;

    public void test(){
        System.out.println(username);
    }
}
