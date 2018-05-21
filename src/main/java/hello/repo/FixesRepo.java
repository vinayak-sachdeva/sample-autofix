package hello.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hello.entity.Fixes;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FixesRepo extends CrudRepository<Fixes, Integer> {
    List<Fixes> findAllByFixId(Integer fixId);
    @Modifying
    @Transactional
    @Query(value = "UPDATE fixes SET s3link = ?1, fixed = 1 WHERE fix_id = ?2", nativeQuery = true)
    public void updateAllByFixID(String s3Link, Integer fixId);
}