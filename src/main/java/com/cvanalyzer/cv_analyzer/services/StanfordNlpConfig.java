package com.cvanalyzer.cv_analyzer.services;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class StanfordNlpConfig {

@Bean
public StanfordCoreNLP coreNLPipeline() {
    Properties props = new Properties();
    // Annotateurs minimaux pour l'analyse de CV
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
    props.setProperty("ner.applyFineGrained", "false");
    return new StanfordCoreNLP(props);
}
}

