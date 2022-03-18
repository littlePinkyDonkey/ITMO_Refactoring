package teplykh.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "exp")
public class Expression {
    @Id
    @Column(name = "exp_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "storage_path")
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}
