package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.FileUtilities;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
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
    SequenceDocument seqDoc;

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
            List<ArrayList<String>> verifExplainMsgs) {

        List<SequenceAnnotation> finalList = new ArrayList<SequenceAnnotation>();
        
        seqDoc = document;
        explainMSGs = verifExplainMsgs;
        annotList = primersList;

        final List<String> columnNames = new ArrayList<String>();
        columnNames.add("Select");
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
            row.add(validatePrimerSet);
            row.add(oneAnnot.getName());
            row.add(oneAnnot.getType());
            row.add(oneAnnot.getIntervals());
            row.add(validatePairA);
            row.add(validatePairV);   //TODO: after adding redo feature for V primers, will need to update checkbox
            row.add(countP);
            myDataObjects.add(row);
            countP +=4;
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
                if (col == 0) {
                    return true;
                } else if (col == 6) {
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

        table.getColumn("Options").setCellRenderer(new ButtonRenderer());
        table.getColumn("Options").setCellEditor(
                new ButtonEditor(new JCheckBox()));
        table.setPreferredScrollableViewportSize(new Dimension(800, 400));
        table.setFillsViewportHeight(true);
        //create scroll pane for the table
        JScrollPane summaryPane = new JScrollPane(table);
        Object[] options = {"Cancel", "Confirm selection"};
        int n = JOptionPane.showOptionDialog(null, summaryPane, "Primer Design Results Summary",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n > 0) {  //TODO: output to file
            Integer counter = 0;
            while (counter < primersList.size()) {
                Boolean flag = true;
                if (primersList.get(counter).getName().equals("dummy")) {
                    flag = false;
                }
                if (primersList.get(counter+1).getName().equals("dummy")) {
                    flag = false;
                }
                if (primersList.get(counter+2).getName().equals("dummy")) {
                    flag = false;
                }
                if (primersList.get(counter+3).getName().equals("dummy")) {
                    flag = false;
                }
                if (flag) {
                    finalList.add(primersList.get(counter));
                    finalList.add(primersList.get(counter+1));
                    finalList.add(primersList.get(counter+2));
                    finalList.add(primersList.get(counter+3));
                }
                counter +=4;
            }
            return finalList;
        } else {
            return new ArrayList<SequenceAnnotation>();
        }
    }

    //launch fourth panel (second round, individual)
    public Integer launchSecondRound(Integer counter) {
        Integer featNum = (counter/4)+1;
        NovoPrimeDoOverOptions novoPrimeDoOverOptions = new NovoPrimeDoOverOptions(
                featNum-1, explainMSGs.get(featNum-1),
                allValidityCheckList.get(featNum-1), amplifValidityCheckList.get(featNum-1));
        Object[] options = {"Done"};
        int n = JOptionPane.showOptionDialog(
                null, novoPrimeDoOverOptions.createPanel(), "Primer Options For Selected Feature",
                JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n > 0) {

        }
        return n;
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
            setText("view");
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
            button.setText("view");
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                Integer counter = Integer.parseInt(label);
                launchSecondRound(counter);
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
