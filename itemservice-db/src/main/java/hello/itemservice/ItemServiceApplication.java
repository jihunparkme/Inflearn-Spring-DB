package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @Import(MemoryConfig.class)
 * - MemoryConfig를 설정 파일로 사용한다
 *
 * @SpringBootApplication(scanBasePackages = "hello.itemservice.web")
 * - 특정 패키지 경로만 컴포넌트 스캔을 할 경우 경로 설정
 */
@Slf4j
@Import(MyBatisConfig.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	/**
	 * @Profile("local")
	 * - 특정 프로필의 경우에만 해당 스프링 빈을 등록
	 * - /src/main/resources/application.properties -> spring.profiles.active
	 * - TestCase 실행 시에는 /src/test/resources/application.properties 파일을 우선으로 참조
	 */
	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}
}
