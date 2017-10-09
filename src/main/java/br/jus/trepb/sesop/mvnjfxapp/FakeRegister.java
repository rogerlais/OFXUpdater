/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

/**
 *
 * @author roger
 */
public class FakeRegister {

    private String trueBranch;
    private String trueBranchVD;
    private String trueAccount;
    private String trueAccountVD;
    private String trueShortName;
    private String fakeBranch;
    private String fakeBranchVD;
    private String fakeAccount;
    private String fakeAccountVD;
    private String fakeShortName;
    private String comment;
    private String cashOutAccount;

    /**
     * @return the trueBranch
     */
    public String getTrueBranch() {
        return trueBranch;
    }

    /**
     * @param trueBranch the trueBranch to set
     */
    public void setTrueBranch(String trueBranch) {
        this.trueBranch = trueBranch;
    }

    /**
     * @param trueBranchVD the trueBranchVD to set
     */
    public void setTrueBranchVD(String trueBranchVD) {
        this.trueBranchVD = trueBranchVD;
    }

    /**
     * @return the trueAccount
     */
    public String getTrueAccount() {
        return trueAccount;
    }

    /**
     * @param trueAccount the trueAccount to set
     */
    public void setTrueAccount(String trueAccount) {
        this.trueAccount = trueAccount;
    }

    /**
     * @param trueAccountVD the trueAccountVD to set
     */
    public void setTrueAccountVD(String trueAccountVD) {
        this.trueAccountVD = trueAccountVD;
    }

    /**
     * @return the trueShortName
     */
    public String getTrueShortName() {
        return trueShortName;
    }

    /**
     * @param trueShortName the trueShortName to set
     */
    public void setTrueShortName(String trueShortName) {
        this.trueShortName = trueShortName;
    }

    /**
     * @return the fakeBranch
     */
    public String getFakeBranch() {
        return fakeBranch;
    }

    /**
     * @param fakeBranch the fakeBranch to set
     */
    public void setFakeBranch(String fakeBranch) {
        this.fakeBranch = fakeBranch;
    }

    /**
     * @return the fakeBranchVD
     */
    public String getFakeBranchVD() {
        return fakeBranchVD;
    }

    /**
     * @param fakeBranchVD the fakeBranchVD to set
     */
    public void setFakeBranchVD(String fakeBranchVD) {
        this.fakeBranchVD = fakeBranchVD;
    }

    /**
     * @return the fakeAccount
     */
    public String getFakeAccount() {
        return this.fakeAccount;
    }

    /**
     * @param fakeAccount the fakeAccount to set
     */
    public void setFakeAccount(String fakeAccount) {
        this.fakeAccount = fakeAccount;
    }

    /**
     * @return the fakeAccountVD
     */
    public String getFakeAccountVD() {
        return fakeAccountVD;
    }

    /**
     * @return the fakeShortName
     */
    public String getFakeShortName() {
        return fakeShortName;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public FakeRegister( //constructor code-format
            String trueBranch, String trueBranchVD, String trueAccount, String trueAccountVD, String trueShortName,
            String fakeBranch, String fakeBranchVD, String fakeAccount, String fakeAccountVD, String fakeShortName,
            String cashOutAccount, String comment) {

        this.trueBranch = trueBranch; //todo: validar entradas
        this.trueBranchVD = trueBranchVD;
        this.trueAccount = trueAccount;
        this.trueAccountVD = trueAccountVD;
        this.trueShortName = trueShortName;
        this.fakeBranch = fakeBranch;
        this.fakeBranchVD = fakeBranchVD;
        this.fakeAccount = fakeAccount;
        this.fakeAccountVD = fakeAccountVD;
        this.fakeShortName = fakeShortName;
        this.cashOutAccount = cashOutAccount;
        this.comment = comment;
    }

    /**
     * @return the cashOutAccount
     */
    public String getCashOutAccount() {
        return cashOutAccount;
    }
}
