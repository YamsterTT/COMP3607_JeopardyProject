
package edu.uwi.comp3607.logging;

import edu.uwi.comp3607.event.GameEvent;
import edu.uwi.comp3607.event.GameObserver;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Instant;

public class CsvEventLogger implements GameObserver {

    private final PrintWriter pw;
    private final String caseId;

    public CsvEventLogger(String filename, String caseId) throws Exception {
        this.caseId = caseId;
        boolean writeHeader = !new java.io.File(filename).exists();
        this.pw = new PrintWriter(new FileWriter(filename, true));
        if (writeHeader) {
            pw.println("Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play");
        }
    }

    @Override
    public void onEvent(GameEvent evt) {
        String line = String.join(",",
                esc(caseId),
                esc(evt.getPlayerId()),
                esc(evt.getActivity()),
                esc(Instant.now().toString()),
                esc(evt.getCategory()),
                escInt(evt.getValue()),
                esc(evt.getAnswerGiven()),
                esc(evt.getResult()),
                escInt(evt.getScoreAfter())
        );
        pw.println(line);
        pw.flush();
    }

    private String esc(String s) {
        if (s == null || s.isEmpty()) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private String escInt(Integer i) {
        return (i == null) ? "" : i.toString();
    }

    @Override
    public void close() {
        pw.close();
    }

    public String getCaseId() {
        return caseId;
    }
}
