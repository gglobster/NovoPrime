package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.IconUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class NovoPrimeDoOverOptions extends Options {

    public NovoPrimeDoOverOptions(
            Integer featNum, ArrayList<String> explainMsg, Boolean allValidityCheck, Boolean amplifValidityCheck) {

        if (allValidityCheck) {
            everythingIsOK();
        } else {
            if (amplifValidityCheck){
                if (explainMsg.get(1).equals("Illegal value for SEQUENCE_INCLUDED_REGION")) {
                    outOFBounds(true);
                } else {
                    parametersTooStringent();
                    addDivider("");
                    diagnosticMessage(explainMsg);
                }
                offerSolutionsOptions();

            } else {
                if (explainMsg.get(1).equals("Illegal value for SEQUENCE_INCLUDED_REGION")) {
                    outOFBounds(false);
                } else if (explainMsg.get(1).equals("TARGET beyond end of sequence")) {
                    overlapUnsupported();
                } else {
                    unknownIssue();
                    addDivider("");
                    diagnosticMessage(explainMsg);
                }
            }
        }
    }

    SequenceDocument seqDoc;

    //offer solutions: do-over, manual input or give up
    private void offerSolutionsOptions() {
        //addCustomComponent(getFeatSubsetSelectionButton());
    }

    //do-over button
/*    public JComponent getFeatSubsetSelectionButton() {
        return new JButton(new AbstractAction("Run Primer3...") {
            public void actionPerformed(ActionEvent e) {
                maskList = novoPrimeLaunchers.launchFeatSubsetSelection(featList, maskList, actionLabel);
            }
        });
    }*/

    //no need to do anything
    private void everythingIsOK() {
        addLabelWithIcon("All primer design was successful.", IconUtilities.getIcons("tick16.png"));
        addLabel("<html>No further action is necessary for this feature/primer set.</html>");
    }

    //parameters too stringent
    private void parametersTooStringent() {
        addLabelWithIcon("Primer design failed for the verification pair.",
                IconUtilities.getIcons("warning16.png"));
        addLabel("<html>Parameters were too stringent (see diagnostic message below for details).<br>" +
                        "Try relaxing design constraints.</html>");
    }

    //not sure what went wrong
    private void unknownIssue() {
        addLabelWithIcon("All primer design failed.", IconUtilities.getIcons("error13.png"));
        addLabel("<html>Cause of failure is unrecognized (see diagnostic message below for details).<br>" +
                        "There is no remedial action available for this feature/primer set.</html>");
    }

    //feature overlaps the origin
    private void overlapUnsupported(){
        addLabelWithIcon("All primer design failed.", IconUtilities.getIcons("error13.png"));
        addLabel("<html>The annotation feature appears to overlap the origin.<br>" +
                        "NovoPrime does not support such a configuration at this time.<br>" +
                        "There is no remedial action available for this feature/primer set.</html>");
    }

    //out of bounds
    private void outOFBounds(Boolean issueIsSalvageable) {
        if (issueIsSalvageable) {
            addLabelWithIcon("Primer design failed for the verification pair.",
                    IconUtilities.getIcons("warning16.png"));
            addLabel("<html>The annotation feature is near the edge of the sequence.<br>" +
                            "Try reducing the Distance Min and Max of the primer picking zone.</html>");
        } else {
            addLabelWithIcon("All primer design failed.", IconUtilities.getIcons("error13.png"));
            addLabel("<html>The annotation feature is too close to the edge of the sequence.<br>" +
                            "There is no remedial action available for this feature/primer set.</html>");
        }
        
    }

    //display Primer3 diagnostics messages for info
    private void diagnosticMessage(ArrayList<String> messageList){
        for (String line:messageList) {
            if (line.startsWith("EXPLAIN_FLAG")) {
                continue;
            } else {
                addLabel(line);
            }
        }
    }

    //tweak verification primers parameters
    private IntegerOption verifPrimerDistMinOption;
    private IntegerOption verifPrimerDistOptimOption;
    private IntegerOption verifPrimerDistMaxOption;
    private IntegerOption verifPrimerLengthMinOption;
    private IntegerOption verifPrimerLengthOptimOption;
    private IntegerOption verifPrimerLengthMaxOption;
    private IntegerOption verifPrimerTmMinOption;
    private IntegerOption verifPrimerTmOptimOption;
    private IntegerOption verifPrimerTmMaxOption;
    private IntegerOption verifPrimerGCMinOption;
    private IntegerOption verifPrimerGCOptimOption;
    private IntegerOption verifPrimerGCMaxOption;
    private void secondRoundVerifPrimersOptions() {
        addDivider("Parameters");
        beginAlignHorizontally("Distance Min:", false);
        verifPrimerDistMinOption = addIntegerOption("verifPrimerDistMin", "", 400, 0, 5000);
        verifPrimerDistOptimOption = addIntegerOption("verifPrimerDistOptim", "Optimal:", 500, 0, 5000);
        verifPrimerDistMaxOption = addIntegerOption("verifPrimerDistMax", "Max:", 600, 0, 5000);
        endAlignHorizontally();
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

        public DoOverOptions(String baseID) {

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

    }

    //create panel
    protected JPanel createPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel defaultPanel = super.createPanel();
        mainPanel.add(defaultPanel, BorderLayout.CENTER);

        return mainPanel;
    }
}
