package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotationInterval;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.*;
import com.biomatters.geneious.publicapi.utilities.Execution;
import com.biomatters.geneious.publicapi.utilities.SequenceUtilities;
import jebl.util.CompositeProgressListener;
import org.virion.jam.util.SimpleListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class NovoPrimeDoOverOptions extends Options {

    public NovoPrimeDoOverOptions(
            final SequenceDocument seqDoc,
            Integer featNum,
            ArrayList<String> messageList, 
            final SequenceAnnotation annotFeat,
            FileSelectionOption codeLocation,
            String baseID,
            Integer offsetStart,
            Integer offsetStop,
            final String mode) {

        featureDetails(seqDoc, featNum, annotFeat, baseID, offsetStart, offsetStop);

        if (mode.equals("primer3")) {

            diagnosticMessage(messageList);
            secondRoundVerifPrimersOptions(codeLocation);

            verifPrimerDistMinOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerDistMinOption.getValue() > verifPrimerDistOptimOption.getValue()) {
                        verifPrimerDistOptimOption.setValue(verifPrimerDistMinOption.getValue());
                    }
                    if (verifPrimerDistMinOption.getValue() > verifPrimerDistMaxOption.getValue()) {
                        verifPrimerDistMaxOption.setValue(verifPrimerDistMinOption.getValue());
                    }
                }
            });
            verifPrimerDistOptimOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerDistOptimOption.getValue() > verifPrimerDistMaxOption.getValue()) {
                        verifPrimerDistMaxOption.setValue(verifPrimerDistOptimOption.getValue());
                    }
                    if (verifPrimerDistOptimOption.getValue() < verifPrimerDistMinOption.getValue()) {
                        verifPrimerDistMinOption.setValue(verifPrimerDistOptimOption.getValue());
                    }
                }
            });
            verifPrimerDistMaxOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerDistMaxOption.getValue() < verifPrimerDistOptimOption.getValue()) {
                        verifPrimerDistOptimOption.setValue(verifPrimerDistMaxOption.getValue());
                    }
                    if (verifPrimerDistMaxOption.getValue() < verifPrimerDistMinOption.getValue()) {
                        verifPrimerDistMinOption.setValue(verifPrimerDistMaxOption.getValue());
                    }
                }
            });
            verifPrimerLengthMinOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerLengthMinOption.getValue() > verifPrimerLengthOptimOption.getValue()) {
                        verifPrimerLengthOptimOption.setValue(verifPrimerLengthMinOption.getValue());
                    }
                    if (verifPrimerLengthMinOption.getValue() > verifPrimerLengthMaxOption.getValue()) {
                        verifPrimerLengthMaxOption.setValue(verifPrimerLengthMinOption.getValue());
                    }
                }
            });
            verifPrimerLengthOptimOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerLengthOptimOption.getValue() > verifPrimerLengthMaxOption.getValue()) {
                        verifPrimerLengthMaxOption.setValue(verifPrimerLengthOptimOption.getValue());
                    }
                    if (verifPrimerLengthOptimOption.getValue() < verifPrimerLengthMinOption.getValue()) {
                        verifPrimerLengthMinOption.setValue(verifPrimerLengthOptimOption.getValue());
                    }
                }
            });
            verifPrimerLengthMaxOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerLengthMaxOption.getValue() < verifPrimerLengthMinOption.getValue()) {
                        verifPrimerLengthMinOption.setValue(verifPrimerLengthMaxOption.getValue());
                    }
                    if (verifPrimerLengthMaxOption.getValue() < verifPrimerLengthOptimOption.getValue()) {
                        verifPrimerLengthOptimOption.setValue(verifPrimerLengthMaxOption.getValue());
                    }
                }
            });
            verifPrimerTmMinOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerTmMinOption.getValue() > verifPrimerTmOptimOption.getValue()) {
                        verifPrimerTmOptimOption.setValue(verifPrimerTmMinOption.getValue());
                    }
                    if (verifPrimerTmMinOption.getValue() > verifPrimerTmMaxOption.getValue()) {
                        verifPrimerTmMaxOption.setValue(verifPrimerTmMinOption.getValue());
                    }
                }
            });
            verifPrimerTmOptimOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerTmOptimOption.getValue() > verifPrimerTmMaxOption.getValue()) {
                        verifPrimerTmMaxOption.setValue(verifPrimerTmOptimOption.getValue());
                    }
                    if (verifPrimerTmOptimOption.getValue() < verifPrimerTmMinOption.getValue()) {
                        verifPrimerTmMinOption.setValue(verifPrimerTmOptimOption.getValue());
                    }
                }
            });
            verifPrimerTmMaxOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerTmMaxOption.getValue() < verifPrimerTmMinOption.getValue()) {
                        verifPrimerTmMinOption.setValue(verifPrimerTmMaxOption.getValue());
                    }
                    if (verifPrimerTmMaxOption.getValue() < verifPrimerTmOptimOption.getValue()) {
                        verifPrimerTmOptimOption.setValue(verifPrimerTmMaxOption.getValue());
                    }
                }
            });
            verifPrimerGCMinOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerGCMinOption.getValue() > verifPrimerGCOptimOption.getValue()) {
                        verifPrimerGCOptimOption.setValue(verifPrimerGCMinOption.getValue());
                    }
                    if (verifPrimerGCMinOption.getValue() > verifPrimerGCMaxOption.getValue()) {
                        verifPrimerGCMaxOption.setValue(verifPrimerGCMinOption.getValue());
                    }
                }
            });
            verifPrimerGCOptimOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (verifPrimerGCOptimOption.getValue() > verifPrimerGCMaxOption.getValue()) {
                        verifPrimerGCMaxOption.setValue(verifPrimerGCOptimOption.getValue());
                    }
                    if (verifPrimerGCOptimOption.getValue() < verifPrimerGCMinOption.getValue()) {
                        verifPrimerGCMinOption.setValue(verifPrimerGCOptimOption.getValue());
                    }
                }
            });
            verifPrimerGCMaxOption.addChangeListener(new SimpleListener() {
            public void objectChanged() {
                if (verifPrimerGCMaxOption.getValue() < verifPrimerGCMinOption.getValue()) {
                    verifPrimerGCMinOption.setValue(verifPrimerGCMaxOption.getValue());
                }
                if (verifPrimerGCMaxOption.getValue() < verifPrimerGCOptimOption.getValue()) {
                    verifPrimerGCOptimOption.setValue(verifPrimerGCMaxOption.getValue());
                }
            }
        });

        } else if (mode.equals("manual")) {
            manualInputPickingZoneOptions();
            setInitialDistState(seqDoc, annotFeat);
            makePickingButtons();

            verifPrimerDistMinOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (mode.equals("manual")){
                        fwdPickZone = extractPickZone(seqDoc, annotFeat).get(0);
                        revPickZone = extractPickZone(seqDoc, annotFeat).get(1);
                    }
                    if (verifPrimerDistMinOption.getValue() > verifPrimerDistMaxOption.getValue()) {
                        verifPrimerDistMaxOption.setValue(verifPrimerDistMinOption.getValue());
                    }
                }
            });
            verifPrimerDistMaxOption.addChangeListener(new SimpleListener() {
                public void objectChanged() {
                    if (mode.equals("manual")) {
                        fwdPickZone = extractPickZone(seqDoc, annotFeat).get(0);
                        revPickZone = extractPickZone(seqDoc, annotFeat).get(1);
                    }
                    if (verifPrimerDistMaxOption.getValue() < verifPrimerDistMinOption.getValue()) {
                        verifPrimerDistMinOption.setValue(verifPrimerDistMaxOption.getValue());
                    }
                }
            });
        }

    }

    NovoPrimeLaunchers novoPrimeLaunchers = new NovoPrimeLaunchers();

    FileSelectionOption codeLocation;
    String baseID;
    String primerBaseName;
    Integer offsetStart;
    Integer offsetStop;

    SequenceDocument seqDoc;

    String fwdPickZone;
    String revPickZone;
    String fwdPickPrimer;
    String revPickPrimer;

    SequenceAnnotation fwdPrimerAnnot;
    SequenceAnnotation revPrimerAnnot;

    JLabel actionFwd = new JLabel(fwdPickPrimer);
    JLabel actionRev = new JLabel(revPickPrimer);
    JLabel actionProximityWarning = new JLabel("The Min and Max Distance must fall within the sequence");

    //initial state
    private void setInitialDistState(SequenceDocument sequenceDocument, SequenceAnnotation annotFeat) {
        seqDoc = sequenceDocument;
        fwdPickZone = extractPickZone(seqDoc, annotFeat).get(0);
        revPickZone = extractPickZone(seqDoc, annotFeat).get(1);
    }

    //extract pick zone
    private List<String> extractPickZone(SequenceDocument seqDoc, SequenceAnnotation annotFeature) {

        List<String> pickZones = new ArrayList<String>();
        String direction = annotFeature.getIntervals().get(0).getDirection().toString();

        Integer featStart;
        Integer featStop;

        Integer fwdLeftBound;
        Integer fwdRightBound;
        Integer revLeftBound;
        Integer revRightBound;

        //calculate coordinate points
        if (direction.equals("leftToRight")) {
            //define feature start & stop
            featStart = annotFeature.getIntervals().get(0).getFrom();
            featStop = featStart+annotFeature.getIntervals().get(0).getLength();
            //forward primer boundaries
            fwdLeftBound = featStart+offsetStart-verifPrimerDistMaxOption.getValue();
            if (fwdLeftBound < 1) {
                fwdLeftBound = 1;
            }
            fwdRightBound = featStart+offsetStart-verifPrimerDistMinOption.getValue();
            if (fwdRightBound < 2) {
                fwdRightBound = 2;
            }
            //reverse primer boundaries
            revLeftBound = featStop+offsetStop+verifPrimerDistMinOption.getValue();
            if (revLeftBound > seqDoc.getSequenceString().length()-2) {
                revLeftBound = seqDoc.getSequenceString().length()-2;
            }
            revRightBound = featStop+offsetStop+verifPrimerDistMaxOption.getValue();
            if (revRightBound > seqDoc.getSequenceString().length()-1) {
                revRightBound = seqDoc.getSequenceString().length()-1;
            }
            //determine pick zone boundaries
            fwdPickZone = seqDoc.getSequenceString().substring(fwdLeftBound, fwdRightBound);
            revPickZone = SequenceUtilities.reverseComplement(
                    seqDoc.getCharSequence().subSequence(revLeftBound, revRightBound)).toString();
        } else { //if (direction.equals("rightToLeft")) {    //TODO: handle "no direction" case
            //define feature start & stop
            featStart = annotFeature.getIntervals().get(0).getFrom();
            featStop = featStart-annotFeature.getIntervals().get(0).getLength();
            //forward primer boundaries
            fwdLeftBound = featStart-offsetStart+verifPrimerDistMinOption.getValue();
            if (fwdLeftBound > seqDoc.getSequenceString().length()-2) {
                fwdLeftBound = seqDoc.getSequenceString().length()-2;
            }
            fwdRightBound = featStart-offsetStart+verifPrimerDistMaxOption.getValue();
            if (fwdRightBound > seqDoc.getSequenceString().length()-1) {
                fwdRightBound = seqDoc.getSequenceString().length()-1;
            }
            //reverse primer boundaries
            revLeftBound = featStop-offsetStop-verifPrimerDistMaxOption.getValue();
            if (revLeftBound < 1) {
                revLeftBound = 1;
            }
            revRightBound = featStop-offsetStop-verifPrimerDistMinOption.getValue();
            if (revRightBound < 2) {
                revRightBound = 2;
            }
            //determine pick zone boundaries
            fwdPickZone = SequenceUtilities.reverseComplement(
                    seqDoc.getCharSequence().subSequence(fwdLeftBound, fwdRightBound)).toString();
            revPickZone = seqDoc.getSequenceString().substring(revLeftBound, revRightBound);
        }
        pickZones.add(fwdPickZone);
        pickZones.add(revPickZone);
        return pickZones;
    }
    
    //load picking buttons
    private void makePickingButtons() {
        addDivider("Pick Primers");
        beginAlignHorizontally("", false);
        addCustomComponent(getFwdPickButton());
        addCustomComponent(actionFwd);
        endAlignHorizontally();
        beginAlignHorizontally("", false);
        addCustomComponent(getRevPickButton());
        addCustomComponent(actionRev);
        endAlignHorizontally();
    }

    //forward picking button
    private JComponent getFwdPickButton() {
        return new JButton(new AbstractAction("Pick FWD...") {
            public void actionPerformed(ActionEvent e) {
                launchManualPicking(fwdPickZone, "fwd");

            }
        });
    }
    //reverse picking button
    private JComponent getRevPickButton() {
        return new JButton(new AbstractAction("Pick REV...") {
            public void actionPerformed(ActionEvent e) {
                launchManualPicking(revPickZone, "rev");
            }
        });
    }

    //manual picking launcher
    private void launchManualPicking(String pickZone, String side) {
        JPanel pickingPane = new JPanel();
        pickingPane.setLayout(new BoxLayout(pickingPane, BoxLayout.PAGE_AXIS));
        pickingPane.add(new JLabel("The selected picking zone is shown in the correct orientation."));
        pickingPane.add(new JLabel("Copy the desired primer sequence and paste it into the box below."));
        pickingPane.add(new JToolBar.Separator());
        JTextArea pickZoneText = new JTextArea(pickZone);
        pickZoneText.setLineWrap(true);
        pickZoneText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(pickZoneText);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        pickingPane.add(scrollPane);
        pickingPane.add(new JToolBar.Separator());
        JTextField primerSeq = new JTextField(null, "", 0);
        primerSeq.setPreferredSize(new Dimension(150, 25));
        pickingPane.add(primerSeq);
        Object[] options = {"Save Primer", "Cancel"};
        int n = JOptionPane.showOptionDialog(null, pickingPane, "Manual Primer Selection",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (n == 0) {
            CharSequence tempPrimer = primerSeq.getText();
            //find the match position of the primer to the sequence
            Integer firstIndex = seqDoc.getSequenceString().indexOf(tempPrimer.toString());
            if (firstIndex != -1) {
                Integer lastIndex = seqDoc.getSequenceString().lastIndexOf(tempPrimer.toString());
                if (firstIndex.equals(lastIndex)) {  //make sure there is only one match
                    savePrimer(firstIndex, tempPrimer.toString(), side,  true);
                } else {
                    JOptionPane.showMessageDialog(
                            null, "The chosen primer matches the sequence in multiple places.\n" +
                            "Please select a different "+side.toUpperCase()+" primer sequence.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {   //try the reverse-complement
                firstIndex = seqDoc.getSequenceString().indexOf(
                        SequenceUtilities.reverseComplement(tempPrimer).toString());
                if (firstIndex != -1) {
                    Integer lastIndex = seqDoc.getSequenceString().lastIndexOf(
                            SequenceUtilities.reverseComplement(tempPrimer).toString());
                    if (firstIndex.equals(lastIndex)) {  //make sure there is only one match
                        savePrimer(firstIndex, tempPrimer.toString(), side, false);
                    } else {
                        JOptionPane.showMessageDialog(
                                null, "The chosen primer matches the sequence in multiple places.\n" +
                                "Please select a different "+side.toUpperCase()+" primer sequence.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null, "The chosen primer did not match any of the sequence.\n" +
                            "Please select a different "+side.toUpperCase()+" primer sequence.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            //nothing happens
        }
    }
    //actually save the primers and make seqAnnots
    private void savePrimer(Integer index, String tempPrimer, String side, Boolean direct) {
        Integer pStart;
        Integer pStop;
        System.out.println(tempPrimer);
        //determine coordinates
        if (direct) {
            pStart = index+1;
            pStop = index+tempPrimer.length();
        } else {
            pStop = index+1;
            pStart = index+tempPrimer.length();
        }
        //create the primer annotation
        SequenceAnnotation tempPrimerAnnot = new SequenceAnnotation(
                    primerBaseName+"M_"+side,
                    SequenceAnnotation.TYPE_PRIMER_BIND,
                    new SequenceAnnotationInterval(
                            pStart, pStop));
        //determine name
        if (side.equals("fwd")){
            fwdPrimerAnnot = tempPrimerAnnot;
            fwdPickPrimer = tempPrimer;
            actionFwd.setText(fwdPickPrimer);
        } else {
            revPrimerAnnot = tempPrimerAnnot;
            revPickPrimer = tempPrimer;
            actionRev.setText(revPickPrimer);
        }
    }

    //display feature details
    private void featureDetails(SequenceDocument seqDoc,
                                Integer featNum,
                                SequenceAnnotation annotFeat,
                                String priorBaseID,
                                Integer priorOffsetStart,
                                Integer priorOffsetStop) {
        baseID = priorBaseID;
        primerBaseName = baseID+"_"+annotFeat.getName().replace(" ", ".")+"_V"+featNum;   //for later
        offsetStart = priorOffsetStart;
        offsetStop = priorOffsetStop;
        addDivider("Target Details");
        beginAlignHorizontally("Name: ", false);
        addLabel(annotFeat.getName());
        endAlignHorizontally();
        beginAlignHorizontally("Location: ", false);
        Integer featStart = annotFeat.getIntervals().get(0).getFrom();
        Integer featStop = annotFeat.getIntervals().get(0).getTo();
        if (featStop > featStart) {
            addLabel(featStart.toString());
            addLabel(annotFeat.getIntervals().get(0).getDirection().toArrowString());
            addLabel(featStop.toString());
        } else {
            addLabel(featStop.toString());
            addLabel(annotFeat.getIntervals().get(0).getDirection().toArrowString());
            addLabel(featStart.toString());
        }
        addLabel("/ "+seqDoc.getSequenceLength()+" bp");
        endAlignHorizontally();
        beginAlignHorizontally("Offset: ", false);
        addLabel("start "+offsetStart.toString()+" -//- stop "+offsetStop.toString());
        endAlignHorizontally();
    }
    //display Primer3 diagnostics messages for info
    private void diagnosticMessage(ArrayList<String> messageList) {
        Boolean flag = false;
        Integer counter = 0;
        addDivider("Diagnostics");
        for (String line:messageList) {
            if (line.startsWith("EXPLAIN_FLAG")) {
                continue;
            } else {
                if (counter == 0){
                    beginAlignHorizontally("Error message: ", false);
                    addLabel("> "+line);
                    endAlignHorizontally();
                    counter +=1;
                } else {
                    addLabel("> "+line);
                }
                if (line.equals("Illegal value for SEQUENCE_INCLUDED_REGION")) {
                    beginAlignHorizontally("Probable cause: ", false);
                    addLabel("The annotation feature is near the edge of the sequence.");
                    endAlignHorizontally();
                    addLabel("The primer picking zone should not extend beyond the end of the sequence.");
                    beginAlignHorizontally("Recommendation: ", false);
                    addLabel("Try reducing the Distance Min and Max.");
                    endAlignHorizontally();
                } else {
                    flag = true;
                }
            }
        }
        if (flag) {
            beginAlignHorizontally("Probable cause: ", false);
            addLabel("Parameters were too stringent.");
            endAlignHorizontally();
            beginAlignHorizontally("Recommendation: ", false);
            addLabel("Try relaxing the design constraints.");
            endAlignHorizontally();
        }
    }

    //set up the primer picking zone
    private IntegerOption verifPrimerDistMinOption;
    private IntegerOption verifPrimerDistMaxOption;
    private void manualInputPickingZoneOptions() {
        addDivider("Picking zone");
        beginAlignHorizontally("Distance Min:", false);
        verifPrimerDistMinOption = addIntegerOption("verifPrimerDistMin", "", 400, 0, 5000);
        verifPrimerDistMaxOption = addIntegerOption("verifPrimerDistMax", "Max:", 600, 0, 5000);
        endAlignHorizontally();
        addCustomComponent(actionProximityWarning);
    }

    //tweak verification primers parameters
    private IntegerOption verifPrimerDistOptimOption;
    private IntegerOption verifPrimerLengthMinOption;
    private IntegerOption verifPrimerLengthOptimOption;
    private IntegerOption verifPrimerLengthMaxOption;
    private IntegerOption verifPrimerTmMinOption;
    private IntegerOption verifPrimerTmOptimOption;
    private IntegerOption verifPrimerTmMaxOption;
    private IntegerOption verifPrimerGCMinOption;
    private IntegerOption verifPrimerGCOptimOption;
    private IntegerOption verifPrimerGCMaxOption;
    private void secondRoundVerifPrimersOptions(FileSelectionOption priorCodeLocation) {
        addDivider("Parameters");
        beginAlignHorizontally("Distance Min:", false);
        verifPrimerDistMinOption = addIntegerOption("verifPrimerDistMin", "", 400, 0, 5000);
        verifPrimerDistOptimOption = addIntegerOption("verifPrimerDistOptim", "Optimal:", 500, 0, 5000);
        verifPrimerDistMaxOption = addIntegerOption("verifPrimerDistMax", "Max:", 600, 0, 5000);
        endAlignHorizontally();
        addCustomComponent(actionProximityWarning);
        addDivider("");
        beginAlignHorizontally("Length Min:", false);
        verifPrimerLengthMinOption = addIntegerOption("verifPrimerLengthMin", "", 18, 0, 100);
        verifPrimerLengthOptimOption = addIntegerOption("verifPrimerLengthOptim", "Optimal:", 20, 0, 100);
        verifPrimerLengthMaxOption = addIntegerOption("verifPrimerLengthMax", "Max:", 27, 0, 100);
        endAlignHorizontally();
        beginAlignHorizontally("Tm Min:", false);
        verifPrimerTmMinOption = addIntegerOption("verifPrimerTmMin", "", 57, 0, 100);
        verifPrimerTmOptimOption = addIntegerOption("verifPrimerTmOptim", "Optimal:", 60, 0, 100);
        verifPrimerTmMaxOption = addIntegerOption("verifPrimerTmMax", "Max:", 63, 0, 100);
        endAlignHorizontally();
        beginAlignHorizontally("%GC Min:", false);
        verifPrimerGCMinOption = addIntegerOption("verifPrimerGCMin", "", 20, 0, 100);
        verifPrimerGCOptimOption = addIntegerOption("verifPrimerGCOptim", "Optimal:", 50, 0, 100);
        verifPrimerGCMaxOption = addIntegerOption("verifPrimerGCMax", "Max:", 80, 0, 100);
        endAlignHorizontally();
        // set text to display on hover
        verifPrimerDistMinOption.setDescription(
                "Specify at what minimal distance to place the verification primers relative to the amplification primers");
        verifPrimerDistOptimOption.setDescription(
                "Specify at what optimal distance to place the verification primers relative to the amplification primers");
        verifPrimerDistMaxOption.setDescription(
                "Specify at what maximal distance to place the verification primers relative to the amplification primers");
        verifPrimerLengthMinOption.setDescription("Specify minimal primer length");
        verifPrimerLengthOptimOption.setDescription("Specify optimal primer length");
        verifPrimerLengthMaxOption.setDescription("Specify maximal primer length");
        verifPrimerTmMinOption.setDescription("Specify minimal melting temperature");
        verifPrimerTmOptimOption.setDescription("Specify optimal melting temperature");
        verifPrimerTmMaxOption.setDescription("Specify maximal melting temperature");
        verifPrimerGCMinOption.setDescription("Specify minimal GC percentage");
        verifPrimerGCOptimOption.setDescription("Specify optimal GC percentage");
        verifPrimerGCMaxOption.setDescription("Specify maximal GC percentage");
        //make these available
        codeLocation = priorCodeLocation;
    }

    //primer generation/options details
    public class DoOverOptions {

        protected Integer distMin;
        protected Integer distOptim;
        protected Integer distMax;
        protected Integer lengthMin;
        protected Integer lengthOptim;
        protected Integer lengthMax;
        protected Integer tmMin;
        protected Integer tmOptim;
        protected Integer tmMax;
        protected Integer gcMin;
        protected Integer gcOptim;
        protected Integer gcMax;

        public DoOverOptions() {

            baseID = baseID;

            offsetStart = offsetStart;
            offsetStop = offsetStop;

            distMin = verifPrimerDistMinOption.getValue();
            distOptim = verifPrimerDistOptimOption.getValue();
            distMax = verifPrimerDistMaxOption.getValue();
            lengthMin = verifPrimerLengthMinOption.getValue();
            lengthOptim = verifPrimerLengthOptimOption.getValue();
            lengthMax = verifPrimerLengthMaxOption.getValue();
            tmMin = verifPrimerTmMinOption.getValue();
            tmOptim = verifPrimerTmOptimOption.getValue();
            tmMax = verifPrimerTmMaxOption.getValue();
            gcMin = verifPrimerGCMinOption.getValue();
            gcOptim = verifPrimerGCOptimOption.getValue();
            gcMax = verifPrimerGCMaxOption.getValue();
        }

        public ArrayList<ArrayList> makeVerifPair(
                SequenceAnnotation annotFeature, Integer index, String type, String seqString,
                CompositeProgressListener progressListener, SequenceDocument seqDoc) {

            ArrayList<ArrayList> resultsPairPlusExplain = new ArrayList<ArrayList>();

            String direction = annotFeature.getIntervals().get(0).getDirection().toString();

            Integer featStart;
            Integer featStop;

            Integer fwdLeftBound;
            Integer fwdRightBound;
            Integer revLeftBound;
            Integer revRightBound;

            Integer actualLeftBound;
            Integer actualRightBound;
            Integer actualLeftInner;
            Integer includedRegionSize;
            Integer targetRegionSize;

            //calculate coordinate points
            if (direction.equals("leftToRight")) {
                //define feature start & stop
                featStart = annotFeature.getIntervals().get(0).getFrom();
                featStop = featStart+annotFeature.getIntervals().get(0).getLength();
                //forward primer boundaries
                fwdLeftBound = featStart+offsetStart-distMax;
                if (fwdLeftBound < 1) {
                    fwdLeftBound = 1;
                }
                fwdRightBound = featStart+offsetStart-distMin;
                if (fwdRightBound < 2) {
                    fwdRightBound = 2;
                }
                //reverse primer boundaries
                revLeftBound = featStop+offsetStop+distMin;
                if (revLeftBound > seqDoc.getSequenceString().length()-2) {
                    revLeftBound = seqDoc.getSequenceString().length()-2;
                }
                revRightBound = featStop+offsetStop+distMax;
                if (revRightBound > seqDoc.getSequenceString().length()-1) {
                    revRightBound = seqDoc.getSequenceString().length()-1;
                }
                //establish overall regions
                actualLeftBound = fwdLeftBound;
                actualRightBound = revRightBound;
                actualLeftInner = fwdRightBound;
                includedRegionSize = actualRightBound-actualLeftBound;
                targetRegionSize = revLeftBound-fwdRightBound;

            } else { //if (direction.equals("rightToLeft")) {    //TODO: handle "no direction" case
                //define feature start & stop
                featStart = annotFeature.getIntervals().get(0).getFrom();
                featStop = featStart-annotFeature.getIntervals().get(0).getLength();
                //forward primer boundaries
                fwdLeftBound = featStart-offsetStart+distMin;
                if (fwdLeftBound > seqDoc.getSequenceString().length()-2) {
                    fwdLeftBound = seqDoc.getSequenceString().length()-2;
                }
                fwdRightBound = featStart-offsetStart+distMax;
                if (fwdRightBound > seqDoc.getSequenceString().length()-1) {
                    fwdRightBound = seqDoc.getSequenceString().length()-1;
                }
                //reverse primer boundaries
                revLeftBound = featStop-offsetStop-distMax;
                if (revLeftBound < 1) {
                    revLeftBound = 1;
                }
                revRightBound = featStop-offsetStop-distMin;
                if (revRightBound < 2) {
                    revRightBound = 2;
                }
                //establish overall regions
                actualLeftBound = revLeftBound;
                actualRightBound = fwdRightBound;
                actualLeftInner = revRightBound;
                includedRegionSize = actualRightBound-actualLeftBound;
                targetRegionSize = fwdLeftBound-revRightBound;
            }

            //temp file
            File tempFile = null;

            //assemble Boulder-IO record for Primer3
            String boulderString =
                    "SEQUENCE_ID="+baseID+"_"+annotFeature.getName().replace(" ", ".")+"_"+type+index.toString()+"\n"
                            +"SEQUENCE_TEMPLATE="+seqString+"\n"
                            +"SEQUENCE_INCLUDED_REGION="+actualLeftBound.toString()+","+includedRegionSize.toString()+"\n"
                            +"SEQUENCE_TARGET="+actualLeftInner.toString()+","+targetRegionSize.toString()+"\n"
                            +"PRIMER_TASK=pick_detection_primers\n"
                            +"PRIMER_PICK_LEFT_PRIMER=1\n"
                            +"PRIMER_PICK_INTERNAL_OLIGO=0\n"
                            +"PRIMER_PICK_RIGHT_PRIMER=1\n"
                            +"PRIMER_MIN_SIZE="+lengthMin.toString()+"\n"
                            +"PRIMER_OPT_SIZE="+lengthOptim.toString()+"\n"
                            +"PRIMER_MAX_SIZE="+lengthMax.toString()+"\n"
                            +"PRIMER_MIN_GC="+gcMin.toString()+"\n"
                            +"PRIMER_OPT_GC_PERCENT="+gcOptim.toString()+"\n"
                            +"PRIMER_MAX_GC="+gcMax.toString()+"\n"
                            +"PRIMER_MIN_TM="+gcMin.toString()+"\n"
                            +"PRIMER_OPT_TM="+gcOptim.toString()+"\n"
                            +"PRIMER_MAX_TM="+gcMax.toString()+"\n"
                            +"PRIMER_PRODUCT_SIZE_RANGE="
                            +targetRegionSize.toString()+"-"+includedRegionSize.toString()+"\n"
                            +"PRIMER_EXPLAIN_FLAG=1\n"
                            +"=";
            //write record to temp file
            tempFile = novoPrimeLaunchers.writeTempFile(tempFile, boulderString);

            //command line call to Primer3
            String[] command = novoPrimeLaunchers.getCommand(tempFile.getAbsolutePath(), codeLocation);

            //set up primer pair and messages container
            ArrayList<String> primerExplainMessages;
            ArrayList<SequenceAnnotation> primerPair = new ArrayList<SequenceAnnotation>();

            try {
                // Need a listener on the output stream
                NovoPrimeExecutionOutputListener outputListener = new NovoPrimeExecutionOutputListener();
                // Build the Execution object
                Execution exec = new Execution(command, progressListener, outputListener, (String)null, false);
                // Run the command
                exec.execute();
                // If it returns having been killed there was a problem
                if (exec.wasKilledByGeneious()) {
                    return null;
                }
                //record primer3 explanation of results
                primerExplainMessages = outputListener.getExplainMsg();
                //evaluate success
                Integer numPairsReturned = outputListener.getNumPairsReturned();
                if (numPairsReturned.equals(null)) {
                    System.out.println("ERROR: Problem parsing output from Primer3");  //TODO: add exception handling
                } else if (numPairsReturned.equals(0)) {
                    primerPair.add(new SequenceAnnotation("dummy",
                            SequenceAnnotation.TYPE_PRIMER_BIND,
                            new SequenceAnnotationInterval(0,0)));
                    primerPair.add(new SequenceAnnotation("dummy",
                            SequenceAnnotation.TYPE_PRIMER_BIND,
                            new SequenceAnnotationInterval(0,0)));
                } else {
                    List<String> pairInfo = outputListener.getPrimerPairInfo();

                    String leftSeq = pairInfo.get(0);
                    String rightSeq = pairInfo.get(1);
                    String[] leftPos = Pattern.compile(",").split(pairInfo.get(2));
                    String[] rightPos = Pattern.compile(",").split(pairInfo.get(3));
                    Integer leftPosStart = Integer.parseInt(leftPos[0]);
                    Integer leftPosStop = leftPosStart+Integer.parseInt(leftPos[1]);
                    Integer rightPosStart = Integer.parseInt(rightPos[0]);
                    Integer rightPosStop = rightPosStart+Integer.parseInt(rightPos[1]);

                    Integer fwdStart = 0;
                    Integer fwdStop = 0;
                    Integer revStart = 0;
                    Integer revStop = 0;

                    //adapt coordinates to feature direction
                    if (direction.equals("leftToRight")) {
                        fwdStart = leftPosStart;
                        fwdStop = leftPosStop;
                        revStart = rightPosStop;
                        revStop = rightPosStart;
                    } else { //if (direction.equals("rightToLeft")) {    //TODO: handle "no direction" case
                        fwdStart = rightPosStop;
                        fwdStop = rightPosStart;
                        revStart = leftPosStart;
                        revStop = leftPosStop;
                    }

                    //forward primer
                    SequenceAnnotation fwdPrimer = new SequenceAnnotation(
                            primerBaseName+"T_fwd",
                            SequenceAnnotation.TYPE_PRIMER_BIND,
                            new SequenceAnnotationInterval(
                                    fwdStart, fwdStop)
                    );
                    primerPair.add(fwdPrimer);

                    //reverse primer
                    SequenceAnnotation revPrimer = new SequenceAnnotation(
                            primerBaseName+"T_rev",
                            SequenceAnnotation.TYPE_PRIMER_BIND,
                            new SequenceAnnotationInterval(
                                    revStart, revStop)
                    );
                    primerPair.add(revPrimer);
                }
                resultsPairPlusExplain.add(primerPair);
                resultsPairPlusExplain.add(primerExplainMessages);

            } catch (IOException e) {
                System.out.println("IOException");
                /*throw new DocumentOperationException("Something went wrong:" + e.getMessage(), e);*/
            } catch (InterruptedException e) {
                System.out.println("InterruptedException");
                /*throw new DocumentOperationException("Process Killed!", e);*/
            }
            return resultsPairPlusExplain;
        }

    }
    public DoOverOptions getPrimerOptions() {
        return new DoOverOptions();
    }

    public NovoPrimeLaunchers getNovoPrimeLaunchers() {
        return novoPrimeLaunchers;
    }

    //create panel
    protected JPanel createPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel defaultPanel = super.createPanel();
        mainPanel.add(defaultPanel, BorderLayout.CENTER);

        return mainPanel;
    }
}
