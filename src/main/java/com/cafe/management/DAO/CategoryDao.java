package com.cafe.management.DAO;

import com.cafe.management.POJO.Category;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.ls.LSInput;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();
}
