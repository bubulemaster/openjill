package org.jill.sha.ui.directory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*; 
import java.io.*;
import java.util.*;


public class FileExplorer extends JFrame implements TreeSelectionListener, ActionListener {
    /**
     *  Root of directory list
     */
    private File root;
    
    /**
     *  Directory list
     */
    private TreeModel modele;
    
    /**
     *  List
     */
    private JTree tree;
    
    /**
     * Button ok
     */
    private final JButton okButton = new JButton("OK") ;
    
    /**
     * Button cancel
     */
    private final JButton cancelButton = new JButton("Cancel") ;
    
    /**
     * Current directory selected
     */
    private File currentDirectorySelected ;
    
    /**
     * Directory who have selected when click ok button
     */
    private String selectedDir = null ;
    
    public FileExplorer(String repertoire){
        super("Explorateur de fichiers");    
        
        root = new File(repertoire);
        modele = new FileTreeModel(root);
        tree = new JTree(modele);
        tree.setCellRenderer(new FileRenderer());
        tree.addTreeSelectionListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        getContentPane().setLayout(new GridLayout(2,1));
        getContentPane().add(new JScrollPane(tree));
        
        // Create button bar
        final LayoutManager buttonBar = new GridLayout(1,2) ;
        final JButton okButton = new JButton("OK") ; 
        final JButton cancelButton = new JButton("Cancel") ;
        final JPanel panelButtonBar = new JPanel() ;
                
        panelButtonBar.setLayout(buttonBar) ;
        
        panelButtonBar.add(okButton) ;
        panelButtonBar.add(cancelButton) ;
        
        getContentPane().add(panelButtonBar);
                
        setSize(500,500);
        setLocationRelativeTo(null); 
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Dimension d = okButton.getSize() ;
        d.height = 25 ;
        
        okButton.setMaximumSize(d) ;
        cancelButton.setMaximumSize(d) ;
        
    }
    
    public void valueChanged(TreeSelectionEvent e){
        TreePath path = e.getPath();
        currentDirectorySelected = (File)path.getLastPathComponent();
    }

    public String getSelectedDir() {
        return selectedDir;
    }

    public void actionPerformed(ActionEvent e) {
        // TODO Module de remplacement de m�thode auto-g�n�r�
        if (cancelButton.getText().equals(e.getActionCommand()))
        {
            selectedDir = null ;
        }
        else if (okButton.getText().equals(e.getActionCommand()))
        {
            selectedDir = currentDirectorySelected.getAbsolutePath() ;
        }
        
    }
}
