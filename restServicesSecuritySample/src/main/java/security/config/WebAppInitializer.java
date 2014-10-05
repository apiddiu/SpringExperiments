package security.config;

import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
	//private Logger LOG = Logger.getLogger(getClass());

	public void onStartup(ServletContext servletContext) throws ServletException {
		System.out.println("STARTUP");
		WebApplicationContext rootContext = createRootContext(servletContext);
		
		configureSpringMvc(servletContext, rootContext);
		configureSpringSecurity(servletContext, rootContext);
	}

	private WebApplicationContext createRootContext(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(CoreConfig.class, SecurityConfig.class);
		rootContext.refresh();

		servletContext.addListener(new ContextLoaderListener(rootContext));
		servletContext.setInitParameter("defaultHtmlEscape", "true");

		return rootContext;
	}

	private void configureSpringMvc(ServletContext servletContext, WebApplicationContext rootContext) {
		AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
		mvcContext.register(MVCConfig.class);

		mvcContext.setParent(rootContext);

		ServletRegistration.Dynamic appServlet = servletContext.addServlet(
				"webservice", new DispatcherServlet(mvcContext));
		appServlet.setLoadOnStartup(1);
		Set<String> mappingConflicts = appServlet.addMapping("/");

		if (!mappingConflicts.isEmpty()) {
			for (String s : mappingConflicts) {
				//LOG.error("Mapping conflict: " + s);
				System.out.println("Mapping conflict: " + s);
			}
			throw new IllegalStateException(
					"'webservice' cannot be mapped to '/'");
		}
	}
	
	private void configureSpringSecurity(ServletContext servletContext, WebApplicationContext rootContext) {
		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
		
		FilterRegistration.Dynamic springSecurity = servletContext.addFilter("springSecurityFilterChain",
	        new DelegatingFilterProxy("springSecurityFilterChain", rootContext));
	    springSecurity.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
	  }
}
