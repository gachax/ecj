/*
  Copyright 2017 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/
package ec.app.tsp;

import ec.EvolutionState;
import ec.Problem;
import ec.co.ConstructiveProblemForm;
import ec.util.Parameter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a Traveling Salesmen Problem loaded from a file.  The format used
 * for the file is similar to the TSPLIB format (https://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/tsp95.pdf),
 * though we don't support all of TSPLIB's features.
 * 
 * @author Eric O. Scott
 */
public class TSPProblem extends Problem implements ConstructiveProblemForm {
    public final static String P_FILE = "file";
    
    private Map<Integer, double[]> nodes;
    
    @Override
    public void setup(EvolutionState state, Parameter base)
    {
        assert(state != null);
        assert(base != null);
        final File file = state.parameters.getFile(base.push(P_FILE), null);
        if (file == null)
            state.output.fatal(String.format("%s: Unable to read file path '%s'.", this.getClass().getSimpleName(), base.push(P_FILE)), base.push(P_FILE));
        try
            {
            nodes = loadNodes(file);
            }
        catch (final Exception e)
            {
            state.output.fatal(String.format("%s: Unable to load TSP instance from file '%s'.", this.getClass().getSimpleName(), base.push(P_FILE)), base.push(P_FILE));
            }
        assert(repOK());
    }
        
    private static Map<Integer, double[]> loadNodes(final File file) throws IOException
    {
        assert(file != null);
        final BufferedReader r = new BufferedReader(new FileReader(file));
        
        final int dimension = readDimension(r);
        final Map<Integer, double[]> nodes = new HashMap<Integer, double[]>();
        
        seekToDataSection(r);
        String line;
        while ( (line = r.readLine()) != null && !line.trim().equals("EOF") )
            {
            final String[] cols = line.split(" ");
            if (cols.length != 3)
                throw new IllegalStateException(String.format("%s: Node '%s' has %d columns, expected 3.", TSPProblem.class.getSimpleName(), line, cols.length));
            final int id = Integer.valueOf(cols[0].trim());
            final int x = Integer.valueOf(cols[1].trim());
            final int y = Integer.valueOf(cols[2].trim());
            nodes.put(id, new double[] {x, y});
            }
        if (nodes.size() != dimension)
            throw new IllegalStateException(String.format("%s: TSP problem 'DIMENSION' is specified to be %d, but %d nodes were found.", TSPProblem.class.getSimpleName(), dimension, nodes.size()));
        return nodes;
    }
    
    private static int readDimension(final BufferedReader tspReader) throws IOException
    {
        assert(tspReader != null);
        boolean done = false;
        String line;
        while ( (line = tspReader.readLine()) != null && !done)
            {
            final String[] keyValue = line.split(":");
            if (keyValue.length == 2 && keyValue[0].trim().toLowerCase().equals("dimension"))
                {
                return Integer.valueOf(keyValue[1].trim());
                }
            }
        throw new IllegalStateException("No valid 'DIMENSION' attribute found in TSP file.  Are you sure this file is in TSPLIB format?");
    }
    
    /** Seek to the line where the coordinate data begins in a TSP problem definition of TSPLIB format. */
    private static void seekToDataSection(final BufferedReader tspReader) throws IOException
    {
        assert(tspReader != null);
        boolean done = false;
        String line;
        while ( (line = tspReader.readLine()) != null && !done)
            {
            if (line.trim().equals("NODE_COORD_SECTION"))
                return;
            }
        throw new IllegalStateException("No 'NODE_COORD_SECTION' found in TSP file.  Are you sure this file is in TSPLIB format?");
    }

    /** Computes Euclidean distance between two nodes, rounded to the nearest integer. **/
    @Override
    public double desireability(final int from, final int to)
    {
        assert(from >= 1); // TSPLIB node IDs start from 1
        assert(from < numComponents() + 1);
        assert(to >= 1);
        assert(to < numComponents() + 1);
        final double[] fp = nodes.get(from);
        final double[] tp = nodes.get(to);
        return Math.rint(Math.sqrt(Math.pow(fp[0] - tp[0], 2) + Math.pow(fp[1] - tp[1], 2)));
    }

    @Override
    public int numComponents()
    {
        return nodes.size();
    }
    
    /** Representation invariant.  Used for verification. */
    public final boolean repOK()
    {
        return nodes != null
                && !containsNullKey(nodes)
                && !containsNullValue(nodes)
                && !pointsInvalid(nodes.values());
    }
    
    private static boolean containsNullKey(final Map map)
    {
        assert(map != null);
        for (Object o : map.keySet())
            if (o == null)
                return true;
        return false;
    }
    
    private static boolean containsNullValue(final Map map)
    {
        assert(map != null);
        for (Object o : map.values())
            if (o == null)
                return true;
        return false;
    }
    
    private static boolean pointsInvalid(final Collection<double[]> points) {
        assert(points != null);
        for (double[] a : points)
            if (a.length != 2 || Double.isNaN(a[0])|| Double.isInfinite(a[0]) || Double.isNaN(a[1]) || Double.isInfinite(a[1]))
                return true;
        return false;
    }
}