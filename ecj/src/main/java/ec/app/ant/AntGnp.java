/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.ant;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.gnp.*;
import ec.gnp.selection.GnpEgreedy;
import ec.gnp.selection.GnpSubnodeSelector;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;

/* 
 * Ant.java
 * 
 * Created: Mon Nov  1 15:46:19 1999
 * By: Sean Luke
 */

/**
 * Ant implements the Artificial Ant problem.
 *
 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>data</tt><br>
 <font size=-1>classname, inherits or == ec.gp.GPData</font></td>
 <td valign=top>(the class for the prototypical GPData object for the Ant problem)</td></tr>
 <tr><td valign=top><i>base</i>.<tt>file</tt><br>
 <font size=-1>String</font></td>
 <td valign=top>(filename of the .trl file for the Ant problem)</td></tr>
 <tr><td valign=top><i>base</i>.<tt>turns</tt><br>
 <font size=-1>int &gt;= 1</td>
 <td valign=top>(maximal number of moves the ant may make)</td></tr>
 </table>

 <p><b>Parameter bases</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>data</tt></td>
 <td>species (the GPData object)</td></tr>
 </table>
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class AntGnp extends Problem implements SimpleProblemForm
    {
    public static final String P_FILE = "file";
    public static final String P_MOVES = "moves";

    // map point descriptions
    public static final int ERROR = 0;
    public static final int FOOD = -1;
    public static final int EMPTY = 1;
    public static final int TRAIL = 2;
    public static final int ATE = 3;

    // orientations
    public static final int O_UP = 0;
    public static final int O_LEFT = 1;
    public static final int O_DOWN = 2;
    public static final int O_RIGHT = 3;

    // maximum number of moves
    public int maxMoves;

    // how much food we have
    public int food;

    // our map
    public int map[][];

    //Map for printing
    public int map2[][];
    
    // store the positions of food so we can reset our map
    // don't need to be deep-cloned, they're read-only
    public int foodx[];
    public int foody[];

    // map[][]'s bounds
    public int maxx;
    public int maxy;

    // our position
    public int posx;
    public int posy;

    // how many points we've gotten
    public int sum;
    
    // our orientation
    public int orientation;

    // how many moves we've made
    public int moves;

    // print modulo for doing the abcdefg.... thing at print-time
    public int pmod;

    //For testing delayed reward functionality of GNP, usually evaluationIds will not be stored on problem, but somewhere else where rewards are set from
    public Object2ObjectOpenHashMap<Individual, IntArrayList> functionEvaluationIds = new Object2ObjectOpenHashMap<>();

    public Object clone()
        {
        AntGnp myobj = (AntGnp) (super.clone());
        myobj.functionEvaluationIds = new Object2ObjectOpenHashMap<>();
        myobj.map = new int[map.length][];
        for(int x=0;x<map.length;x++)
            myobj.map[x] = (int[])(map[x].clone());
        return myobj;
        }

    public void setup(final EvolutionState state,
        final Parameter base)
        {
        // very important, remember this
        super.setup(state,base);

        // No need to verify the GPData object

        // not using any default base -- it's not safe

        // how many maxMoves?
        maxMoves = state.parameters.getInt(base.push(P_MOVES),null,1);
        if (maxMoves==0)
            state.output.error("The number of moves an ant has to make must be >0");
        
        // load our file
        //File filename = state.parameters.getFile(base.push(P_FILE),null);
        //if (filename==null)
        //    state.output.fatal("Ant trail file name not provided.");
        InputStream str = state.parameters.getResource(base.push(P_FILE), null);
        if (str == null)
            state.output.fatal("Error loading file or resource", base.push(P_FILE), null);
        
        food = 0;
        LineNumberReader lnr = null;
        try
            {
            lnr = 
                //new LineNumberReader(new FileReader(filename));
                new LineNumberReader(new InputStreamReader(str));
            
            StringTokenizer st = new StringTokenizer(lnr.readLine()); // ugh
            maxx = Integer.parseInt(st.nextToken());
            maxy = Integer.parseInt(st.nextToken());
            map = new int[maxx][maxy];
            int y;
            for(y=0;y<maxy;y++)
                {
                String s = lnr.readLine();
                if (s==null)
                    {
                    state.output.warning("Ant trail file ended prematurely");
                    break;
                    }
                int x;
                for(x=0;x<s.length();x++)
                    {
                    if (s.charAt(x)==' ')
                        map[x][y]=EMPTY;
                    else if (s.charAt(x)=='#')
                        { map[x][y]=FOOD; food++; }
                    else if (s.charAt(x)=='.')
                        map[x][y]=TRAIL;
                    else state.output.error("Bad character '" + s.charAt(x) + "' on line number " + lnr.getLineNumber() + " of the Ant trail file.");
                    }
                // fill out rest of X's
                for(int z=x;z<maxx;z++)
                    map[z][y]=EMPTY;
                }
            // fill out rest of Y's
            for (int z=y;z<maxy;z++)
                for(int x=0;x<maxx;x++)
                    map[x][z]=EMPTY;
            }
        catch (NumberFormatException e)
            {
            state.output.fatal("The Ant trail file does not begin with x and y integer values.");
            }
        catch (IOException e)
            {
            state.output.fatal("The Ant trail file could not be read due to an IOException:\n" + e);
            }
        finally
            {
            try { if (lnr != null) lnr.close(); } catch (IOException e) { }
            }
        state.output.exitIfErrors();

        // load foodx and foody reset arrays
        foodx = new int[food];
        foody = new int[food];
        int tmpf = 0;
        for(int x=0;x<map.length;x++)
            for(int y=0;y<map[0].length;y++)
                if (map[x][y]==FOOD) 
                    { foodx[tmpf] = x; foody[tmpf] = y; tmpf++; }

        //for printing
        map2 = new int[map.length][];
        for(int x=0;x<map.length;x++)
            map2[x] = (int[])(map[x].clone());

        map2[posx][posy] = pmod; pmod++;

        }

    public void evaluate(final EvolutionState state, 
        final Individual ind, 
        final int subpopulation,
        final int threadnum)
        {
            //System.out.println("---------- ind: " + ind);
            //System.out.println(((GnpIndividual)ind).stringOutputGenomeVsNetwork());

        if (!ind.evaluated)  {

            //learning phase
            sum = 0;
            posx = 0;
            posy = 0;
            orientation = O_RIGHT;

            //remove previous evaluations
            int prevMoves = 0;
            for(moves=0;moves<maxMoves && sum<food; ) {
                ((GnpIndividual) ind).evaluateLearnExplore(state, threadnum, null,this);
                if (prevMoves == moves){
                    break;
                }
                prevMoves = moves;

                //if delayed reward is used, then set it
                setDelayedReward(ind);

            }

            //clear evaluation data
            ((GnpIndividual) ind).afterEvaluation();

            //execution phase

            // clean up array
            for(int y=0;y<food;y++)
                map[foodx[y]][foody[y]] = FOOD;

            sum = 0;
            posx = 0;
            posy = 0;
            orientation = O_RIGHT;

            prevMoves = 0;
            for(moves=0;moves<maxMoves && sum<food; ) {
                ((GnpIndividual) ind).evaluateDontLearnDontExplore(state, threadnum, this);
                if (prevMoves == moves){
                    break;
                }
                prevMoves = moves;

                //if delayed reward is used, then set it
                setDelayedReward(ind);

            }

            //clear evaluation data
            ((GnpIndividual) ind).afterEvaluation();

            SimpleFitness f = (SimpleFitness) ind.fitness;
            f.setFitness(state, sum, sum >=food);
            ind.evaluated = true;

            // clean up array
            for(int y=0;y<food;y++)
                map[foodx[y]][foody[y]] = FOOD;
            }
        }

    public void describe(
        final EvolutionState state, 
        final Individual ind, 
        final int subpopulation, 
        final int threadnum,
        final int log)

        {

        sum = 0;
        pmod = 97; /** ascii a */
        posx = 0;
        posy = 0;
        orientation = O_RIGHT;

        map2 = new int[map.length][];
        for(int x=0;x<map.length;x++)
            map2[x] = (int[])(map[x].clone());

        map2[posx][posy] = pmod; pmod++;

        state.output.println("\n\nBest Individual's Stats\n=====================", log);
        ((GnpIndividual) ind).clearExecutionPaths();
        state.output.println(((GnpIndividual)ind).stringOutputGenomeVsNetwork(), log);
        state.output.println("", log);
        state.output.println(((GnpIndividual)ind).graphOutputNetwork(), log);
        state.output.println("", log);

        int prevMoves = 0;
        for(moves=0;moves<maxMoves && sum<food; ) {
            Int2ObjectOpenHashMap<ObjectArrayList<GnpNodeEvaluationResult>> executionPaths = (((GnpIndividual) ind).evaluateDontLearnDontExplore(state, threadnum, this));
            //state.output.println("", log);
            //state.output.println(((GnpIndividual) ind).stringOutputExecutionPath(executionPath), log);
            state.output.println("", log);
            for (Int2ObjectOpenHashMap.Entry  entry : executionPaths.int2ObjectEntrySet()) {
                state.output.println("--- "  + String.valueOf(entry.getIntKey()), log);
                state.output.println(((GnpIndividual) ind).graphOutputExecutionPath((List<GnpNodeEvaluationResult>) entry.getValue()), log);
            }
            state.output.println("", log);
            if (prevMoves == moves){
                break;
            }
            prevMoves = moves;
            setDelayedReward(ind);

        }

        //clear evaluation data
        ((GnpIndividual) ind).afterEvaluation();

        for (Int2ObjectOpenHashMap<ObjectArrayList<GnpNodeEvaluationResult>> executionPaths : ((GnpIndividual) ind).getAllExecPaths()) {
            for (Int2ObjectOpenHashMap.Entry  entry : executionPaths.int2ObjectEntrySet()) {
                state.output.println(((GnpIndividual) ind).stringOutputExecutionPath((List<GnpNodeEvaluationResult>) entry.getValue()), log);
            }
        }

        state.output.println("", log);
        state.output.println("Moves: " + moves, log);
        state.output.println("Max moves: " + maxMoves, log);
        state.output.println("Food: " + sum, log);
        state.output.println("Max food: " + food, log);

        state.output.println("\n\nBest Individual's Map\n=====================", log);

        // print out the map
        for(int y=0;y<map2.length;y++)
            {
            for(int x=0;x<map2.length;x++)
                {
                switch(map2[x][y])
                    {
                    case FOOD: 
                        state.output.print("#",log);
                        break;
                    case EMPTY: 
                        state.output.print(".",log);
                        break;
                    case TRAIL: 
                        state.output.print("+",log);
                        break;
                    case ATE:
                        state.output.print("?",log);
                        break;
                    default:
                        state.output.print(""+((char)map2[x][y]),log);
                        break;
                    }
                }
            state.output.println("",log);
        }

    }

        private void setDelayedReward(Individual ind) {
            //if delayed reward is used, then set it
            IntArrayList evalIds = functionEvaluationIds.get(ind);
            if (evalIds != null && !evalIds.isEmpty()) {
                for (int evalId : evalIds) {
                    ((GnpIndividual) ind).setDelayedReward(evalId, 1.0);
                }
                functionEvaluationIds.remove(ind);
            }
        }

    }
