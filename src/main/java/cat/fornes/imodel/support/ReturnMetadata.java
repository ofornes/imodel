/*
 * This file is part of "imodel-core".
 * 
 * "imodel-core" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "imodel-core" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with calendar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2014 Octavi Fornés
 */
package cat.fornes.imodel.support;

import java.io.Serializable;

import org.springframework.util.ObjectUtils;

/**
 * The metadata of the returned value method for method dispatchers.
 * 
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
public class ReturnMetadata implements Serializable
{
    private static final long serialVersionUID = -6223976426095230020L;

    private boolean continueProcessing;
    private Object returnedValue;
    private Throwable exceptionToBeThrown;
    
    /**
     * Flag to indicate if others processors should to go ahead with the process of the method.
     * @return true if should to be processed by others or false if not.
     */
    public boolean isContinueProcessing()
    {
        return continueProcessing;
    }
    /**
     * Flag to indicate if others processors should to go ahead with the process of the method.
     * @param continueProcessing true if should to be processed by others or false if not.
     */
    public void setContinueProcessing(boolean continueProcessing)
    {
        this.continueProcessing = continueProcessing;
    }
    /**
     * The returned value, if applicable.
     * @return the returned value. Ignored if {@link #isContinueProcessing()} is true
     */
    public Object getReturnedValue()
    {
        return returnedValue;
    }
    /**
     * The returned value, if applicable.
     * @param returnedValue the returned value. Ignored if {@link #isContinueProcessing()} is true
     */
    public void setReturnedValue(Object returnedValue)
    {
        this.returnedValue = returnedValue;
    }
    
    /**
     * An exception to be thrown by the method.
     * @return The exception, or null if none should to be thrown
     */
    public Throwable getExceptionToBeThrown()
    {
        return exceptionToBeThrown;
    }
    /**
     * An exception to be thrown by the method.
     * @param exceptionToBeThrown The exception, or null if none should to be thrown
     */
    public void setExceptionToBeThrown(Throwable exceptionToBeThrown)
    {
        this.exceptionToBeThrown = exceptionToBeThrown;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return ObjectUtils.nullSafeHashCode(new Object[] {continueProcessing, returnedValue});
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || !ReturnMetadata.class.isAssignableFrom(obj.getClass()))
        {
            return false;
        }
        if(obj == this)
        {
            return true;
        }
        return (continueProcessing == ((ReturnMetadata)obj).isContinueProcessing()
                && ObjectUtils.nullSafeEquals(returnedValue, ((ReturnMetadata)obj).getReturnedValue()));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format("ReturnMetadata [continueProcessing=%s, returnedValue=%s]", continueProcessing, returnedValue);
    }
    
}
