package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity //=> JPA가 사용하는 객체
@Table(name = "item") //=> 객체명과 테이블명이 같이면 생략 가능
public class Item {

    @Id @GeneratedValue(strategy = IDENTITY) //=> 테이블 PK와 매핑
    private Long id;

    @Column(name = "item_name", length = 10) //=> 객체 필드를 테이블 컬럼과 매핑(카멜케이스 언더스코어 자동 변환)
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() { //=> JPA는 기본 생성자가 필수(Proxy 기술 사용을 위함)
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
