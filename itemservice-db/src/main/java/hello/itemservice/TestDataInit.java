package hello.itemservice;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemRepository itemRepository;

    /**
     * 확인용 초기 데이터 추가 (메모리 활용 시)
     * 애플리케이션 실행 시 초기 데이터 저장
     *
     * @EventListener(ApplicationReadyEvent.class)
     * - AOP를 포함한 스프링 컨테이너가 초기화를 완전히 끝내고, 실행 준비가 되었을 때 발생하는 이벤트
     *
     * @PostConstruct
     * - AOP(ex. @Transactional 관련)가 처리되지 않은 시점에 호출될 수 있기 때문에, 간혹 문제 발생
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("test data init");
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
