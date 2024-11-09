package com.example.fibonacci.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Fibonaccis", schema = "public")
public class Fibonacci {

    @Id
    private Integer position;

    @NotNull
    @Column(nullable = false)
    private Long value;

    @NotNull
    @Column(nullable = false)
    private Integer times = 0;
}
