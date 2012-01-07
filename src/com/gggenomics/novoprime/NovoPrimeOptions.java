package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.components.GButton;
import com.biomatters.geneious.publicapi.components.GEditorPane;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator;
import com.biomatters.geneious.publicapi.utilities.IconUtilities;
import com.sun.xml.internal.bind.v2.TODO;
import org.virion.jam.html.SimpleLinkListener;
import org.virion.jam.util.SimpleListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.List;

public class NovoPrimeOptions extends Options {

    public NovoPrimeOptions(AnnotatedPluginDocument[] mydocs) {

        codeLocationOptions();
        featSelectOptions(mydocs);
        amplifPrimersOptions();
        verifPrimersOptions();

        featTypeOption.addChangeListener(new SimpleListener() {
            public void objectChanged() {
                System.out.println(featTypeOption.getValue().getName());
            }
        });

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

    }

    // Option blocks

    private FileSelectionOption codeLocation;
    private void codeLocationOptions() {
        beginAlignHorizontally(null, false);
        codeLocation = addFileSelectionOption("EXE", "The Primer3 executable:", "");
        codeLocation.setRestoreDefaultApplies(false);
        addCustomComponent(getHelpButtonForLocation());
        endAlignHorizontally();
        addDivider(""); // Add some empty space to separate this option a bit
    }

    private ComboBoxOption<OptionValue> featTypeOption;
    private static final String ORF = "ORF";
    private static final String CDS = "CDS";
    private static final String GENE = "gene";
    private static final String OTHER = "other";
    private void featSelectOptions(AnnotatedPluginDocument[] mydocs) {
        addDivider("Target Selection");
        Options.OptionValue[] featTypeComboBoxList = {
            new Options.OptionValue(CDS, "CDS"), // Default
            new Options.OptionValue(ORF, "ORF"),
            new Options.OptionValue(GENE, "gene"),
            new Options.OptionValue(OTHER, "other")};
        beginAlignHorizontally("Feature Type:", false);
        featTypeOption = addComboBoxOption("featTypes", "",
                Arrays.asList(featTypeComboBoxList), featTypeComboBoxList[0]);
        addCustomComponent(getFeatSubsetSelection());
        endAlignHorizontally();
        // set text to display on hover
        featTypeOption.setDescription("Select which feature type to process: CDS, ORF, gene or other");
        }



    private IntegerOption amplifPrimerLengthOption;
    private IntegerOption amplifPrimerPosStartOption;
    private IntegerOption amplifPrimerPosStopOption;
    private StringOption amplifPrimerFwdTailOption;
    private StringOption amplifPrimerRevTailOption;
    private void amplifPrimersOptions() {
        addDivider("Amplification Primers");
        amplifPrimerLengthOption = addIntegerOption("amplifPrimerLength", "Length:", 25, 0, 100);
        amplifPrimerFwdTailOption = addStringOption("amplifPrimerFwdTail", "Fwd Tail:", "ATTAT TEST STRING");
        amplifPrimerRevTailOption = addStringOption("amplifPrimerRevTail", "Rev Tail:", "CCGCG TEST STRING");
        beginAlignHorizontally("Distance From Start:", false);
        amplifPrimerPosStartOption = addIntegerOption("amplifPrimerPosStart", "", 0, -1000, 1000);
        amplifPrimerPosStopOption = addIntegerOption("amplifPrimerPosStop", "From Stop:", 0, -1000, 1000);
        endAlignHorizontally();
        // set text to display on hover
        amplifPrimerLengthOption.setDescription("Specify primer length");
        amplifPrimerPosStartOption.setDescription(
                "Specify at what position to anchor the 3' end of the primer relative to the start position of the feature");
        amplifPrimerPosStopOption.setDescription(
                "Specify at what position to anchor the 5' end of the primer relative to the stop position of the feature");
        amplifPrimerFwdTailOption.setDescription("Specify nucleotide sequence to attach as tail to the forward primer");
        amplifPrimerRevTailOption.setDescription("Specify nucleotide sequence to attach as tail to the reverse primer");
    }

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
    private void verifPrimersOptions() {
        addDivider("Verification Primers");
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

    // public methods to access option values

    /*public String getFeatType() {
        return featTypeOption.getValue().getName();
    }

    // New idea
    public List<SequenceAnnotation> getSelectFeatures(SequenceDocument seqDoc,
                                                      NovoPrimeOptions novoprimeOptions) {
        List<SequenceAnnotation> allAnnotations = seqDoc.getSequenceAnnotations();
        String featType = novoprimeOptions.getFeatType();
        if (featType == "other") {
            System.out.println("taking all comers");   // TODO: make this pass the entire list
        } else {
            for (SequenceAnnotation oneAnnot:allAnnotations) {
                if (oneAnnot.getType() == featType) {
                    System.out.println(featType);   // TODO: make this add the feature to the selection list
                }
            }
        }
        return allAnnotations;
    }

        String getSelectOption() {
        return featTypeOption.getValue().getName();
    }
*/

    // Get subset selection via popup table

    private String selectFeatSubset() {
        return "This dialog offers the possibility to (de)select a subset of feature annotations.";
}
    private JComponent getFeatSubsetSelection() {
        return new JButton(new AbstractAction("Select subset...") {
            public void actionPerformed(ActionEvent e) {
                launchFeatSubsetSelection();
            }
        });
    }
    private void launchFeatSubsetSelection() {
        Dialogs.showMessageDialog(selectFeatSubset(), "Select subset of annotation features");
    }


    private final String PRIMER3_URL = "http://primer3.sourceforge.net/";

    // Provide additional information
    public String verifyOptionsAreValid() {
        String executable = codeLocation.getValue();
        if (executable.trim().length() == 0) {
            return "Primer3 executable location not set.<br><br>" + getExecutableLocationHelp();
        }
        else return null;
    }
    private String getExecutableLocationHelp() {
        return "This plugin makes use of the external program Primer3 by Steve Rozen and Helen Skaletsky to design " +
                "primers.<br><br>The Primer3 package can be downloaded from <a href=\"" + PRIMER3_URL + "\">" +
                PRIMER3_URL + "</a>.<br><br>After downloading and extracting the package, point Geneious to the " +
                "location of the executable called primer3_core.";
    }
    private JComponent getHelpButtonForLocation() {
        return new JButton(new AbstractAction("", IconUtilities.getIcons("help16.png").getIcon16()) {
            public void actionPerformed(ActionEvent e) {
                showHelpForPrimer3Location();
            }
        });
    }
    private void showHelpForPrimer3Location() {
        Dialogs.showMessageDialog(getExecutableLocationHelp(), "Where to get Primer3");
    }

    // Create a custom citation
    protected JPanel createPanel() {
        addDivider("Credits");
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel defaultPanel = super.createPanel();
        mainPanel.add(defaultPanel, BorderLayout.CENTER);
        GEditorPane citationPane = new GEditorPane();
        citationPane.setEditable(false);
        citationPane.setOpaque(false);
        citationPane.setContentType("text/html");
        citationPane.addHyperlinkListener(new SimpleLinkListener());
        citationPane.setText("<html><i><center>NovoPrime - a plugin by Geraldine A. Van der Auwera for " +
                "Novophage, Inc.<br><a href=\"" + PRIMER3_URL + "\">Primer3</a> - a primer design tool by " +
                "Steve Rozen and Helen Skaletsky<br>");
        mainPanel.add(citationPane, BorderLayout.SOUTH);

        return mainPanel;
    }

}