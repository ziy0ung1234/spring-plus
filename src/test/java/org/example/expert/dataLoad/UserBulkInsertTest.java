package org.example.expert.dataLoad;

import org.example.expert.ExpertApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.username=${DB_USERNAME}",
        "spring.datasource.password=${DB_PASSWORD}",
        "spring.datasource.url=${DB_URL}"
})
public class UserBulkInsertTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void 유저_500만건_JDBC_Bulk_Insert_테스트() throws Exception{
        int TOTAL_COUNT = 5000000;
        int BATCH_SIZE = 10000;

        long start = System.currentTimeMillis();
        try(Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            String sql = """
                    INSERT INTO users (
                        email,
                        password,
                        user_role,
                        nickname,
                        created_at,
                        modified_at
                    ) VALUES (?,?,?,?,?,?);
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (int i = 1; i <= TOTAL_COUNT; i++) {
                    String nickname = "user_" + i;
                    String email = nickname + "@test.com";

                    LocalDateTime now = LocalDateTime.now();

                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, "example-password");
                    preparedStatement.setString(3, "USER");
                    preparedStatement.setString(4, nickname);
                    preparedStatement.setTimestamp(5, Timestamp.valueOf(now));
                    preparedStatement.setTimestamp(6, Timestamp.valueOf(now));
                    preparedStatement.addBatch();

                    if(i%BATCH_SIZE==0){
                        preparedStatement.executeBatch();
                        connection.commit();
                        preparedStatement.clearBatch();
                    }
                }
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("총 소요시간: "+(end-start));
    }
}
