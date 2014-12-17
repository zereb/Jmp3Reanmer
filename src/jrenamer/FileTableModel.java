package jrenamer;


import java.io.File;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author qqqq
 */
public class FileTableModel extends AbstractTableModel{
        private String[] columnNames = { "Old name", "new Name", "Artist (ID3)", "Song name (ID3)" };
        private Object[][] data;

        public final Object[] longValues = { "Jane", "None of the above", "hui", "hui"};

        public FileTableModel(Object[][] data) {
            this.data=data;

        }
        
        
        

        @Override
        public int getColumnCount ()
        {
            return columnNames.length;
        }

        @Override
        public int getRowCount ()
        {
            return data.length;
        }

        @Override
        public String getColumnName ( int col )
        {
            return columnNames[ col ];
        }

        @Override
        public Object getValueAt ( int row, int col )
        {
            return data[ row ][ col ];
        }

        @Override
        public Class getColumnClass ( int c )
        {
            return longValues[ c ].getClass ();
        }

        @Override
        public boolean isCellEditable ( int row, int col )
        {
            return col >= 1;
        }

        @Override
        public void setValueAt ( Object value, int row, int col )
        {
            data[ row ][ col ] = value;
            fireTableCellUpdated ( row, col );
        }
}
