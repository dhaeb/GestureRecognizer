package de.kdi.pojo;

import java.util.ArrayList;
import java.util.List;

public class TemplateCollection {

	//Holds the predefined templates
	public List<GestureTemplate> resampledLastTemplates;
	public List<GestureTemplate> resampledFirstTemplates;
	
	public TemplateCollection(List<GestureTemplate> resampledFirstTemplates, List<GestureTemplate> resampledLastTemplates) {
		this.resampledFirstTemplates = resampledFirstTemplates;
		this.resampledLastTemplates = resampledLastTemplates;
	}

	public TemplateCollection() {
		resampledFirstTemplates = new ArrayList<GestureTemplate>();
		resampledLastTemplates = new ArrayList<GestureTemplate>();
	}
}
