/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher.forms;

import filesearcher.data.DataFactory;
import filesearcher.data.FileHandler;
import wheeler.generic.data.DialogFactory;
import wheeler.generic.data.LogicHandler;

/**
 *
 * @author Greg
 */
public class Main extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        initialize();
    }
    
    
    /**
     * Initialize the program as necessary.
     * Makes sure Program Files\Wheeler\File Searcher is accessible. If it isn't, closes the program.
     */
    private void initialize(){
        try{
            if (!FileHandler.testProgramFolder(this)) System.exit(0);
        }
        catch(Exception e){
            DialogFactory.errorMsg(this, "An error occurred initializing the program", e, 1, 0);
        }
    }
    
    
    /**
     * Ask the user to choose the root of the search
     */
    private void setSearchRoot(boolean chooseFile){
        String root = (!chooseFile)
                ? DialogFactory.chooseFolder(this, txtRoot.getText()) // Our standard action
                : DialogFactory.chooseFile(this, txtRoot.getText());  // For when we have a specific file in mind
        if (root == null) return;
        txtRoot.setText(root);
    }
    
    
    /**
     * Enable/disable the "line" portion of the interface based on whether the search is for files or lines
     * @param setEnabled Are lines within files the subject of the search?
     */
    private void setSearchLineEnabled(boolean setEnabled){
        txtLine.setEnabled(setEnabled);
        chkExclude.setEnabled(setEnabled);
        txtExclude.setEnabled(setEnabled && chkExclude.isSelected());
        chkHide.setEnabled(setEnabled);
        chkCase.setEnabled(setEnabled);
        chkRegex.setEnabled(setEnabled);
        btnFind.setText((setEnabled) ? "Find Lines" : "Find Files");
    }
    
    
    /**
     * Enable/disable the field for line exclusions
     * @param setEnabled Are lines being excluded from the results?
     */
    private void setExcludeLineEnabled(boolean setEnabled){
        txtExclude.setEnabled(setEnabled);
    }
    
    
    /**
     * Set the line-field label according to the type of search string being provided
     * @param setEnabled Is the search being performed using a regular expression?
     */
    private void setSearchByRegex(boolean setEnabled){
        lblLine.setText((setEnabled) ? "Line matches:" : "Line contains:");
    }
    
    
    /**
     * Starts the search.
     * Collects/checks parameters, writes to params file, calls JAR file as seeker
     */
    private void startSearch() throws Exception{
        // Make sure we're using the right kind of slashes
        setSlashes(txtRoot); setSlashes(txtName); setSlashes(txtPath); setSlashes(txtTypes);
        
        // Call the DataFactory to handle the logic
        DataFactory.startSearch(
                txtRoot.getText(), txtName.getText(), txtPath.getText(), txtTypes.getText(),
                chkLine.isSelected(), txtLine.getText(), chkExclude.isSelected(), txtExclude.getText(),
                chkHide.isSelected(), chkCase.isSelected(), chkRegex.isSelected(), this
            );
    }
    
    
    /**
     * Set the slashes in a text field to the Windows filepath divider character.
     * Changes '/' characters to '\' characters
     * @param textField The textfield to set the slashes of.
     */
    private void setSlashes(javax.swing.JTextField textField){
        textField.setText(FileHandler.setSlashes(textField.getText()));
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblRoot = new javax.swing.JLabel();
        txtRoot = new javax.swing.JTextField();
        btnRoot = new javax.swing.JButton();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblPath = new javax.swing.JLabel();
        txtPath = new javax.swing.JTextField();
        lblTypes = new javax.swing.JLabel();
        txtTypes = new javax.swing.JTextField();
        sepLine = new javax.swing.JSeparator();
        chkLine = new javax.swing.JCheckBox();
        lblLine = new javax.swing.JLabel();
        txtLine = new javax.swing.JTextField();
        chkExclude = new javax.swing.JCheckBox();
        txtExclude = new javax.swing.JTextField();
        chkHide = new javax.swing.JCheckBox();
        chkCase = new javax.swing.JCheckBox();
        chkRegex = new javax.swing.JCheckBox();
        btnFind = new javax.swing.JButton();
        btnOutput = new javax.swing.JButton();
        barMenu = new javax.swing.JMenuBar();
        menPresets = new javax.swing.JMenu();
        itmLoadParameters = new javax.swing.JMenuItem();
        itmSaveParameters = new javax.swing.JMenuItem();
        menLine = new javax.swing.JMenu();
        itmLoadLine = new javax.swing.JMenuItem();
        itmCopyLine = new javax.swing.JMenuItem();
        itmSaveLine = new javax.swing.JMenuItem();
        menActions = new javax.swing.JMenu();
        itmOpen = new javax.swing.JMenuItem();
        itmCleanup = new javax.swing.JMenuItem();
        itmClearJar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("File, Line Searcher");

        lblTitle.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("File, Line Searcher");
        lblTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lblRoot.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblRoot.setText("Search Folder");

        txtRoot.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnRoot.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnRoot.setText("Choose");
        btnRoot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRootMouseClicked(evt);
            }
        });
        btnRoot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRootActionPerformed(evt);
            }
        });

        lblName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblName.setText("Filename");

        txtName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtName.setToolTipText("String(s) that must occur within the filename. If desired, starting with '-' can be used to exclude files.");

        lblPath.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPath.setText("Path");

        txtPath.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPath.setToolTipText("String(s) that must occur within the filepath. If desired, starting with '-' can be used to exclude files (also excludes sub-directories if it's a folder).");

        lblTypes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblTypes.setText("Filetypes");

        txtTypes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTypes.setToolTipText("The filepath must end with one of these strings. If any are provided, folders will automatically be excluded unless '\\' is specified. If desired, starting with '-' can be used to exclude files.");

        chkLine.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkLine.setText("Find lines within files");
        chkLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLineActionPerformed(evt);
            }
        });

        lblLine.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblLine.setText("Line contains:");

        txtLine.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtLine.setEnabled(false);

        chkExclude.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chkExclude.setText("Exclude:");
        chkExclude.setToolTipText("Exclude lines that match the search string but also this one");
        chkExclude.setEnabled(false);
        chkExclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkExcludeActionPerformed(evt);
            }
        });

        txtExclude.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtExclude.setEnabled(false);

        chkHide.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkHide.setText("Hide lines");
        chkHide.setToolTipText("Hides matching lines in the output file, printing only the file's path");
        chkHide.setEnabled(false);
        chkHide.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        chkCase.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkCase.setText("Check case");
        chkCase.setToolTipText("Toggle case-sensitivity");
        chkCase.setEnabled(false);

        chkRegex.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkRegex.setText("Regular expression");
        chkRegex.setToolTipText("Is the search string a regular expression? If yes, must match the entire line. Otherwise, the string need only occur somewhere within the line.");
        chkRegex.setEnabled(false);
        chkRegex.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkRegex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRegexActionPerformed(evt);
            }
        });

        btnFind.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFind.setText("Find Files");
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        btnOutput.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnOutput.setText("Open output file");

        menPresets.setText("Presets");

        itmLoadParameters.setText("Load parameters");
        menPresets.add(itmLoadParameters);

        itmSaveParameters.setText("Save parameters");
        menPresets.add(itmSaveParameters);

        barMenu.add(menPresets);

        menLine.setText("Line");

        itmLoadLine.setText("Load saved line");
        menLine.add(itmLoadLine);

        itmCopyLine.setText("Copy saved line");
        menLine.add(itmCopyLine);

        itmSaveLine.setText("Save current line");
        menLine.add(itmSaveLine);

        barMenu.add(menLine);

        menActions.setText("Actions");

        itmOpen.setText("Open a file");
        menActions.add(itmOpen);

        itmCleanup.setText("Cleanup output files");
        menActions.add(itmCleanup);

        itmClearJar.setText("Clear JAR location");
        menActions.add(itmClearJar);

        barMenu.add(menActions);

        setJMenuBar(barMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(chkLine)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkHide))
                    .addComponent(sepLine, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblPath)
                            .addComponent(lblTypes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtName)
                            .addComponent(txtTypes)
                            .addComponent(txtPath)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLine)
                            .addComponent(chkExclude))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtExclude)
                            .addComponent(txtLine)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkCase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkRegex))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOutput)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFind))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblRoot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(txtRoot))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRoot)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRoot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRoot)
                    .addComponent(btnRoot))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPath))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTypes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sepLine, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkLine)
                    .addComponent(chkHide))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkExclude)
                    .addComponent(txtExclude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCase)
                    .addComponent(chkRegex))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOutput)
                    .addComponent(btnFind))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRootActionPerformed
        setSearchRoot(false);
    }//GEN-LAST:event_btnRootActionPerformed

    private void chkLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLineActionPerformed
        setSearchLineEnabled(chkLine.isSelected());
    }//GEN-LAST:event_chkLineActionPerformed

    private void chkExcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkExcludeActionPerformed
        setExcludeLineEnabled(chkExclude.isSelected());
    }//GEN-LAST:event_chkExcludeActionPerformed

    private void chkRegexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRegexActionPerformed
        setSearchByRegex(chkRegex.isSelected());
    }//GEN-LAST:event_chkRegexActionPerformed

    private void btnRootMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRootMouseClicked
        if (LogicHandler.clickIsRightClick(evt)) setSearchRoot(true);
    }//GEN-LAST:event_btnRootMouseClicked

    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        try{
            startSearch();
        }
        catch(Exception e){
            DialogFactory.errorMsg(this, "An error occurred starting the search", e, 1, 0);
        }
    }//GEN-LAST:event_btnFindActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar barMenu;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnOutput;
    private javax.swing.JButton btnRoot;
    private javax.swing.JCheckBox chkCase;
    private javax.swing.JCheckBox chkExclude;
    private javax.swing.JCheckBox chkHide;
    private javax.swing.JCheckBox chkLine;
    private javax.swing.JCheckBox chkRegex;
    private javax.swing.JMenuItem itmCleanup;
    private javax.swing.JMenuItem itmClearJar;
    private javax.swing.JMenuItem itmCopyLine;
    private javax.swing.JMenuItem itmLoadLine;
    private javax.swing.JMenuItem itmLoadParameters;
    private javax.swing.JMenuItem itmOpen;
    private javax.swing.JMenuItem itmSaveLine;
    private javax.swing.JMenuItem itmSaveParameters;
    private javax.swing.JLabel lblLine;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblRoot;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTypes;
    private javax.swing.JMenu menActions;
    private javax.swing.JMenu menLine;
    private javax.swing.JMenu menPresets;
    private javax.swing.JSeparator sepLine;
    private javax.swing.JTextField txtExclude;
    private javax.swing.JTextField txtLine;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPath;
    private javax.swing.JTextField txtRoot;
    private javax.swing.JTextField txtTypes;
    // End of variables declaration//GEN-END:variables
}
