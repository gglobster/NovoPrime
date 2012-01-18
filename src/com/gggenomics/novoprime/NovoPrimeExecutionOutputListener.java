package com.gggenomics.novoprime;

import com.biomatters.geneious.publicapi.utilities.Execution;

import java.util.ArrayList;
import java.util.List;

public class NovoPrimeExecutionOutputListener extends Execution.OutputListener {

    final List primerPairInfo = new ArrayList();

    ArrayList<String> primerExplainMsg = new ArrayList<String>();

    Integer numPairsReturned;

    public Integer getNumPairsReturned() {
        return numPairsReturned;
    }
    public List<String> getPrimerPairInfo() {
        return primerPairInfo;
    }
    public ArrayList<String> getExplainMsg() {
        return primerExplainMsg;
    }

    @Override
    public void stderrWritten(String output) {
        // Pass error messages through flagging them as coming from stderr
        System.out.println("stderr " + output);
    }
    @Override
    public void stdoutWritten(String output) {
        if (output.contains("PRIMER_ERROR=")) {
            primerExplainMsg.add(output.substring(13).trim());
            numPairsReturned = 0;
        } else if (output.contains("PRIMER_PAIR_NUM_RETURNED=")) {
            numPairsReturned = Integer.parseInt(output.substring(25).trim());
        }
        /*if (output.contains("SEQUENCE_ID=")) {
            primerPairInfo.add(output.substring(12).trim());
        }*/    //not actually needed
        if (output.contains("EXPLAIN")) {
            primerExplainMsg.add(output.substring(7));
        }
        if (output.contains("PRIMER_LEFT_0_SEQUENCE=")) {
            primerPairInfo.add( output.substring(23).trim());
        }
        if (output.contains("PRIMER_RIGHT_0_SEQUENCE=")) {
            primerPairInfo.add(output.substring(24).trim());
        }
        if (output.contains("PRIMER_LEFT_0=")) {
            primerPairInfo.add(output.substring(14).trim());
        }
        if (output.contains("PRIMER_RIGHT_0=")) {
            primerPairInfo.add(output.substring(15).trim());
        }

    }
}