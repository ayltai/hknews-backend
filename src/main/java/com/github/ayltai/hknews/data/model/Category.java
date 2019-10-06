package com.github.ayltai.hknews.data.model;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public final class Category {
    @Getter
    @Setter
    private List<String> urls;

    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Indexed
    private String name;
}
