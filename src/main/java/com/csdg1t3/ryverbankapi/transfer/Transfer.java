package com.csdg1t3.ryverbankapi.transfer;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Trasfer {
    private  @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;
    // @ManyToOne
    // @JoinColumn(name = "account_id", nullable = false)

    

}