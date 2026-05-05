package hl.ml.djl.transformer.embedding.common;

import java.util.HashMap;
import java.util.Map;

public class DjlModelConfig {
	
	public static final String RT_ENGINE_ONNX = "OnnxRuntime";
	public static final String RT_ENGINE_PYTORCH = "PyTorch";
	
	private String model_name 				= null;
	private String model_uri 				= null;
	private String runtime_engine 			= DJLConstants.RT_ENGINE_ONNX;
	private Map<String, Object> mapOptArgs 	= new HashMap<>();
	
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	
	public String getModel_uri() {
		return model_uri;
	}
	public void setModel_uri(String model_uri) {
		this.model_uri = model_uri;
	}
	
	public String getRuntime_engine() {
		return runtime_engine;
	}
	public void setRuntime_engine(String runtime_engine) {
		this.runtime_engine = runtime_engine;
	}
	
	public void clearOptArgs()
	{
		mapOptArgs.clear();
	}
	
	public void addOptArg(String key, String value)
	{
		mapOptArgs.put(key, value);
	}
	
	public void removeOptArg(String key, String value)
	{
		mapOptArgs.remove(key, value);
	}
	
	public Map<String, Object> getOptArgs()
	{
		return mapOptArgs;
	}
	
}