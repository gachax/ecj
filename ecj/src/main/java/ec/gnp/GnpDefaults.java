package ec.gnp;

import ec.DefaultsForm;
import ec.util.Parameter;

import java.io.Serializable;

/**
 * Contains default values of parameters. (Saw it somewhere in ecj, but I guess the full potential of it is missed here)
 * @author Gatis Birkens
 */
public class GnpDefaults implements DefaultsForm, Serializable {

    static final String P_GNP = "gnp";

    /** Returns the default base. */
    public static Parameter base()
    {
        return new Parameter(P_GNP);
    }

}
