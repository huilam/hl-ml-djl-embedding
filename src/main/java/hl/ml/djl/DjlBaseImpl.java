package hl.ml.djl;

import java.net.URL;
import java.util.Map;
import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslatorFactory;

public class DjlBaseImpl {
	
	protected DjlModelConfig djl_model_config 		= null;
	protected Predictor<String, float[]> predictor 	= null;
	protected ZooModel<String, float[]> model 		= null;
	protected Map<String, String> model_prop 		= null;
	//
	protected boolean model_init_ok 				= false;

	@SuppressWarnings("rawtypes")
	protected DjlBaseImpl(
			Class aImplClass, 
			DjlModelConfig aDjlModelConfig,
			TranslatorFactory aTranslatorFactory)
	{
		if(aDjlModelConfig.getModel_uri()==null)
		{
			URL url = aImplClass.getProtectionDomain().getCodeSource().getLocation();
			String sModelFolder = url.toString()+aImplClass.getPackageName().replace(".","/")+"/model/";
			aDjlModelConfig.setModel_uri( sModelFolder + aDjlModelConfig.getModel_name());
		}
		
		aDjlModelConfig.setDevice_type(Device.cpu());
		
		this.model = DjlModelLoader.loadModel(
				aDjlModelConfig, 
				aTranslatorFactory);
		
		if(this.model!=null)
		{
			this.predictor = this.model.newPredictor();
			this.djl_model_config = aDjlModelConfig;
			this.model_prop = this.model.getProperties();
		}
	}
	
    public boolean isModelInitOk() {
		return this.model_init_ok;
	}
    
    public String getRt_engine() {
		return djl_model_config.getRuntime_engine();
	}

    public String getModel_name() {
		return djl_model_config.getModel_name();
	}
    
}