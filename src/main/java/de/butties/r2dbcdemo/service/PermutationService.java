package de.butties.r2dbcdemo.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

import de.butties.r2dbcdemo.domain.PermutationRequest;

@Service
public class PermutationService {

    public Mono<List<List<Integer>>> calculatePermutation(PermutationRequest permutationRequest) {
        int i;
        List<Integer> sample = new ArrayList<>();
        for(i=0; i<= permutationRequest.getSampleSize(); i++) {
            sample.add(Integer.valueOf(i));
        }

        return Mono.just(subsets(new ArrayList<>(), sample, permutationRequest.getChoices()));
    }

    static List<List<Integer>> subsets(List<Integer> root, List<Integer> in, int m) {
        if (m < 1) {
            return Collections.singletonList(root);
        }
        return in.stream()
                .flatMap(e -> subsets(add(root, e), remove(in, e), m-1).stream()).collect(Collectors.toList());
    }

    static List<Integer> add(List<Integer> in, Integer e) {
        List<Integer> out = new ArrayList<>(in);
        out.add(e);
        return out;
    }

    static List<Integer> remove(List<Integer> in, Integer e) {
        return in.stream().filter(l -> l >= e).collect(Collectors.toList());
    }

}
