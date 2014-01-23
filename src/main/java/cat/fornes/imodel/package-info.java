/**
 * Utilities for beans dynamic implementations.
 * 
 * Enables data definition as JavaBean applying the "facade pattern".
 * Next, you can create a implementation with the Proxy utility.<br/>
 * Features:
 * <ul>
 * <li>Implement getters and setters as declared on interface</li>
 * <li>Implement equals, hashCode and toString default methods</li>
 * <li>The proxy can be serialized safely; only errors if some java bean property was defined as non-serializable</li>
 * <li>Also implement a clone method if declared on interface</li>
 * </ul>
 */
package cat.fornes.imodel;