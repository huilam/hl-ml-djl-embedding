package hl.ml.djl.embedding.text.impl.alibaba;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
// Highlight-line: Import the NoBatchifyTranslator interface
import ai.djl.translate.NoBatchifyTranslator; 
import ai.djl.translate.TranslatorContext;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// Highlight-line: Change interface here
public class QwenEmbeddingTranslator implements NoBatchifyTranslator<String, float[]> {

    private HuggingFaceTokenizer tokenizer;

    @Override
    public void prepare(TranslatorContext ctx) throws Exception {
        Path modelPath = ctx.getModel().getModelPath();
        // Resolve the exact path pointing to the tokenizer.json file
        Path tokenizerPath = modelPath.resolve("tokenizer.json");

        // Pass configuration parameters as a Map directly into the static builder
        Map<String, String> options = new HashMap<>();
        options.put("padding", "true");
        options.put("truncation", "true");

        // Instantiates cleanly with the file path directly
        this.tokenizer = HuggingFaceTokenizer.newInstance(tokenizerPath, options);
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String input) {
        NDManager manager = ctx.getNDManager();
        var encoding = tokenizer.encode(input);
        
        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();
        long seqLen = inputIds.length;
        
        NDList inputs = new NDList();
        
        // These will now pass through completely unaltered as Rank 2 matrices [1, seqLen]
        NDArray inputIdsArray = manager.create(inputIds, new Shape(1, seqLen));
        inputs.add(inputIdsArray);
        
        NDArray attentionMaskArray = manager.create(attentionMask, new Shape(1, seqLen));
        inputs.add(attentionMaskArray);
        
        long[] positionIds = new long[(int) seqLen];
        for (int i = 0; i < seqLen; i++) positionIds[i] = i;
        inputs.add(manager.create(positionIds, new Shape(1, seqLen)));

        Shape dummyKVShape = new Shape(1, 8, 0, 128);
        for (int i = 0; i < 28; i++) {
            inputs.add(manager.create(dummyKVShape, DataType.FLOAT32)); 
            inputs.add(manager.create(dummyKVShape, DataType.FLOAT32));
        }

        ctx.setAttachment("attention_mask", attentionMaskArray);
        return inputs;
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        NDArray hiddenStates = list.get(0); 
        NDArray attentionMask = (NDArray) ctx.getAttachment("attention_mask");
        
        NDArray embeddings = meanPooling(hiddenStates, attentionMask);
        embeddings = embeddings.div(embeddings.norm(new int[]{-1}, true));
        
        return embeddings.toFloatArray();
    }

    private NDArray meanPooling(NDArray hiddenStates, NDArray attentionMask) {
        NDArray expandedMask = attentionMask.expandDims(-1).toType(DataType.FLOAT32, false);
        NDArray maskedHidden = hiddenStates.mul(expandedMask);
        NDArray sumHidden = maskedHidden.sum(new int[]{1});
        NDArray sumMask = expandedMask.sum(new int[]{1}).clip(1e-9, Float.MAX_VALUE);
        return sumHidden.div(sumMask).squeeze(0);
    }
}