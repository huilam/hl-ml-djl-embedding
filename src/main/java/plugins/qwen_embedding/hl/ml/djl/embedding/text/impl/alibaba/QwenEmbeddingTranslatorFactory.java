package hl.ml.djl.embedding.text.impl.alibaba;

import ai.djl.Model;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorFactory;
import ai.djl.util.Pair; // Highlight-line: Import DJL's Pair utility
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class QwenEmbeddingTranslatorFactory implements TranslatorFactory, Serializable {

    private static final long serialVersionUID = 1L;

    // Fix: Match the expected Set<Pair<Type, Type>> signature
    @Override
    public Set<Pair<Type, Type>> getSupportedTypes() {
        return Collections.singleton(new Pair<>(String.class, float[].class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I, O> Translator<I, O> newInstance(
            Class<I> input, 
            Class<O> output, 
            Model model, 
            Map<String, ?> arguments) {
        
        if (input == String.class && output == float[].class) {
            return (Translator<I, O>) new QwenEmbeddingTranslator();
        }
        
        throw new IllegalArgumentException("Unsupported input/output types for Qwen factory.");
    }
}