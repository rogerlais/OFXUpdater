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
public class OFXException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Only to silent hints
     *
     * @param message
     */
    public OFXException(String message) {
        super(message);
    }

}
