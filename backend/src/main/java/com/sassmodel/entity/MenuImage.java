package com.sassmodel.entity;

import com.sassmodel.entity.base.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Organization-level menu gallery images (not tied to a single item). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "menu_images")
public class MenuImage extends TenantEntity {

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 200)
    private String caption;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}
