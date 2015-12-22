package org.jvnet.hyperjaxb3.ejb.extensions.naming.pre_0_5_6.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.Validate;
import org.jvnet.hyperjaxb3.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.impl.DefaultNaming;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class provides the hyperjaxb3 version 0.5.6 naming.
 *
 * @author bhausen
 */
public class Pre056DefaultNaming extends DefaultNaming implements Naming, InitializingBean {
    private Map<String, String> keyNameMap = new TreeMap<String, String>();
    private Map<String, String> nameKeyMap = new TreeMap<String, String>();
    @SuppressWarnings("unused")
    private boolean updated = false;

    /**
     * @see org.jvnet.hyperjaxb3.ejb.strategy.naming.impl.DefaultNaming#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
	logger.info("Using com.qpark.eip.hyperjaxb3.naming.Pre600DefaultNaming to generate names!");
	final Set<Entry<Object, Object>> entries = this.getReservedNames().entrySet();
	for (final Entry<Object, Object> entry : entries) {
	    final Object entryKey = entry.getKey();
	    if (entryKey != null) {
		final String key = entryKey.toString().toUpperCase();
		final Object entryValue = entry.getValue();
		final String value = entryValue == null || "".equals(entryValue.toString().trim()) ? key + "_"
			: entryValue.toString();
		this.nameKeyMap.put(key, value);
		this.keyNameMap.put(value, key);
	    }
	}
    }

    /**
     * @see org.jvnet.hyperjaxb3.ejb.strategy.naming.impl.DefaultNaming#getName(org.jvnet.hyperjaxb3.ejb.strategy.mapping.Mapping,
     *      java.lang.String)
     */
    @Override
    public String getName(final Mapping context, final String draftName) {
	return this.getName(draftName);
    }

    /**
     * The old name implementation.
     *
     * @param draftName
     *            the drafted name.
     * @return the name to use in hyperjaxb3.
     */
    public String getName(final String draftName) {
	Validate.notNull(draftName, "Name must not be null.");
	final String name = draftName.replace('$', '_').toUpperCase();
	if (this.nameKeyMap.containsKey(name)) {
	    return this.nameKeyMap.get(name);
	} else if (name.length() >= this.getMaxIdentifierLength()) {
	    for (int i = 0;; i++) {
		final String suffix = Integer.toString(i);
		final String prefix = name.substring(0, super.getMaxIdentifierLength() - suffix.length() - 1);
		final String identifier = prefix + "_" + suffix;
		if (!this.keyNameMap.containsKey(identifier)) {
		    this.nameKeyMap.put(name, identifier);
		    this.keyNameMap.put(identifier, name);
		    this.updated = true;
		    return identifier;
		}
	    }
	} else if (this.getReservedNames().contains(name.toUpperCase())) {
	    return name + "_";
	} else {
	    return name;
	}
    }
}
