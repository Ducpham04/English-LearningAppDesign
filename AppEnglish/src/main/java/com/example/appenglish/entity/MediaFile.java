package com.example.appenglish.entity;




import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "media_files")
@Getter @Setter
public class MediaFile extends BaseEntity {
    private String url;
    private String type; // IMAGE, VIDEO

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;
}

