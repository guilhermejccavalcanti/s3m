package com.pa.bean.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.pa.entity.QualisData;
	
@FacesConverter(value = "qualisDataConverter")
public class QualisDataOneMenuConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		UIComponent src = arg1;
		if (src != null) {
			List<UIComponent> childs = src.getChildren();
			UISelectItems itens = null;
			if (childs != null) {
				for (UIComponent ui : childs) {
					if (ui instanceof UISelectItems) {
						itens = (UISelectItems) ui;
						break;
					} else if (ui instanceof UISelectItem) {
						UISelectItem item = (UISelectItem) ui;
						try {
							QualisData val = (QualisData) item.getItemValue();
							if (arg2.equals("" + val.getId())) {
								ret = val;
								break;
							}
						} catch (Exception e) {
						}
					}
				}
			}

			if (itens != null) {
				List<QualisData> values = (List<QualisData>) itens.getValue();
				if (values != null) {
					for (QualisData val : values) {
						if (arg2.equals("" + val.getId())) {
							ret = val;
							break;
						}
					}
				}
			}
		}
		return ret;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String str = "";
		if (arg2 instanceof QualisData) {
			str = "" + ((QualisData) arg2).getId();
		}
		return str;
	}
}