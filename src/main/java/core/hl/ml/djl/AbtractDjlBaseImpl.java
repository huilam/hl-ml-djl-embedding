package hl.ml.djl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;

public abstract class AbtractDjlBaseImpl<I, O> {
	
	protected Criteria.Builder<I, O> criteria_builder	= null;
	protected Criteria<I, O> criteria 					= null;
	protected Predictor<I, O> predictor 				= null;
	protected ZooModel<I, O> model 						= null;
	//
	protected DjlModelConfig djl_model_config 	= null;
	protected boolean model_init_ok 			= false;

	@SuppressWarnings("rawtypes")
	protected AbtractDjlBaseImpl(
			Class aImplClass, 
			DjlModelConfig aDjlModelConfig,
			Criteria.Builder<I, O> aCriteriaBuilder)
	{
		if(aImplClass!=null && aDjlModelConfig!=null)
		{
			if(aDjlModelConfig.getModel_folder()!=null)
			{
				File folder = new File(aDjlModelConfig.getModel_folder());
				if(!folder.isDirectory())
				{
					URL url = aImplClass.getProtectionDomain().getCodeSource().getLocation();
					String sModelFolder = url.toString()+aImplClass.getPackageName().replace(".","/")+"/model/";
					aDjlModelConfig.setModel_folder( sModelFolder + aDjlModelConfig.getModel_folder());
				}
			}
			
			if(aDjlModelConfig.getModel_folder()==null)
			{
				URL url = aImplClass.getProtectionDomain().getCodeSource().getLocation();
				String sModelFolder = url.toString()+aImplClass.getPackageName().replace(".","/")+"/model/";
				aDjlModelConfig.setModel_folder( sModelFolder + aDjlModelConfig.getModel_name());
			}
			
			String sMlModelFileName = aDjlModelConfig.getModel_filename();
			if(sMlModelFileName!=null && sMlModelFileName.trim().length()>0)
			{
				aCriteriaBuilder.optModelName(sMlModelFileName);
			}
			//
			this.djl_model_config = aDjlModelConfig;
			this.criteria_builder = aCriteriaBuilder;
		}
	}
	
	public void loadModel() {
		
		DjlModelConfig djlModelConfig 	= this.djl_model_config;
		Criteria.Builder<I, O> builder 	= this.criteria_builder;
		
		if(djlModelConfig!=null && builder!=null)
		{
			String sModelPath = djlModelConfig.getModel_folder();
			int iPos = sModelPath.indexOf(":");
			if(iPos>-1)
			{
				sModelPath = sModelPath.substring(iPos+1);
			}
			
			builder.optModelUrls(sModelPath);
			builder.optEngine(djlModelConfig.getRuntime_engine());
			
			if(djlModelConfig.getDevice_type()!=null)
				builder.optDevice(djlModelConfig.getDevice_type());
		        	
			if(djlModelConfig.getMLArgs()!=null && djlModelConfig.getMLArgs().size()>0)
		        builder.optArguments(djlModelConfig.getMLArgs());
			
			builder.optOptions(djlModelConfig.getOptions());
			
			if(djlModelConfig.getTranslator_factory()!=null)
				builder.optTranslatorFactory(djlModelConfig.getTranslator_factory());
	
			this.criteria = builder.build();
			
			try {
				this.model = this.criteria.loadModel();
				this.predictor = this.model.newPredictor();
				this.model_init_ok = true;
			} catch (ModelNotFoundException | MalformedModelException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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