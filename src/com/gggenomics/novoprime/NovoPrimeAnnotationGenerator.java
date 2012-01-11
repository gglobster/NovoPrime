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

        //temporary file to store sequence in Boulder-IO format

        for (AnnotatedPluginDocument annotatedPluginDocument:documents) {
            SequenceDocument seqDoc = (SequenceDocument) annotatedPluginDocument.getDocument();


            try {
                // Need a listener on the output stream
                //NovoPrimeExecutionOutputListener outputListener = new NovoPrimeExecutionOutputListener();

                //set up container to store results
                SequenceAnnotationGenerator.AnnotationGeneratorResult primerResults =
                        new SequenceAnnotationGenerator.AnnotationGeneratorResult();

                //get primer design options
                NovoPrimeOptions.AmplifOptions amplifOptions = novoprimeOptions.getAmplifOptions();
                NovoPrimeOptions.VerifOptions verifOptions = novoprimeOptions.getVerifOptions();

                //process selected annotation features
                List<SequenceAnnotation> selectFeatList = novoprimeOptions.getSelectFeatList();
                int counter = 0;
                for (SequenceAnnotation oneAnnot:selectFeatList) {
                    counter +=1;
                    //design amplification primer pair
                    List<SequenceAnnotation> amplifPrimerPair = amplifOptions.makeAmplifPair(oneAnnot, counter, "A");
                    //add primers in pair to results collector
                    for (SequenceAnnotation amplifPrimer:amplifPrimerPair) {
                        primerResults.addAnnotationToAdd(amplifPrimer);
                    }
                    //design verification primer pair
                    List<SequenceAnnotation> verifPrimerPair = verifOptions.makeVerifPair(oneAnnot, counter, "V");
                    //add primers in pair to results collector
                    for (SequenceAnnotation verifPrimer:verifPrimerPair) {
                        primerResults.addAnnotationToAdd(verifPrimer);
                    }
                //TODO: catch cases where features are too close to the edges

                }

                resultsList.add(primerResults);

            } catch (Exception e) {
                throw new DocumentOperationException("Something went wrong:" + e.getMessage(), e);
            }
        }



        return resultsList;
    }

}