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

    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        /**
         * Creating new transaction with name [null]
         * Acquired Connection [HikariProxyConnection@2120431435 wrapping conn0: ...] for JDBC transaction -> 트랜잭션1 시작, 커넥션 풀에서 conn0 커넥션 획득
         * Switching JDBC Connection [HikariProxyConnection@2120431435 wrapping conn0: ...] to manual commit
         */
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        /**
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@2120431435 wrapping conn0: ...]
         * Releasing JDBC Connection [HikariProxyConnection@2120431435 wrapping conn0: ...] after transaction -> 트랜잭션1 커밋, 커넥션 풀에 conn0 커넥션 반납
         */
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        /**
         * Creating new transaction with name [null]
         * Acquired Connection [HikariProxyConnection@1567077043 wrapping conn0: ...] for JDBC transaction -> 트랜잭션2 시작, 커넥션 풀에서 conn0 커넥션 획득
         * Switching JDBC Connection [HikariProxyConnection@1567077043 wrapping conn0: ...] to manual commit -> 트랜잭션2 커밋, 커넥션 풀에 conn0 커넥션 반납
         */
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋");
        /**
         * Initiating transaction commit
         * Committing JDBC transaction on Connection [HikariProxyConnection@1567077043 wrapping conn0: ...]
         * Releasing JDBC Connection [HikariProxyConnection@1567077043 wrapping conn0: ...] after transaction
         */
        txManager.commit(tx2);
    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        txManager.rollback(tx2);
    }

    @Test
    void inner_commit() {
        /**
         * Creating new transaction with name [null]
         * ...
         * Switching JDBC Connection [..] to manual commit => on auto commit
         * outer.isNewTransaction()=true
         */
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        /**
         * Participating in existing transaction => 외부 트랜잭션에 참여
         * inner.isNewTransaction()=false
         */
        log.info("내부 트랜잭션 추가 시작 (외부 트랜잭션에 참여)");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());

        /**
         * nothing
         */
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        /**
         * Initiating transaction commit
         * Committing JDBC transaction on Connection
         * Releasing JDBC Connection
         */
        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);
    }
}