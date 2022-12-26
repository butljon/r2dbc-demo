package de.butties.r2dbcdemo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Builder;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "transaction")
public class Transaction {

    @Id
    @Column("t_id")
    private Long id;

    @NonNull
    @Column("t_period")
    private Long period;

    @NonNull
    @Column("t_sequence")
    private Long sequence;

    @Column("t_created")
    private Instant created;

}