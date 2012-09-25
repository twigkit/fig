package twigkit.fig.jsp;

import twigkit.fig.Fig;
import twigkit.fig.loader.PropertiesLoader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * @author mr.olafsson
 */
public class ConfigTag extends SimpleTagSupport {

	private String var;
	private Fig fig;
	private String name;
	
	@Override
	public void doTag() throws JspException, IOException {
		if (var == null) {
			var = name;
		}
        if (fig != null) {
            getJspContext().setAttribute(var, fig.get(name.split("\\.")).map());
        }
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setFrom(String confs) {
		this.fig = Fig.load(new PropertiesLoader(confs));
	}

	public void setLoad(String name) {
		this.name = name;
	}
}
