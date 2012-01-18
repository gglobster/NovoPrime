package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotationInterval;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.*;
import com.biomatters.geneious.publicapi.utilities.FileUtilities;
import com.biomatters.geneious.publicapi.utilities.SequenceUtilities;
import jebl.util.CompositeProgressListener;
import jebl.util.ProgressListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

        // Create a progress bar calibrated to the number of documents
        CompositeProgressListener progress =
                new CompositeProgressListener(progressListener, documents.length);
                //TODO: modify to make it reflect number of features instead, or tasks (amplif. vs. verif.)

        for (AnnotatedPluginDocument annotatedPluginDocument:documents) {
            SequenceDocument seqDoc = (SequenceDocument) annotatedPluginDocument.getDocument();

            // Update progress bar with sequence name and progress
            progress.beginSubtask("Running NovoPrime: <i>" + seqDoc.getName() + "</i>");

            //set up container to store results
            SequenceAnnotationGenerator.AnnotationGeneratorResult primerResults =
                    new SequenceAnnotationGenerator.AnnotationGeneratorResult();

            String seqString = seqDoc.getSequenceString();

            try {
                //get primer design options
                NovoPrimeOptions.PrimerOptions primerOptions = novoprimeOptions.getPrimerOptions();
                NovoPrimeLaunchers novoPrimeLaunchers = novoprimeOptions.getNovoPrimeLaunchers();

                //process selected annotation features
                List<SequenceAnnotation> selectFeatList = novoPrimeLaunchers.getSelectFeatList(
                        novoprimeOptions.getFeatList(), novoprimeOptions.getMaskList());

                List<SequenceAnnotation> firstRoundResults = new ArrayList<SequenceAnnotation>();
                ArrayList<ArrayList<String>> verifExplainMsgs = new ArrayList<ArrayList<String>>() {};

                int counter = 0;
                for (SequenceAnnotation oneAnnot:selectFeatList) {
                    counter +=1;
                    //design amplification primer pair
                    List<SequenceAnnotation> amplifPrimerPair = primerOptions.makeAmplifPair(
                            oneAnnot, counter, "A");
                    //add primers in pair to results collector
                    for (SequenceAnnotation amplifPrimer:amplifPrimerPair) {
                        if (amplifPrimer.getIntervals().get(0).getFrom() > seqString.length()) {
                            firstRoundResults.add(new SequenceAnnotation("dummy",
                                    SequenceAnnotation.TYPE_PRIMER_BIND,
                                    new SequenceAnnotationInterval(0,0)));
                        } else if (amplifPrimer.getIntervals().get(0).getFrom() < 0) {
                            firstRoundResults.add(new SequenceAnnotation("dummy",
                                    SequenceAnnotation.TYPE_PRIMER_BIND,
                                    new SequenceAnnotationInterval(0,0)));
                        } else {
                            firstRoundResults.add(amplifPrimer);
                        }
                    }
                    //design verification primer pair
                    ArrayList<ArrayList> verifResults = primerOptions.makeVerifPair(
                            oneAnnot, counter, "V", seqString, progress);
                    List<SequenceAnnotation> verifPrimerPair = verifResults.get(0);
                    ArrayList<String> verifExplainMsg = verifResults.get(1);
                    //add primers in pair to results collector
                    for (SequenceAnnotation verifPrimer:verifPrimerPair) {
                        if (verifPrimer.getIntervals().get(0).getFrom() >= seqString.length()) {
                            firstRoundResults.add(new SequenceAnnotation("dummy",
                                    SequenceAnnotation.TYPE_PRIMER_BIND,
                                    new SequenceAnnotationInterval(0,0)));
                        } else if (verifPrimer.getIntervals().get(0).getFrom() <= 0) {
                            firstRoundResults.add(new SequenceAnnotation("dummy",
                                    SequenceAnnotation.TYPE_PRIMER_BIND,
                                    new SequenceAnnotationInterval(0,0)));
                        } else {
                            firstRoundResults.add(verifPrimer);
                        }
                    }
                    //add primer3 "EXPLAIN" message set
                    verifExplainMsgs.add(verifExplainMsg);
                }

                //offer results summary + options for a second round
                List<SequenceAnnotation> finalList = novoPrimeLaunchers.launchFirstRoundSummary(
                        seqDoc, selectFeatList, firstRoundResults, verifExplainMsgs);

                BufferedWriter out = new BufferedWriter(new FileWriter(
                        FileUtilities.getUserSelectedSaveFile(
                                "Save", "Select Output File Location", primerOptions.baseID+"_primers.txt", "TXT")));

                for (SequenceAnnotation primer:finalList) {
                    primerResults.addAnnotationToAdd(primer);
                    //extract primer info
                    String primerSequence;
                    Integer primer_start = primer.getIntervals().get(0).getFrom();
                    Integer primer_stop = primer.getIntervals().get(0).getTo();
                    String primer_dir = primer.getIntervals().get(0).getDirection().toString();
                    if (primer_dir.equals("leftToRight")) {
                        primerSequence = seqString.subSequence(primer_start-1, primer_stop).toString();
                    } else {
                        primerSequence = SequenceUtilities.reverseComplement(
                                seqString.subSequence(primer_stop-1, primer_start)).toString();
                    }
                    if (primer.getName().endsWith("_fwd")) {
                        primerSequence += primer.getQualifierValue("fwd_tail");
                    } else if (primer.getName().endsWith("_rev")) {
                        primerSequence += primer.getQualifierValue("rev_tail");
                    }
                    //write info to file
                    out.write(primer.getName());
                    out.write("\t");
                    out.write(primerSequence);
                    out.write("\n");
                }
                out.close();

                resultsList.add(primerResults);
                
            } catch (Exception e) {
                throw new DocumentOperationException("Something went wrong:" + e.getMessage(), e);
            }
        }

        return resultsList;
    }

}