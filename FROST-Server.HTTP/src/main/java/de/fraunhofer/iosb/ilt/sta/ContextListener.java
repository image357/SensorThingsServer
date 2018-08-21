/*
 * Copyright (C) 2016 Fraunhofer IOSB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta;

import de.fraunhofer.iosb.ilt.sta.messagebus.MessageBusFactory;
import de.fraunhofer.iosb.ilt.sta.persistence.PersistenceManagerFactory;
import de.fraunhofer.iosb.ilt.sta.settings.CoreSettings;
import de.fraunhofer.iosb.ilt.sta.settings.Settings;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab
 */
@WebListener
public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class);
    public static final String TAG_CORE_SETTINGS = "CoreSettings";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (sce != null && sce.getServletContext() != null) {
            LOGGER.info("Context initialised, loading settings.");
            ServletContext context = sce.getServletContext();

            Properties properties = new Properties();
            Enumeration<String> names = context.getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String targetName = name.replaceAll("_", ".");
                properties.put(targetName, context.getInitParameter(name));
            }

            properties.setProperty(CoreSettings.TAG_TEMP_PATH, context.getAttribute(ServletContext.TEMPDIR).toString());
            CoreSettings coreSettings = new CoreSettings(properties);
            context.setAttribute(TAG_CORE_SETTINGS, coreSettings);

            setUpCorsFilter(context, coreSettings);

            PersistenceManagerFactory.init(coreSettings);
            MessageBusFactory.init(coreSettings);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Context destroyed, shutting down threads...");
        MessageBusFactory.getMessageBus().stop();
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException ex) {
            LOGGER.debug("Rude wakeup?", ex);
        }
        LOGGER.info("Context destroyed, done shutting down threads.");
    }

    private void setUpCorsFilter(ServletContext servletContext, CoreSettings coreSettings) {
        Settings httpSettings = coreSettings.getHttpSettings();
        boolean corsEnable = httpSettings.getBoolean(CoreSettings.TAG_CORS_ENABLE, CoreSettings.DEFAULT_CORS_ENABLE);
        if (corsEnable) {
            try {
                String filterName = "CorsFilter";

                FilterRegistration.Dynamic corsFilter = servletContext.addFilter(filterName, "org.apache.catalina.filters.CorsFilter");
                corsFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");

                String allowedOrigins = httpSettings.get(CoreSettings.TAG_CORS_ALLOWED_ORIGINS, CoreSettings.DEFAULT_CORS_ALLOWED_ORIGINS);
                corsFilter.setInitParameter("cors.allowed.origins", allowedOrigins);

                String allowedMethods = httpSettings.get(CoreSettings.TAG_CORS_ALLOWED_METHODS, CoreSettings.DEFAULT_CORS_ALLOWED_METHODS);
                corsFilter.setInitParameter("cors.allowed.methods", allowedMethods);

                String exposedHeaders = httpSettings.get(CoreSettings.TAG_CORS_EXPOSED_HEADERS, CoreSettings.DEFAULT_CORS_EXPOSED_HEADERS);
                corsFilter.setInitParameter("cors.exposed.headers", exposedHeaders);

                String allowedHeaders = httpSettings.get(CoreSettings.TAG_CORS_ALLOWED_HEADERS, CoreSettings.DEFAULT_CORS_ALLOWED_HEADERS);
                corsFilter.setInitParameter("cors.allowed.headers", allowedHeaders);

                String supportCreds = httpSettings.get(CoreSettings.TAG_CORS_SUPPORT_CREDENTIALS, CoreSettings.DEFAULT_CORS_SUPPORT_CREDENTIALS);
                corsFilter.setInitParameter("cors.support.credentials", supportCreds);

                String preflightMaxage = httpSettings.get(CoreSettings.TAG_CORS_PREFLIGHT_MAXAGE, CoreSettings.DEFAULT_CORS_PREFLIGHT_MAXAGE);
                corsFilter.setInitParameter("cors.preflight.maxage", preflightMaxage);

                String requestDecorate = httpSettings.get(CoreSettings.TAG_CORS_REQUEST_DECORATE, CoreSettings.DEFAULT_CORS_REQUEST_DECORATE);
                corsFilter.setInitParameter("cors.request.decorate", requestDecorate);
            } catch (Exception exc) {
                LOGGER.error("Failed to initialise CORS filter.", exc);
            }
        }
    }
}
