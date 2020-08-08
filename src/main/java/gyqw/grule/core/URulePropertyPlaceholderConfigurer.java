package gyqw.grule.core;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class URulePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    public URulePropertyPlaceholderConfigurer() {
        setIgnoreUnresolvablePlaceholders(true);
        setOrder(100);
    }
}
