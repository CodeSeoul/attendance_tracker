package org.learnteachcode.seoul.attendance.tracker.api.member;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MemberRepository extends PagingAndSortingRepository<Member, Long> {
    List<Member> findByUsernameIgnoreCaseContaining(String name);

    Member findByUsername(String username);
}
