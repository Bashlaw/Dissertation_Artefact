package com.staffs.backend.entity.packages;

import com.staffs.backend.entity.item.Item;
import com.staffs.backend.entity.packageType.PackageType;
import com.staffs.backend.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.Proxy;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Proxy(lazy=false)
public class Packages extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    @Column(unique = true)
    private String packageName;

    private String description;

    private Long duration;

    private boolean activation;

    private boolean recurring;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private PackageType packageType;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Item> itemList;

    public void setItems(List<Item> items) {
        this.itemList = items;
    }

    public void setItems(Item item) {
        List<Item> items = new ArrayList<>();
        items.add(item);
        setItems(items);
    }

}
