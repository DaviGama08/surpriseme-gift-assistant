package pt.isec.gps2526_g42.surprise_me.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// Singleton that manages PropertyChangeSupport for the UI
public class UIPropertyChangeManager {
    private static UIPropertyChangeManager instance = null;
    public static final String PROP_SHOW_ENJOYERS = "prop_show_enjoyers";
    public static final String PROP_SHOW_GENERATE_GIFTS = "prop_show_generate_gifts";
    public static final String PROP_SHOW_GIFTS = "prop_show_gifts";
    public static final String PROP_SHOW_EVENTS = "prop_show_events";
    public static final String PROP_SHOW_DASHBOARD = "prop_show_dashboard";
    public static final String PROP_SHOW_PROFILE = "prop_show_profile";
    public static final String PROP_SHOW_LOGIN = "prop_show_login";
    public static final String PROP_SHOW_REGISTER = "prop_show_register";

    private final PropertyChangeSupport pcs;

    // Private constructor
    private UIPropertyChangeManager() {
        pcs = new PropertyChangeSupport(this);
    }

    // Static method to obtain instance
    public static UIPropertyChangeManager getInstance() {
        if (instance == null) {
            instance = new UIPropertyChangeManager();
        }
        return instance;
    }

    // Method to add listeners
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    // Method to make "fires"
    public void firePropertyChange(String property, Object oldValue, Object newValue) {
        pcs.firePropertyChange(property, oldValue, newValue);
    }
}
