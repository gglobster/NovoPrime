package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator;

/**
 * This plugin automates the design of two types of primers (amplification & verification) for use
 * in recombination engineering.
 * <p/>
 */
public class NovoPrimePlugin extends GeneiousPlugin {
    public SequenceAnnotationGenerator[] getSequenceAnnotationGenerators() {
        return new SequenceAnnotationGenerator[]{
                new NovoPrimeAnnotationGenerator()
        };
    }

    public String getName() {
        return "NovoPrime Primer Design Tool";
    }

    public String getHelp() {
        return NovoPrimeAnnotationGenerator.HELP ;
    }

    public String getDescription() {
        return "Automated primer design for recombination engineering.";
    }

    public String getAuthors() {
        return "Geraldine A. Van der Auwera";
    }

    public String getVersion() {
        return "0.4";
    }

    public String getMinimumApiVersion() {
        return "4.1";
    }

    public int getMaximumApiVersion() {
        return 4;
    }
}

//TODO: JRE version check