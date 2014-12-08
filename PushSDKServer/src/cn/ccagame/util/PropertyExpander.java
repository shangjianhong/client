package cn.ccagame.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Property expansion utility.<br>
 * This utility provides static methods to expand properties appearing in strings.
 */
public class PropertyExpander
{
    private PropertyExpander()
    {
    }

    // The pattern for a system property. Matches ${property.name}, with the interior matched reluctantly.
    private static final Pattern EXPANSION_PATTERN = Pattern.compile("(\\$\\{([^}]+?)\\})", java.util.regex.Pattern.MULTILINE);

    /**
     * Expand property expressions in the input. Expands property expressions of the form <code>${propertyname}</code>
     * in the input, replacing each such expression with the value associated to the respective key
     * <code>propertyname</code> in the supplied map. If for a given expression, the property is undefined (has null
     * value) in the map supplied, that expression is left unexpanded in the resulting string. <p/> Note that expansion
     * is not recursive. If the value of one property contains another expression, the expression appearing in the value
     * will not be expanded further.
     * 
     * @param input
     *            the input string. This may be null, in which case null is returned.
     * @param props
     *            the map of property values to use for expansion. This map should have <code>String</code> keys and
     *            <code>String</code> values. Any object of class {@link java.util.Properties} works here, as will
     *            other implementations of such maps.
     * @return the result of replacing property expressions with the values of the corresponding properties from the
     *         supplied property map, null if the input string is null.
     */
    @SuppressWarnings("rawtypes")
	public static String expandProperties(String input, Map props)
    {
        if (input == null)
        {
            return null;
        }

        Matcher matcher = EXPANSION_PATTERN.matcher(input);

        StringBuffer expanded = new StringBuffer(input.length());

        while (matcher.find())
        {
            String propName = matcher.group(2);
            String value = (String)props.get(propName);

            // if no value is found, use a value equal to the original expression
            if (value == null)
            {
                value = matcher.group(0);
            }
            // Fake a literal replacement since Matcher.quoteReplacement() is not present in 1.4.
            matcher.appendReplacement(expanded, "");
            expanded.append(value);
        }
        matcher.appendTail(expanded);

        return expanded.toString();
    }

    /**
     * Expand system properties in the input string. This is equivalent to calling <code>expandProperties(input,
     * System.getProperties())</code>.
     * 
     * @param input
     * @return the result of replacing property expressions with the values of the corresponding system properties.
     * @see System#getProperties()
     */
    public static String expandSystemProperties(String input)
    {
        return expandProperties(input, System.getProperties());
    }
}
