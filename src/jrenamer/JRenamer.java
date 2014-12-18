/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jrenamer;

import com.alee.extended.filechooser.WebFileChooserField;
import com.alee.extended.layout.HorizontalFlowLayout;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WebOverlay;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import javax.swing.SwingUtilities;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.table.renderers.WebTableCellRenderer;
import com.alee.laf.text.WebTextField;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotificationPopup;
import com.alee.utils.swing.EmptyMouseAdapter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author qqqq
 */
public class JRenamer implements  Constants, Observer{
    private final JFrame frame;
    private final WebTextField urlTextField;
    private final WebFileChooserField prefixFileField;
    private Reaname rnm;
    public WebProgressBar progresBar;
    private JPanel j;
    private WebPanel upPanel;
    private WebStatusBar statusBar;
    private  final WebNotificationPopup notificationPopup;
    private  WebTextField ruleTexrField;
    public JRenamer() {
        frame = new JFrame(PROGRAMM_NAME);
        frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        
        statusBar = new WebStatusBar ();
        WebMemoryBar memoryBar = new WebMemoryBar ();
        memoryBar.setPreferredWidth ( memoryBar.getPreferredSize ().width + 20 );
        statusBar.add(memoryBar, ToolbarLayout.END);
        ruleTexrField = new WebTextField(35);
        ruleTexrField.setText(STANDART_RULE);
        statusBar.add(ruleTexrField, ToolbarLayout.START);
        
        
        
        upPanel = new WebPanel();
        upPanel.setUndecorated ( false );
        upPanel.setLayout ( new HorizontalFlowLayout() );
        upPanel.setPaintSides ( false, false, true, false );
        upPanel.setMargin(5);
        
        
        //----------------------- up panel 
        urlTextField = new WebTextField(20);
        urlTextField.setInputPrompt ( "Enter name" );
        upPanel.add(urlTextField);
        
        prefixFileField = new WebFileChooserField ( frame );
        prefixFileField.setPreferredWidth ( 300 );
        prefixFileField.setMultiSelectionEnabled ( true );
        prefixFileField.setShowFileShortName ( false );
        prefixFileField.setShowRemoveButton ( false );
        
        upPanel.add(prefixFileField);
        
       
        notificationPopup = new WebNotificationPopup();
        notificationPopup.setIcon(NotificationIcon.information);

        final JLabel label = new JLabel("All files was succesfully renamed.");
        final WebButton button = new WebButton("Show concole log?");
        button.setRolloverDecoratedOnly(true);
        button.setDrawFocus(false);
        button.setLeftRightSpacing(0);
        button.setBoldFont();
        button.addActionListener((ActionEvent e1) -> {
            notificationPopup.hidePopup();
            
        });
        final CenterPanel centerPanel = new CenterPanel(button, false, true);
        notificationPopup.setContent(new GroupPanel(2, label, centerPanel));


        
        j = new JPanel();
        j.setLayout(new BorderLayout());

        WebButton okButton = new WebButton(" OK ");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rnm = new Reaname((ArrayList<File>) prefixFileField.getSelectedFiles(), urlTextField.getText());
                rnm.addObserver(JRenamer.this);

                
                progresBar = new WebProgressBar(0, rnm.totalFiles);
                progresBar.setStringPainted(true);
                upPanel.add(progresBar);
                
               
            }
        });
        upPanel.add(okButton);

        //----------------------- end up panel


        frame.setLayout(new BorderLayout());
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
        
        j.add(upPanel, BorderLayout.NORTH);
        frame.add(j, BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);
    }
  private void initColumnSizes ( JTable table )
    {
        FileTableModel model = ( FileTableModel ) table.getModel ();
        TableColumn column;
        Component comp;
        int headerWidth;
        int cellWidth;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader ().getDefaultRenderer ();

        for ( int i = 0; i < model.getColumnCount (); i++ )
        {
            column = table.getColumnModel ().getColumn ( i );

            comp = headerRenderer.getTableCellRendererComponent ( null, column.getHeaderValue (), false, false, 0, 0 );
            headerWidth = comp.getPreferredSize ().width;

            comp = table.getDefaultRenderer ( model.getColumnClass ( i ) ).
                    getTableCellRendererComponent ( table, longValues[ i ], false, false, 0, i );
            cellWidth = comp.getPreferredSize ().width;  

            column.setPreferredWidth ( Math.max ( headerWidth, cellWidth ) );
        }
    }
 
    public void update(Observable o, Object arg) {
        progresBar.setValue((int) rnm.progress);
        if (rnm.progress >= rnm.totalFiles) {
            FileTableModel ftm;
            WebTable table = new WebTable(ftm=new FileTableModel(rnm.data));
            WebScrollPane scrollPane = new WebScrollPane(table);
            // Custom column
            TableColumn column = table.getColumnModel().getColumn(1);

            // Custom renderer
            WebTableCellRenderer renderer = new WebTableCellRenderer();
            renderer.setToolTipText("Click for combo box");
            column.setCellRenderer(renderer);
            initColumnSizes(table);
            j.add(scrollPane, BorderLayout.CENTER);
            progresBar.setVisible(false);
            WebButton renameButton = new WebButton("Rename", (ActionEvent e) -> {
                if(rnm.doReaname(ftm.getData(),ruleTexrField.getText())){
                  NotificationManager.showNotification ( notificationPopup );
                };
            });
            statusBar.add(renameButton);

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WebLookAndFeel.install();
                WebLookAndFeel.initializeManagers();
                new JRenamer();
            }
        });
    }
}

