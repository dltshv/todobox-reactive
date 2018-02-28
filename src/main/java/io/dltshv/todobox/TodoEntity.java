package io.dltshv.todobox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoEntity {

    @Id
    private String id;
    private String description;

    public TodoEntity() {
    }

    public TodoEntity(String description) {
        this.description = description;
    }
}
