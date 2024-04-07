package com.staffs.backend.entity.paymentSource;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.staffs.backend.entity.country.Country;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Proxy(lazy = false)
public class PaymentSource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentSourceId;

    @Column(unique = true, nullable = false)
    private String sourceCode;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<Country> countryList;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PaymentURL> urlList;

    public void setUrls(List<PaymentURL> urls) {
        this.urlList = urls;
    }

    public void setUrls(PaymentURL url) {
        List<PaymentURL> urls = new ArrayList<>();
        urls.add(url);
        setUrls(urls);
    }

    public void setCountries(List<Country> countries) {
        this.countryList = countries;
    }

    public void setCountries(Country country) {
        List<Country> countries = new ArrayList<>();
        countries.add(country);
        setCountries(countries);
    }

}
