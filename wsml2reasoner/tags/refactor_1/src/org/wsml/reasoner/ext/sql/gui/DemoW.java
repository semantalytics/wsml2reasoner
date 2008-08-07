package org.wsml.reasoner.ext.sql.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ext.sql.WSMLQuery;

/**
 * A GUI version of the Demo application.
 */
public class DemoW {
    /**
     * Application entry point.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
        }

        // Create the main window and show it.
        MainFrame mainFrame = new MainFrame();
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);

    }

    /**
     * The main application window
     */
    public static class MainFrame extends JFrame implements ActionListener {
        /** The serialisation ID. */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor
         */
        public MainFrame() {
            super("WSML Reasoner Interface - WSML Flight-A Prototype");

            setup();
        }

        /**
         * Create all the widgets, lay them out and create listeners.
         */
        private void setup() {
            setLayout(new BorderLayout());

            mQuery.setText("SELECT ?child, ?school, ?principle " + NEW_LINE + "FROM _\"http://wsml2reasoner.svn.sourceforge.net/viewvc/*checkout*/wsml2reasoner/wsml2reasoner/trunk/test/files/simpsons.wsml\" " + NEW_LINE + "WHERE ?child[attends hasValue ?school] and ?principle[principleOf hasValue ?school] " + NEW_LINE + "\n" + "\n" + "\n" + "\n");

            mRun.addActionListener(this);

            mAbort.addActionListener(this);
            mAbort.setEnabled(false);

            JScrollPane programScroller = new JScrollPane(mQuery);
            JScrollPane outputScroller = new JScrollPane(mOutput);

            // mOutput.setFillsViewportHeight(true);
            mOutput.setPreferredScrollableViewportSize(new Dimension(500, 70));
            // mOutput.setFillsViewportHeight(true);

            JSplitPane mainSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, programScroller, outputScroller);

            getContentPane().add(mainSplitter, BorderLayout.CENTER);

            JPanel panel = new JPanel();
            panel.add(mRun);
            panel.add(mAbort);

            getContentPane().add(panel, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        }

        private final JTextArea mQuery = new JTextArea();

        private final QueryResultTable queryResult = new QueryResultTable();

        private final JTable mOutput = new JTable(queryResult);

        private final JButton mRun = new JButton("Evaluate");

        private final JButton mAbort = new JButton("Abort");

        Thread mExecutionThread;

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == mRun) {
                run();
            }
            else if (e.getSource() == mAbort) {
                abort();
            }
        }

        /**
         * Called when evaluation has finished.
         * 
         * @param output
         *            The evaluation output
         */
        synchronized void setOutput(Set<Map<Variable, Term>> output, Ontology o) {
            mRun.setEnabled(true);
            mAbort.setEnabled(false);
            queryResult.setContent(output, o);
        }

        /**
         * Notifier class that 'hops' the output from the evaluation thread to
         * the UI thread.
         */
        class NotifyOutput implements Runnable {
            public NotifyOutput(Set<Map<Variable, Term>> result, Ontology ontology) {
                r = result;
                o = ontology;
            }

            public void run() {
                setOutput(r, o);
            }

            private final Set<Map<Variable, Term>> r;

            private final Ontology o;
        }

        /**
         * Starts the evaluation.
         */
        synchronized void run() {
            mRun.setEnabled(false);
            mAbort.setEnabled(true);

            String query = mQuery.getText();

            mExecutionThread = new Thread(new ExecutionTask(query), "query task");

            mExecutionThread.setPriority(Thread.MIN_PRIORITY);
            mExecutionThread.start();
        }

        /**
         * Aborts the evaluation.
         */
        synchronized void abort() {
            mRun.setEnabled(true);
            mAbort.setEnabled(false);

            // Not very nice, but hey, that's life.
            mExecutionThread.stop();
        }

        /**
         * Runnable task for performing the evaluation.
         */
        class ExecutionTask implements Runnable {
            ExecutionTask(String query) {
                this.query = query;
            }

            // @Override
            public void run() {
                try {
                    long queryDuration = -System.currentTimeMillis();
                    WSMLQuery wqe = new WSMLQuery();
                    Set<Map<Variable, Term>> r = wqe.executeQuery(query);
                    queryDuration += System.currentTimeMillis();
                    Ontology o = wqe.getOntology();

                    SwingUtilities.invokeLater(new NotifyOutput(r, o));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private final String query;
        }
    }

    private static final String NEW_LINE = "\r\n";
}
