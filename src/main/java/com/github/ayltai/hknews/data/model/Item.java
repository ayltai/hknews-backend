package com.github.ayltai.hknews.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Document
public final class Item {
    public static final String FIELD_PUBLISH_DATE = "publishDate";

    //region Variables

    @Getter
    @Setter
    private String recordId;

    @Getter
    @Setter
    @Id
    @JsonIgnore
    private ObjectId _id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Indexed(unique = true)
    private String url;

    @Getter
    @Setter
    @Indexed(direction = IndexDirection.DESCENDING)
    private Date publishDate;

    @Getter
    @Setter
    @Indexed
    @DBRef
    private Source source;

    @Getter
    @Setter
    @Indexed
    private Category category;

    @NonNull
    @Getter
    @Setter
    private List<Image> images = new ArrayList<>();

    @NonNull
    @Getter
    @Setter
    private List<Video> videos = new ArrayList<>();

    //endregion
}
