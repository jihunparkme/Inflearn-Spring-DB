package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    /**
     * 트랜잭션 매니저인 PlatformTransactionManager 를 주입 받으면 등록한 DataSourceTransactionManager 주입
     */
    @Autowired PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource); // 등록한 TransactionManager를 주입
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");
        /**
         * Creating new transaction with name [null]:
         * Acquired Connection [HikariProxyConnection@675475202 wrapping conn0: ...] for JDBC transaction
         * Switching JDBC Connection [HikariProxyConnection@675475202 wrapping conn0: ... to manual commit
         */
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        /**
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@675475202 wrapping conn0: ...]
         * Releasing JDBC Connection [HikariProxyConnection@675475202 wrapping conn0: ...] after transaction
         */
        txManager.commit(status);
        log.info("트랜잭션 커밋 완료");
        /**
         * Closing JPA EntityManagerFactory for persistence unit 'default'
         */
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        /**
         * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * Acquired Connection [HikariProxyConnection@2120431435 wrapping conn0: ...] for JDBC transaction
         * Switching JDBC Connection [HikariProxyConnection@2120431435 wrapping conn0: ...] to manual commit
         */
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 롤백 시작");
        /**
         * Initiating transaction rollback
         * Rolling back JDBC transaction on Connection [HikariProxyConnection@675475202 wrapping conn0: ...]
         * Releasing JDBC Connection [HikariProxyConnection@675475202 wrapping conn0: ...] after transaction
         */
        txManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }
}