package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotationInterval;
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

        //TODO: modify to parse output from Primer3 instead of Phobos
        String repeatClass = output.substring(0, 16).trim();
        int startPos = Integer.parseInt(output.substring(17, 27).trim());
        int stopPos = Integer.parseInt(output.substring(29, 39).trim());

        final SequenceAnnotationInterval interval = new SequenceAnnotationInterval(startPos, stopPos);
        SequenceAnnotation annotation = new SequenceAnnotation(repeatClass,
                SequenceAnnotation.TYPE_PRIMER_BIND, interval);

        results.addAnnotationToAdd(annotation);
    }
}