package com.cvanalyzer.utils;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SimilarityCalculator {

    private final CosineSimilarity cosineSimilarity = new CosineSimilarity();

    public double calculateSimilarity(Map<CharSequence, Integer> vector1,
                                      Map<CharSequence, Integer> vector2) {
        return cosineSimilarity.cosineSimilarity(vector1, vector2);
    }
}
