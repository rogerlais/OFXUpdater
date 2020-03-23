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
public class OFXException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private BBTransactionHelper transaction;

    /**
     * Only to silent hints
     *
     * @param message
     */
    public OFXException(String message) {
        super(message);
    }

    public OFXException(BBTransactionHelper transaction, String message) {
        super(message);
        this.transaction = transaction;
    }

    @Override
    public String getMessage() {
        String msg;
        if (this.transaction != null) {
            msg = super.getMessage() + 
                    "\r\nRefNum=" + this.transaction.getRefNum() + 
                    "\r\nCheckum=" + this.transaction.getCheckNum() + 
                    "\r\nOriginal Memo=" + this.transaction.getOriginalMemo();
        } else {
            msg = super.getMessage();
        }
        return msg;
    }
}
