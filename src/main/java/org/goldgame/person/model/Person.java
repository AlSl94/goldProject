package org.goldgame.person.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class Person {
    private Long id;
    private String name;
}
