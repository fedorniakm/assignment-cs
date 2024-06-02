package com.fedorniakm.demo.persistence.repository;

import com.fedorniakm.demo.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Transactional
@Log4j2
public class JpaUserRepository implements UserRepository {

    private final EntityManager em;

    public JpaUserRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<UserEntity> getAll() {
        return em.createQuery("FROM UserEntity", UserEntity.class).getResultList();
    }

    @Override
    public List<UserEntity> getAll(Optional<LocalDate> from, Optional<LocalDate> to) {
        var jpql = "from UserEntity u";
        if (from.isPresent() || to.isPresent()) {
            jpql += " where";
            if (from.isPresent()) {
                jpql += " u.birthDate > :from";
            }
            if (to.isPresent()) {
                if (from.isPresent()) {
                    jpql += " and";
                }
                jpql += " u.birthDate < :to";
            }
        }
        var query = em.createQuery(jpql, UserEntity.class);
        from.ifPresent(fromDate -> query.setParameter("from", fromDate));
        to.ifPresent(toDate -> query.setParameter("to", toDate));
        return query.getResultList();
    }

    @Override
    public Optional<UserEntity> getById(Long id) {
        return Optional.ofNullable(em.find(UserEntity.class, id));
    }

    @Override
    public UserEntity create(UserEntity user) {
        em.persist(user);
        return user;
    }

    @Override
    public boolean update(UserEntity user) {
        var target = em.find(UserEntity.class, user.getId());
        if (Objects.nonNull(target)) {
            em.merge(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        var target = em.find(UserEntity.class, id);
        if (Objects.nonNull(target)) {
            em.remove(target);
            return true;
        }
        return false;
    }

}
