package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.FileUtilities;
import com.biomatters.geneious.publicapi.utilities.IconUtilities;
import jebl.util.CompositeProgressListener;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class NovoPrimeLaunchers {

    public NovoPrimeLaunchers() {

    }

    //get values of a given column from a table
    private List<Object> getColumnValues(JTable table, int col){
        List<Object> selectionValues = new ArrayList<Object>();
        int counter = 0;
        while (counter < table.getRowCount()) {
            selectionValues.add(table.getValueAt(counter, col));
            counter +=1;
        }
        return selectionValues;
    }

    //return the command line options for Primer3
    String[] getCommand(String tempFile, Options.FileSelectionOption codeLocation) {
        ArrayList<String> commandList = new ArrayList<String>();
        commandList.add(codeLocation.getValue());
        commandList.add(tempFile);
        //this will be the command used by execute
        return commandList.toArray(new String[commandList.size()]);
    }

    List<ArrayList<String>> explainMSGs;
    List<SequenceAnnotation> annotList;
    List<SequenceAnnotation> featList;
    SequenceDocument seqDoc;
    Options.FileSelectionOption codeLocation;
    String baseID;
    Integer offsetStart;
    Integer offsetStop;
    CompositeProgressListener progress;
    JTable summaryTable;

    List<Boolean> allValidityCheckList = new ArrayList<Boolean>();
    List<Boolean> amplifValidityCheckList = new ArrayList<Boolean>();
    
    // write a temp file
    public File writeTempFile(File tempFile, String textContent)
    /*throws DocumentOperationException*/ {
        //create temporary Boulder-IO file
        try {
            tempFile = FileUtilities.createTempFile("temp_novoprime", ".txt", true);
            // Delete temp file when program exits.
            tempFile.deleteOnExit();
            // write out
            BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
            out.write(textContent);
            out.close();
        } catch (IOException e) {    //TODO: fix unhandled exception issue
            System.out.println("IOException?");
            /*throw new DocumentOperationException(
                    "Failed to write temp file:" + e.getMessage(), e); */
        }
        return tempFile;
    }

    //count how many features are selected
    public int countSelected(List<Object> maskList) {
        int counter = 0;
        for (Object item:maskList) {
            if ((Boolean) item)
                counter +=1;
        }
        return counter;
    }

    //retrieve feature subset list
    public List<SequenceAnnotation> getSelectFeatList(List<SequenceAnnotation> featList, List<Object> maskList) {
        List<SequenceAnnotation> selectFeats = new ArrayList<SequenceAnnotation>();
        int counter = 0;
        for (SequenceAnnotation oneAnnot:featList) {
            if ((Boolean) maskList.get(counter)) {
                selectFeats.add(oneAnnot);
            }
            counter +=1;
        }
        return selectFeats;
    }

    //launch second panel (feature subset selection)
    public List<Object> launchFeatSubsetSelection(
            List<SequenceAnnotation> featList, List<Object> maskList, JLabel actionLabel) {

        final java.util.List<String> columnNames = new ArrayList<String>();
        columnNames.add("Select");
        columnNames.add("Locus Tag");
        columnNames.add("Type");
        columnNames.add("Location");

        final Vector<Vector<Object>> myDataObjects = new Vector<Vector<Object>>();
        int counter = 0;
        for (SequenceAnnotation oneAnnot:featList) {
            Vector<Object> row = new Vector<Object>();
            row.add(maskList.get(counter));
            row.add(oneAnnot.getName());
            row.add(oneAnnot.getType());
            row.add(oneAnnot.getIntervals());
            myDataObjects.add(row);
            counter +=1;
        }

        JTable table = new JTable(new AbstractTableModel() {

            public int getRowCount() {
                return myDataObjects.size();
            }
            public int getColumnCount() {
                return columnNames.size();
            }
            public Object getValueAt(int row, int column){
                return myDataObjects.get(row).get(column);
            }
            public String getColumnName(int col) {
                return columnNames.get(col);
            }
            //checkbox rendering enabler
            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }
            //specifying which columns are editable
            public boolean isCellEditable(int row, int col) {
                if (col > 0) {       //ignore warning because this is the logic we want
                    return false;    //in case we decide to make another column editable
                } else {
                    return true;
                }
            }
            //actualizing changes
            public void setValueAt(Object value, int row, int col) {
                Vector<Object> tempRow = myDataObjects.get(row);
                tempRow.setElementAt(value, col);
                myDataObjects.set(row, tempRow);
                fireTableCellUpdated(row, col);
            }
        });

        table.setPreferredScrollableViewportSize(new Dimension(800, 400));
        table.setFillsViewportHeight(true);
        //create scroll pane for the table
        JScrollPane selectionPane = new JScrollPane(table);
        Object[] options = {"Cancel", "Confirm selection"};
        int n = JOptionPane.showOptionDialog(null, selectionPane, "Select Annotation Features",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n > 0) {
            //use last state of selection to modify maskList
            maskList.clear();
            maskList = getColumnValues(table, 0);
            Integer selectFeatNum = countSelected(maskList);
            actionLabel.setText(selectFeatNum+" selected (of "+featList.size()+")");
        }
        return maskList;
    }

    //launch third panel (summary)
    public List<SequenceAnnotation> launchFirstRoundSummary(
            SequenceDocument document,
            List<SequenceAnnotation> selectFeatList,
            List<SequenceAnnotation> primersList,
            List<ArrayList<String>> verifExplainMsgs,
            Options.FileSelectionOption priorCodeLocation,
            String priorBaseID,
            Integer priorOffsetStart,
            Integer priorOffsetStop,
            CompositeProgressListener progressListener) {

        List<SequenceAnnotation> finalList = new ArrayList<SequenceAnnotation>();
        
        seqDoc = document;
        explainMSGs = verifExplainMsgs;
        annotList = primersList;
        featList = selectFeatList;
        codeLocation = priorCodeLocation;
        baseID = priorBaseID;
        offsetStart = priorOffsetStart;
        offsetStop = priorOffsetStop;
        progress = progressListener;


        final List<String> columnNames = new ArrayList<String>();
        columnNames.add("Status");
        columnNames.add("Locus Tag");
        columnNames.add("Type");
        columnNames.add("Location");
        columnNames.add("Amplif");
        columnNames.add("Verif");
        columnNames.add("Options");

        final Vector<Vector<Object>> myDataObjects = new Vector<Vector<Object>>();
        int countP = 0;
        for (SequenceAnnotation oneAnnot:selectFeatList) {

            Boolean validatePrimerSet = true;
            Boolean validatePairA = true;
            Boolean validatePairV = true;

            if (primersList.get(countP).getName().equals("dummy")) {
                validatePrimerSet = false;
                validatePairA = false;
            }
            if (primersList.get(countP+1).getName().equals("dummy")) {
                validatePrimerSet = false;
                validatePairA = false;
            }
            if (primersList.get(countP+2).getName().equals("dummy")) {
                validatePrimerSet = false;
                validatePairV = false;
            }
            if (primersList.get(countP+3).getName().equals("dummy")) {
                validatePrimerSet = false;
                validatePairV = false;
            }

            amplifValidityCheckList.add(validatePairA);
            allValidityCheckList.add(validatePrimerSet);

            Vector<Object> row = new Vector<Object>();
            row.add(boolToIcon(validatePrimerSet, "annot"));
            row.add(oneAnnot.getName());
            row.add(oneAnnot.getType());
            row.add(oneAnnot.getIntervals());
            row.add(boolToIcon(validatePairA, "pair"));
            row.add(boolToIcon(validatePairV, "pair"));
            row.add(countP);
            myDataObjects.add(row);
            countP +=4;
        }


        summaryTable = new JTable(new AbstractTableModel() {

            public int getRowCount() {
                return myDataObjects.size();
            }
            public int getColumnCount() {
                return columnNames.size();
            }
            public Object getValueAt(int row, int column){
                return myDataObjects.get(row).get(column);
            }
            public String getColumnName(int col) {
                return columnNames.get(col);
            }
            //rendering enabler
            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }
            //specifying which columns are editable
            public boolean isCellEditable(int row, int col) {
                if (col == 6) {
                    return true;
                } else {
                    return false;
                }
            }
            //actualizing changes
            public void setValueAt(Object value, int row, int col) {
                Vector<Object> tempRow = myDataObjects.get(row);
                tempRow.setElementAt(value, col);
                myDataObjects.set(row, tempRow);
                fireTableCellUpdated(row, col);
            }
        });
        summaryTable.getColumn("Options").setCellRenderer(new ButtonRenderer());
        summaryTable.getColumn("Options").setCellEditor(
                new ButtonEditor(new JCheckBox()));
        summaryTable.setPreferredScrollableViewportSize(new Dimension(800, 400));
        summaryTable.setFillsViewportHeight(true);
        //create scroll pane for the table
        JScrollPane summaryPane = new JScrollPane(summaryTable);
        Object[] options = {"Save Primers", "Cancel"};
        int n = JOptionPane.showOptionDialog(null, summaryPane, "Primer Design Results Summary",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == 0) {
            Integer counter = 0;
            while (counter < primersList.size()) {
                if (allValidityCheckList.get(counter/4)) {
                    finalList.add(annotList.get(counter));
                    finalList.add(annotList.get(counter+1));
                    finalList.add(annotList.get(counter+2));
                    finalList.add(annotList.get(counter+3));
                }
                counter +=4;
            }
            return finalList;
        } else {
            return new ArrayList<SequenceAnnotation>();
        }
    }

    //launch individual info panels (second round, individual)
    public Integer launchStatusDialog(Integer counter) {
        Integer featNum = (counter/4)+1;
        if (allValidityCheckList.get(featNum-1)) {
            everythingIsOK(featList.get(featNum-1));
            return 0;
        } else {
            if (amplifValidityCheckList.get(featNum-1)){
                if (explainMSGs.get(featNum-1).get(1).equals("Illegal value for SEQUENCE_INCLUDED_REGION")) {
                    return outOFBounds(true, featNum, featList.get(featNum-1));
                } else {
                    return parametersTooStringent(featNum, featList.get(featNum-1));
                }
            } else {
                if (explainMSGs.get(featNum-1).get(1).equals("Illegal value for SEQUENCE_INCLUDED_REGION")) {
                    outOFBounds(false, featNum, featList.get(featNum-1));
                    return 0;
                } else if (explainMSGs.get(featNum-1).get(1).equals("TARGET beyond end of sequence")) {
                    overlapUnsupported(featList.get(featNum-1));
                    return 0;
                } else {
                    unknownIssue(featList.get(featNum-1));
                    return 0;
                }
            }
        }
    }
    
    //everything is OK
    private void everythingIsOK(SequenceAnnotation annotFeat) {
        String infoMessage = "\nAll primer design was successful.\n" +
                "No further action is necessary for this feature/primer set.";
        Icon statusIcon = IconUtilities.getIcons("tick16.png").getOriginalIcon();
        JOptionPane.showMessageDialog(
                null, infoMessage, annotFeat.getName(), JOptionPane.INFORMATION_MESSAGE, statusIcon);
    }
    //not sure what went wrong
    private void unknownIssue(SequenceAnnotation annotFeat) {
        String infoMessage = "\nAll primer design failed.\n" +
                "Cause of failure is unrecognized.\n" +
                "There is no remedial action available for this feature/primer set.";
        JOptionPane.showMessageDialog(
                null, infoMessage, annotFeat.getName(), JOptionPane.ERROR_MESSAGE);
    }
    //feature overlaps the origin
    private void overlapUnsupported(SequenceAnnotation annotFeat){
        String infoMessage = "\nAll primer design failed.\n" +
                "The annotation feature appears to overlap the origin.\n" +
                "NovoPrime does not support such a configuration at this time.\n" +
                "There is no remedial action available for this feature/primer set.";
        JOptionPane.showMessageDialog(
                null, infoMessage, annotFeat.getName(), JOptionPane.ERROR_MESSAGE);
    }
    //out of bounds
    private Integer outOFBounds(Boolean issueIsSalvageable, Integer featNum, SequenceAnnotation annotFeat) {
        if (issueIsSalvageable) {
            String infoMessage = "\nPrimer design failed for the verification pair.\n" +
                    "The annotation feature  appears to be near the edge of the sequence.\n\n" +
                    "Options:\n" +
                    "- Run Primer3 again (try reducing the Distance Min and Max)\n" +
                    "- Input sequences manually";
            return launchSecondRound(infoMessage, annotFeat, featNum);
        } else {
            String infoMessage = "\nAll primer design failed.\n" +
                    "The annotation feature is too close to the edge of the sequence.\n" +
                    "There is no remedial action available for this feature/primer set.\n";
            JOptionPane.showMessageDialog(
                    null, infoMessage, annotFeat.getName(), JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }
    //parameters too stringent
    private Integer parametersTooStringent(Integer featNum, SequenceAnnotation annotFeat) {
        String infoMessage = "\nPrimer design failed for the verification pair.\n" +
                "Parameters were too stringent.\n\n" +
                "Options:\n" +
                "- Run Primer3 again (try relaxing design constraints)\n" +
                "- Input sequences manually";
        return launchSecondRound(infoMessage, annotFeat, featNum);
    }
    //second round for realz
    private Integer launchSecondRound(String infoMessage, SequenceAnnotation annotFeat, Integer featNum) {
        Object[] options = {"Cancel", "Manual Input", "Run Primer3"};
        int n = JOptionPane.showOptionDialog(null, infoMessage, annotFeat.getName(),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (n == 0) {
            //nothing happens
            return 0;
        } else if (n == 1) {
            //offer manual design interface (how???)
            NovoPrimeDoOverOptions novoPrimeDoOverOptions = new NovoPrimeDoOverOptions(seqDoc, featNum,
                    explainMSGs.get(featNum-1), annotFeat, codeLocation, baseID, offsetStart, offsetStop, "manual");
            JPanel manualPanel = novoPrimeDoOverOptions.createPanel();
            Object[] npOptions = {"Save", "Cancel"};
            int np = JOptionPane.showOptionDialog(null, manualPanel, "Manual Primer Design",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, npOptions, npOptions[0]);
            if (np == 0) {
                //OK -- retrieve and validate results
                SequenceAnnotation fwdPrimer = novoPrimeDoOverOptions.fwdPrimerAnnot;
                SequenceAnnotation revPrimer = novoPrimeDoOverOptions.revPrimerAnnot;
                ArrayList<String> manualExplainMsg = new ArrayList<String>();
                manualExplainMsg.add("Manual primer design completed successfully.");
                if (fwdPrimer != null) {
                    if (revPrimer != null) {
                        annotList.set(((featNum-1)*4)+2, fwdPrimer);
                        annotList.set(((featNum-1)*4)+3, revPrimer);
                        explainMSGs.set(featNum - 1, manualExplainMsg);
                        allValidityCheckList.set(featNum-1, true);
                        summaryTable.setValueAt(boolToIcon(true, "annot"), featNum-1, 0);
                        summaryTable.setValueAt(boolToIcon(true, "pair"), featNum - 1, 5);
                        return 1;
                    } else {
                        JOptionPane.showMessageDialog(
                                null, "No REV primer was specified.","Error",
                                JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null, "No FWD primer was specified.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return -1;
                }
            } else {
                return 0;
            }
        } else if (n == 2) {
            NovoPrimeDoOverOptions novoPrimeDoOverOptions = new NovoPrimeDoOverOptions(seqDoc, featNum,
                    explainMSGs.get(featNum-1), annotFeat, codeLocation, baseID, offsetStart, offsetStop, "primer3");
            JPanel npPanel = novoPrimeDoOverOptions.createPanel();
            Object[] npOptions = {"Run", "Cancel"};
            int np = JOptionPane.showOptionDialog(null, npPanel, "Primer3 Design Parameters",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, npOptions, npOptions[0]);
            if (np == 0) {
                //OK -- run command and retrieve results
                NovoPrimeDoOverOptions.DoOverOptions doOverOptions = novoPrimeDoOverOptions.getPrimerOptions();
                ArrayList<ArrayList> verifResults = doOverOptions.makeVerifPair(
                        annotFeat, featNum, "V", seqDoc.getSequenceString(), progress, seqDoc);
                List<SequenceAnnotation> verifPrimerPair = verifResults.get(0);
                ArrayList<String> verifExplainMsg = verifResults.get(1);
                if (verifPrimerPair.get(0).getName().equals("dummy")) {
                    //replace the error msg in the old list with the new one and return user to launchStatusDialog
                    explainMSGs.set(featNum-1, verifExplainMsg);
                    return -1;
                } else {
                    //replace the status msg and primer details in the old list and edit the table checkboxes
                    annotList.set(((featNum-1)*4)+2, verifPrimerPair.get(0));
                    annotList.set(((featNum-1)*4)+3, verifPrimerPair.get(1));
                    explainMSGs.set(featNum - 1, verifExplainMsg);
                    allValidityCheckList.set(featNum-1, true);
                    summaryTable.setValueAt(boolToIcon(true, "annot"), featNum-1, 0);
                    summaryTable.setValueAt(boolToIcon(true, "pair"), featNum-1, 5);
                    return 1;
                }
            } else {
                //nothing happens
                return 0;
            }
        } else {
            return 0;
        }
    }

    private Icon boolToIcon(Boolean value, String colType) {
        Icon iconValue;
        if (colType.equals("annot")) {
            if (value) {
                iconValue = IconUtilities.getIcons("addAnnotation16.png").getOriginalIcon();
            } else {
                iconValue = IconUtilities.getIcons("annotatePredict16.png").getOriginalIcon();
            }
        } else {
            if (value) {
                iconValue = IconUtilities.getIcons("tick16.png").getOriginalIcon();
            } else {
                iconValue = IconUtilities.getIcons("x16.png").getOriginalIcon();
            }
        }
        return iconValue;
    }


    //classes for summary/second round interaction

    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText("more...");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;

        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText("more...");
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                Integer counter = Integer.parseInt(label);
                Integer looper = -1;
                while (looper != 0) {
                    looper = launchStatusDialog(counter);
                } //TODO: update table checkboxes
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

}
