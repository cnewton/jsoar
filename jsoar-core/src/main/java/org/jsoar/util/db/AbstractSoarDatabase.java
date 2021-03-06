/*
 * Copyright (c) 2010 Dave Ray <daveray@gmail.com>
 *
 * Created on Sep 11, 2010
 */
package org.jsoar.util.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jsoar.kernel.SoarException;
import org.jsoar.util.JdbcTools;

import com.google.common.io.ByteStreams;

/**
 * Base class for Soar databases like SMEM and EPMEM. 
 * 
 * @author ray
 */
public abstract class AbstractSoarDatabase
{
    private final String driver;
    private final Connection db;
    private final Properties statements = new Properties();
    private final Map<String, String> filterMap = new HashMap<String, String>();
    private final String signatureTable;
    
    /**
     * Construct a new database instance.
     * 
     * @param driver the driver name
     * @param db the database connection
     * @throws SoarException
     */
    public AbstractSoarDatabase(String driver, Connection db, String signatureTable)
    {
        this.driver = driver;
        this.db = db;
        this.signatureTable = signatureTable;
    }
    
    /**
     * @return the driver name
     */
    public String getDriver()
    {
        return driver;
    }
    
    /**
     * @return the database connection
     */
    public Connection getConnection()
    {
        return db;
    }
    
    /**
     * @return the filter map used to filter resources. Additional replacement 
     *  entries can be added to this map.
     */
    public Map<String, String> getFilterMap()
    {
        return filterMap;
    }
    /**
     * Load and prepare statements.
     * 
     * @throws SoarException
     * @throws IOException
     */
    public void prepare() throws SoarException, IOException
    {
        loadStatementsFromResource("statements.properties", true);
        loadStatementsFromResource(driver + ".statements.properties", false);
        assignStatements();
    }
    
    /**
     * Sets up initial database structures if not already present.
     * 
     * @return true if the database was initialize, false if it already existed
     * @throws SoarException
     * @throws IOException
     */
    public boolean structure() throws SoarException, IOException
    {
        // First check if the signature table is already present. If it is, the
        // db is initialized.
        if(signatureTable != null)
        {
            try
            {
                if(JdbcTools.tableExists(getConnection(), signatureTable))
                {
                    // If we're here, the table already exists, so don't set up the rest of the 
                    // db structure.
                    return false;
                }
            }
            catch (SQLException e)
            {
                throw new SoarException("While detecting signature table '" + signatureTable + "': " + e.getMessage(), e);
            }
        }
        
        // Load the database structure by executing structures.sql
        final InputStream is = filter(getClass().getResourceAsStream("structures.sql"), getFilterMap());
        if(is == null)
        {
            throw new FileNotFoundException("Failed to open '" + getResourcePath("structures.sql") + "' resource");
        }
        try
        {
            JdbcTools.executeSqlBatch(getConnection(), is, getDriver());
        }
        finally
        {
            is.close();
        }
        
        // The signature table (tested above) is created at the end of structures.sql
        
        return true;
    }
    
    private String getResourcePath(String name)
    {
        return "/" + getClass().getPackage().getName().replace('.', '/') + "/" + name;
    }
    
    private void loadStatementsFromResource(String resource, boolean required) throws IOException
    {
        InputStream is = filter(getClass().getResourceAsStream(resource), filterMap);
        if(is == null)
        {
            if(required)
            {
                throw new FileNotFoundException("Failed to open '" + getResourcePath(resource) + "' resource");
            }
            return;
        }
        try
        {
            // Overwrite here rather than useing Properties delegation because 
            // we want to be able to iterate over the property keys. :(
            statements.load(is);
        }
        finally
        {
            is.close();
        }        
    }
    
    private void assignStatements() throws SoarException
    {
        for(Object name : statements.keySet())
        {
            assignStatement(name.toString());
        }
    }
    
    private void assignStatement(String name) throws SoarException
    {
        try
        {
            // Reflectively find the field and assign the prepared statement
            // to it.
            final Field field = getClass().getDeclaredField(name);
            // This is necessary since we're trying to set a possibly non-public
            // field in a sub-class. Another option is to add a protected 
            // abstract method, implemented by the sub-class that sets the field.
            // This works for now.
            field.setAccessible(true);
            field.set(this, prepareNamedStatement(name));
        }
        catch (SecurityException e)
        {
            throw new SoarException("While assigning statement field '" + name + "': " + e.getMessage(), e);
        }
        catch (NoSuchFieldException e)
        {
            throw new SoarException("While assigning statement field '" + name + "': " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e)
        {
            throw new SoarException("While assigning statement field '" + name + "': " + e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new SoarException("While assigning statement field '" + name + "': " + e.getMessage(), e);
        }
    }
    
    private PreparedStatement prepareNamedStatement(String name) throws SoarException
    {
        final String sql = statements.getProperty(name);
        if(sql == null)
        {
            throw new SoarException("Could not find statement '" + name + "'");
        }
        try
        {
            final String trimmed = sql.trim();
            return trimmed.startsWith("INSERT") ? db.prepareStatement(trimmed, Statement.RETURN_GENERATED_KEYS) :
                                                  db.prepareStatement(trimmed);
        }
        catch (SQLException e)
        {
            throw new SoarException("Failed to prepare statement '" + sql + "': " + e.getMessage(), e);
        }
    }
    
    private static final InputStream filter(InputStream in, Map<String, String> replacements) throws IOException
    {
        if(in == null)
        {
            return null;
        }
        
        final ByteArrayOutputStream temp = new ByteArrayOutputStream();
        try
        {
            ByteStreams.copy(in, temp);
        }
        finally
        {
            in.close();
        }
        
        String tempString = temp.toString("UTF-8");
        for(Map.Entry<String, String> entry : replacements.entrySet())
        {
            tempString = tempString.replace(entry.getKey(), entry.getValue());
        }
        
        return new ByteArrayInputStream(tempString.getBytes("UTF-8"));
    }

}
