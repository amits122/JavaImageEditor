/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projecti;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.util.*;


/**
 *
 * @author amits
 */

class imgFilters{
    
    BufferedImage img, resultImg;
    String opFileName;
    int height, width;

    imgFilters(){
        try {
            img = ImageIO.read(new File(home.workingFileDirectory));
//            img = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\lenna.png")); // Testing code
            height = img.getHeight();
            width = img.getWidth();
            resultImg= new BufferedImage(width,height,5);
        } catch (IOException e) {
            e.printStackTrace();
        }            
    }

    public void writeImg(String filename){
        try{
            File outputfile= new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filename+".png");
            ImageIO.write(resultImg,"png",outputfile);
        }
        catch(IOException e){
            System.out.println(e);
        }
    
    }
    
    public void negative(){
        
        for(int y = 0; y < height ; y++){
            for(int x = 0; x < width ; x++){
                int pixelVal = img.getRGB(x,y);
                int newPixelVal = (0xffffffff-pixelVal)|0xff000000;
                resultImg.setRGB(x,y,newPixelVal);
            }
        }
        
        this.writeImg(home.workingFile+"negative");
    }
    
    public int greyMaker(int px){
        int alpha = (px>>24)&0xff;
        int r = (px>>16)&0xff;
        int g = (px>>8)&0xff;
        int b = (px)&0xff;
        int average = (r+g+b)/3;
        int newPixelVal = alpha<<24|average<<16|average<<8|average;
        return newPixelVal;
    }
    
    public void greyscale(){
        
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixelVal = img.getRGB(x,y);
                int newPixelVal = this.greyMaker(pixelVal);
                resultImg.setRGB(x,y,newPixelVal);
            }
        }
        this.writeImg(home.workingFile+"grey");
    
    }

    public int getSepia(int px){
        int alpha = (px>>24)&0xff;
        int r = (px>>16)&0xff;
        int g = (px>>8)&0xff;
        int b = (px)&0xff;
        r *= 0.43;
        g *= 0.259;
        b *= 0.078;
        int newPixelVal = alpha<<24|r<<16|g<<8|b;
        return newPixelVal;
    }

    public void sepia(){    
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixelVal = img.getRGB(x,y);
                int newPixelVal = this.getSepia(pixelVal);
                resultImg.setRGB(x,y,newPixelVal);
            }
        }
        this.writeImg(home.workingFile+"sepia");
    }
    
    public void makeRed(){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixelVal = img.getRGB(x,y);
                int newPixelVal = pixelVal&0xffff0000;
                resultImg.setRGB(x,y,newPixelVal);
            }
        }
        this.writeImg(home.workingFile+"red");
    }
    
    public void blackWhite(){
        //Implementation applies Otsu's Algorithm of thresholding.
        int t;
        BufferedImage greyscale = new BufferedImage(width,height,5);
        BufferedImage bwimage = new BufferedImage(width,height,5);
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixelVal = img.getRGB(x,y);
                int newPixelVal = greyMaker(pixelVal);
                greyscale.setRGB(x,y,newPixelVal);
            }
        }
        int hist[] = new int[256];
        float bcv[] = new float[256];
        for(int i = 0; i < 256; i++)
            bcv[i] = 0;
        int max_index = 0;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixelVal = greyscale.getRGB(x,y);
                int r = (pixelVal>>16)&0xff;
                hist[r]++;
            }
        }

        for(t = 0; t < 256; t++){
            float wb = 0,wf = 0,meanb = 0,meanf = 0;
            for(int i = 0; i < t; i++){
                wb += hist[i];
                meanb += (i * hist[i]);
            }
            if(wb != 0)
                meanb /= wb;
            else
                meanb = 0;
            wb /= (width*height);

            for(int i = t; i < 256; i++){
                wf += hist[i];
                meanf += (i * hist[i]);
            }
            if(wf != 0)
                meanf /= wf;
            else
                meanf = 0;
            wf /= (width * height);
            bcv[t] = (wb * wf) * (meanb - meanf) * (meanb - meanf);
            if(bcv[t] >= bcv[max_index])
                max_index = t;
        }
        for(int y = 0; y < height; y++){
                for(int x = 0;x < width; x++){
                        int pixelVal = greyscale.getRGB(x,y);
                        int r = (pixelVal>>16)&0xff;
                        int newPixelVal;
                        if(r>max_index)
                                newPixelVal=0xffffffff;
                        else
                                newPixelVal=0x00000000;
                        bwimage.setRGB(x,y,newPixelVal);
                }
        }
        resultImg = bwimage;
        this.writeImg(home.workingFile+"b&w");
        
    }

    public int merger(int px1, int px2, float alpha)
    {
            int res_alpha = 0xff;
            int r1 = ((px1>>16)&0xff);
            r1 *= alpha;
            int g1 = ((px1>>8)&0xff);
            g1 *= alpha;
            int b1 = (px1&0xff);
            b1 *= alpha;
            int r2 = ((px2>>16)&0xff);
            r2 *= (1-alpha);
            int g2 = ((px2>>8)&0xff);
            g2 *= (1-alpha);
            int b2 = (px2&0xff);
            b2 *= (1-alpha);
            int new_pixel_val = res_alpha<<24|(r1+r2)<<16|(g1+g2)<<8|(b1+b2);
            return new_pixel_val;
    }
    
    
}

public class filter extends javax.swing.JFrame {
    
    public final List<String> filterList = Arrays.asList(home.workingFile+"negative.png", home.workingFile+"grey.png", home.workingFile+"sepia.png", home.workingFile+"red.png", home.workingFile+"b&w.png");
    //public final List<String> filterList = Arrays.asList("lennanegative.png", "lennagrey.png", "lennasepia.png", "lennared.png", "lennab&w.png"); //Testing Code
    public final List<String> filterListNames = Arrays.asList("Negative", "Greyscale", "Sepia", "Red", "B&W");

    public int filterIndex = -1;//the index of first item being displayed in bottom
    public imgFilters imgf = new imgFilters();

    /**
     * Creates new form filter
     */
    public filter() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        mainImage = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        filterImg1 = new javax.swing.JLabel();
        filterLabel1 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        filterImg2 = new javax.swing.JLabel();
        filterLabel2 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        filterImg3 = new javax.swing.JLabel();
        filterLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 102));
        jLabel2.setText("FILTERS");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projecti/images/baseline_arrow_back_black_18dp.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(170, 170, 170))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addContainerGap(27, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setPreferredSize(new java.awt.Dimension(0, 6));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(204, 102, 255));

        mainImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainImage, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
        );

        jPanel19.setBackground(new java.awt.Color(0, 0, 0));
        jPanel19.setPreferredSize(new java.awt.Dimension(0, 6));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projecti/images/baseline_keyboard_arrow_left_black_24dp.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projecti/images/baseline_keyboard_arrow_right_black_24dp.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setPreferredSize(new java.awt.Dimension(90, 90));

        filterImg1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        filterImg1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterImg1MouseClicked(evt);
            }
        });

        filterLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        filterLabel1.setToolTipText("");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filterImg1, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
            .addComponent(filterLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(filterImg1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filterLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setPreferredSize(new java.awt.Dimension(90, 90));

        filterImg2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        filterImg2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterImg2MouseClicked(evt);
            }
        });

        filterLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filterImg2, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
            .addComponent(filterLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(filterImg2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filterLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setPreferredSize(new java.awt.Dimension(90, 90));

        filterImg3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        filterImg3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterImg3MouseClicked(evt);
            }
        });

        filterLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filterImg3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(filterLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(filterImg3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filterLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //home.workingFile = "lenna"; //testing code
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(home.workingFileDirectory));
            //img = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\lenna.png")); // Testing code
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainImage.setIcon(new ImageIcon(img));
 
        //Uncomment the below if you want to scale image to label size. Comment the above
        
        Image scaledImg = img.getScaledInstance(mainImage.getWidth(), mainImage.getHeight(),Image.SCALE_SMOOTH);
        mainImage.setIcon(new ImageIcon(scaledImg));
        
        
        imgf.greyscale();
        imgf.negative();
        imgf.sepia();
        imgf.makeRed();
        imgf.blackWhite();
        jButton2.doClick();
        
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        new home().setVisible(true);
        this.dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        System.out.println(Arrays.toString(filterList.toArray()));
        System.out.println(Arrays.toString(filterListNames.toArray()));
        
        if(filterIndex < filterList.size() - 3){
            filterIndex += 1;
            BufferedImage img1, img2, img3;
            Image scaledImg1, scaledImg2, scaledImg3;
            try {
                img1 = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filterList.get(filterIndex)));
                img2 = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filterList.get(filterIndex + 1)));
                img3 = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filterList.get(filterIndex + 2)));
                
                scaledImg1 = img1.getScaledInstance(filterImg1.getWidth(), filterImg1.getHeight(), Image.SCALE_SMOOTH);
                filterImg1.setIcon(new ImageIcon(scaledImg1));
                filterLabel1.setText(filterListNames.get(filterIndex));
                
                scaledImg2 = img2.getScaledInstance(filterImg2.getWidth(), filterImg2.getHeight(), Image.SCALE_SMOOTH);
                filterImg2.setIcon(new ImageIcon(scaledImg2));
                filterLabel2.setText(filterListNames.get(filterIndex + 1));
                
                scaledImg3 = img3.getScaledInstance(filterImg3.getWidth(), filterImg3.getHeight(), Image.SCALE_SMOOTH);
                filterImg3.setIcon(new ImageIcon(scaledImg3));
                filterLabel3.setText(filterListNames.get(filterIndex + 2));
                
            } catch (Exception e) {
                e.printStackTrace();
            }

            
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(filterIndex > 0){
            filterIndex -= 1;
            BufferedImage img1, img2, img3;
            Image scaledImg1, scaledImg2, scaledImg3;
            try {
                img1 = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filterList.get(filterIndex)));
                img2 = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filterList.get(filterIndex + 1)));
                img3 = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filterList.get(filterIndex + 2)));
                
                scaledImg1 = img1.getScaledInstance(filterImg1.getWidth(), filterImg1.getHeight(), Image.SCALE_SMOOTH);
                filterImg1.setIcon(new ImageIcon(scaledImg1));
                filterLabel1.setText(filterListNames.get(filterIndex));
                
                scaledImg2 = img2.getScaledInstance(filterImg2.getWidth(), filterImg2.getHeight(), Image.SCALE_SMOOTH);
                filterImg2.setIcon(new ImageIcon(scaledImg2));
                filterLabel2.setText(filterListNames.get(filterIndex + 1));
                
                scaledImg3 = img3.getScaledInstance(filterImg3.getWidth(), filterImg3.getHeight(), Image.SCALE_SMOOTH);
                filterImg3.setIcon(new ImageIcon(scaledImg3));
                filterLabel3.setText(filterListNames.get(filterIndex + 2));
                
            } catch (Exception e) {
                e.printStackTrace();
            }

            
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void filterImg1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterImg1MouseClicked
    int iter = filterListNames.indexOf(filterLabel1.getText());
    String filename = filterList.get(iter);
    BufferedImage img = null;

    try {
        //img = ImageIO.read(new File(home.workingFileDirectory));
        img = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filename)); // Testing code
    } catch (IOException e) {
        e.printStackTrace();
    }
    mainImage.setIcon(new ImageIcon(img));

    Image scaledImg = img.getScaledInstance(mainImage.getWidth(), mainImage.getHeight(),Image.SCALE_SMOOTH);
    mainImage.setIcon(new ImageIcon(scaledImg));
    // TODO add your handling code here:
    }//GEN-LAST:event_filterImg1MouseClicked

    private void filterImg2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterImg2MouseClicked
    int iter = filterListNames.indexOf(filterLabel2.getText());
    String filename = filterList.get(iter);
    BufferedImage img = null;

    try {
        //img = ImageIO.read(new File(home.workingFileDirectory));
        img = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filename)); // Testing code
    } catch (IOException e) {
        e.printStackTrace();
    }
    mainImage.setIcon(new ImageIcon(img));

    Image scaledImg = img.getScaledInstance(mainImage.getWidth(), mainImage.getHeight(),Image.SCALE_SMOOTH);
    mainImage.setIcon(new ImageIcon(scaledImg));        // TODO add your handling code here:
    }//GEN-LAST:event_filterImg2MouseClicked

    private void filterImg3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterImg3MouseClicked
    int iter = filterListNames.indexOf(filterLabel3.getText());
    String filename = filterList.get(iter);
    BufferedImage img = null;

    try {
        //img = ImageIO.read(new File(home.workingFileDirectory));
        img = ImageIO.read(new File("..\\ProjectI\\src\\projecti\\images\\mods\\"+filename)); // Testing code
    } catch (IOException e) {
        e.printStackTrace();
    }
    mainImage.setIcon(new ImageIcon(img));

    Image scaledImg = img.getScaledInstance(mainImage.getWidth(), mainImage.getHeight(),Image.SCALE_SMOOTH);
    mainImage.setIcon(new ImageIcon(scaledImg));        // TODO add your handling code here:
    }//GEN-LAST:event_filterImg3MouseClicked

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
            java.util.logging.Logger.getLogger(filter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(filter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(filter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(filter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new filter().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filterImg1;
    private javax.swing.JLabel filterImg2;
    private javax.swing.JLabel filterImg3;
    private javax.swing.JLabel filterLabel1;
    private javax.swing.JLabel filterLabel2;
    private javax.swing.JLabel filterLabel3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel mainImage;
    // End of variables declaration//GEN-END:variables
}
