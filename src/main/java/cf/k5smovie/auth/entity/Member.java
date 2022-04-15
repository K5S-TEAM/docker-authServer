package cf.k5smovie.auth.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(length = 11, unique = true)
    private String phone;

    @Column(length = 128, unique = true)
    private String email;

    @Column(length = 8, nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String passwordHash;

    public Member(String name, String phone, String email, String birthDate, String passwordHash) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
        this.passwordHash = passwordHash;
    }
}
