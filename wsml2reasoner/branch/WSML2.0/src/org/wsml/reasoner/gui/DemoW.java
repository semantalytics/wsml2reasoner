/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.wsml.reasoner.gui;

import java.awt.BorderLayout;
import java.awt.Font;
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
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.DLUtilities;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

import com.ontotext.wsmo4j.common.IRIImpl;

/**
 * A GUI version of the Demo application.
 */
public class DemoW
{
	public static final int FONT_SIZE = 12;
	public static final String NEW_LINE = System.getProperty( "line.separator" );

	/**
	 * Application entry point.
	 * @param args
	 */
	public static void main( String[] args )
	{
		// Set up the native look and feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
		}

		// Create the main window and show it.
		MainFrame mainFrame = new MainFrame();
		mainFrame.setSize( 800, 600 );
		mainFrame.setVisible( true );
	}
	
	/**
	 * The main application window
	 */
	public static class MainFrame extends JFrame implements ActionListener
	{
		/** The serialisation ID. */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor
         */
		public MainFrame()
		{
			super( "IRIS - new" );

			setup();
		}

		/**
		 * Create all the widgets, lay them out and create listeners.
		 */
		private void setup()
		{
			setLayout( new BorderLayout() );
			
			String ns = "http://ex1.org#";
			String ontology = "namespace _\"" + ns + "\" \n"
					+ "ontology o1 \n"
					+ "concept A \n"
					+ "  attr ofType C \n "
					+ "concept B subConceptOf A \n ";
			
			ontology =
				"namespace { _\"http://www.ip-super.org/ontologies/execution-history#\",\n" + 
			     "EVO _\"http://www.ip-super.org/ontologies/EVO/20071215#\",\n" +
			     "COBRA _\"http://www.ip-super.org/ontologies/COBRA/20071215#\",\n" +
			     "TIME _\"http://www.ip-super.org/ontologies/time-ontology/20080612#\"\n" +
			"}\n" +

			"ontology eventData3e213ccad8e5\n" +
			"     importsOntology\n" +
			"            _\"http://www.ip-super.org/ontologies/EVO/20071215#EVO\"\n" +

			"instance\n" +
			"_\"http://www.ip-super.org/ontologies/execution-history#sequence-activity-line-114_ai2\" memberOf COBRA#ActivityInstance\n" +
			"     COBRA#performs hasValue\n" +
			"_\"http://ip-super.org/etel/bpel/Fulfilment/Fulfilment#sequence-activity-line-114\"\n" +

			"instance event3e213ccbd895 memberOf EVO#ActivityStarted\n" +
			"     COBRA#concernsProcessInstance hasValue Fulflment_pi71\n" +
			"     COBRA#concernsActivityInstance hasValue\n" +
			"_\"http://www.ip-super.org/ontologies/execution-history#sequence-activity-line-114_ai2\"\n" +
			"     TIME#occursAt hasValue\n" +
			"_dateTime(2008,11,18,11,44,19.4009990692138671875,4,0)\n" +
			"     COBRA#generatedBy hasValue SBPELEE\n";

			String query = "?x[?attribute hasValue ?value]";
			
			mOntology.setText( ontology );
			mQuery.setText( query );
							
			mRun.addActionListener( this );

			mAbort.addActionListener( this );
			mAbort.setEnabled( false );

			JScrollPane programScroller = new JScrollPane( mOntology );
			JScrollPane outputScroller = new JScrollPane( mOutput );
			
			Font f = new Font( "courier", Font.PLAIN, FONT_SIZE );
			mOntology.setFont( f );
			mOutput.setFont( f );

			JSplitPane mainSplitter = new JSplitPane( JSplitPane.VERTICAL_SPLIT, false, programScroller, outputScroller );

			getContentPane().add( mainSplitter, BorderLayout.CENTER );
			
			JPanel panel = new JPanel();

			panel.add( mQuery );
			panel.add( mRun );
			panel.add( mAbort );

			getContentPane().add( panel, BorderLayout.SOUTH );

			// Can't seem to make this happen before showinG, even with:
//			 mainSplitter.putClientProperty( JSplitPane.RESIZE_WEIGHT_PROPERTY, "0.5" );
//			mainSplitter.setDividerLocation( 0.5 );
			
			addWindowListener(
							new WindowAdapter()
							{
								public void windowClosing( WindowEvent e )
								{
									System.exit( 0 );
								}
							}
						);
			
		}

		private final JTextArea mOntology = new JTextArea();
		private final JTextArea mQuery = new JTextArea();
		private final JTextArea mOutput = new JTextArea();
		
		private final JButton mRun = new JButton( "Evaluate" );
		private final JButton mAbort = new JButton( "Abort" );
		
		Thread mExecutionThread;
		
		public void actionPerformed( ActionEvent e )
        {
	        if( e.getSource() == mRun )
	        {
	        	run();
	        }
	        else if( e.getSource() == mAbort )
	        {
	        	abort();
	        }
        }

		/**
		 * Called when evaluation has finished.
		 * @param output The evaluation output 
		 */
		synchronized void setOutput( String output )
		{
			mRun.setEnabled( true );
			mAbort.setEnabled( false );

			mOutput.setText( output );
		}
		
		/**
		 * Notifier class that 'hops' the output from the evaluation thread to the UI thread.
		 */
		class NotifyOutput implements Runnable
		{
			NotifyOutput( String output )
			{
				mOutput = output;
			}
			
			public void run()
            {
	            setOutput( mOutput );
            }
			
			final String mOutput;
		}
		
		/**
		 * Starts the evaluation.
		 */
		synchronized void run()
		{
			mOutput.setText( "" );

			mRun.setEnabled( false );
			mAbort.setEnabled( true );
			
			String program = mOntology.getText();
			String query = mQuery.getText();
			
			mExecutionThread = new Thread( new ExecutionTask( program, query ), "Evaluation task" );

			mExecutionThread.setPriority( Thread.MIN_PRIORITY );
			mExecutionThread.start();
		}
		
		/**
		 * Aborts the evaluation.
		 */
		synchronized void abort()
		{
			mRun.setEnabled( true );
			mAbort.setEnabled( false );

			// Not very nice, but hey, that's life.
			mExecutionThread.stop();
		}
		
		/**
		 * Runnable task for performing the evaluation.
		 */
		class ExecutionTask implements Runnable
		{
			
			ExecutionTask( String ontology, String query )
			{
				mOntology = ontology;
				mQuery = query;
			}
			
//			@Override
	        public void run()
	        {
	        	try {
	        		FactoryContainer factory = new FactoryImpl();
	        		LPReasoner reasoner = ReasonerHelper.getLPReasoner( BuiltInReasoner.IRIS_WELL_FOUNDED );

	        		Ontology ontology = OntologyHelper.parseOntology( mOntology );
	        		
	        		reasoner.registerOntology( ontology );
	        		
	        		new IRIImpl( "http://ip-super.org/etel/bpel/Fulfilment/Fulfilment#sequence-activity-line-114" );
	        		
	        		String strResults;
	        		if( true ) {
	        			LogicalExpressionParser leParser = new WsmlLogicalExpressionParser();
	        	        LogicalExpression qExpression = leParser.parse( mQuery );

		        		Set<Map<Variable, Term>> results = reasoner.executeQuery( qExpression );
		        		strResults = OntologyHelper.toString( results );
	        		}
	        		else {
	        			DLUtilities dlUtils = new DLUtilities( reasoner, factory );
	        			Set<Instance> instances = dlUtils.getAllInstances();
	        			strResults = OntologyHelper.toStringInstances( instances );
	        		}
	        		
	        		SwingUtilities.invokeLater( new NotifyOutput( strResults ) );
	        	} catch( Exception e ) {
	        		SwingUtilities.invokeLater( new NotifyOutput( e.toString() ) );
	        	}
	        }
			
			private final String mOntology;
			private final String mQuery;
		}
	}
}
