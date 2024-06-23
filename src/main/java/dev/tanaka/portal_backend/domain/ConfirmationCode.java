package dev.tanaka.portal_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name= "confirmation_code")
@NoArgsConstructor
public class ConfirmationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="code_id")
    private Long codeId;

    @Column(name="confirmation_code")
    private Integer confirmationCode;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
