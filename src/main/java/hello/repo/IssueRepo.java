package hello.repo;

import org.springframework.data.repository.CrudRepository;

import hello.entity.Issue;

public interface IssueRepo extends CrudRepository<Issue, Integer> {

}