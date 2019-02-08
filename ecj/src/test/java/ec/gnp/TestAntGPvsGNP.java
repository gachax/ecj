package ec.gnp;

import ec.EvolutionState;
import ec.Evolve;
import ec.util.Output;
import ec.util.ParameterDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestAntGPvsGNP {

    private final String antGpParamsFilePath = System.getProperty("user.dir") + "/ecj/src/main/resources/ec/app/ant/ant.params";
    private final String antGnpParamsFilePath = System.getProperty("user.dir") + "/ecj/src/main/resources/ec/app/ant/gnp.params";

    private EvolutionState antGpState;
    private EvolutionState antGnpState;

    @Before
    public void setup() throws IOException {

        //Initialize everything without running the evolution
        ParameterDatabase dbase = new ParameterDatabase(new File(antGpParamsFilePath));
        Output out = Evolve.buildOutput();
        //out.getLog(0).silent = true;
        //out.getLog(1).silent = true;
        antGpState = Evolve.initialize(dbase, 0, out);

        //Initialize everything without running the evolution
        ParameterDatabase dbase2 = new ParameterDatabase(new File(antGnpParamsFilePath));
        Output out2 = Evolve.buildOutput();
        //out2.getLog(0).silent = true;
        //out2.getLog(1).silent = true;
        antGnpState = Evolve.initialize(dbase2, 0, out);

    }

    @After
    public void cleanup() {

        Evolve.cleanup(antGpState);

    }

    @Test
    //@Ignore
    public void runAntGp(){

        antGpState.run(EvolutionState.C_STARTED_FRESH);

    }

    @Test
    public  void runAntGnp(){

        antGnpState.run(EvolutionState.C_STARTED_FRESH);

    }

}
