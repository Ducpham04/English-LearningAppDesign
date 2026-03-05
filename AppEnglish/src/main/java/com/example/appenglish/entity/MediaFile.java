package com.example.AppEnglish.entity;




import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "media_files")
@Getter @Setter
public class MediaFile extends BaseEntity {
    private String url;
    private String type; // IMAGE, VIDEO

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;
}

