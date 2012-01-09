package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator;
import com.biomatters.geneious.publicapi.utilities.Execution;

public class NovoPrimeExecutionOutputListener extends Execution.OutputListener {

    final SequenceAnnotationGenerator.AnnotationGeneratorResult results =
            new SequenceAnnotationGenerator.AnnotationGeneratorResult();

    SequenceAnnotationGenerator.AnnotationGeneratorResult getResults() {
        return results;
    }

    @Override
    public void stderrWritten(String output) {
        // Pass error messages through flagging them as coming from stderr
        System.out.println("stderr " + output);
    }
    @Override
    public void stdoutWritten(String output) {
        // Pass error messages through flagging them as coming from stdout
        System.out.println("stdout " + output);
    }
}