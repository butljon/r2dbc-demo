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
@Table(value = "aggregate")
public class Aggregate {

    @Id
    @NonNull
    @Column("a_id")
    private Long id;

    @NonNull
    @Column("a_period")
    private Long period;

    @NonNull
    @Column("a_count")
    private Long count;

    @NonNull
    @Column("a_created")
    private Instant created;

    @Column("a_updated")
    private Instant updated;

}