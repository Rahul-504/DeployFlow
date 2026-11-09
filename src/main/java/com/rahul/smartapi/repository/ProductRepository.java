
package com.rahul.smartapi.repository;

import com.rahul.smartapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
