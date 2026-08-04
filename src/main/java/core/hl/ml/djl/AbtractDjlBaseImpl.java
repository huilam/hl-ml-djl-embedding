package hl.ml.djl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.util.ZipUtils;

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
			File folder = new File(aDjlModelConfig.getModel_folder());
			if(!folder.exists())
			{
				URL jarUrl = aImplClass.getProtectionDomain().getCodeSource().getLocation();
				String sImplPackagePath =  "/"+aImplClass.getPackageName().replace(".","/")+"/model/";
				String sModelFolder = jarUrl + sImplPackagePath + aDjlModelConfig.getModel_folder();
				//is jar
				if(jarUrl.getPath().toLowerCase().endsWith(".jar"))
				{
					String sZipResName = sImplPackagePath + aDjlModelConfig.getModel_folder() +".zip";
					
					String sCacheModelPath = unpackToCache(aImplClass, sZipResName, aDjlModelConfig.getModel_folder());
					if(sCacheModelPath!=null)
					{
						sModelFolder = sCacheModelPath;
					}
				}
				aDjlModelConfig.setModel_folder( sModelFolder);
			}
			
			//System.out.println("getModel_folder()="+aDjlModelConfig.getModel_folder());
			
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
	private boolean clearFromCache(String aModelFolder) {
		boolean isCleared = false;
		// Basic safety check
		if (aModelFolder != null && aModelFolder.contains("/.djl.ai/cache/")) {
			try {
				URL urlModel = new URL(aModelFolder);
				File folder = new File(urlModel.getFile());
				
				if (folder.exists()) {
					deleteDirRecursively(folder);
					System.out.println("deleted.");
					// Wait a moment for OS file locks to release if necessary
					Thread.sleep(20); 
					isCleared = !folder.exists();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isCleared;
	}

	// Recursive helper method
	private void deleteDirRecursively(File fileToDrop) {
		// If it's a directory, list its contents and call this method on each one
		if (fileToDrop.isDirectory()) {
			File[] children = fileToDrop.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteDirRecursively(child); // Deletes the contents inside the subfolder
				}
			}
		}
		
		// At this point, if it was a directory, it is now empty. 
		// If it was a file, we can just delete it.
		if (fileToDrop.delete()) {
			System.out.println("  - Deleted : " + fileToDrop.getAbsolutePath());
		} else {
			System.out.println("  - Failed to delete : " + fileToDrop.getAbsolutePath());
		}
	}
	
	private String unpackToCache(Class aImplClass, String aZipResourceName, String aUnpackFolderName)
	{
		URL jarZipUrl = aImplClass.getResource(aZipResourceName);
		if(jarZipUrl!=null)
		{
			// 1. Define a directory inside DJL's default cache path (~/.djl.ai/cache)
		    String userHome = System.getProperty("user.home");
		    Path djlCacheDir = Paths.get(userHome, ".djl.ai", "cache", "jar-models", aUnpackFolderName);
		    
		    // 2. Only unzip if the directory doesn't already exist
		    if (!Files.exists(djlCacheDir)) {
		        try {
		        	Files.createDirectories(djlCacheDir);
		        } 
		        catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		    File folder = djlCacheDir.toFile();
		    if(folder!=null && folder.listFiles().length==0)
		    {
		        InputStream is = null;
		        try {
	        		is = getClass().getResourceAsStream(aZipResourceName);
		            if (is == null) {
		                throw new RuntimeException("Could not find model zip in classpath: " + aZipResourceName);
		            }
		            // 3. Use DJL's built-in ZipUtils to unpack the stream directly to the folder
		            ZipUtils.unzip(is, djlCacheDir);
		        } 
		        catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        finally
		        {
		        	if(is!=null)
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        }
		    }
		    // 4. Pass the unzipped cache directory URI to DJL
		    return djlCacheDir.toUri().toString();
		}
		return null;
	}
	
	public void loadModel() {
		
		DjlModelConfig djlModelConfig 	= this.djl_model_config;
		Criteria.Builder<I, O> builder 	= this.criteria_builder;
		
		if(djlModelConfig!=null && builder!=null)
		{
			String sModelPath = djlModelConfig.getModel_folder();
			/**
			int iPos = sModelPath.indexOf(":");
			if(iPos>-1)
			{
				sModelPath = sModelPath.substring(iPos+1);
			}
			**/
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
				this.model_init_ok = false;
				clearFromCache(sModelPath);
				
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