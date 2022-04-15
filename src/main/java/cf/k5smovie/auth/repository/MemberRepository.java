package cf.k5smovie.auth.repository;

import cf.k5smovie.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Member> findByPhoneAndNameAndBirthDate(String phone, String name, String birthDate);
}
