package com.staffs.backend.entity.item;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Proxy(lazy = false)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(unique = true)
    private String itemName;

    private String Description;

    private boolean delFlag = false;

    private String itemRef;

    private double itemPrice;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<Packages> packages;

    private String unit;

    private double itemMinPrice;

    private boolean standalone;

    public void setPackageList(List<Packages> packages) {
        this.packages = packages;
    }

    public void setPackages(Packages packages) {
        List<Packages> packageList = new ArrayList<>();
        packageList.add(packages);
        setPackageList(packageList);
    }

}
