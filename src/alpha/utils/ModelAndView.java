package alpha.utils;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private Map<String, Object> attributes;
    private String url;

    public ModelAndView(String url) {
        this.url = url;
        this.attributes = new HashMap<>();
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }
}