/* 
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, 
 *            Concurrent computing with Security and Mobility
 * 
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *  
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s): 
 * 
 * ################################################################
 */ 
package org.objectweb.proactive.ext.scilab.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.objectweb.proactive.ext.scilab.SciDeployEngine;
import org.objectweb.proactive.ext.scilab.SciEngineInfo;
import org.objectweb.proactive.ext.scilab.SciEvent;
import org.objectweb.proactive.ext.scilab.SciEventListener;
import org.objectweb.proactive.ext.scilab.SciTaskInfo;
import org.objectweb.proactive.ext.scilab.ScilabService;



/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SciFrame extends javax.swing.JFrame {
	private JMenuBar menuBar;
	private JSplitPane splitMain2;
	private JScrollPane scrollTreeEngine;
	private JTree treeEngine;
	private JScrollPane scrollTaskWait;
	private JPanel pnlTaskRun;
	private JButton btnClearTaskRun;
	private JFileChooser chooserDescriptor;
	private JMenuItem itemDesktopGrid;
	private JMenuItem itemDescriptor;
	private JMenu menuEngine;
	private JMenuItem itemExit;
	private JMenuItem itemTask;
	private JMenu menuCommand;
	private JButton btnClearLog;
	private JPanel pnlBtnLog;
	private JTextArea txtLog;
	private JScrollPane scrollLog;
	private JLabel lblLog;
	private JPanel pnlMainLog;
	private JButton btnDeleteTaskEnd;
	private JButton btnSaveTaskEnd;
	private JPanel pnlBtnTaskEnd;
	private JTable tableTaskEnd;
	private JScrollPane scrollTaskEnd;
	private JLabel lblTaskEnd;
	private JPanel pnlTaskEnd;
	private JButton btnKillTaskRun;
	private JPanel pnlBtnTaskRun;
	private JTable tableTaskRun;
	private JScrollPane scrollTaskRun;
	private JLabel lblTaskRun;
	private JButton btnClearTaskWait;
	private JButton btnCancelTaskWait;
	private JPanel pnlBtnTaskWait;
	private JTable tableTaskWait;
	private JLabel lblTaskWait;
	private JPanel pnlTaskWait;
	private JSplitPane splitMain1;
	private JSplitPane splitTask1;
	private JList listPreview;
	private JSplitPane splitTask2;
	private DefaultTableModel tableTaskWaitModel;
	private DefaultTableModel tableTaskRunModel;
	private DefaultTableModel tableTaskEndModel;
	private DefaultMutableTreeNode rootEngine;
	private ScilabService service;
	private DialogTask dialogTask;
	private DialogResult dialogResult;
	private String pathDescriptor;
	private String nameVn;
	private Timer timerRefresh;
	private JFileChooser chooserSave;
	private JPanel pnlPreview;
	private JLabel lblVn;
	private DefaultComboBoxModel listPreviewModel;
	private JScrollPane scrollPreview;
	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SciFrame inst = new SciFrame();
		inst.setVisible(true);
	}

	public SciFrame() {
		super();
		initGUI();
		dialogTask = new DialogTask(this);
		dialogTask.setModal(true);
		
		dialogResult = new DialogResult(this);
		dialogResult.setModal(true);
		
		service = new ScilabService();
		
		service.getTaskObservable().addSciEventListener( new SciEventListener(){
			public void actionPerformed(SciEvent evt){
				SciTaskInfo sciTaskInfo = (SciTaskInfo) evt.getSource();
				
				if(sciTaskInfo.getState() == SciTaskInfo.WAIT){
					updateTableTaskWait(sciTaskInfo);
					return;
				}
				
				if(sciTaskInfo.getState() == SciTaskInfo.CANCEL){
					updateTableTaskCancel(sciTaskInfo);
					return;
				}
				
				if(sciTaskInfo.getState() == SciTaskInfo.RUN){
					updateTableTaskRun(sciTaskInfo);
					return;
				}
				
				if(sciTaskInfo.getState() == SciTaskInfo.KILL){
					updateTableTaskKill(sciTaskInfo);
					return;
				}
				
				if(sciTaskInfo.getState() == SciTaskInfo.SUCCESS || sciTaskInfo.getState() == SciTaskInfo.ABORT){
					updateTableTaskEnd(sciTaskInfo);
					return;
				}
			}	
		});
		
		
		service.getEngineObservable().addSciEventListener(new SciEventListener(){
			public void actionPerformed(SciEvent evt){
				refreshTreeEngine();
			}
		});
		
		timerRefresh = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				timerRefreshActionPerformed(evt);
			}
		}); 
		

		timerRefresh.start();
	}

	private void initGUI() {
		try {
			{
				chooserSave = new JFileChooser();
				chooserSave.setDialogTitle("Save Scilab Result");
			}
			{
				splitMain1 = new JSplitPane();
				getContentPane().add(splitMain1, BorderLayout.CENTER);
				splitMain1.setOrientation(JSplitPane.VERTICAL_SPLIT);
				splitMain1.setOneTouchExpandable(true);
				splitMain1.setDividerLocation(520);
				splitMain1.setDividerSize(7);
				{
					splitMain2 = new JSplitPane();
					splitMain1.add(splitMain2, JSplitPane.TOP);
					splitMain2.setOneTouchExpandable(true);
					splitMain2.setDividerLocation(160);
					splitMain2.setDividerSize(7);
					{
						scrollTreeEngine = new JScrollPane();
						splitMain2.add(scrollTreeEngine, JSplitPane.LEFT);
						{
							rootEngine = new DefaultMutableTreeNode("Scilab Engines");
							treeEngine = new JTree(rootEngine);
							scrollTreeEngine.setViewportView(treeEngine);
							treeEngine.addMouseListener(new MouseAdapter() {
								public void mouseClicked(MouseEvent evt) {
									treeEngineMouseClicked(evt);
								}
							});
						}
					}
					{
						splitTask1 = new JSplitPane();
						splitTask1.setOrientation(JSplitPane.VERTICAL_SPLIT);
						splitTask1.setDividerSize(7);
						splitTask1.setOneTouchExpandable(true);
						splitMain2.add(splitTask1, JSplitPane.RIGHT);
						{
							splitTask2 = new JSplitPane();
							splitTask2.setOneTouchExpandable(true);
							splitTask2.setOrientation(JSplitPane.VERTICAL_SPLIT);
							splitTask2.setDividerSize(7);
							splitTask1.add(splitTask2, JSplitPane.TOP);
							{
								pnlTaskWait = new JPanel();
								splitTask2.add(pnlTaskWait, JSplitPane.TOP);
								BorderLayout pnlTaskWaitLayout = new BorderLayout();
								pnlTaskWait.setLayout(pnlTaskWaitLayout);
								pnlTaskWait.setSize(700, 180);
								pnlTaskWait
										.setPreferredSize(new java.awt.Dimension(
												702, 150));
								{
									lblTaskWait = new JLabel();
									pnlTaskWait.add(lblTaskWait, BorderLayout.NORTH);
									lblTaskWait.setText("Pending Tasks:");
								}
								{
									scrollTaskWait = new JScrollPane();
									pnlTaskWait.add(scrollTaskWait,
											BorderLayout.CENTER);
									scrollTaskWait.setPreferredSize(new java.awt.Dimension(690, 135));
								
									{
										tableTaskWaitModel = new DefaultTableModel(
														null, new String[] { "Id Task", "Script",
															    "Priority", "Awaited Time",
																"State"});
										
										tableTaskWait = new JTable() {
									        public boolean isCellEditable(int rowIndex, int vColIndex) {
									            return false;
									        }
									    };
									    
										scrollTaskWait.setViewportView(tableTaskWait);
										scrollTaskWait.getViewport().setBackground(Color.WHITE);
										tableTaskWait.setModel(tableTaskWaitModel);
										tableTaskWait.getColumnModel().getColumn(4).setCellRenderer(new IconRenderer());
										tableTaskWait.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
										tableTaskWait
												.addMouseListener(new MouseAdapter() {
													public void mouseClicked(
															MouseEvent evt) {
														tableTaskWaitMouseClicked(evt);
													}
												});
									}
								}
								{
									pnlBtnTaskWait = new JPanel();
									pnlTaskWait.add(pnlBtnTaskWait,
											BorderLayout.SOUTH);
									FlowLayout pnlBtnTaskWaitLayout = new FlowLayout();
									pnlBtnTaskWaitLayout
											.setAlignment(FlowLayout.RIGHT);
									pnlBtnTaskWait.setLayout(pnlBtnTaskWaitLayout);
									{
										btnCancelTaskWait = new JButton();
										pnlBtnTaskWait.add(btnCancelTaskWait);
										btnCancelTaskWait.setText("Cancel");
										btnCancelTaskWait.setToolTipText("Cancel pending tasks ");
										btnCancelTaskWait
												.addActionListener(new ActionListener() {
													public void actionPerformed(
															ActionEvent evt) {
														btnCancelTaskWaitActionPerformed(evt);
													}
												});
									}
									{
										btnClearTaskWait = new JButton();
										pnlBtnTaskWait.add(btnClearTaskWait);
										btnClearTaskWait.setText("Clear");
										btnClearTaskWait.setSize(70, 22);
										btnClearTaskWait.setToolTipText("Clear  cancelled tasks");
										btnClearTaskWait
												.addActionListener(new ActionListener() {
													public void actionPerformed(
															ActionEvent evt) {
														btnClearTaskWaitActionPerformed(evt);
													}
												});
									}
								}
							}
							{
								pnlTaskRun = new JPanel();
								splitTask2.add(pnlTaskRun, JSplitPane.BOTTOM);
								BorderLayout pnlTaskRunLayout = new BorderLayout();
								pnlTaskRun.setLayout(pnlTaskRunLayout);
								pnlTaskRun.setSize(700, 180);
								pnlTaskRun.setPreferredSize(new java.awt.Dimension(
										702, 150));
								{
									lblTaskRun = new JLabel();
									pnlTaskRun.add(lblTaskRun, BorderLayout.NORTH);
									lblTaskRun.setText("Executing Tasks:");
								}
								{
									scrollTaskRun = new JScrollPane();
									pnlTaskRun.add(scrollTaskRun,
											BorderLayout.CENTER);
									scrollTaskRun.setPreferredSize(new java.awt.Dimension(690, 135));
									//scrollTaskRun.setSize(692, 104);
									{
										tableTaskRunModel = new DefaultTableModel(
												null, new String[] { "Id Task", "Script",
														"Id Engine", "Global Time",
														"State"});
						
										tableTaskRun = new JTable() {
									        public boolean isCellEditable(int rowIndex, int vColIndex) {
									            return false;
									        }
									    };
									 
										scrollTaskRun.setViewportView(tableTaskRun);
										scrollTaskRun.getViewport().setBackground(Color.WHITE);
										
										tableTaskRun.setModel(tableTaskRunModel);
										tableTaskRun.getColumnModel().getColumn(4).setCellRenderer(new IconRenderer());
										tableTaskRun.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
										tableTaskRun.addMouseListener(new MouseAdapter() {
													public void mouseClicked(
															MouseEvent evt) {
														tableTaskRunMouseClicked(evt);
													}
												});
									}
								}
								{
									pnlBtnTaskRun = new JPanel();
									pnlTaskRun.add(pnlBtnTaskRun,
											BorderLayout.SOUTH);
									FlowLayout pnlBtnTaskRunLayout = new FlowLayout();
									pnlBtnTaskRunLayout
											.setAlignment(FlowLayout.RIGHT);
									pnlBtnTaskRun.setLayout(pnlBtnTaskRunLayout);
									{
										btnKillTaskRun = new JButton();
										pnlBtnTaskRun.add(btnKillTaskRun);
										btnKillTaskRun.setText("Kill");
										btnKillTaskRun.setSize(70, 22);
										btnKillTaskRun.setToolTipText("Kill executing tasks");
										btnKillTaskRun.addActionListener(new ActionListener() {
													public void actionPerformed(
															ActionEvent evt) {
														btnKillTaskRunActionPerformed(evt);
													}
												});
									}
									{
										btnClearTaskRun = new JButton();
										pnlBtnTaskRun.add(btnClearTaskRun);
										btnClearTaskRun.setText("Clear");
										btnClearTaskRun.setSize(70, 22);
										btnClearTaskRun.setToolTipText("Clear killed tasks");
										btnClearTaskRun.addActionListener(new ActionListener() {
													public void actionPerformed(
															ActionEvent evt) {
														btnClearTaskRunActionPerformed(evt);
													}
												});
									}
								}
							}

						}	
						{
							pnlTaskEnd = new JPanel();
							splitTask1.add(pnlTaskEnd, JSplitPane.BOTTOM);
							BorderLayout pnlTaskEndLayout = new BorderLayout();
							pnlTaskEnd.setLayout(pnlTaskEndLayout);
							pnlTaskEnd.setSize(700, 180);
							pnlTaskEnd.setPreferredSize(new java.awt.Dimension(
									702, 150));
							{
								lblTaskEnd = new JLabel();
								pnlTaskEnd.add(lblTaskEnd, BorderLayout.NORTH);
								lblTaskEnd.setText("Terminated Tasks:");
							}
							{
								scrollTaskEnd = new JScrollPane();
								pnlTaskEnd.add(scrollTaskEnd,BorderLayout.CENTER);
								scrollTaskEnd.setPreferredSize(new java.awt.Dimension(690, 135));
								{
									tableTaskEndModel = new DefaultTableModel(
											null, new String[] { "Id Task", "Script", 
													"Execution Time", "Global Time",
													"State"});
									
									tableTaskEnd = new JTable() {
								        public boolean isCellEditable(int rowIndex, int vColIndex) {
								            return false;
								        }
								    };
									scrollTaskEnd.setViewportView(tableTaskEnd);
									scrollTaskEnd.getViewport().setBackground(Color.WHITE);
									tableTaskEnd.setModel(tableTaskEndModel);
									tableTaskEnd.getColumnModel().getColumn(4).setCellRenderer(new IconRenderer());
									tableTaskEnd.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
									tableTaskEnd.addMouseListener(new MouseAdapter() {
												public void mouseClicked(
														MouseEvent evt) {
													tableTaskEndMouseClicked(evt);
												}
											});
								}
							}
							{
								pnlBtnTaskEnd = new JPanel();
								pnlTaskEnd.add(pnlBtnTaskEnd, BorderLayout.SOUTH);
								FlowLayout pnlBtnTaskEndLayout = new FlowLayout();
								pnlBtnTaskEndLayout
										.setAlignment(FlowLayout.RIGHT);
								pnlBtnTaskEnd.setLayout(pnlBtnTaskEndLayout);
								{
									btnSaveTaskEnd = new JButton();
									pnlBtnTaskEnd.add(btnSaveTaskEnd);
									btnSaveTaskEnd.setText("Save");
									btnSaveTaskEnd.setSize(70, 22);
									btnSaveTaskEnd.setToolTipText("Save results");
									btnSaveTaskEnd
											.addActionListener(new ActionListener() {
												public void actionPerformed(
														ActionEvent evt) {
													btnSaveTaskEndActionPerformed(evt);
												}
											});
								}
								{
									btnDeleteTaskEnd = new JButton();
									pnlBtnTaskEnd.add(btnDeleteTaskEnd);
									btnDeleteTaskEnd.setText("Delete");
									btnDeleteTaskEnd.setSize(70, 22);
									btnDeleteTaskEnd.setToolTipText("Delete result");
									btnDeleteTaskEnd
											.addActionListener(new ActionListener() {
												public void actionPerformed(
														ActionEvent evt) {
													btnDeleteTaskEndActionPerformed(evt);
												}
											});
								}
							}
						}
					}
				}
				{
					pnlMainLog = new JPanel();
					splitMain1.add(pnlMainLog, JSplitPane.BOTTOM);
					BorderLayout pnlMainLogLayout = new BorderLayout();
					pnlMainLog.setLayout(pnlMainLogLayout);
					pnlMainLog
							.setPreferredSize(new java.awt.Dimension(790, 348));
					pnlMainLog.setSize(790, 200);
					{
						lblLog = new JLabel();
						pnlMainLog.add(lblLog, BorderLayout.NORTH);
						lblLog.setText("Operations:");
					}
					{
						scrollLog = new JScrollPane();
						pnlMainLog.add(scrollLog, BorderLayout.CENTER);
						{
							txtLog = new JTextArea();
							scrollLog.setViewportView(txtLog);
						}
					}
					{
						pnlBtnLog = new JPanel();
						pnlMainLog.add(pnlBtnLog, BorderLayout.SOUTH);
						FlowLayout pnlBtnLogLayout = new FlowLayout();
						pnlBtnLogLayout.setAlignment(FlowLayout.RIGHT);
						pnlBtnLog.setLayout(pnlBtnLogLayout);
						{
							btnClearLog = new JButton();
							pnlBtnLog.add(btnClearLog);
							btnClearLog.setText("Clear");
							btnClearLog.setToolTipText("Clear operation logs");
							btnClearLog.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									btnClearLogActionPerformed(evt);
								}
							});
						}
					}
				}
			}

			{
				menuBar = new JMenuBar();
				setJMenuBar(menuBar);
				{
					menuCommand = new JMenu();
					menuBar.add(menuCommand);
					menuCommand.setText("Command");
					{
						menuEngine = new JMenu();
						menuCommand.add(menuEngine);
						menuEngine.setText("New Engine");
						{
							itemDescriptor = new JMenuItem();
							menuEngine.add(itemDescriptor);
							itemDescriptor.setText("From Descriptor");
							itemDescriptor
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											itemDescriptorActionPerformed(evt);
										}
									});
						}
						{
							itemDesktopGrid = new JMenuItem();
							menuEngine.add(itemDesktopGrid);
							itemDesktopGrid.setText("From Desktop Grid");
							//to modify
							itemDesktopGrid.setEnabled(false);
							itemDesktopGrid
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											itemDesktopGridActionPerformed(evt);
										}
									});
						}
					}
					{
						itemTask = new JMenuItem();
						menuCommand.add(itemTask);
						itemTask.setText("New Task");
						itemTask.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								itemTaskActionPerformed(evt);
							}
						});
					}
					{
						itemExit = new JMenuItem();
						menuCommand.add(itemExit);
						itemExit.setText("Exit");
						itemExit.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								System.out.println("itemExit.actionPerformed, event="+ evt);
								if(service != null){
									service.exit();
								}
								
								System.exit(0);
							}
						});
					}
				}
			}
			{
				chooserDescriptor = new JFileChooser();
				FileFilter filter = new FileFilter() {
					public boolean accept(File f) {

						if (f == null) {
							return false;
						}

						if (f.isDirectory()) {
							return true;
						}

						return f.getName().endsWith(".xml");
					}

					public String getDescription() {
						return "XML Filter";
					}
				};

				chooserDescriptor.setApproveButtonText("Deploy");
				chooserDescriptor.setFileFilter(filter);
				
				{
					pnlPreview = new JPanel();
					BorderLayout pnlPreviewLayout = new BorderLayout();
					pnlPreview.setLayout(pnlPreviewLayout);
					pnlPreview.setBorder(BorderFactory.createTitledBorder(""));
					{
						lblVn = new JLabel();
						lblVn.setText("Select Virtual Node");
						pnlPreview.add(lblVn, BorderLayout.NORTH);
					}
					{
						scrollPreview = new JScrollPane();
						scrollPreview.setPreferredSize(new java.awt.Dimension(90, 80));
						scrollPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						pnlPreview.add(scrollPreview, BorderLayout.CENTER);
						{
							listPreviewModel = new DefaultComboBoxModel();
							listPreview = new JList();
							listPreview.setModel(listPreviewModel);
							scrollPreview.setViewportView(listPreview);
						}
					}
				}
				
				chooserDescriptor.setAccessory(pnlPreview);
				chooserDescriptor.addPropertyChangeListener( new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {	
						if(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
							File newFile = (File) evt.getNewValue();
							if(newFile != null) {
								String path = newFile.getAbsolutePath();
								
								String arrayNameVn[] = SciDeployEngine.getListVirtualNode(path);
								listPreviewModel.removeAllElements();
								for(int i=0; i<arrayNameVn.length; i++){
									listPreviewModel.addElement(arrayNameVn[i]);
								}
							}			
						}
					}
				});
			}
			
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					
					if(service != null){
						service.exit();
					}
					
					System.exit(0);
				}
			});
			pack();
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension f = this.getSize();
	        int x = (d.width - f.width) / 2;
	        int y = (d.height - f.height) / 2;
	        this.setBounds(x, y, f.width, f.height );
	        this.setIconImage(new ImageIcon(getClass().getResource("img/icone.png")).getImage());
	        this.setTitle("Grid Scilab ToolBox");
			this.setSize(812, 744);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void itemDescriptorActionPerformed(ActionEvent evt) {
		System.out.println("itemDescriptor.actionPerformed, event=" + evt);
		this.deployDescriptor();
	}

	private void itemDesktopGridActionPerformed(ActionEvent evt) {
		System.out.println("itemDesktopGrid.actionPerformed, event=" + evt);
		//TODO add your code for itemDesktopGrid.actionPerformed
	}
	
	private void itemTaskActionPerformed(ActionEvent evt) {
		System.out.println("itemTask.actionPerformed, event=" + evt);
		// TODO add your code for itemTask.actionPerformed
		this.addTask();
	}

	private void treeEngineMouseClicked(MouseEvent evt) {
		System.out.println("treeEngine.mouseClicked, event=" + evt);
		// TODO add your code for treeEngine.mouseClicked
	}

	private void tableTaskWaitMouseClicked(MouseEvent evt) {
		System.out.println("tableTaskWait.mouseClicked, event=" + evt);
		if (evt.getClickCount() == 2) {
			String idTask = (String) this.tableTaskWaitModel.getValueAt(this.tableTaskWait.getSelectedRow(), 0);
			
			ArrayList listTask = service.getListTaskWait();
			
			SciTaskInfo sciTaskInfo = null;
			int i; 
			for(i=0; i<listTask.size(); i++){
				sciTaskInfo = (SciTaskInfo) listTask.get(i);
				if(idTask.equals(sciTaskInfo.getIdTask())){
					break;
				}
			}
			
			if(i == listTask.size()){
				return;
			}
				
			
			this.dialogResult.setPathScript(sciTaskInfo.getPathScript());
			this.dialogResult.setJobInit(sciTaskInfo.getSciTask().getJobInit());
			this.dialogResult.setDataOut("");
			this.dialogResult.setSaveEnable(false);
			
			this.dialogResult.setLocationRelativeTo(this);
	        this.dialogResult.setVisible(true);
		}
	}

	private void tableTaskRunMouseClicked(MouseEvent evt) {
		System.out.println("tableTaskRun.mouseClicked, event=" + evt);
		if (evt.getClickCount() == 2) {
			String idTask = (String)this.tableTaskRunModel.getValueAt(this.tableTaskRun.getSelectedRow(), 0);
			
			SciTaskInfo sciTaskInfo = (SciTaskInfo) service.getMapTaskRun().get(idTask);
			this.dialogResult.setPathScript(sciTaskInfo.getPathScript());
			this.dialogResult.setJobInit(sciTaskInfo.getSciTask().getJobInit());
			this.dialogResult.setDataOut("");
			this.dialogResult.setSaveEnable(false);
			
			this.dialogResult.setLocationRelativeTo(this);
	        this.dialogResult.setVisible(true);
		}
	}

	private void tableTaskEndMouseClicked(MouseEvent evt) {
		System.out.println("tableTaskEnd.mouseClicked, event=" + evt);
	
		if (evt.getClickCount() == 2) {
			String idTask = (String)this.tableTaskEndModel.getValueAt(this.tableTaskEnd.getSelectedRow(), 0);
			
			SciTaskInfo sciTaskInfo = service.getTaskEnd(idTask);
			this.dialogResult.setPathScript(sciTaskInfo.getPathScript());
			this.dialogResult.setJobInit(sciTaskInfo.getSciTask().getJobInit());
			
			String strResult="";
			ArrayList listResult = sciTaskInfo.getSciResult().getList();
			for(int i=0; i< listResult.size(); i++){
				strResult += listResult.get(i).toString() + "\n";
			}
			
			this.dialogResult.setSaveEnable(true);
			this.dialogResult.setDataOut(strResult);
			this.dialogResult.setLocationRelativeTo(this);
	        this.dialogResult.setVisible(true);
		}
	}

	private void btnCancelTaskWaitActionPerformed(ActionEvent evt) {
		System.out.println("btnCancelTaskWait.actionPerformed, event=" + evt);
		
		int array[] = this.tableTaskWait.getSelectedRows();
		
		String idEngine;
		for(int i=0; i<array.length; i++){
			idEngine = (String) this.tableTaskWaitModel.getValueAt(array[i], 0);
			service.cancelTask(idEngine);
		}	
	}

	private void btnClearTaskWaitActionPerformed(ActionEvent evt) {
		System.out.println("btnClearTaskWait.actionPerformed, event=" + evt);
		int i = 0;
		int count =  this.tableTaskWaitModel.getRowCount();
		String value;
		
		while(i<count){
			value = (String) this.tableTaskWaitModel.getValueAt(i, 3);
			
			if(value.equals("-")){
				this.tableTaskWaitModel.removeRow(i);
				count--;
			}else{
				i++;
			}		
		}
	}

	private void btnKillTaskRunActionPerformed(ActionEvent evt) {
		System.out.println("btnKillTaskRun.actionPerformed, event=" + evt);
		
		int array[] = this.tableTaskRun.getSelectedRows();
		
		String idEngine;
		for(int i=0; i<array.length; i++){
			idEngine = (String) this.tableTaskRunModel.getValueAt(array[i], 0);
			service.killTask(idEngine);
		}	
	}

	private void btnClearTaskRunActionPerformed(ActionEvent evt) {
		System.out.println("btnClearTaskRun.actionPerformed, event=" + evt);
		int i = 0;
		int count =  this.tableTaskRunModel.getRowCount();
		String value;
		
		while(i<count){
			value = (String) this.tableTaskRunModel.getValueAt(i, 3);
			
			if(value.equals("-")){
				this.tableTaskRunModel.removeRow(i);
				count--;
			}else{
				i++;
			}		
		}
	}

	private void btnSaveTaskEndActionPerformed(ActionEvent evt) {
		System.out.println("btnSaveTaskEnd.actionPerformed, event=" + evt);
		
		int i = this.tableTaskWait.getSelectedRow();
		
		if( i== -1) return;
		
		String idTask = (String) this.tableTaskEndModel.getValueAt(i, 0);
		SciTaskInfo sciTaskInfo = service.getTaskEnd(idTask);
		
		if (this.chooserSave.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		File f = this.chooserSave.getSelectedFile().getAbsoluteFile();
		if(f.exists() && !f.canWrite()){
			return;
		}
	
		String strResult="";
		ArrayList listResult = sciTaskInfo.getSciResult().getList();
		for(i=0; i< listResult.size(); i++){
			strResult += listResult.get(i).toString() + "\n";
		}
		try{
			FileWriter fw = new FileWriter(f);
			fw.write(strResult);
			fw.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void btnDeleteTaskEndActionPerformed(ActionEvent evt) {
		System.out.println("btnDeleteTaskEnd.actionPerformed, event=" + evt);
		
		int array[] = this.tableTaskEnd.getSelectedRows();
		int length = array.length;
		String idEngine;
		for(int i=length-1; i >= 0; i--){
			idEngine = (String) this.tableTaskEndModel.getValueAt(array[i], 0);
			service.removeTask(idEngine);
			this.tableTaskEndModel.removeRow(array[i]);
		}	
	}
	
	private void btnClearLogActionPerformed(ActionEvent evt) {
		System.out.println("btnClearLog.actionPerformed, event=" + evt);
		this.txtLog.setText("");
	}

	private void timerRefreshActionPerformed(ActionEvent evt){
		this.refreshTableTask();
	}
	
	private void deployDescriptor() {
		if (this.chooserDescriptor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		pathDescriptor = this.chooserDescriptor.getSelectedFile().getAbsolutePath();
		nameVn = (String)listPreview.getSelectedValue();
		
		if(nameVn == null){
			txtLog.append("->Please select a Virtual Node: "
					+ pathDescriptor + "\n");
			return;
		}
		
		txtLog.append("->Deployment is running: "
				+ pathDescriptor + "\n");
		
		(new Thread(){
			private String path = pathDescriptor;
			public void run(){
				service.deployEngine(nameVn, path, 4);
				txtLog.append("->Deployment is successfull:" + path + "\n");
			}
		}).start();
	}
	
	private void addTask() {
	
		if(this.service == null){
			this.txtLog.append("->No Node Descriptor is deployed" + "\n");
			return;
		}
		
		if(this.service.getNbEngine() == 0){
			this.txtLog.append("->No Scilab Engine is launched" + "\n");
			return;
		}
		
        this.dialogTask.setLocationRelativeTo(this);
        this.dialogTask.setVisible(true);
		
		if ((this.dialogTask.getState() == DialogTask.CANCEL)) {
			return;
		}
		
		int priority = SciTaskInfo.NORMAL;
		
		if(dialogTask.getTaskPriority().equals("Low")){
			priority = SciTaskInfo.LOW;
		}else if(dialogTask.getTaskPriority().equals("High")){
			priority = SciTaskInfo.HIGH;
		}
		
		try {
			service.sendTask(dialogTask.getPath(), dialogTask.getJobInit(), dialogTask.getDataOut(), priority);
		} catch (IOException e) {
			txtLog.append("->Path Error :" + dialogTask.getPath() + "\n");
		}	
	}

	private void refreshTreeEngine(){
		HashMap mapEngine = this.service.getMapEngine();
		SciEngineInfo sciEngineInfo;
		DefaultMutableTreeNode nodeEngine;
		
		int i = 0;
		int count = this.rootEngine.getChildCount();
		while(i<count){
			nodeEngine = (DefaultMutableTreeNode) this.rootEngine.getChildAt(i);
			sciEngineInfo = (SciEngineInfo) mapEngine.remove(nodeEngine.toString());
			if(sciEngineInfo == null){
				nodeEngine.removeFromParent();
				count--;
			}else{
				nodeEngine.removeAllChildren();
				nodeEngine.add(new DefaultMutableTreeNode(sciEngineInfo.getSciEngineUrl()));
				i++;
			}
		}
		
		TreeSet listSort = new TreeSet(mapEngine.keySet());
		Iterator it = listSort.iterator();
		
		while(it.hasNext()){
			sciEngineInfo = (SciEngineInfo) mapEngine.get(it.next());
			nodeEngine = new DefaultMutableTreeNode(sciEngineInfo.getIdEngine());
			nodeEngine.add(new DefaultMutableTreeNode(sciEngineInfo.getSciEngineUrl()));
			this.rootEngine.add(nodeEngine);
		}
		
		treeNodesInserted();
	}
	
	private void treeNodesInserted(){
		EventQueue.invokeLater( new Runnable(){
			public void run() {
				treeEngine.updateUI();
			}} );

	}

	private void refreshTableTask(){
		String value;
		for(int i=0; i<this.tableTaskWaitModel.getRowCount(); i++){
			value = (String)this.tableTaskWaitModel.getValueAt(i, 3);
			
			if(value.equals("-"))
				continue;
			
			value = (String)this.tableTaskWaitModel.getValueAt(i, 3);
			value = Integer.parseInt(value) + 1000 + ""; 
			this.tableTaskWaitModel.setValueAt(value, i, 3);
		}
		
		for(int i=0; i<this.tableTaskRunModel.getRowCount(); i++){
			value = (String)this.tableTaskRunModel.getValueAt(i, 3);
			if(value.equals("-"))
				continue;
			
			value = (String)this.tableTaskRunModel.getValueAt(i, 3);
			value = Integer.parseInt(value) + 1000 + ""; 
			this.tableTaskRunModel.setValueAt(value, i, 3);
		}
	}

	private void updateTableTaskWait(SciTaskInfo sciTaskInfo){
		String strPriority;

		if (sciTaskInfo.getPriority() == SciTaskInfo.HIGH) {
			strPriority = "High";
		} else if (sciTaskInfo.getPriority() == SciTaskInfo.NORMAL) {
			strPriority = "Normal";
		} else {
			strPriority = "Low";
		}
		
		Object row[] = new Object[]{
				sciTaskInfo.getIdTask(),
				sciTaskInfo.getNameScript(),
				strPriority,
				(new Date()).getTime() - sciTaskInfo.getDateStart() + "",		
				new ImageIcon(getClass().getResource("img/runTask.gif"))
		};
		this.tableTaskWaitModel.addRow(row);
		txtLog.append("->Add new Scilab Task :" + sciTaskInfo.getIdTask() + "\n");
	}
	
	private void updateTableTaskCancel(SciTaskInfo sciTaskInfo){
		String idTask;
		for(int i=0; i<this.tableTaskWaitModel.getRowCount(); i++){
			idTask = (String) this.tableTaskWaitModel.getValueAt(i,0);
			if(idTask.equals(sciTaskInfo.getIdTask())){
				this.tableTaskWaitModel.setValueAt("-", i, 2);
				this.tableTaskWaitModel.setValueAt("-", i, 3);
				this.tableTaskWaitModel.setValueAt(new ImageIcon(getClass().getResource("img/stopTask.gif")), i, 4);
				break;
			}	
		}
		txtLog.append("->Cancel Scilab Task :" + sciTaskInfo.getIdTask() + "\n");
	}
	
	private void updateTableTaskRun(SciTaskInfo sciTaskInfo){
		String idTask;
		
		for(int i=0; i<this.tableTaskWaitModel.getRowCount(); i++){
			idTask = (String) this.tableTaskWaitModel.getValueAt(i,0);
			if(idTask.equals(sciTaskInfo.getIdTask())){
				this.tableTaskWaitModel.removeRow(i);
				break;
			}	
		}
		
		Object row[] = new Object[]{
				sciTaskInfo.getIdTask(),
				sciTaskInfo.getNameScript(),
				sciTaskInfo.getIdEngine(),
				(new Date()).getTime() - sciTaskInfo.getDateStart() + "",		
				new ImageIcon(getClass().getResource("img/runTask.gif"))
		};
		
		this.tableTaskRunModel.addRow(row);
		txtLog.append("->Execute Scilab Task :" + sciTaskInfo.getIdTask() + "\n");
	}
	
	private void updateTableTaskKill(SciTaskInfo sciTaskInfo){
		String idTask;
		for(int i=0; i<this.tableTaskRunModel.getRowCount(); i++){
			idTask = (String) this.tableTaskRunModel.getValueAt(i,0);
			if(idTask.equals(sciTaskInfo.getIdTask())){
				this.tableTaskRunModel.setValueAt("-", i, 2);
				this.tableTaskRunModel.setValueAt("-", i, 3);
				this.tableTaskRunModel.setValueAt(new ImageIcon(getClass().getResource("img/stopTask.gif")), i, 4);
				txtLog.append("->Execute Scilab Task :" + sciTaskInfo.getIdTask() + "\n");
				break;
			}	
		}
	}
	
	private void updateTableTaskEnd(SciTaskInfo sciTaskInfo){
		String idTask;
		for(int i=0; i<this.tableTaskRunModel.getRowCount(); i++){
			idTask = (String) this.tableTaskRunModel.getValueAt(i,0);
			if(idTask.equals(sciTaskInfo.getIdTask())){
				this.tableTaskRunModel.removeRow(i);
				break;
			}	
		}
		
		String strTmp = (sciTaskInfo.getState() == SciTaskInfo.SUCCESS)? "img/successTask.gif" : "img/abortTask.gif";
		
		Object row[] = new Object[]{
				sciTaskInfo.getIdTask(),
				sciTaskInfo.getNameScript(),
				sciTaskInfo.getTimeExecution() + "",
				sciTaskInfo.getTimeGlobal() + "",		
				new ImageIcon(getClass().getResource(strTmp))
		};
		this.tableTaskEndModel.addRow(row);
		txtLog.append("->Terminate Scilab Task :" + sciTaskInfo.getIdTask() + "\n");
	}
}
