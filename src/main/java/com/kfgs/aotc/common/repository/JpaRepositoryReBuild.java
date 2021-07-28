package com.kfgs.aotc.common.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
@NoRepositoryBean
public class JpaRepositoryReBuild<T, ID>extends SimpleJpaRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;

    @Autowired
    public JpaRepositoryReBuild(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        ArrayList list = new ArrayList(3000);
       for(S s: entities){
           em.persist(s);
           list.add(s);
       }
       return list;
        //return super.saveAll(entities);
    }
}
