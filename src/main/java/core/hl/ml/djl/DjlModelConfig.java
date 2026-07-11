package hl.ml.djl;

import java.util.HashMap;
import java.util.Map;

import ai.djl.Device;
import ai.djl.translate.TranslatorFactory;

public class DjlModelConfig {
	
	public static final String RT_ENGINE_ONNX 		= "OnnxRuntime";
	public static final String RT_ENGINE_PYTORCH 	= "PyTorch";
	
	private String model_name 			= null;
	private String model_filename 		= null;
	private String model_download_url 	= null;
	private String model_folder 		= null;
	private String model_license 		= null;
	private String runtime_engine 		= DjlConstants.RT_ENGINE_ONNX;
	private Device device_type			= null;
	
	private TranslatorFactory translator_factory 	= null;
	private Map<String, Object> mapMLArgs 			= new HashMap<>();
	private Map<String, String> mapOptions 			= new HashMap<>();
	
	
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	
	public TranslatorFactory getTranslator_factory() {
		return translator_factory;
	}
	public void setTranslator_factory(TranslatorFactory translator_factory) {
		this.translator_factory = translator_factory;
	}
	
	public String getModel_download_url() {
		return model_download_url;
	}
	public void setModel_download_url(String model_download_url) {
		this.model_download_url = model_download_url;
	}
	
	public String getModel_filename() {
		return model_filename;
	}
	
	public void setModel_filename(String model_filename) {
		this.model_filename = model_filename;
	}
	
	public String getModel_folder() {
		return model_folder;
	}
	public void setModel_folder(String model_folder) {
		this.model_folder = model_folder;
	}
	
	public String getModel_license() {
		return model_license;
	}
	public void setModel_license(String model_license) {
		this.model_license = model_license;
	}
	
	public String getRuntime_engine() {
		return runtime_engine;
	}
	public void setRuntime_engine(String runtime_engine) {
		
		if(DjlConstants.RT_ENGINE_ONNX.equalsIgnoreCase(runtime_engine))
		{
			this.runtime_engine = DjlConstants.RT_ENGINE_ONNX;
		}
		else if(DjlConstants.RT_ENGINE_PYTORCH.equalsIgnoreCase(runtime_engine))
		{
			this.runtime_engine = DjlConstants.RT_ENGINE_PYTORCH;
		}
		else
		{
			this.runtime_engine = runtime_engine;
		}
	}
	
	public Device getDevice_type() {
		return device_type;
	}
	
	public void setDevice_type(String aDevice_type) {
		
		switch(aDevice_type.toLowerCase())
		{
			case "cpu":
				this.device_type = Device.cpu();
				break;
			case "gpu":
				this.device_type = Device.gpu();
				break;
			default:
				break;
		}
	}
	
	public void setDevice_type(Device device_type) {
		this.device_type = device_type;
	}
	
	public void clearMLArgs()
	{
		mapMLArgs.clear();
	}
	
	public void addMLArg(String key, String value)
	{
		mapMLArgs.put(key, value);
	}
	
	public void removeMLArg(String key, String value)
	{
		mapMLArgs.remove(key, value);
	}
	
	public Map<String, Object> getMLArgs()
	{
		return mapMLArgs;
	}
	
	public void addOption(String key, String value)
	{
		mapOptions.put(key, value);
	}
	
	public void removeOption(String key, String value)
	{
		mapOptions.remove(key, value);
	}
	
	public Map<String, String> getOptions()
	{
		return mapOptions;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("model_name="+model_name);
		sb.append("\n").append(",model_download_url="+model_download_url);
		sb.append("\n").append(",model_license="+model_license);
		sb.append("\n").append(",model_folder="+model_folder);
		sb.append("\n").append(",model_filename="+model_filename);
		sb.append("\n").append(",runtime_engine="+runtime_engine);
		sb.append("\n").append(",device_type="+device_type);
		sb.append("\n").append(",translator_factory="+translator_factory);
		sb.append("\n").append(",mapMLArgs="+mapMLArgs);
		sb.append("\n").append(",mapOptions="+mapOptions);
		
		return sb.toString();
	}
}