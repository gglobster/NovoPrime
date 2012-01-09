package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.*;
import jebl.util.ProgressListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class NovoPrimeAnnotationGenerator extends SequenceAnnotationGenerator {

    static final String HELP = "NovoPrime automates primer design for recombination engineering";

    public GeneiousActionOptions getActionOptions() {
        // Put the menu item in the Find/Locate part of the Sequence menu
        return new GeneiousActionOptions("Design primers with NovoPrime...")
                .setMainMenuLocation(GeneiousActionOptions.MainMenu.AnnotateAndPredict);
    }

    public String getHelp() {
        return HELP;
    }

    public DocumentSelectionSignature[] getSelectionSignatures() {
        return new DocumentSelectionSignature[] {      // restrict to a single sequence
                new DocumentSelectionSignature(NucleotideSequenceDocument.class, 1, 1)
        };
    }

    public Options getOptions(AnnotatedPluginDocument[] documents,
                              SequenceAnnotationGenerator.SelectionRange selectionRange)
            throws DocumentOperationException {
                SequenceDocument document = (SequenceDocument) documents[0].getDocument();
                return new NovoPrimeOptions(document);
            }

    @Override
    public List<AnnotationGeneratorResult>
    generate(AnnotatedPluginDocument[] documents,
             SequenceAnnotationGenerator.SelectionRange selectionRange,
             ProgressListener progressListener,
             Options options)
            throws DocumentOperationException {

        List<SequenceAnnotationGenerator.AnnotationGeneratorResult> resultsList =
                new ArrayList<SequenceAnnotationGenerator.AnnotationGeneratorResult>();

        NovoPrimeOptions novoprimeOptions = (NovoPrimeOptions) options;

        for (AnnotatedPluginDocument annotatedPluginDocument:documents) {
            //SequenceDocument seqDoc = (SequenceDocument) annotatedPluginDocument.getDocument();
            try {
                // Need a listener on the output stream
                //NovoPrimeExecutionOutputListener outputListener = new NovoPrimeExecutionOutputListener();
                //List<SequenceAnnotation> myAnnots = getTypedFeatures(seqDoc, novoprimeOptions);
                // run method that processes each annotation, generating two sets of primers
                // for annots where design fails, return empty primer annot -- this will be handled later
                //resultsList.add(outputListener.getResults());

                //Testing new idea
                //List<SequenceAnnotation> selectFeatures = novoprimeOptions.getSelectFeatures(seqDoc);


            } catch (Exception e) {
                throw new DocumentOperationException("Something failed:" + e.getMessage(), e);
            }
        }

        // this is later -- display results and offer option to rerun where failed (if empty annot)

        return resultsList;
    }
}